package Packet;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class ErrorPacket extends Packet {
    private final byte[] opCode = {0, 3};
    private char errorCode;
    private String errorMessage;

    public ErrorPacket(InetAddress address, int port, byte[] data) {
        super(address, port);

        byte[] message = Arrays.copyOfRange(data, 2, data.length - 1);

        if (data[data.length - 1] != 0) {
            throw new Error("Invalid packet content");
        }

        this.errorCode = (char) (data[0] * 256 + data[1]);
        this.errorMessage = new String(message);
    }

    public ErrorPacket(InetAddress address, int port, int errorCode) {
        super(address, port);
        this.errorCode = (char) errorCode;
        this.errorMessage = getDefaultErrorMessage(this.errorCode);
    }

    public ErrorPacket(InetAddress address, int port, int errorCode, String errorMessage) {
        super(address, port);
        this.errorCode = (char) errorCode;
        this.errorMessage = errorMessage;
    }

    private String getDefaultErrorMessage(char errorCode) {
        if (errorCode == 1) {
            return "File not found.";
        } else if (errorCode == 2) {
            return "Access Violation - You are not authorized to access this file.";
        } else if (errorCode == 3) {
            return "Disk full or allocation exceeded.";
        } else if (errorCode == 6) {
            return "File Already Exists";
        }  else {
            return "Unknown error";
        }
    }

    @Override
    public DatagramPacket toDataGramPacket() {
        byte[] byteArray = toByteArray();

        return new DatagramPacket(byteArray, byteArray.length, address, port);
    }

    @Override
    byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(opCode, 0, 2);
        out.write(errorCode);
        out.write(errorMessage.getBytes(), 0, errorMessage.getBytes().length);
        out.write(0);

        return out.toByteArray();
    }
}
