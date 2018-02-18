package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ErrorPacket extends Packet {
	private final byte[] opCode = {0, 3};
	private int errorCode;
	private byte[] arr = null;
    public ErrorPacket(InetAddress address, int port, int errorCode) {
        super(address, port);
        this.errorCode = errorCode;
    }

    @Override
    DatagramPacket toDataGramPacket() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
		out.write(opCode, 0, opCode.length);
    	out.write(0);
    	out.write(errorCode);
    	
    	String str = "";
    	if (errorCode == 1) {
    		str = "File not found.";
    	} else if (errorCode == 2) {
    		str = "Access Violation - You are not authorized to access this file.";
    	} else if (errorCode == 3) {
    		str = "Disk full or allocation exceeded.";
    	} else if (errorCode == 6) {
    		str = "File Already Exists";
    	}
    	
    	out.write(str.getBytes(), 0, str.getBytes().length);
    	
    	arr = out.toByteArray();
    	return new DatagramPacket(arr, arr.length, address, port);
    }

    @Override
    byte[] toByteArray() {
        return arr;
    }
}
