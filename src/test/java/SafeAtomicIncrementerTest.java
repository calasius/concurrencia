import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SafeAtomicIncrementerTest {

    private static final int THREADS_COUNT = 50;
    private static final long ITERATIONS_1_COUNT = 10000;
    private static final long ITERATIONS_2_COUNT = 50000;

    @Test
    public void incrementTest() throws InterruptedException {
        Data data = new Data();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
        LocalDateTime from = LocalDateTime.now();
        for (int i = 0; i < ITERATIONS_1_COUNT; i++) {
            Runnable worker = new IncrementerThread(data);
            executor.execute(worker);
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        logDuration(from);

        Assert.assertEquals(data.getValue().get(), ITERATIONS_1_COUNT * ITERATIONS_2_COUNT);
    }

    private class IncrementerThread implements Runnable {
        private Data data;

        public IncrementerThread(Data data) {
            this.data = data;
        }

        public void run() {
            data.incValue();
        }
    }

    private class Data {

        private AtomicInteger value = new AtomicInteger();

        public Data() {
            this.value.set(0);
        }

        public AtomicInteger getValue() {
            return value;
        }

        public void incValue() {
            for (int i = 0; i < ITERATIONS_2_COUNT; i++) {
                this.value.incrementAndGet();
            }
        }

    }

    private void logDuration(LocalDateTime from) {
        LocalDateTime to = LocalDateTime.now();
        Duration duration = Duration.between(from, to);
        System.out.println(duration.getSeconds() + " seconds");
    }
}