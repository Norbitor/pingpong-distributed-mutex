package pl.net.norbitor.put.mutexmisra.util;

public class NetworkUtil {
    public static String getZMQConnectionString(String address, int port) {
        return "tcp://" + address + ":" + port;
    }
}
