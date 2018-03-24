package Packet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PacketErrorTest {

    @Test
    public void testErrorAck() throws Exception {

        byte[] bytes = {0, 4, 32, 8, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Extra bytes are not being thrown");
    }

    @Test
    public void testErrorData() throws Exception {

        byte[] bytes = {0, 3, 32, 8,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, // 1
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, // 10
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, // 20
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, // 25
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, // more than 256 bytes.
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Too many bytes does not cause error.");
    }

    @Test
    public void testBytesAfterStringError() throws Exception {

        byte[] bytes = {0, 5, 0, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0, 1};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Bytes after zero byte do not cause error.");
    }

    @Test
    public void testInvalidFormatRead() throws Exception {

        byte[] bytes = {0, 1, 78, 97, 109, 101, 0, 78, 69, 84, 64, 83, 67, 73, 73, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Invalid format accepted.");
    }

    @Test
    public void testExtraZeroRead() throws Exception {

        byte[] bytes = {0, 1, 78, 97, 109, 101, 0, 78, 69, 84, 64, 83, 67, 73, 73, 0, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Extra Zero accepted.");
    }

    @Test
    public void testInvalidFormatWrite() throws Exception {

        byte[] bytes = {0, 2, 78, 97, 109, 101, 0, 78, 69, 84, 64, 83, 67, 73, 73, 0};

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), 25556);

        Executable closureContainingCodeToTest = () -> Packet.parse(datagramPacket);

        assertThrows(IllegalArgumentException.class, closureContainingCodeToTest, "Invalid format accepted.");
    }
}
