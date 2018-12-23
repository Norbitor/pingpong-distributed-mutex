package pl.net.norbitor.put.mutexmisra;

import pl.net.norbitor.put.mutexmisra.message.PingMessage;
import pl.net.norbitor.put.mutexmisra.message.PongMessage;

public class RingNode {
    private static final int NODE_NUM = 2;

    private int lastToken = 0;

    public void receivePing(PingMessage msg) {
        if (msg.getValue() == lastToken) {
            regenerate();
        }
        // go to CS
    }

    public void receivePong(PongMessage msg) {
        if (msg.getValue() == lastToken) {
            regenerate();
        }
    }

    public void leaveCS() {
        incarnate();
    }

    private void regenerate() {
        int pingValue = Math.abs(lastToken);
        int pongValue = -pingValue;
        // send ping
        // send pong
    }

    private void incarnate() {
        int pingValue = Math.abs(lastToken) + 1;
        int pongValue = -pingValue;
        // send ping
        // send pong
    }
}
