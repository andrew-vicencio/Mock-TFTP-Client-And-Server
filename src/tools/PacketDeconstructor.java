package tools;

import java.net.DatagramPacket;

public class PacketDeconstructor {
	public PacketDeconstructor() {}
	
	public static byte[] getHeader(DatagramPacket pkt) {
		return pkt.getData();
	}
	
	public static boolean isWrite(DatagramPacket pkt) {
		byte[] header = pkt.getData();
		if (header[1] == 2) {
			return true;
		}
		return false;
	}
	
	public static byte[] getByteData(DatagramPacket pkt) {
		byte[] data = null;
		//TODO: Find data in header as byte array
		return data;
	}
	
	public static String getStringData(DatagramPacket pkt) {
		String data = null;
		//TODO: Find data in header as string
		return data;
	}
	
	public static byte[] getByteMode(DatagramPacket pkt) {
		byte[] mode = null;
		//TODO: Find mode in header as byte array
		return mode;
	}
	
	public static String getStringMode(DatagramPacket pkt) {
		String mode = null;
		//TODO: Find mode in header as string
		return mode;
	}

}
