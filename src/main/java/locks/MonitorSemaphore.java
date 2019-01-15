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
                    //Supongamos que ping ejecuta wait() se libera el monitor atomicamente. De esta forma en el caso de ping-pong
                    //El thread pong puede ejecutar el metodo release sobre este semaforo para poder salir del wait()
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
            //Supongamos el thread ping se encuentra esperando en wait(), entonces el thread pong puede hacer realese
            //Cuando se ejecuta notify() se despierta algunos de los thread esperando en wait() en este caso al thread ping.
            this.notify();
        }
    }
}
