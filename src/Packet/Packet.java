package Packet;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public abstract class Packet {

    private InetAddress address;
    private int port;

    public Packet(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public static Packet parse(byte[] str) throws Exception {
        if (str.length < 2) {
            throw new Exception("Error must have at least 2 bytes for op-code");
        }

        switch (str[1]) {
            case 1:
                return new ReadPacket("1");
            case 2:
                return new WritePacket("");
            case 3:
                return new DataPacket("");
            case 4:
                return new AcknowledgementPacket("");
            case 5:
                return new ErrorPacket("");
        }

        throw new Exception("Invalid opCode");
    }

    abstract byte[] toByteArray() throws IOException;
}
