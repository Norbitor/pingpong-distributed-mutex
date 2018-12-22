package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Starting the ZMQ test app");

        Thread serverThread = new Thread(new ZServer());
        Thread clientThread = new Thread(new ZClient());

        logger.info("Starting server thread");
        serverThread.start();
        try {
            logger.debug("Performing small delay to avoid issues");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Starting client thread");
        clientThread.start();
    }
}
