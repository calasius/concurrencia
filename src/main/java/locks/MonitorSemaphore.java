package locks;

public class MonitorSemaphore {
    private int permits;

    public MonitorSemaphore(int permits) {
        this.permits = permits;
    }

    public void acquire() {
        synchronized (this) {
            while(permits == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            permits--;
        }
    }

    public void release() {
        synchronized (this) {
            permits++;
            this.notify();
        }
    }
}
