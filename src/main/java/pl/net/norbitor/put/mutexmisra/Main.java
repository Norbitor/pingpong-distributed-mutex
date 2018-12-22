package pl.net.norbitor.put.mutexmisra;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, ZMQ");

        Thread serverThread = new Thread(new ZServer());
        Thread clientThread = new Thread(new ZClient());

        serverThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientThread.start();
    }
}
