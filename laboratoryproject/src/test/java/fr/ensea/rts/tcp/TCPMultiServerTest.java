package fr.ensea.rts.tcp;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.*;

class TCPMultiServerTest {
    private TCPMultiServer server;
    private Thread serverThread;

    @BeforeEach
    void setUp() {
        server = new TCPMultiServer(8080);
        serverThread = new Thread(() -> server.launch());
        serverThread.start();
    }

    @AfterEach
    void tearDown() {
        serverThread.interrupt();
    }

    @Test
    void testServerStartsOnDefaultPort() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            fail("Port 8080 should be in use by the server");
        } catch (IOException e) {
            assertTrue(true, "Port 8080 is in use as expected");
        }
    }

    @Test
    void testServerAcceptsConnection() {
        try (Socket socket = new Socket("localhost", 8080)) {
            assertTrue(socket.isConnected(), "Client should be able to connect to the server");
        } catch (IOException e) {
            fail("Client should be able to connect to the server");
        }
    }

    @Test
    void testServerAcceptsMultipleConnections() {

        try (Socket socket1 = new Socket("localhost", 8080);
             PrintWriter out1 = new PrintWriter(new OutputStreamWriter(socket1.getOutputStream(), "UTF-8"), true);
             BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream(), "UTF-8"));
             Socket socket2 = new Socket("localhost", 8080);
             PrintWriter out2 = new PrintWriter(new OutputStreamWriter(socket2.getOutputStream(), "UTF-8"), true);
             BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream(), "UTF-8"))) {
            assertTrue(socket1.isConnected(), "Client 1 should be able to connect to the server");
            assertTrue(socket2.isConnected(), "Client 2 should be able to connect to the server");

            String message1 = "Hello, Server 1!";
            out1.println(message1);
            String response1 = in1.readLine();
            assertEquals("/127.0.0.1: " + message1, response1);

            String message2 = "Hello, Server 2!";
            out2.println(message2);
            String response2 = in2.readLine();
            assertEquals("/127.0.0.1: " + message2, response2);
        } catch (IOException e) {
            fail("Clients should be able to connect to the server");
        }
    }
}