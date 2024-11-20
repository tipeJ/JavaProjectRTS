package fr.ensea.rts;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class UDPServerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testServerDefaultPort() {
        UDPServer server = new UDPServer();
        assertEquals("Server listening on port 8080", server.toString());
    }

    @Test
    public void testMainWithInvalidPort() {
        UDPServer.main(new String[]{"invalidPort"});
        assertEquals("Invalid port", errContent.toString().trim());
    }

    @Test
    public void testLaunch() throws Exception {
        int port = 9999;
        UDPServer server = new UDPServer(port);

        Thread serverThread = new Thread(() -> server.launch());
        serverThread.start();

        // Give the server some time to start
        Thread.sleep(1000);

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");
        byte[] sendData = "Hello, Server".getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        clientSocket.send(sendPacket);

        // Give the server some time to receive the packet
        Thread.sleep(1000);

        String output = outContent.toString();
        assertTrue(output.contains("Server started on port " + port));
        assertTrue(output.contains("Received packets from"));

        clientSocket.close();
        serverThread.interrupt();
    }
}
