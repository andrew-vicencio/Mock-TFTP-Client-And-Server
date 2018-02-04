package Packet;

import java.io.IOException;
import java.util.Arrays;

public abstract class Packet {

    public static Packet parse(String str) throws Exception {
        if (str.length() < 2) {
            throw new Exception("Error must have at least 2 bytes for op-code");
        }

        switch(str.charAt(0) * 255 + str.charAt(1)) {
            case 1:
                return new ReadPacket(str.substring(2));
            case 2:
                return new WritePacket(str.substring(2));
            case 3:
                return new DataPacket(str.substring(2));
            case 4:
                return new AcknowledgementPacket(str.substring(2));
            case 5:
                return new ErrorPacket(str.substring(2));
        }

        throw new Exception("Invalid opCode");
    }

    abstract byte[] toByteArray() throws IOException;
}
