package fr.ensea.rts;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import static org.junit.jupiter.api.Assertions.*;

public class UDPclientTest {

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
        System.setErr(originalErr);
    }

    @Test
    public void testMainWithInvalidArguments() {
        String[] args = {};
        UDPclient.main(args);
        assertEquals("Usage: java UDPclient <URL> <PORT>", errContent.toString().trim());
    }

    @Test
    public void testSendLine() throws Exception {
        // Create mock server URL and port
        String serverURL = "localhost";
        int serverPort = 1234;
        
        // Simulate a server to receive the packet
        DatagramSocket serverSocket = new DatagramSocket(serverPort);

        // Test sendLine command
        UDPclient.sendLine(serverURL, serverPort, "Hello, World!");

        // Receive the packet
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        String receivedLine = new String(receivePacket.getData(), 0, receivePacket.getLength());
        serverSocket.close();
        // Need to format the string to match the expected output, unpacking the byte array creates a string with a null character at the end
        receivedLine = receivedLine.substring(0, receivedLine.length() - 1);
        assertEquals("Hello, World!", receivedLine);

    }
}