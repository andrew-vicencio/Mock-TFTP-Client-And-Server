package Packet;

import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PacketTest {

    @Test
    public void testAck() throws Exception {

        byte[] bytes = {0, 4, 32, 8};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        AcknowledgementPacket ack = (AcknowledgementPacket) packet;

        assertEquals((long) 8200, ack.getBlockNumber(), "Conversion of bytes to number incorrect");

        assertEquals(Arrays.toString(ack.toByteArray()), Arrays.toString(bytes), "Packet data is not preserved");
    }

    @Test
    public void testData() throws Exception {

        byte[] bytes = {0, 3, 32, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        DataPacket dataPacket = (DataPacket) packet;

        assertEquals(
                Arrays.toString(dataPacket.getData()),
                Arrays.toString(new Byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}),
                "Data is not preserved");

        assertEquals((long) 8200, dataPacket.getBlockNumber(), "Conversion of bytes to number incorrect");

        assertEquals(Arrays.toString(dataPacket.toByteArray()), Arrays.toString(bytes), "Packet data is not preserved");

        assertEquals(Arrays.toString(Packet.parse(packet.toDataGramPacket()).toByteArray()), Arrays.toString(bytes), "A full cycle in the other direction is not consistent");
    }

    @Test
    public void testError() throws Exception {

        byte[] bytes = {0, 5, 0, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        ErrorPacket errorPacket = (ErrorPacket) packet;

        assertEquals(Arrays.toString(errorPacket.toByteArray()), Arrays.toString(bytes), "Packet data is not preserved");
    }

    @Test
    public void testRead() throws Exception {

        byte[] bytes = {0, 1, 78, 97, 109, 101, 0, 77, 111, 100, 101, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        ReadPacket readPacket = (ReadPacket) packet;

        assertEquals(readPacket.getFileName(), "Name", "File name");
        assertEquals(readPacket.getFileMode(), "Mode", "File mode");

        assertEquals(Arrays.toString(readPacket.toByteArray()), Arrays.toString(bytes), "Packet data is not preserved");
    }

    @Test
    public void testWrite() throws Exception {

        byte[] bytes = {0, 2, 78, 97, 109, 101, 0, 77, 111, 100, 101, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        WritePacket writePacket = (WritePacket) packet;

        assertEquals(writePacket.getFileName(), "Name", "File name");
        assertEquals(writePacket.getFileMode(), "Mode", "File mode");

        assertEquals(Arrays.toString(writePacket.toByteArray()), Arrays.toString(bytes), "Packet data is not preserved");
    }

    @Test
    public void testTrimming() throws Exception {

        byte[] bytes = {0, 4, 0, 8, 0, 0, 0, 0, 0};

        byte[] actual = {0, 4, 0, 8};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, 4, InetAddress.getLocalHost(), 25556);

        Packet packet = Packet.parse(datagramPacket);

        AcknowledgementPacket ack = (AcknowledgementPacket) packet;

        assertEquals((long) 8, ack.getBlockNumber(), "Conversion of bytes to number incorrect");

        assertEquals(Arrays.toString(ack.toByteArray()), Arrays.toString(actual), "Packet data is not preserved");
    }
}
