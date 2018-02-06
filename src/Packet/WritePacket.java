package Packet;

public class WritePacket extends Packet {
    public WritePacket(String substring) {
        super(null, 0);

    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
