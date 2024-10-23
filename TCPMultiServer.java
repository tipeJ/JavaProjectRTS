import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer {
    private int port;
    private static final int DEFAULT_PORT = 8080;

    public TCPMultiServer(int port) {
        this.port = port;
    }

    public TCPMultiServer() {
        this.port = DEFAULT_PORT;
    }

    public void launch() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Multi-threaded server started on port " + port);

            while (true) {
        
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

                
                ConnectionThread clientThread = new ConnectionThread(clientSocket);
                clientThread.start();            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

