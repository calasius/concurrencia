package problemas;

import utils.ConcurrentUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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

    static class ConditionMonitor {

        private boolean condition;

        public ConditionMonitor(boolean condition) {
            this.condition = condition;
        }

        public void checkCondition() {
            synchronized (this) {
                while(!condition) {
                    try {
                        this.wait();
                        condition = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                condition = false;
            }
        }

        public void signal() {
            synchronized (this) {
                this.notify();
            }
        }
    }

    private static void monitorSolution() throws Exception {
        ConditionMonitor pongMonitor = new ConditionMonitor(false);
        ConditionMonitor pingMonitor = new ConditionMonitor(true);

        Thread thread1 = new Thread(() -> {
            int count = 10;
            while(count > 0) {
                pingMonitor.checkCondition();
                System.out.println("ping");
                ConcurrentUtils.sleep(1000);
                pongMonitor.signal();
                count--;
            }
        });


        Thread thread2 = new Thread(() -> {
            int count = 10;
            while(count > 0) {
                pongMonitor.checkCondition();
                System.out.println("pong");
                ConcurrentUtils.sleep(1000);
                pingMonitor.signal();
                count--;
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }

    private static void threadPoolSolution() throws InterruptedException {
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
