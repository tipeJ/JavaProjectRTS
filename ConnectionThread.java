import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private Socket clientSocket;

    public ConnectionThread(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Input and output streams to communicate with the client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);

            String clientAddress = clientSocket.getInetAddress().toString();
            String receivedLine;

            // Read lines from the client, echo them back
            while ((receivedLine = in.readLine()) != null) {
                String response = clientAddress + ": " + receivedLine;
                System.out.println(response); // Display on server console
                out.println(response);        // Echo it back to the client
            }

            // Close the client socket when done
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
