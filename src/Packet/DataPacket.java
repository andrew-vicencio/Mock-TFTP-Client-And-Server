package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket extends Packet {

    private int blockNumber;
    private byte[] data;


    public DataPacket(InetAddress address, int port, byte[] remaining) {
        super(address, port);

        this.blockNumber = remaining[0] * 255 + remaining[1];
        this.data = Arrays.copyOfRange(remaining, 2, remaining.length);
    }

    public DataPacket(InetAddress address, int port, int blockNumber, byte[] data) {
        super(address, port);

        this.blockNumber = blockNumber;
        this.data = data;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    @Override
    DatagramPacket toDataGramPacket() {
        return null;
    }


    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(new byte[]{0, 3});

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 2, byteArray.length));
        outputStream.write(data);
        return outputStream.toByteArray();
    }



    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
