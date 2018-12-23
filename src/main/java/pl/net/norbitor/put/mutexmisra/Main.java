package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.network.MessagePublisher;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Yet another POC");
        Thread pubthr = new Thread(() -> {
            try (MessagePublisher pub = new MessagePublisher("*", 5555, "A")) {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    pub.sendMessage("Hello " + i + " time(s).");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread subthr = new Thread(new MessageSubscriber());

        subthr.start();
        pubthr.start();
    }
}
