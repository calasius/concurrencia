package counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PruebaDefaultCounter {

    private static final int n_threads = 500;

    public static void main(String ... args) throws InterruptedException {

        Counter counter = new NonSecureCounter();

        ExecutorService executor = Executors.newFixedThreadPool(n_threads);

        IntStream.range(0,10*n_threads).forEach(val -> executor.execute(() -> counter.increment()));

        IntStream.range(0,10*n_threads).forEach(val -> executor.execute(() -> counter.decrement()));

        executor.shutdown();

        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        System.out.println(String.format("Valor total del counter = %s", counter.value()));
    }
}
