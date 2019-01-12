import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import com.google.common.collect.Lists;
import org.junit.Test;

public class ParallelIncrementerTest {

    private static final int THREADS_COUNT = 50;
    private static final long ITERATIONS_1_COUNT = 50000;
    private static final long ITERATIONS_2_COUNT = 500000;

    @Test
    public void incrementTest() throws InterruptedException {
        List<Data> values = Lists.newArrayList();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
        LocalDateTime from = LocalDateTime.now();
        for (int i = 0; i < ITERATIONS_1_COUNT; i++) {
            Data data = new Data();
            Runnable worker = new IncrementerThread(data);
            executor.execute(worker);
            values.add(data);
        }
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        logDuration(from);

        for (int i = 0; i < ITERATIONS_1_COUNT; i++) {
            Assert.assertEquals(values.get(i).getValue(), ITERATIONS_2_COUNT);
        }
    }

    private class IncrementerThread implements Runnable {
        private Data data;

        public IncrementerThread(Data data) {
            this.data = data;
        }

        @Override
        public void run() {
            data.incValue();
        }
    }

    private class Data {

        private long value;

        public Data() {
            this.value = 0;
        }

        public long getValue() {
            return value;
        }

        public void incValue() {
            for (int i = 0; i < ITERATIONS_2_COUNT; i++) {
                this.value += 1;
            }
        }
    }

    private void logDuration(LocalDateTime from) {
        LocalDateTime to = LocalDateTime.now();
        Duration duration = Duration.between(from, to);
        System.out.println(duration.getSeconds() + " seconds");
    }
}