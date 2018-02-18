package Packet;

import javafx.util.Pair;

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

    /* (non-Javadoc)
     * @see Packet.Packet#toDataGramPacket()
     */
    @Override
    DatagramPacket toDataGramPacket() {
        return null;
    }

    /* (non-Javadoc)
     * @see Packet.Packet#toByteArray()
     */
    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
