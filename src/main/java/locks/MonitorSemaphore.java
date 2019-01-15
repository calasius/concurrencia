package locks;

public class MonitorSemaphore {
    private int permits;

    public MonitorSemaphore(int permits) {
        this.permits = permits;
    }

    public void acquire() {
        //Monitor
        synchronized (this) {
            while(permits == 0) {
                try {
                    //Supongamos que ping ejecuta wait() se libera el monitor atomicamente. De esta forma el
                    //thread pong puede ejecutar el metodo release sobre este semaforo.
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
            //El thread pong puede hacer realese
            //Cuando se ejecuta notify() se despierta algunos de los thread esperando en wait() en este caso al thread ping.
            this.notify();
        }
    }
}
