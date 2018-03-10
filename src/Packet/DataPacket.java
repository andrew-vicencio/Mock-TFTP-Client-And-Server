package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket extends Packet {

    private long blockNumber;
    private byte[] data;


    /**
     * @param address
     * @param port
     * @param remaining
     */
    public DataPacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);

        this.blockNumber = remaining[0] * 256 + remaining[1];
        this.data = Arrays.copyOfRange(remaining, 2, remaining.length);
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

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 2, byteArray.length));
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
