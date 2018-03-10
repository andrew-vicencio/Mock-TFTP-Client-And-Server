package Packet;

import java.net.InetAddress;

public class WritePacket extends Packet {

    private String fileName;
    private String mode;

    /**
     * @param address
     * @param port
     * @param remaining
     */
    public WritePacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);
        Pair<String, String> pair = decomposeReadWriteData(remaining);
        this.fileName = pair.getKey();
        this.mode = pair.getValue();
    }

    public WritePacket(InetAddress address, int port, String fileName, String mode) {
        super(address, port);
        this.fileName = fileName;
        if (mode.equals("")) {
            this.mode = "netascii";
        } else {
            this.mode = mode;
        }
    }

    /**
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return
     */
    public String getFileMode() {
        return mode;
    }

    @Override
    byte[] toByteArray() {
        return composeReadWriteData(2, fileName, mode);
    }
}
