package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;

public class RingNode {

    private final Logger logger = LoggerFactory.getLogger(RingNode.class);

    private int lastToken = 0;
    private int pingNumber = 1;
    private int pongNumber = -1;

    private boolean havePing = false;
    private boolean havePong = false;

    private int nodeId;
    private MessageSubscriber subscriber;
    private MessagePublisher publisher;
    private Thread csThread;

    public RingNode(String subaddr, int pubport, int nodeId) {
        this.nodeId = nodeId;
        this.subscriber = new MessageSubscriber(subaddr, "A", this);
        this.publisher = new MessagePublisher("*", pubport, "A");
    }

    public void start() {
        logger.info("Starting Ring Node...");
        Thread subscriberThread = new Thread(subscriber, "subscriber-"+nodeId);
        subscriberThread.start();
        if (nodeId == 1) {
            initialProcessing();
        }
    }

    private void initialProcessing() {
        logger.info("This is first node, assuming token possession and starting CS");
        this.havePing = true;
        this.havePong = true;
        new Worker(this).run();
    }

    public void receivePing(PingMessage msg) {
        logger.info("Ping message received (" + msg.getValue() + ") last token: " + lastToken);
        this.havePing = true;
        this.pingNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Pong message loss detected");
            regenerate(pingNumber);
        }
        csThread = new Thread(new Worker(this));
        csThread.start();
    }

    public void receivePong(PongMessage msg) {
        logger.info("Pong message received (" + msg.getValue() + ") last token: " + lastToken);
        this.havePong = true;
        pongNumber = msg.getValue();
        if (msg.getValue() == lastToken) {
            logger.warn("Ping message loss detected");
            regenerate(pongNumber);
        }
        if (!this.havePing) {
            sendPong(this.pongNumber);
        }
    }

    public void leaveCS() {
        logger.info("Leaving Critical Section");
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
