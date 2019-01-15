package locks;

public class MonitorSemaphore {
    private int permits;

    public MonitorSemaphore(int permits) {
        this.permits = permits;
    }

    public void acquire() {
        //Monitor
        synchronized (this) {
            //Cuando el thread entra al bloque synchronized y se queda esperando en wait(), este lock se libera atomicamente
            //De esta manera otros threads pueden intentar entrar.
            while(permits == 0) {
                //Tiene que estar en while porque cuando se hace notifyAll() todos se despiertan y no hay garantia de
                //de que thread gana. Entonces en caso de perder tiene que volver a intentar mas adelante.
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
