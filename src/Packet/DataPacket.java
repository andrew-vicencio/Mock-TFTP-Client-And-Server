package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class DataPacket extends Packet {

    private long blockNumber;
    private byte[] data;


    /**
     * @param address
     * @param port
     * @param remaining
     */
    public DataPacket(InetAddress address, int port, byte[] remaining) throws IllegalArgumentException {
        super(address, port);

        this.blockNumber = twoBytesToLong(remaining);
        this.data = Arrays.copyOfRange(remaining, 2, remaining.length);

        if (this.data.length > 512) throw new IllegalArgumentException("Invalid data length");
    }

    /**
     * @param address
     * @param port
     * @param blockNumber
     * @param data
     */
    public DataPacket(InetAddress address, int port, long blockNumber, byte[] data) {
        super(address, port);

        this.blockNumber = blockNumber;
        this.data = data;
    }

    /**
     * @return
     */
    public long getBlockNumber() {
        return blockNumber;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(dataResponse);
        outputStream.write(to2Bytes(blockNumber));
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    /**
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
