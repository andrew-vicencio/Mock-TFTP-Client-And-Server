package Packet;



import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Packet {

    private InetAddress address;
    private int port;

    public static Pair<String, String> decomposeReadWriteData(byte[] data) {
        String str = new String(data);
        Matcher matcher = Pattern.compile("^(.+?)\\x00(.+?)\\x00$").matcher(str);
        if (!matcher.matches()) {
            throw new Error("Invalid packet content");
        }
        String fileName = matcher.group(1);
        String mode = matcher.group(2);
        return new Pair<>(fileName, mode);
    }

    public Packet(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    InetAddress getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }

    public static Packet parse(DatagramPacket datagramPacket) throws Exception {
        if (datagramPacket.getLength() < 2) {
            throw new Exception("Error must have at least 2 bytes for op-code");
        }

        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();

        byte[] bytes = datagramPacket.getData();

        byte[] remaining = Arrays.copyOfRange(bytes, 2, bytes.length);

        switch (bytes[0] * 256 + bytes[1]) {
            case 1:
                return new ReadPacket(address, port, remaining);
            case 2:
                return new WritePacket(address, port, remaining);
            case 3:
                return new DataPacket(address, port, remaining);
            case 4:
                return new AcknowledgementPacket(address, port, remaining);
            case 5:
                return new ErrorPacket(address, port, remaining);
        }

        throw new Exception("Invalid opCode");
    }

    public DataPacket toDataPacket() throws IOException {
        return new DataPacket(getAddress(), getPort(), toByteArray());
    }

    abstract byte[] toByteArray() throws IOException;
}
