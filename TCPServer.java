import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private int port;
    private static final int DEFAULT_PORT = 8080;

    public TCPServer(int port) {
        this.port = port;
    }

    public TCPServer() {
        this.port = DEFAULT_PORT;
    }


    public void launch() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server started on port " + port);

        
            while (true) {
               
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

            
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);

                
                String receivedLine = in.readLine();
                if (receivedLine != null) {
                    System.out.println("Received: " + receivedLine);

                    
                    out.println("Echo: " + receivedLine);
                }
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "TCPServer listening on port " + port;
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
        TCPServer server = new TCPServer(port);
        System.out.println(server); // Calls toString() method
        server.launch();
    }
}
