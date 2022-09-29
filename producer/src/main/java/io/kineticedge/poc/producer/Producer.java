package io.kineticedge.poc.producer;

import io.kineticedge.poc.common.domain.PurchaseOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Producer {

    private static final Random RANDOM = new Random();

    private static final String ORDER_PREFIX = RandomStringUtils.randomAlphabetic(2).toUpperCase(Locale.ROOT);

    private final Options options;

    public Producer(final Options options) {
        this.options = options;
    }

    private String getRandomSku(int index) {

        if (options.getSkus() == null) {
            return StringUtils.leftPad(Integer.toString(RANDOM.nextInt(options.getMaxSku())), 10, '0');
        } else {

            final int productId = options.getSkus().get(index);

            if (productId < 0 || productId >= options.getMaxSku()) {
                throw new IllegalArgumentException("invalid product number");
            }

            return StringUtils.leftPad(Integer.toString(productId), 10, '0');
        }
    }

    private String getRandomUser() {
        return Integer.toString(RANDOM.nextInt(options.getNumberOfUsers()));
    }

    private String getRandomStore() {
        return Integer.toString(RANDOM.nextInt(options.getNumberOfStores()));
    }

    private int getRandomItemCount() {

        if (options.getLineItemCount().indexOf('-') < 0) {
            return Integer.parseInt(options.getLineItemCount());
        } else {
            String[] split = options.getLineItemCount().split("-");
            int min = Integer.parseInt(split[0]);
            int max = Integer.parseInt(split[1]);
            return RANDOM.nextInt(max + 1 - min) + min;
        }
    }

    private int getRandomQuantity() {
        return RANDOM.nextInt(options.getMaxQuantity()) + 1;
    }

    private static int counter = 0;

    private static String orderNumber() {
        return ORDER_PREFIX + "-" + (counter++);
    }

    /**
     * Creates a fake purchase order for demonstration.
     */
    private PurchaseOrder createPurchaseOrder() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        purchaseOrder.setTimestamp(Instant.now());
        purchaseOrder.setOrderId(orderNumber());
        purchaseOrder.setUserId(getRandomUser());
        purchaseOrder.setStoreId(getRandomStore());
        purchaseOrder.setItems(IntStream.range(0, getRandomItemCount())
                .boxed()
                .map(i -> {
                    final PurchaseOrder.LineItem item = new PurchaseOrder.LineItem();
                    item.setSku(getRandomSku(i));
                    item.setQuantity(getRandomQuantity());
                    item.setQuotedPrice(null); // TODO remove from domain
                    return item;
                })
                .collect(Collectors.toList())
        );

        return purchaseOrder;
    }

    private static class ProducerCallback implements Callback {

        private long start = System.currentTimeMillis();

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                log.error("error producing to kafka", exception);
            } else {
                log.debug("topic={}, partition={}, offset={}, time={}", metadata.topic(), metadata.partition(), metadata.offset(), (System.currentTimeMillis() - start));
            }
        }
    }

    public void start() {


        try (KafkaProducer<String, PurchaseOrder> kafkaProducer = new KafkaProducer<>(options.getKafkaProperties())) {

            while (true) {

                PurchaseOrder purchaseOrder = createPurchaseOrder();

                log.info("Sending key={}, value={}", purchaseOrder.getOrderId(), purchaseOrder);
                // 2nd argument can be the actual partition of interest, but is typically not done.
                Future<RecordMetadata> future = kafkaProducer.send(
                        new ProducerRecord<>(options.getPurchaseTopic(), purchaseOrder.getOrderId(), purchaseOrder),
                        new ProducerCallback()
                );

                // NEVER DO THIS! -- this prevents batching and overall performance; use the callback handler as shown above
                // this does exist if there is a reason to do so, but typically it is an anti-pattern.
                //future.get();

                // for demonstration, not something to actually do in a real application.
                try {
                    Thread.sleep(options.getPause());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private static void dumpRecord(final ConsumerRecord<String, String> record) {
        log.info("Record:\n\ttopic     : {}\n\tpartition : {}\n\toffset    : {}\n\tkey       : {}\n\tvalue     : {}", record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }

    public static Properties toProperties(final Map<String, Object> map) {
        final Properties properties = new Properties();
        properties.putAll(map);
        return properties;
    }
}
