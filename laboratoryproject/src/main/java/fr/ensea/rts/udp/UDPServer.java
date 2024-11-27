/**
 * A UDP server that listens for incoming UDP packets and prints the received messages.
 * It listens on a specified port and prints the received data along with the client's address.
 */
package fr.ensea.rts.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    private int port;
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_BUFFER_SIZE = 1024;

    /**
     * Constructs a UDP server with the specified port.
     *
     * @param port The port on which the server will listen.
     */
    public UDPServer(int port) {
        this.port = port;
    }

    /**
     * Constructs a UDP server with the default port (8080).
     */
    public UDPServer() {
        this.port = DEFAULT_PORT;
    }

    /**
     * Starts the UDP server, receiving packets from clients and printing the received data.
     */
    public void launch() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Server started on port " + port);
            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String received = new String(packet.getData(), 0, packet.getLength(), "UTF-8");

                System.out.println("Received packets from " + clientAddress + ":" + clientPort + " - " + received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string representation of the server.
     *
     * @return A string indicating the server is listening on a specific port.
     */
    @Override
    public String toString() {
        return "Server listening on port " + port;
    }

    /**
     * Main method that starts the UDP server.
     *
     * @param args Command-line arguments: optional port number.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 1) {
            System.err.println("Usage: java UDPServer <PORT>");
            return;
        } else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.err.println("Invalid port");
                return;
            }
        }
        UDPServer server = new UDPServer(port);
        System.out.println(server);
        server.launch();
    }
}
