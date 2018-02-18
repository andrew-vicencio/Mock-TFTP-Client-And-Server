package tools;

import Packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketConstructor {
    private static String mode = "netascii";
    private static final byte writeHeader[] = {0, 2};
    private static final byte readHeader[] = {0, 1};

    /*
     * write - boolean to see if read or write
     * write = true if write
     * write = false if read
     */

    /**
     * @param write
     * @param filename
     * @return
     * @throws IOException
     */
    private static byte[] getHeader(boolean write, String filename) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (write) {
            outputStream.write(writeHeader);
        } else {
            outputStream.write(readHeader);
        }

        outputStream.write(filename.getBytes());
        outputStream.write(0);
        outputStream.write(mode.getBytes());
        outputStream.write(0);

        return outputStream.toByteArray();
    }

    /**
     * @param write
     * @param filename
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static DatagramPacket createPacket(boolean write, String filename) throws UnknownHostException, IOException {
        return createPacket(write, filename, InetAddress.getLocalHost(), 69);
    }

    /**
     * @param write
     * @param filename
     * @param port
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static DatagramPacket createPacket(boolean write, String filename, int port) throws UnknownHostException, IOException {
        return createPacket(write, filename, InetAddress.getLocalHost(), port);
    }

    /**
     * @param ack
     * @param blockNumber
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static DatagramPacket createPacket(byte[] ack, int blockNumber) throws UnknownHostException, IOException {
        return new DatagramPacket(ack, ack.length, InetAddress.getLocalHost(), 23);
    }

    /**
     * @param write
     * @param filename
     * @param address
     * @param port
     * @return
     * @throws IOException
     */
    public static DatagramPacket createPacket(boolean write, String filename, InetAddress address, int port) throws IOException {
        DatagramPacket pkt = null;
        byte[] header = getHeader(write, filename);
        pkt = new DatagramPacket(header, header.length, address, port);
        System.out.println("Client - Packet created successfully.");
        return pkt;
    }

    /**
     * @param opCode
     * @param blockNumber
     * @param data
     * @param address
     * @param port
     * @return
     */
    public static DatagramPacket createDatapackets(byte[] opCode, byte[] blockNumber, byte[] data, InetAddress address, int port) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatagramPacket newPkt = null;
        try {
            outputStream.write(opCode);
            outputStream.write(blockNumber);
            outputStream.write(data);
            newPkt = new DatagramPacket(outputStream.toByteArray(), outputStream.toByteArray().length, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newPkt;

    }

}
