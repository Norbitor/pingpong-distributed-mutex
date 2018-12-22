package pl.net.norbitor.put.mutexmisra;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZServer implements Runnable {
    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
                );

                String response = "world";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.err.println("Unable to do awesome thing!");
            e.printStackTrace();
        }
    }
}
