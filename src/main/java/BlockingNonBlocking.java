import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class BlockingNonBlocking {

    interface Counter {
        void increment();
        long getCounter();
    }

    static class BlockingCounter implements Counter {

        private long counter = 0;

        public synchronized void increment() {
            this.counter ++;
        }

        public synchronized long getCounter() {
            return this.counter;
        }
    }

    static class NonBlockingCounter implements Counter {

        private AtomicLong counter;

        public void increment() {
            this.counter.incrementAndGet();
        }

        public long getCounter() {
            return this.counter.get();
        }

    }

    public static void main(String ... args) throws InterruptedException {
        final Counter nonBlocking = new NonBlockingCounter();
        Counter blocking = new BlockingCounter();
        ExecutorService executor = Executors.newCachedThreadPool();
        int numThreads = 2000000;
        Instant start = Instant.now();
        for(int i = 0; i < numThreads; i++) {
            executor.submit(() -> nonBlocking.increment());
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        Instant end = Instant.now();
        System.out.println(String.format("Nonblocking toke %d seconds", Duration.between(start, end).getSeconds()));

        executor = Executors.newCachedThreadPool();
        start = Instant.now();
        for(int i = 0; i < numThreads; i++) {
            executor.submit(() -> blocking.increment());
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        end = Instant.now();
        System.out.println(String.format("Blocking toke %d seconds", Duration.between(start, end).getSeconds()));
    }
}
