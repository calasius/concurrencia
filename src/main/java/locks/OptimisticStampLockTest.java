package locks;

import com.google.common.collect.Maps;
import utils.ConcurrentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class OptimisticStampLockTest {

    public static void main(String ... args) throws InterruptedException {
        Map<String, String> map = new HashMap<>();
        StampedLock lock = new StampedLock();

        int n_threads = 500;
        double[] percentages = {0.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 1.0};
        Map<Double, Long> timeMeasurements = Maps.newHashMap();

        for (Double percentage : percentages) {
            long total = 0;
            for(int i = 0; i < 10; i++) {
                ExecutorService executor = Executors.newFixedThreadPool(50);
                long start = System.currentTimeMillis();
                int readers = (int)Math.floor(n_threads * percentage);
                int writers = n_threads - readers;
                IntStream.range(0,readers).forEach(val -> executor.execute(() -> {
                    long stamp = lock.tryOptimisticRead();
                    String value = map.get("key");
                    if (!lock.validate(stamp)) {
                        stamp = lock.readLock();
                        value = map.get("key");
                        ConcurrentUtils.sleep(1);
                        lock.unlockRead(stamp);
                    } else {
                        ConcurrentUtils.sleep(1);
                    }
                }));

                IntStream.range(0,writers).forEach(value -> executor.execute(() -> {
                    long stamp = lock.writeLock();
                    map.put("hola", "mundo");
                    ConcurrentUtils.sleep(1);
                    lock.unlockWrite(stamp);
                }));
                executor.shutdown();
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                long end =  System.currentTimeMillis();
                long time = end - start;
                total += time;
            }
            timeMeasurements.put(percentage, total / 10);
        }

        System.out.println(timeMeasurements);
    }
}
