package Packet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPacket extends Packet {

    private Pattern readRequest = Pattern.compile("^(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");


    public ReadPacket(String str) {
        super(null, 0);
        Matcher m2 = readRequest.matcher(str);
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
