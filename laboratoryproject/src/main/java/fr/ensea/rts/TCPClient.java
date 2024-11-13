package fr.ensea.rts;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    public static void main(String[] args) {
        String serverURL = "";
        int serverPort = -1;

        if (args.length != 2) {
            System.err.println("Usage: java TCPClient <URL> <PORT>");
            return;
        }

        try {
            serverURL = args[0];
            serverPort = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Invalid URL or PORT");
            return;
        }

        System.out.println("Enter text lines to send to the server. Press <CTRL>+D to stop, or <CTRL>+C on Windows.");

        try (
            Socket socket = new Socket(serverURL, serverPort);
            // Input stream from user (console)
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            // Output stream to send data to the server
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String line;

            // Continuously read input from the user, until <CTRL>+D is pressed
            while ((line = userInput.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                outputStream.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // Read the response from the server (assume it's a hex string)
                String response = serverResponse.readLine();
                if (response != null) {
                    System.out.println("Received from server (hex): " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
