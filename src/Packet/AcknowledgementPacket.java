package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class AcknowledgementPacket extends Packet {
    //TODO: Finish making all datapacket classes
    private long blockNumber;

    /**
     * @param address
     * @param port
     * @param blockNumber
     */
    public AcknowledgementPacket(InetAddress address, int port, long blockNumber) {
        super(address, port);
        this.blockNumber = blockNumber;
    }

    /**
     * @param address
     * @param port
     * @param blockString
     * @throws Exception
     */
    public AcknowledgementPacket(InetAddress address, int port, byte[] blockString) throws Exception {
        super(address, port);
        if (blockString.length != 2) {
            throw new Exception("Invalid Acknowledgement packet length: +");
        }
        this.blockNumber = twoBytesToLong(blockString);
    }

    /**
     * @return blocknumber
     */
    public long getBlockNumber() {
        return blockNumber;
    }

    @Override
    byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(acknowledgeResponse);
        outputStream.write(to2Bytes(blockNumber));

        return outputStream.toByteArray();
    }
}
