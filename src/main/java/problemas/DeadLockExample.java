package problemas;

import utils.ConcurrentUtils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class DeadLockExample {

    public static void main(String ... args) throws InterruptedException {
        Semaphore sem1 = new Semaphore(1);
        Semaphore sem2 = new Semaphore(1);

        Thread t1 = new Thread(() -> {
            sem1.acquireUninterruptibly();
            ConcurrentUtils.sleep(1000);
            sem2.acquireUninterruptibly();
        });

        Thread t2 = new Thread(() -> {
            sem2.acquireUninterruptibly();
            ConcurrentUtils.sleep(1000);
            sem1.acquireUninterruptibly();
        });
        t1.setName("t1");
        t2.setName("t2");
        t1.start();
        t2.start();
    }
}
