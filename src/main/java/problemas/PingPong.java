package problemas;

import locks.MonitorSemaphore;
import utils.ConcurrentUtils;

import java.util.concurrent.Semaphore;

public class PingPong {

    public static void main(String ... args) throws Exception {
        monitorSolution();
    }

    private static void threadOnlySolution() throws InterruptedException {
        Semaphore ping = new Semaphore(1);
        Semaphore pong = new Semaphore(0);

        Thread thread1 = new Thread(() -> {
            int count = 10;
            while(count > 0) {
                ping.acquireUninterruptibly();
                System.out.println("ping");
                ConcurrentUtils.sleep(1000);
                pong.release(1);
                count--;
            }
        });
        Thread thread2 = new Thread(() -> {
            int count = 10;
            while(count > 0){
                pong.acquireUninterruptibly();
                System.out.println("pong");
                ConcurrentUtils.sleep(1000);
                ping.release(1);
                count--;
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    private static void monitorSolution() throws Exception {
        MonitorSemaphore pongMonitor = new MonitorSemaphore(0);
        MonitorSemaphore pingMonitor = new MonitorSemaphore(1);

        Thread thread1 = new Thread(() -> {
            int count = 10;
            while(count > 0) {
                pingMonitor.acquire();
                System.out.println("ping");
                ConcurrentUtils.sleep(1000);
                pongMonitor.release();
                count--;
            }
        });


        Thread thread2 = new Thread(() -> {
            int count = 10;
            while(count > 0) {
                pongMonitor.acquire();
                System.out.println("pong");
                ConcurrentUtils.sleep(1000);
                pingMonitor.release();
                count--;
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }
}
