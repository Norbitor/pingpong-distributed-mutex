package pl.net.norbitor.put.mutexmisra;

import org.apache.commons.lang3.ThreadUtils;
import pl.net.norbitor.put.mutexmisra.message.Message;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;
import pl.net.norbitor.put.mutexmisra.util.AppUtil;

public class RingNode {

    private int lastToken = 0;
    private int nodeId;
    private int nodeNumber;
    private Thread csWorkerThread;
    private MessageSubscriber subscriber;

    public RingNode() {
        this.nodeId = 0;
        this.nodeNumber = 1;
        this.csWorkerThread = new Thread(new Worker(this));
        this.subscriber = new MessageSubscriber("localhost:5555", "A", this);
    }

    public void start() {
        Thread subscriberThread = new Thread(subscriber, "subscriber");
        subscriberThread.start();
        if (nodeId == 0) {
            csWorkerThread.start();
        }
        // configure listening for tokens
    }

    public void receivePing(PingMessage msg) {
        csWorkerThread.start();
        if (msg.getValue() == lastToken) {
            regenerate();
        }
    }

    public void receivePong(PongMessage msg) {
        if (msg.getValue() == lastToken) {
            regenerate();
        }
    }

    public void leaveCS() {
        incarnate();
    }

    private void regenerate() {
        PingMessage pingMessage = new PingMessage(Math.abs(lastToken));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        publishMessage(pingMessage);
        publishMessage(pongMessage);
    }

    private void incarnate() {
        PingMessage pingMessage = new PingMessage(Math.abs((lastToken + 1) % nodeNumber));
        PongMessage pongMessage = AppUtil.getPongFromPing(pingMessage);
        publishMessage(pingMessage);
        publishMessage(pongMessage);
        // send ping
        // send pong
    }

    private void publishMessage(Message message) {
        try (MessagePublisher publisher = new MessagePublisher("*", 5555, "A")) {
            publisher.sendMessage(message);
        }
    }
}
