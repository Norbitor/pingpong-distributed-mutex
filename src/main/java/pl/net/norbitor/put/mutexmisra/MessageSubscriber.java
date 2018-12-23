package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import pl.net.norbitor.put.mutexmisra.message.Message;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageSubscriber implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(MessageSubscriber.class);

    @Override
    public void run() {
        logger.info("Subscriber test class starting");

        int msgcnt = 0;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

        subscriber.connect("tcp://localhost:5555");
        subscriber.subscribe("A".getBytes());
        logger.info("Subscriber listens to localhost:5555 for group A");
        while (msgcnt < 20) {
            // Read envelope with address
            String address = subscriber.recvStr ();
            // Read message contents
            byte[] contents = subscriber.recv();

            try {
                Message m;
                ByteArrayInputStream bais = new ByteArrayInputStream(contents);
                ObjectInputStream ois = new ObjectInputStream(bais);
                m = (Message) ois.readObject();
                ois.close();
                bais.close();
                logger.info("Received: " + address + " : " + m);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            msgcnt++;
        }
        logger.info("Closing Subscriber");
        subscriber.close();
        context.term ();
    }
}
