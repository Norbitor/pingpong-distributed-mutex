package pl.net.norbitor.put.mutexmisra;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(Worker.class);

    @Override
    public void run() {
        logger.info("Performing very time consuming calculation within CS.");
        try {
            Thread.sleep(RandomUtils.nextInt(1000,2000));
        } catch (InterruptedException e) {
            logger.warn("Something went wrong when trying to sleep.");
            e.printStackTrace();
        }
        logger.info("Finished CS computation.");
    }
}
