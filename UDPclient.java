import java.io.Console;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UDPclient {
    public static void main(String[] args) {
        String serverURL = "";
        int serverPort = -1;
        if (args.length != 2) {
            System.err.println("Usage: java UDPclient <URL> <PORT>");
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
