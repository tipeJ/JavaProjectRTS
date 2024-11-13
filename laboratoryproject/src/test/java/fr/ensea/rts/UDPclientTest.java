package fr.ensea.rts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertEquals("Usage: java UDPclient <URL> <PORT>\n", errContent.toString());
    }

    @Test
    public void testSendLine() throws Exception {
        DatagramSocket mockSocket = Mockito.mock(DatagramSocket.class);
        DatagramSocket originalSocket = UDPclient.socket;
        UDPclient.socket = mockSocket;

        String serverURL = "localhost";
        int serverPort = 12345;
        String line = "test message";

        UDPclient.sendLine(serverURL, serverPort, line);

        ByteBuffer buffer = StandardCharsets.UTF_8.encode(line);
        byte[] data = buffer.array();
        InetAddress address = InetAddress.getByName(serverURL);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, serverPort);

        verify(mockSocket, times(1)).send(packet);

        UDPclient.socket = originalSocket;
    }
}