package Packet;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.Arrays;

public class ErrorPacket extends Packet {
    private final byte[] opCode = {0, 5};
    private char errorCode;
    private String errorMessage;

    /**
     * @param address
     * @param port
     * @param data
     */
    public ErrorPacket(InetAddress address, int port, byte[] data) throws IllegalArgumentException {
        super(address, port);

        byte[] message = Arrays.copyOfRange(data, 2, data.length - 1);

        if (data[data.length - 1] != 0) {
            throw new IllegalArgumentException("Invalid packet content");
        }

        this.errorCode = (char) twoBytesToLong(data);
        this.errorMessage = new String(message);
    }

    /**
     * @param address
     * @param port
     * @param errorCode
     */

    public ErrorPacket(InetAddress address, int port, int errorCode) {
        super(address, port);
        this.errorCode = (char) errorCode;
        this.errorMessage = getDefaultErrorMessage(this.errorCode);
    }

    /**
     * @param address
     * @param port
     * @param errorCode
     * @param errorMessage
     */
    public ErrorPacket(InetAddress address, int port, int errorCode, String errorMessage) {
        super(address, port);
        this.errorCode = (char) errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Get's the error message
     *
     * @return string representation of error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get's the error code
     *
     * @return a char representation of the error code.
     */
    public char getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     * @return
     */
    private String getDefaultErrorMessage(char errorCode) {
        switch (errorCode) {
            case 0:
                return "Error not defined";
            case 1:
                return "File not found.";
            case 2:
                return "Access Violation - Application is not authorized to access this file.";
            case 3:
                return "Disk full or allocation exceeded.";
            case 4:
                return "Illegal TFTP operation";
            case 5:
                return "Unknown transfer ID.";
            case 6:
                return "File already exists.";
            case 7:
                return "No such user.";
            default:
                return "Invalid error message code";
        }
    }

    @Override
    byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(opCode, 0, 2);
        out.write(to2Bytes(errorCode), 0, 2);
        out.write(errorMessage.getBytes(), 0, errorMessage.getBytes().length);
        out.write(0);

        return out.toByteArray();
    }
}
