package Packet;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPacket extends Packet {

    private String fileName;
    private String mode;

    public ReadPacket(InetAddress address, int port, byte[] remaining) {
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
