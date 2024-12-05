package fr.ensea.rts.tls;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class TLSClient {
    private static final String KEYSTORE_PASSWORD = "123456"; // Use constants for reusable values
    private static final String ENCODING = "UTF-8";

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
    private static void connectToServer(String serverURL, int serverPort, SSLContext sslContext) {
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(serverURL, serverPort)) {
            System.out.println("Secure connection established with " + serverURL + ":" + serverPort);
            communicateWithServer(socket);
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

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

