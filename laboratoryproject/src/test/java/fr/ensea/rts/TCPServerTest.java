package fr.ensea.rts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TCPServerTest {
    private TCPServer server;
    private Thread serverThread;

    @AfterEach
    public void tearDown() throws Exception {
        serverThread.interrupt();
        serverThread.join();
    }

    @Test
    public void testServerStartsOnDefaultPort() {
        server = new TCPServer();
        serverThread = new Thread(() -> server.launch());
        serverThread.start();
        assertEquals("TCPServer listening on port 8080", server.toString());
        serverThread.interrupt();
    }

    @Test
    public void testServerStartsOnSpecifiedPort() {
        server = new TCPServer(9095);
        serverThread = new Thread(() -> server.launch());
        serverThread.start();
        assertEquals("TCPServer listening on port 9095", server.toString());
        serverThread.interrupt();
    }

    @Test
    public void testServerEchoesMessage() throws Exception {
        server = new TCPServer();
        serverThread = new Thread(() -> server.launch());
        serverThread.start();
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            String message = "Hello, Server!";
            out.println(message);
            String response = in.readLine();
            assertEquals("Echo: " + message, response);
        }
    }

    @Test
    public void testServerHandlesMultipleConnections() throws Exception {
        server = new TCPServer();
        serverThread = new Thread(() -> server.launch());
        serverThread.start();
        try (Socket socket1 = new Socket("localhost", 8080);
             PrintWriter out1 = new PrintWriter(new OutputStreamWriter(socket1.getOutputStream(), "UTF-8"), true);
             BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream(), "UTF-8"));
             Socket socket2 = new Socket("localhost", 8080);
             PrintWriter out2 = new PrintWriter(new OutputStreamWriter(socket2.getOutputStream(), "UTF-8"), true);
             BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream(), "UTF-8"))) {

            String message1 = "Hello, Server 1!";
            out1.println(message1);
            String response1 = in1.readLine();
            assertEquals("Echo: " + message1, response1);

            String message2 = "Hello, Server 2!";
            out2.println(message2);
            String response2 = in2.readLine();
            assertEquals("Echo: " + message2, response2);
        }
    }
}