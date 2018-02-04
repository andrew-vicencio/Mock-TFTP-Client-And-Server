package Packet;

public class ErrorPacket extends Packet {
    public ErrorPacket(String substring) {
        super();
    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
