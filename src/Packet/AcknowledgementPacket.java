package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcknowledgementPacket extends Packet {
    //TODO: Finish making all datapacket classes
    private long blockNumber;

    AcknowledgementPacket(long blockNumber) {
        super(null, 0);
        this.blockNumber = blockNumber;
    }

    public AcknowledgementPacket(String blockString) throws Exception {
        super(null, 0);

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

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 2, byteArray.length));
        return outputStream.toByteArray();
    }
}
