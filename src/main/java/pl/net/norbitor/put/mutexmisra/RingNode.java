package pl.net.norbitor.put.mutexmisra;

import org.apache.commons.lang3.RandomUtils;
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
    private int receivedPing = 1;
    private int receivedPong = -1;
    private int nodeId;
    private int nodeNumber;
    private MessageSubscriber subscriber;
    private MessagePublisher publisher;
    private Thread csThread;

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
            incarnate(receivedPing);
        }
    }

    public void receivePing(PingMessage msg) {
        logger.info("Ping message received (" + msg.getValue() + ") last token: " + lastToken);
        receivedPing = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Pong message loss detected");
            regenerate(receivedPing);
        } else {
            lastToken = msg.getValue();
        }
        csThread = new Thread(new Worker(this));
        csThread.start();
    }

    public void receivePong(PongMessage msg) {
        logger.info("Pong message received (" + msg.getValue() + ") last token: " + lastToken);
        receivedPong = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Ping message loss detected");
            regenerate(receivedPong);
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
        incarnate(receivedPing);
    }

    private void regenerate(int value) {
        logger.info("Regenerating Ping and Pong messages");
        PingMessage pingMessage = new PingMessage(Math.abs(value));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        publishMessage(pingMessage);
        publishMessage(pongMessage);
    }

    private void incarnate(int value) {
        logger.info("Incarnating Ping and Pong messages");
        PingMessage pingMessage = new PingMessage(Math.abs((value + 1)));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        int loss = RandomUtils.nextInt(0,4);
        if (loss != 3) {
            publishMessage(pingMessage);
        } else {
            logger.warn("Will loss PING");
        }
        if (RandomUtils.nextInt(0,4) != 3 || loss == 3) {
            publishMessage(pongMessage);
        } else {
            logger.warn("Will loss PONG");
        }
    }

    private void publishMessage(Message message) {
        publisher.sendMessage(message);
    }
}
