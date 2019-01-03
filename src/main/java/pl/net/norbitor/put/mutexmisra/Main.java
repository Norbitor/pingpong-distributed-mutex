package pl.net.norbitor.put.mutexmisra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.norbitor.put.mutexmisra.exception.ValidationException;
import pl.net.norbitor.put.mutexmisra.util.AppUtil;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 4) {
            printHelp();
            return;
        }

        String previousNode;
        int listenPort;
        int nodeId;
        int totalNodes;

        try {
            previousNode = AppUtil.validateStringWithAddressAndPort(args[0]);
            listenPort = AppUtil.validateStringWithPort(args[1]);
            nodeId = AppUtil.validateStringWithNodeId(args[2]);
            totalNodes = AppUtil.validateStringWithNodeId(args[3]);
        } catch (ValidationException e) {
            System.out.println("An error occurred while validating parameters: " + e.getMessage() +
                    "\nPlease refer to the usage instructions below:");
            printHelp();
            return;
        }

        LOG.info("Starting ring node with ID=" + nodeId);
        RingNode node = new RingNode(previousNode, listenPort, nodeId, totalNodes);
        node.start();
    }

    private static void printHelp() {
        System.out.println("Usage: mutexmisra previous_node publish_port node_id total_nodes\n" +
                "    previous_node - IP and port of previous node\n" +
                "    publish_port  - a port where app will publish messages\n" +
                "    node_id       - numeric ID of this node\n" +
                "      Notice: The node with ID=1 is primary and have to be run as last!\n" +
                "    total_nodes   - total amount of nodes in the ring\n\n" +
                "EXAMPLE: mutexmisra 192.168.1.10:5555 5555 1");
    }
}
