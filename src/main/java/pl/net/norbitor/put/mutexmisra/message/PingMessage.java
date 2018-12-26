package pl.net.norbitor.put.mutexmisra.message;

public class PingMessage implements Message {
    private final int pingNumber;

    public PingMessage(int pingNumber) {
        this.pingNumber = pingNumber;
    }

    @Override
    public int getValue() {
        return pingNumber;
    }

    @Override
    public String toString() {
        return "PingMessage[" + pingNumber + ']';
    }
}
