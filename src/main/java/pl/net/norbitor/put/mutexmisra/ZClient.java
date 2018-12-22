package pl.net.norbitor.put.mutexmisra;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZClient implements Runnable {
    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            System.out.println("Connecting to hello world server");

            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://*:5555");

            for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
                String request = "Hello";
                System.out.println("Sending Hello " + requestNbr);
                socket.send(request.getBytes(ZMQ.CHARSET), 0);

                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received " + new String(reply, ZMQ.CHARSET) + " " +
                                requestNbr
                );
            }
        }
    }
}
