package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            printHelp();
            return;
        }

        String previousNode = args[0];
        String listenPort = args[1];
        String nodeId = args[2];

        RingNode node = new RingNode(previousNode, Integer.parseInt(listenPort), Integer.parseInt(nodeId));
        node.start();
    }

    private static void printHelp() {
        System.out.println("Usage: mutexmisra previous_node publish_port node_id\n" +
                           "    previous_node - IP and port of previous node\n" +
                           "    publish_port  - a port where app will publish messages\n" +
                           "    node_id       - ID of this node\n" +
                           "      Notice: The node with ID=1 have to be run last!\n\n" +
                           "EXAMPLE: mutexmisra 192.168.1.10:5555 5555 1");
    }
}
