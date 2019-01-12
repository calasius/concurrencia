import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ProbabilisticPi {

    static class WorkerThread implements Runnable {
        private int me;
        private long nThrow;
        public long hits = 0;

        public WorkerThread(int me, long nThrow) {
            this.me = me;
            this.nThrow = nThrow;
        }

        @Override
        public void run() {
            Random r = new Random(me);
            for (int i = 0; i < nThrow; i++) {
                Double x = r.nextDouble();
                Double y = r.nextDouble();
                if ((x * x + y * y) < 1) {
                    hits++;
                }
            }
        }
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        int cores = Runtime.getRuntime().availableProcessors();
        //cores = 1;

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pi-%d")
                .setDaemon(true)
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(cores, threadFactory);
        long total = (long) 1e8;
        long nThrow = total / cores;
        WorkerThread[] threads = new WorkerThread[cores];
        for (int i = 0; i < cores; i++) {
            threads[i] = new WorkerThread(i, nThrow);
            Runnable worker = threads[i];
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }
        long hits = 0;
        for (int i = 0; i < cores; i++) {
            hits += threads[i].hits;
        }

        double pi = 4 * (((double) hits) / total);
        Instant end = Instant.now();
        System.out.println(String.format("pi = %.10f toke %d seconds", pi, Duration.between(start, end).getSeconds()));
    }
}
