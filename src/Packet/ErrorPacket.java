package Packet;

import java.net.InetAddress;

public class ErrorPacket extends Packet {
    public ErrorPacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
