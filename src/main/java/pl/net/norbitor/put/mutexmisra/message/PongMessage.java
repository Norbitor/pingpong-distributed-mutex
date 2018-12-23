package pl.net.norbitor.put.mutexmisra.message;

public class PongMessage implements Message {
    private final int pongNumber;

    public PongMessage(int pongNumber) {
        this.pongNumber = pongNumber;
    }

    public int getPongNumber() {
        return pongNumber;
    }

    @Override
    public String toString() {
        return "PongMessage{" +
                "pongNumber=" + pongNumber +
                '}';
    }
}
