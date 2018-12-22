package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZServer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(ZServer.class);
    @Override
    public void run() {
        logger.info("Running Server's thread.");
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            logger.info("Ready and waiting for client connections.");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                logger.info(
                        "Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
                );

                String response = "world";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("An error occur while processing", e);
        }
        logger.info("Server finished listening");
    }
}
