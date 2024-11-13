package fr.ensea.rts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
             Socket socket2 = new Socket("localhost", 8080)) {
            assertTrue(socket1.isConnected(), "Client 1 should be able to connect to the server");
            assertTrue(socket2.isConnected(), "Client 2 should be able to connect to the server");
        } catch (IOException e) {
            fail("Clients should be able to connect to the server");
        }
    }
}