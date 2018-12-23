package pl.net.norbitor.put.mutexmisra.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import pl.net.norbitor.put.mutexmisra.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MessagePublisher implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    private final ZMQ.Context context;
    private final ZMQ.Socket publisher;
    private final String messageGroup;

    public MessagePublisher(String address, int port, String messageGroup) {
        logger.info("Starting Publisher for " + address + ":" + port + ", publishing for group: " + messageGroup);

        context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind(getAddrString(address, port));
        this.messageGroup = messageGroup;

        logger.info("Publisher started.");
    }

    public void sendMessage(Message message) {
        logger.info("Publishing message: " + message);
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.close();
            publisher.sendMore(messageGroup);
            publisher.send(byteStream.toByteArray());
            byteStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        logger.info("Closing Publisher");
        publisher.close();
        context.term();
    }

    private String getAddrString(String address, int port) {
        return "tcp://" + address + ":" + port;
    }
}
