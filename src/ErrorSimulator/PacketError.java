package ErrorSimulator;

public class PacketError {
    private PacketType packetType; // type of packet to modify
    private int packetIndex; // index of packet to modify
    private ErrorOperation errorOperation; // lose, delay, modify
    private int packetDelay;
    private PacketModification PacketModification;
    private int packetModificationIndex;
    private char newData;

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public int getPacketIndex() {
        return packetIndex;
    }

    public ErrorOperation getPacketOperation() {
        return errorOperation;
    }

    public void setPacketOperation(ErrorOperation packetOperation) {
        this.errorOperation = packetOperation;
    }

    public int getPacketDelay() {
        return packetDelay;
    }

    public void setPacketDelay(int packetDelay) {
        this.packetDelay = packetDelay;
    }

    public PacketModification getPacketModification() {
        return PacketModification;
    }

    public void setPacketModification(PacketModification packetModification) {
        this.PacketModification = packetModification;
    }

    public int getPacketModificationIndex() {
        return packetModificationIndex;
    }

    public byte getNewData() {
        return (byte) newData;
    }

    public void setNewData(char newData) {
        this.newData = newData;
    }

    public void setPacketModificationIndex(int packetModificationIndex) {
        this.packetModificationIndex = packetModificationIndex;
    }

    public void setPacketIndex(int packetIndex) {
        this.packetIndex = packetIndex;
    }
}
