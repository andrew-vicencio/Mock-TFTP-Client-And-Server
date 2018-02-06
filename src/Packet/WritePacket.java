package Packet;

import javafx.util.Pair;

import java.net.InetAddress;

public class WritePacket extends Packet {

    private String fileName;
    private String mode;

    public WritePacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);
        Pair<String, String> pair = decomposeReadWriteData(remaining);
        this.fileName = pair.getKey();
        this.mode = pair.getValue();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileMode() {
        return mode;
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
