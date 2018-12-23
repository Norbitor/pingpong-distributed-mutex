package pl.net.norbitor.put.mutexmisra.util;

import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;

public class AppUtil {
    public static String getZMQConnectionString(String address, int port) {
        return "tcp://" + address + ":" + port;
    }

    public static String getZMQConnectionString(String address) {
        return "tcp://" + address;
    }

    public static PongMessage getPongFromPing(PingMessage message) {
        return new PongMessage(-message.getValue());
    }
}
