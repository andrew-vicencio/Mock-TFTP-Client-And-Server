package Packet;

public class ErrorPacket extends Packet {
    public ErrorPacket(String substring) {
        super(null, 0);
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
