package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;
import pl.net.norbitor.put.mutexmisra.network.MessageSubscriber;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Yet another POC");
        Thread pubthr = new Thread(() -> {
            try (MessagePublisher pub = new MessagePublisher("*", 5555, "A")) {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(1000);
                    if (i % 2 == 0) {
                        pub.sendMessage(new PingMessage((i+1)/2));
                    } else {
                        pub.sendMessage(new PongMessage((i)/2));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread subthr = new Thread(new MessageSubscriber("localhost", 5555, "A"));

        subthr.start();
        pubthr.start();
    }
}
