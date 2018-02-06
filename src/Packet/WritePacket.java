package Packet;

import java.net.InetAddress;

public class WritePacket extends Packet {
    public WritePacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);

    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
