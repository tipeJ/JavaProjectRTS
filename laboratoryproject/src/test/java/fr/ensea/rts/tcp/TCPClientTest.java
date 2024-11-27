package fr.ensea.rts.tcp;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TCPClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ServerSocket serverSocket;

    @BeforeEach
    public void setUpStreams() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        serverSocket = new ServerSocket(0); // Bind to any available port
    }

    @AfterEach
    public void restoreStreams() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
        serverSocket.close();
    }

    @Test
    public void testInvalidArguments() {
        TCPClient.main(new String[]{});
        assertTrue(errContent.toString().contains("Usage: java TCPClient <URL> <PORT>"));
    }

    @Test
    public void testInvalidPort() {
        TCPClient.main(new String[]{"localhost", "invalidPort"});
        assertTrue(errContent.toString().contains("Invalid URL or PORT"));
    }

    @Test
    public void testClientServerCommunication() throws Exception {
        // Start a simple echo server in a separate thread
        new Thread(() -> {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintStream out = new PrintStream(clientSocket.getOutputStream())) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    out.println(inputLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Simulate user input
        String simulatedUserInput = "Hello, Server!\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // Run the client
        TCPClient.main(new String[]{"localhost", String.valueOf(serverSocket.getLocalPort())});

        System.out.println(outContent.toString());

        // Check the output
        assertTrue(outContent.toString().contains("Enter text"));
    }
}