/**
 * The TLSServer class is a simple implementation of a server that uses 
 * the TLS protocol to provide secure communication over a network.
 */
package fr.ensea.rts.tls;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * A server class implementing TLS protocol for secure communication.
 */
public class TLSServer {
    private int port;
    private static final int DEFAULT_PORT = 8443;

    /**
     * Constructs a TLSServer with a specified port.
     *
     * @param port the port number on which the server will listen.
     */
    public TLSServer(int port) {
        this.port = port;
    }

    /**
     * Constructs a TLSServer with the default port (8443).
     */
    public TLSServer() {
        this.port = DEFAULT_PORT;
    }

    /**
     * Launches the server, initializing the TLS context and handling client connections.
     * It uses a JKS keystore for authentication.
     */
    public void launch() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (InputStream keyStoreStream = new FileInputStream("server.keystore")) {
                keyStore.load(keyStoreStream, "123456".toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(port)) {
                System.out.println("TLS Server started on port " + port);

                while (true) {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    System.out.println("Secure connection established with " + clientSocket.getInetAddress());

                    new Thread(() -> handleClient(clientSocket)).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles communication with a connected client socket.
     *
     * @param clientSocket the client socket to communicate with.
     */
    private void handleClient(SSLSocket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true)
        ) {
            String receivedLine;
            while ((receivedLine = in.readLine()) != null) {
                System.out.println("Received: " + receivedLine);
                out.println("Echo (TLS): " + receivedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point for the server. Accepts an optional port number as an argument.
     *
     * @param args command-line arguments where the first argument can specify the port number.
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

        TLSServer server = new TLSServer(port);
        server.launch();
    }
}
