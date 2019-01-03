package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;

public class RingNode {

    private static final Logger LOG = LoggerFactory.getLogger(RingNode.class);
    private static final String MESSAGE_GROUP = "A";
    private static final String PUBLISHING_ADDRESS = "*";
    private static final String SUBSCRIBER_THREAD_PREFIX = "subscriber-";

    private int lastToken = 0;
    private int pingNumber = 1;
    private int pongNumber = -1;

    private boolean havePing = false;
    private boolean havePong = false;

    private int nodeId;
    private MessageSubscriber subscriber;
    private MessagePublisher publisher;
    private Thread csThread;

    public RingNode(String subscribedAddress, int publishingPort, int nodeId) {
        this.nodeId = nodeId;
        this.subscriber = new MessageSubscriber(subscribedAddress, MESSAGE_GROUP, this);
        this.publisher = new MessagePublisher(PUBLISHING_ADDRESS, publishingPort, MESSAGE_GROUP);
    }

    public void start() {
        LOG.info("Starting Ring Node...");
        Thread subscriberThread = new Thread(subscriber, SUBSCRIBER_THREAD_PREFIX + nodeId);
        subscriberThread.start();
        if (nodeId == 1) {
            initialProcessing();
        }
    }

    private void initialProcessing() {
        LOG.info("This is first node, assuming token possession and starting CS");
        this.havePing = true;
        this.havePong = true;
        new Worker(this).run();
    }

    public void receivePing(PingMessage msg) {
        LOG.info("Ping message received (" + msg.getValue() + ") last token: " + lastToken);
        this.havePing = true;
        this.pingNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            LOG.warn("Pong message loss detected");
            regenerate(pingNumber);
        }
        csThread = new Thread(new Worker(this));
        csThread.start();
    }

    public void receivePong(PongMessage msg) {
        LOG.info("Pong message received (" + msg.getValue() + ") last token: " + lastToken);
        this.havePong = true;
        pongNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            LOG.warn("Ping message loss detected");
            regenerate(pongNumber);
        }
        if (!this.havePing) {
            sendPong(this.pongNumber);
        }
    }

    public void leaveCS() {
        LOG.info("Leaving Critical Section");
        if (this.havePong) {
            incarnate(this.pingNumber);
            sendPing(this.pingNumber);
            sendPong(this.pongNumber);
        } else {
            sendPing(this.pingNumber);
        }
    }

    private void sendPing(int value) {
        PingMessage pingMessage = new PingMessage(value);
        this.havePing = false;
        this.lastToken = this.pingNumber;
        publisher.sendMessage(pingMessage);
    }

    private void sendPong(int value) {
        PongMessage pongMessage = new PongMessage(value);
        this.havePong = false;
        this.lastToken = this.pongNumber;
        publisher.sendMessage(pongMessage);
    }

    private void regenerate(int value) {
        LOG.info("Regenerating Ping and Pong messages");
        this.pingNumber = Math.abs(value);
        this.pongNumber = -this.pingNumber;
    }

    private void incarnate(int value) {
        LOG.info("Incarnating Ping and Pong messages");
        this.pingNumber = Math.abs(value) + 1;
        this.pongNumber = -this.pingNumber;
    }
}
