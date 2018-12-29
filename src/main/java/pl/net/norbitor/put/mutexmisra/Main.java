package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Yet another POC");

        Thread node1 = new Thread(() -> {
            RingNode node = new RingNode("localhost:5556", 5555, 1);
            node.start();
        }, "Node-1");
        Thread node2 = new Thread(() -> {
            RingNode node = new RingNode("localhost:5555", 5556, 2);
            node.start();
        }, "Node-2");

        node1.start();
        node2.start();
    }
}
