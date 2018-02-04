package Packet;

public class WritePacket extends Packet.Packet {
    public WritePacket(String substring) {

    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
