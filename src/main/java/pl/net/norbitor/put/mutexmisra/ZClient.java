package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZClient implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(ZClient.class);
    @Override
    public void run() {
        logger.info("Running Client's thread.");
        try (ZContext context = new ZContext()) {
            logger.info("Connecting to hello world server...");

            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://*:5555");

            for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
                String request = "Hello";
                logger.info("Sending Hello " + requestNbr);
                socket.send(request.getBytes(ZMQ.CHARSET), 0);

                byte[] reply = socket.recv(0);
                logger.info(
                        "Received " + new String(reply, ZMQ.CHARSET) + " " +
                                requestNbr
                );
            }
        }
    }
}
