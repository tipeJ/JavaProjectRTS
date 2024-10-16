package fr.ensea.rts;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;

public class httpClient {

    private static URL url;
    private static String protocol;
    private static String host;
    private static String filename;
    private static String path;
    private static int port;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java httpClient <url>");
            return;
        }
        String in = args[0];
        readURL(in);
        getURL();
    }

    public static void getURL() {
        SSLSocket socket = null;
        try {
            // ! SSL used as the website rejects HTTP requests as in the lab assignment
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(host, port);

            if (socket.isConnected()) {
                System.out.println("Connected to " + host + " on port " + port);
                InputStream from = socket.getInputStream();
                PrintWriter to = new PrintWriter(socket.getOutputStream());

                String command = "GET " + filename + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n";
                to.println(command);
                to.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(from));
                // Discard the headers (until the first empty line)
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.isEmpty()) {
                        break;
                    }
                }

                // The filename needs to be parsed, as it contains the path as well
                String filename = path.substring(path.lastIndexOf('/') + 1);
                filename = System.getProperty("user.dir") + "/lab1/" + filename;

                OutputStream out = new FileOutputStream(filename);
                char[] buf = new char[4096];
                int bytesRead;
                while ((bytesRead = reader.read(buf)) != -1) {
                    out.write(new String(buf, 0, bytesRead).getBytes());
                }

                out.close();
                from.close();
                System.out.println("Disconnected from " + host + " on port " + port);
            }
        } catch (Exception e) {
            System.out.println("Error creating SSLSocket: " + e);
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e);
            }
        }
    }

    public static void readURL(String in) {
        try {
            url = new URL(in);
            port = url.getPort();
            if (port == -1) {
                // ! Default port for HTTPS is 443, not 80 as in HTTP for the original assignment
                port = 443;
            }
            path = url.getPath();
            host = url.getHost();
            filename = url.getFile();
            if (filename.isEmpty()) {
                filename = "/rfc.txt";
            }
            protocol = url.getProtocol();
        } catch (Exception e) {
            System.out.println("Error parsing URL: " + e);
        }
    }
}
