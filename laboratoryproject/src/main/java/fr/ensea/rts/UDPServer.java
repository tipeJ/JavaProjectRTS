package fr.ensea.rts;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer{

    private int port;
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_BUFFER_SIZE = 1024;

    public UDPServer(int port) {
        this.port = port;
    }

    public UDPServer() {
        this.port = DEFAULT_PORT;
    }

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


                System.out.println("Received packets from" + clientAddress + ":" + clientPort + " - " + received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Server listening on port " + port;
    }
     public static void main(String[] args) {
        int port = DEFAULT_PORT;
        UDPServer server = new UDPServer(port);
        System.out.println(server);
        server.launch();
    }

}

