package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Packet {

    protected InetAddress address;
    protected int port;
    protected byte[] byteDataCache;

    protected static final byte writeHeader[] = {0, 2};
    protected static final byte readHeader[] = {0, 1};
    protected static final byte dataResponse[] = {0, 3};
    protected static final byte acknowledgeResponse[] = {0, 4};

    /**
     * @param data
     * @return
     */
    public static Pair<String, String> decomposeReadWriteData(byte[] data) {
        String str = new String(data);
        Matcher matcher = Pattern.compile("^(.+?)\\x00(.+?)\\x00$").matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid packet content");
        }
        String fileName = matcher.group(1);
        String mode = matcher.group(2);
        if (!(mode.equalsIgnoreCase("netascii")
                || mode.equalsIgnoreCase("octet")
                || mode.equalsIgnoreCase("mail"))) {
            throw new IllegalArgumentException("Invalid mode");
        }
        return new Pair<>(fileName, mode);
    }

    public static byte[] composeReadWriteData(int opCode, String filename, String mode) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(to2Bytes(opCode), 0, 2);
        out.write(filename.getBytes(), 0, filename.getBytes().length);
        out.write(0);
        out.write(mode.getBytes(), 0, mode.getBytes().length);
        out.write(0);

        return out.toByteArray();
    }

    /**
     * @param address
     * @param port
     */
    public Packet(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * @return
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Changes the datagramPacket port
     * @param port port
     */
    public void setPort(int port) { this.port = port; }

    /**
     * @param datagramPacket
     * @return
     * @throws Exception
     */
    public static Packet parse(DatagramPacket datagramPacket) throws IllegalArgumentException {
        if (datagramPacket.getLength() < 2) {
            throw new IllegalArgumentException("Error must have at least 2 bytes for op-code");
        }

        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();

        byte[] bytes = datagramPacket.getData();

        byte[] remaining = Arrays.copyOfRange(bytes, 2, datagramPacket.getLength());

        switch ((int) twoBytesToLong(bytes)) {
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

        throw new IllegalArgumentException("Invalid opCode");
    }

    /**
     * @param address
     * @param port
     * @return
     */
    public static DatagramPacket createEmptyPacket(InetAddress address, int port) {
        byte[] newArray = new byte[1];
        DatagramPacket newPtk = new DatagramPacket(newArray, 1, address, port);
        return newPtk;
    }

    static long twoBytesToLong(byte[] bytes) {
        return (bytes[0]) << 8 | (bytes[1] & 0xFF);
    }

    static byte[] to2Bytes(int i) {
        byte[] result = new byte[2];

        result[0] = (byte) (i >> 8);
        result[1] = (byte) (i);

        return result;
    }

    static byte[] to2Bytes(char c) {
        byte[] result = new byte[2];
        result[0] = (byte) (c >> 8);
        result[1] = (byte) (c);

        return result;
    }

    static byte[] to2Bytes(long l) {
        byte[] result = new byte[2];
        result[0] = (byte) (l >> 8);
        result[1] = (byte) (l);

        return result;
    }

    public DatagramPacket toDataGramPacket() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = getCacheAwareByteData();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(49);
        }

        return new DatagramPacket(byteArray, byteArray.length, address, port);
    }

    public void cacheByteData(byte[] bytes) {
        this.byteDataCache = bytes;
    }

    public byte[] getCacheAwareByteData() throws IOException {
        if (this.byteDataCache != null) {
            return this.byteDataCache;
        } else {
            return this.toByteArray();
        }
    }

    /**
     * @return
     * @throws IOException
     */
    abstract public byte[] toByteArray() throws IOException;
}
