/**
 * A UDP client that sends text messages to a UDP server.
 * The client reads input from the console, sends it to the server, and prints the server's response.
 */
package fr.ensea.rts;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    /**
     * Main method that runs the UDP client.
     * Accepts server URL and port as arguments, and sends user-inputted lines to the server.
     *
     * @param args Command-line arguments: server URL and port.
     */
    public static void main(String[] args) {
        String serverURL = "";
        int serverPort = -1;
        if (args.length != 2) {
            System.err.println("Usage: java UDPClient <URL> <PORT>");
            return;
        }

        try {
            serverURL = args[0];
            serverPort = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Invalid URL or PORT");
            return;
        }

        System.out.println("Enter text lines to send to the server. Enter an empty line to stop.");

        String line = null;
        while ((line = System.console().readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sendLine(serverURL, serverPort, line);
        }
    }

    /**
     * Sends a single line of text to the server using UDP.
     *
     * @param serverURL The server's URL.
     * @param serverPort The server's port.
     * @param line The text line to send to the server.
     */
    public static void sendLine(String serverURL, int serverPort, String line) {
        System.out.println("Sending line to server: " + line);
        DatagramSocket socket = null;
        try {
            // Encode the line in 'utf-8'
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(line);
            socket = new DatagramSocket();
            byte[] data = buffer.array();
            InetAddress address = InetAddress.getByName(serverURL);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, serverPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}