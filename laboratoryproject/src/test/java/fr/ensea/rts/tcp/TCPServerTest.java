package fr.ensea.rts.tcp;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TCPServerTest {
    private TCPServer server;
    private Thread serverThread;

    @AfterEach
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop(); // Signal the server to stop
        }
        if (serverThread != null) {
            serverThread.join(); // Wait for the thread to terminate
        }
    }

    @BeforeEach
    public void setUp() {
        server = null;
        serverThread = null;
    }

    @Test
    public void testServerIsCreatedOnDefaultPort() {
        server = new TCPServer();
        assertEquals("TCPServer idle on port 8080", server.toString());
    }

    @Test
    public void testServerQuitsOnInvalidPort() {
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> new TCPServer(-1));
        assertEquals("Invalid port number: -1", exception.getMessage());
    }

    @Test
    public void testServerStartsOnSpecifiedPort() {
        server = new TCPServer(9095);
        assertEquals("TCPServer idle on port 9095", server.toString());
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
            System.out.println("Response: " + response);
        }
    }

}
