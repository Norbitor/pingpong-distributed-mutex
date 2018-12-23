package pl.net.norbitor.put.mutexmisra.message;

import java.io.Serializable;

public class PingMessage implements Message {
    private final int pingNumber;

    public PingMessage(int pingNumber) {
        this.pingNumber = pingNumber;
    }

    public int getPingNumber() {
        return pingNumber;
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "pingNumber=" + pingNumber +
                '}';
    }
}
