package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcknowledgementPacket extends Packet.Packet {
    private long blockNumber;

    AcknowledgementPacket(long blockNumber) throws IOException {
        this.blockNumber = blockNumber;
    }

    AcknowledgementPacket(String blockString) throws IOException {
        blockNumber = Long.parseLong(blockString);
    }

    long getBlockNumber() {
        return blockNumber;
    }

    @Override
    byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(new byte[]{0, 4});

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 3, byteArray.length - 1));
        return outputStream.toByteArray();
    }
}
