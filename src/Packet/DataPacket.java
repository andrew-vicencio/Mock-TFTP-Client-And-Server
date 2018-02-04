package Packet;

public class DataPacket extends Packet.Packet {
    public DataPacket(String substring) {

    }

    @Override
    byte[] toByteArray() {
        return new byte[0];
    }
}
