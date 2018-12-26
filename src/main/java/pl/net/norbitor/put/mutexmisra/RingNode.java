package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.Message;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;
import pl.net.norbitor.put.mutexmisra.util.AppUtil;

public class RingNode {

    private final Logger logger = LoggerFactory.getLogger(RingNode.class);

    private int lastToken = 0;
    private int receivedPing;
    private int nodeId;
    private int nodeNumber;
    private MessageSubscriber subscriber;
    private MessagePublisher publisher;

    public RingNode() {
        this.nodeId = 1;
        this.nodeNumber = 2;
        this.subscriber = new MessageSubscriber("localhost:5555", "A", this);
        this.publisher = new MessagePublisher("*", 5555, "A");
    }

    public void start() {
        logger.info("Starting Ring Node...");
        Thread subscriberThread = new Thread(subscriber, "subscriber");
        subscriberThread.start();
        if (nodeId == 1) {
            logger.info("This is first node, assuming token possession");
            new Worker(this).run();
        }
    }

    public void receivePing(PingMessage msg) {
        logger.info("Ping message received (" + msg.getValue() + ") last token: " + lastToken);
        receivedPing = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Pong message loss detected");
            regenerate();
        }
        new Worker(this).run();
    }

    public void receivePong(PongMessage msg) {
        logger.info("Pong message received (" + msg.getValue() + ") last token: " + lastToken);
        if (msg.getValue() == lastToken) {
            logger.warn("Ping message loss detected");
            regenerate();
        }
    }

    public void leaveCS() {
        logger.info("Leaving Critical Section");
        incarnate();
    }

    private void regenerate() {
        logger.info("Regenerating Ping and Pong messages");
        PingMessage pingMessage = new PingMessage(Math.abs(nodeId));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        publishMessage(pingMessage);
        publishMessage(pongMessage);
    }

    private void incarnate() {
        logger.info("Incarnating Ping and Pong messages");
        PingMessage pingMessage = new PingMessage(Math.abs((lastToken + 1) % nodeNumber));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        publishMessage(pingMessage);
        publishMessage(pongMessage);
    }

    private void publishMessage(Message message) {
        lastToken = message.getValue();
        publisher.sendMessage(message);
    }
}
