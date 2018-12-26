package pl.net.norbitor.put.mutexmisra.network;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import pl.net.norbitor.put.mutexmisra.RingNode;
import pl.net.norbitor.put.mutexmisra.message.Message;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.util.AppUtil;

public class MessageSubscriber implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(MessageSubscriber.class);

    private final String connectionString;
    private final String messageGroup;
    private final RingNode nodeRef;

    public MessageSubscriber(String address, int port, String messageGroup, RingNode nodeRef) {
        this.connectionString = AppUtil.getZMQConnectionString(address, port);
        this.messageGroup = messageGroup;
        this.nodeRef = nodeRef;
    }

    public MessageSubscriber(String address, String messageGroup, RingNode nodeRef) {
        this.connectionString = AppUtil.getZMQConnectionString(address);
        this.messageGroup = messageGroup;
        this.nodeRef = nodeRef;
    }

    @Override
    public void run() {
        logger.info("Subscriber test class starting");

        int msgcnt = 0;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

        subscriber.connect(connectionString);
        subscriber.subscribe(messageGroup);
        logger.info("Subscriber listens to " + connectionString + " for group " + messageGroup);
        while (msgcnt < 20) {
            // Read envelope with address
            String address = subscriber.recvStr ();
            // Read message contents
            byte[] contents = subscriber.recv();
            Message message = SerializationUtils.deserialize(contents);
            logger.info("Received: " + address + " : " + message);
            if (message.getClass() == PingMessage.class) {
                nodeRef.receivePing((PingMessage)message);
            } else if (message.getClass() == PongMessage.class) {
                nodeRef.receivePong((PongMessage) message);
            } else {
                logger.warn("Unknown message received");
            }
            msgcnt++;
        }
        logger.info("Closing Subscriber");
        subscriber.close();
        context.term ();
    }
}
