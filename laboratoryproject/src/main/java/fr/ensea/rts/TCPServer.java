package fr.ensea.rts;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServer {
    private final int port;
    private static final int DEFAULT_PORT = 8080;
    private AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    public TCPServer(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }
        this.port = port;
    }

    public TCPServer() {
        this(DEFAULT_PORT);
    }

    public void launch() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            running.set(true);
            System.out.println("TCP Server started on port " + port);

            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

                // Handle the client
                handleClient(clientSocket);

            } catch (IOException e) {
                if (!running.get()) {
                    System.out.println("Server stopped.");
                } else {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            if (!running.get()) {
                System.out.println("Server socket closed.");
            } else {
                e.printStackTrace();
            }
        } finally {
            running.set(false);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true)) {

            String receivedLine;
            while ((receivedLine = in.readLine()) != null) {
                System.out.println("Received: " + receivedLine);
                out.println("Echo: " + receivedLine);
            }

            System.out.println("Client " + clientSocket.getInetAddress() + " disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (running.get()) {
            return "TCPServer listening on port " + port;
        } else {
            return "TCPServer idle on port " + port;
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
        TCPServer server = new TCPServer(port);
        System.out.println(server);
        server.launch();
    }
}
