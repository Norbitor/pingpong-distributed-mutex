package pl.net.norbitor.put.mutexmisra.util;

import pl.net.norbitor.put.mutexmisra.exception.ValidationException;
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

    public static String validateStringWithAddressAndPort(final String addressAndPort) throws ValidationException {
        String[] dividedAddressAndPort = addressAndPort.split(":");
        if (dividedAddressAndPort.length != 2) {
            throw new ValidationException("The string is not in host:port format.");
        }
        validateStringWithPort(dividedAddressAndPort[1]);
        return addressAndPort;
    }

    public static int validateStringWithPort(String port) throws ValidationException {
        int portNumber;
        try {
            portNumber = Integer.parseUnsignedInt(port);
            if (portNumber < 1025 || portNumber > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("The port number is not valid integer from range 1025-65535", e);
        }
        return portNumber;
    }

    public static int validateStringWithNodeId(String nodeIdText) throws ValidationException {
        int nodeId;
        try {
            nodeId = Integer.parseUnsignedInt(nodeIdText);
            if (nodeId == 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("The node ID is not valid positive integer", e);
        }
        return 0;
    }
}
