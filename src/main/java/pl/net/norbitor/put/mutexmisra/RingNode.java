package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.Message;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;

public class RingNode {

    private final Logger logger = LoggerFactory.getLogger(RingNode.class);

    private int lastToken = 0;
    private int pingNumber = 1;
    private int pongNumber = -1;
    private int nodeId;
    private MessageSubscriber subscriber;
    private MessagePublisher publisher;
    private Thread csThread;

    public RingNode() {
        this.nodeId = 1;
        this.subscriber = new MessageSubscriber("localhost:5555", "A", this);
        this.publisher = new MessagePublisher("*", 5555, "A");
    }

    public void start() {
        logger.info("Starting Ring Node...");
        Thread subscriberThread = new Thread(subscriber, "subscriber");
        subscriberThread.start();
        if (nodeId == 1) {
            initialProcessing();
        }
    }

    private void initialProcessing() {
        logger.info("This is first node, assuming token possession and starting CS");
        new Worker(this).run();
        incarnate(pingNumber);
        PingMessage pingMessage = new PingMessage(this.pingNumber);
        PongMessage pongMessage = new PongMessage(this.pongNumber);
        this.publisher.sendMessage(pingMessage);
        this.publisher.sendMessage(pongMessage);
    }

    public void receivePing(PingMessage msg) {
        logger.info("Ping message received (" + msg.getValue() + ") last token: " + lastToken);
        pingNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Pong message loss detected");
            regenerate(pingNumber);
        } else {
            lastToken = msg.getValue();
        }
        csThread = new Thread(new Worker(this));
        csThread.start();
    }

    public void receivePong(PongMessage msg) {
        logger.info("Pong message received (" + msg.getValue() + ") last token: " + lastToken);
        pongNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Ping message loss detected");
            regenerate(pongNumber);
        } else {
            lastToken = msg.getValue();
        }
        try {
            csThread.join();
            leaveCS();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void leaveCS() {
        logger.info("Leaving Critical Section");
        incarnate(pingNumber);
    }

    private void regenerate(int value) {
        logger.info("Regenerating Ping and Pong messages");
        this.pingNumber = Math.abs(value);
        this.pongNumber = -this.pingNumber;
    }

    private void incarnate(int value) {
        logger.info("Incarnating Ping and Pong messages");
        this.pingNumber = Math.abs(value) + 1;
        this.pongNumber = -this.pingNumber;
    }
}
