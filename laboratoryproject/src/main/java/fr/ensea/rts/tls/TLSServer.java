
package fr.ensea.rts.tls;


import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class TLSServer {
    private int port;
    private static final int DEFAULT_PORT = 8443; // Common HTTPS/TLS port

    public TLSServer(int port) {
        this.port = port;
    }

    public TLSServer() {
        this.port = DEFAULT_PORT;
    }

    public void launch() {
        try {
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (InputStream keyStoreStream = new FileInputStream("server.keystore")) {
                keyStore.load(keyStoreStream, "123456".toCharArray());
            }

            // Initialize KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSLServerSocket
            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(port)) {
                System.out.println("TLS Server started on port " + port);

                while (true) {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    System.out.println("Secure connection established with " + clientSocket.getInetAddress());

                    // Handle client in a new thread
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
