package io.kineticedge.poc.consumer;

import io.kineticedge.poc.tools.config.OptionsUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Main {

    final static Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> System.err.println("Uncaught exception in thread '" + t.getName() + "': " + e.getMessage());

    public static void main(final String[] args) throws Exception {
        Main main = new Main();
        main.start(args);
    }

    public void start(final String[] args) {
        final Options options = OptionsUtil.parse(Options.class, args);

        if (options == null) {
            return;
        }

        final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            //t.setDaemon(true);
            t.setUncaughtExceptionHandler(exceptionHandler);
            return t;
        });

        final Consumer consumer = new Consumer(options);

        //
        // Using a thread to do consumer, because in most application there is additional activity than just
        // consuming from Kafka. This is also where most frameworks tend to provide code to manage this for you;
        // and typically why developers believe the framework is necessary.
        //
        // If there were 2 consumers then each would have their own thread, their own heartbeat as a result.
        //
        // Future<?> future =
        executor.submit(consumer::start);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.stop();
        }));

    }
}

