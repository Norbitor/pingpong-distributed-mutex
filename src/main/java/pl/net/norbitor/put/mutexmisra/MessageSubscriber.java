package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

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
        while (msgcnt < 10) {
            // Read envelope with address
            String address = subscriber.recvStr ();
            // Read message contents
            String contents = subscriber.recvStr ();
            logger.info("Received: " + address + " : " + contents);
            msgcnt++;
        }
        logger.info("Closing Subscriber");
        subscriber.close();
        context.term ();
    }
}
