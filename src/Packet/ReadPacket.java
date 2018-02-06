package Packet;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPacket extends Packet {

    private Pattern readRequest = Pattern.compile("^(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");

    public ReadPacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);
        String str = new String(remaining);
        Matcher m2 = readRequest.matcher(str);
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
