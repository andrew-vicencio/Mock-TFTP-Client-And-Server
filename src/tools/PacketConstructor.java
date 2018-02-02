package tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketConstructor {
	private static String mode = "netascii";
	private static final byte writeHeader[] = {0, 2};
	private static final byte readHeader[] = {0, 1};
	
	public PacketConstructor() {}
	
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
    
    public static DatagramPacket createPacket(boolean write, String filename) throws UnknownHostException, IOException
    {
    	return createPacket(write, filename, InetAddress.getLocalHost(), 69);
    }
    
    public static DatagramPacket createPacket(boolean write, String filename, int port) throws UnknownHostException, IOException
    {
    	return createPacket(write, filename, InetAddress.getLocalHost(), port);
    }
    
    public static DatagramPacket createPacket(boolean write, String filename, InetAddress address, int port) throws IOException
    {
    	DatagramPacket pkt = null;
		byte[] header = getHeader(write, filename);
		pkt = new DatagramPacket(header, header.length, address, port);
		System.out.println("Client - Packet created successfully.");
    	return pkt;
    }
}
