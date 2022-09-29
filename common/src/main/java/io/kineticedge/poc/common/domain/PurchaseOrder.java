package io.kineticedge.poc.common.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$type")
public class PurchaseOrder {

    @Data
    public static class LineItem {
        private String sku;
        private int quantity;
        private BigDecimal quotedPrice;
        private BigDecimal price;
    }

    private Instant timestamp;
    private String orderId;
    private String userId;
    private String storeId;
    private List<LineItem> items;

    private BigDecimal tax;
    private User user;
    private Store store;
}
