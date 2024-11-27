/**
 * A multi-threaded TCP server that accepts multiple client connections concurrently.
 * Each client is handled by a new thread.
 */
package fr.ensea.rts.tcp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer {
    private int port;
    private static final int DEFAULT_PORT = 8080;

    /**
     * Constructs a multi-threaded TCP server with the specified port.
     *
     * @param port The port on which the server will listen.
     */
    public TCPMultiServer(int port) {
        this.port = port;
    }

    /**
     * Constructs a multi-threaded TCP server with the default port (8080).
     */
    public TCPMultiServer() {
        this.port = DEFAULT_PORT;
    }

    /**
     * Starts the server and accepts client connections. Each connection is handled in a new thread.
     */
    public void launch() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Multi-threaded server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

                // Handle the client in a new thread
                ConnectionThread clientThread = new ConnectionThread(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to start the multi-threaded TCP server.
     *
     * @param args Command-line arguments: optional port number.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }

        TCPMultiServer server = new TCPMultiServer(port);
        server.launch();
    }
}
