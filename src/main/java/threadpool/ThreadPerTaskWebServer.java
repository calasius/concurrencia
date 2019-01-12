package threadpool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerTaskWebServer {

    public static void handleRequest(Socket connection) {

    }
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                public void run() {
                    handleRequest(connection);
                }
            };
            new Thread(task).start();
        }
    }

    public class TwoCounters {
        private long c1 = 0;
        private long c2 = 0;
        private Object lock1 = new Object();
        private Object lock2 = new Object();

        public void inc1() {
            synchronized(lock1) {
                c1++;   //Seccion critica 1
            }
        }

        public void inc2() {
            synchronized(lock2) {
                c2++;   //Seccion critica 2
            }
        }
    }

}
