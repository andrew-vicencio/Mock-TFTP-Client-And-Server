package Packet;

import java.net.InetAddress;

public class DataPacket extends Packet {

    private int blocknumber;
    private byte[] data;

    public DataPacket(String str) {
        super(null, 0);
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
