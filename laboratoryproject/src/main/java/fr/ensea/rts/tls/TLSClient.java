/**
 * The TLSClient class demonstrates a simple client implementation using
 * the TLS protocol to connect to a secure server.
 */
package fr.ensea.rts.tls;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * A client class implementing TLS protocol for secure communication with a server.
 */
public class TLSClient {
    private static final String KEYSTORE_PASSWORD = "123456";
    private static final String ENCODING = "UTF-8";

    /**
     * The entry point for the client application.
     * Accepts a server URL and port number as arguments.
     *
     * @param args command-line arguments specifying the server URL and port number.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java TLSClient <URL> <PORT>");
            return;
        }

        String serverURL = args[0];
        int serverPort;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[1]);
            return;
        }

        try {
            SSLContext sslContext = initializeSSLContext("server.keystore");
            connectToServer(serverURL, serverPort, sslContext);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the SSLContext using the specified keystore.
     *
     * @param keystorePath the path to the keystore file.
     * @return an initialized SSLContext.
     * @throws Exception if an error occurs during initialization.
     */
    private static SSLContext initializeSSLContext(String keystorePath) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream trustStoreStream = new FileInputStream(keystorePath)) {
            trustStore.load(trustStoreStream, KEYSTORE_PASSWORD.toCharArray());
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Keystore file not found: " + keystorePath);
        } catch (IOException e) {
            throw new IOException("Error loading keystore: " + e.getMessage(), e);
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }

    /**
     * Establishes a connection to the server using the specified SSLContext.
     *
     * @param serverURL the server's URL or IP address.
     * @param serverPort the server's port number.
     * @param sslContext the SSLContext to use for the connection.
     */
    private static void connectToServer(String serverURL, int serverPort, SSLContext sslContext) {
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(serverURL, serverPort)) {
            System.out.println("Secure connection established with " + serverURL + ":" + serverPort);
            communicateWithServer(socket);
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Handles communication with the server via the provided socket.
     *
     * @param socket the SSL socket connected to the server.
     */
    private static void communicateWithServer(SSLSocket socket) {
        try (
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, ENCODING));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING))
        ) {
            System.out.println("Enter text lines to send to the server. Commands: #nickname, @user, #stat");
            System.out.println("Type 'exit' to close the connection.");

            String line;
            while ((line = userInput.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line)) {
                    System.out.println("Closing connection.");
                    break;
                }

                out.println(line);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                System.out.println("Received (TLS): " + response);
            }
        } catch (IOException e) {
            System.err.println("Error during communication: " + e.getMessage());
        }
    }
}
