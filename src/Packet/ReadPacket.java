package Packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPacket extends Packet {

    private String fileName;
    private String mode;

    /**
     * @param address
     * @param port
     * @param remaining
     */
    public ReadPacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);
        Pair<String, String> pair = decomposeReadWriteData(remaining);
        this.fileName = pair.getKey();
        this.mode = pair.getValue();
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
        return composeReadWriteData(1, fileName, mode);
    }
}
