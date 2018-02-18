package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcknowledgementPacket extends Packet {
    //TODO: Finish making all datapacket classes
    private long blockNumber;

    public AcknowledgementPacket(InetAddress address, int port, long blockNumber) {
        super(address, port);
        this.blockNumber = blockNumber;
    }

    public AcknowledgementPacket(InetAddress address, int port, byte[] blockString) throws Exception {
        super(address, port);
        if (blockString.length != 2) {
            throw new Exception("Invalid Acknowledgement packet length");
        }
        this.blockNumber = blockString[0] * 256 + blockString[1];
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    @Override
    public DatagramPacket toDataGramPacket()  {
        try {
            return new DatagramPacket(toByteArray(), toByteArray().length , address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(acknowledgeResponse);

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(blockNumber);
        byte[] byteArray = buffer.array();

        outputStream.write(Arrays.copyOfRange(byteArray, byteArray.length - 2, byteArray.length));
        return outputStream.toByteArray();
    }
}
