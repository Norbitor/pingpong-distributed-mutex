package pl.net.norbitor.put.mutexmisra;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private final RingNode nodeRef;

    public Worker(RingNode nodeRef) {
        this.nodeRef = nodeRef;
    }

    @Override
    public void run() {
        LOG.info("Performing very time consuming calculation within CS.");
        try {
            Thread.sleep(RandomUtils.nextInt(1000,2000));
        } catch (InterruptedException e) {
            LOG.warn("Something went wrong when trying to sleep.");
            e.printStackTrace();
        }
        LOG.info("Finished CS computation.");
        nodeRef.leaveCS();
    }
}
