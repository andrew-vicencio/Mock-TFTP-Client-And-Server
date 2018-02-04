package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcknowledgementPacket extends Packet.Packet {
    byte[] bytes;

    AcknowledgementPacket(long blockNumber) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(new byte[]{0, 4});

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 3, byteArray.length - 1));
        bytes = outputStream.toByteArray();
    }

    AcknowledgementPacket(String blockString) throws IOException {
        long blockNumber = Long.parseLong(blockString);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(new byte[]{0, 4});

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 3, byteArray.length - 1));
        bytes = outputStream.toByteArray();
    }

    @Override
    byte[] toByteArray() {
        return bytes;
    }
}
