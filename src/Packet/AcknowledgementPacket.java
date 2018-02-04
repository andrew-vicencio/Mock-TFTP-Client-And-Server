package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcknowledgementPacket extends Packet {
    private long blockNumber;

    AcknowledgementPacket(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    AcknowledgementPacket(String blockString) throws Exception {
        if (blockString.length() > 2) {
            throw new Exception("Invalid blockNumber byte length");
        }

        blockNumber = blockString.charAt(0) * 256 + blockString.charAt(1);
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
