package problemas;

import utils.ConcurrentUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PingPong {

    public static void main(String ... args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Semaphore ping = new Semaphore(1);
        Semaphore pong = new Semaphore(0);

        executor.execute(() -> {
            int count = 10;
            while(count > 0) {
                ping.acquireUninterruptibly();
                System.out.println("ping");
                ConcurrentUtils.sleep(1000);
                pong.release(1);
                count--;
            }
        });

        executor.execute(() -> {
            int count = 10;
            while(count > 0){
                pong.acquireUninterruptibly(1);
                System.out.println("pong");
                ConcurrentUtils.sleep(1000);
                ping.release(1);
                count--;
            }
        });

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }
}
