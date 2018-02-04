package Server;

import Logger.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Packet.AcknowledgementPacket;
import Packet.Packet;
import tools.*;

public class Connection extends Thread {
    private Logger logger;
    private DatagramPacket receivePacket;
    private ArrayList<DatagramPacket> file;
    private int port;
    private InetAddress address;

    /**
     * Construct a connection class, used to handle a packet being received by the server.
     *
     * @param logger        Logger to be used by the connection to log packets, exceptions and errors with variable log levels.
     * @param receivePacket Packet that prompted this connection thread to be created
     */
    public Connection(Logger logger, DatagramPacket receivePacket) {
        this.address = receivePacket.getAddress();
        this.port = receivePacket.getPort();
        this.receivePacket = receivePacket;
        this.logger = logger;
    }

    // socket to be used to send / receive data.
    private DatagramSocket sendReceiveSocket;

    // pre created read and write response headers
    final byte readResponse[] = {0, 3};
    final byte writeResponse[] = {0, 4};

    // Regexes to match read and write requests from the client.
    private Pattern readRequest = Pattern.compile("^\\x00([\\x01])(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");
    private Pattern writeRequest = Pattern.compile("^\\x00([\\x02])(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");

    /**
     * Method called when thread is initialised to handle the packet it was created to handle.
     */
    public void run() {
        System.out.println("Server: Packet received:");
        logger.printPacket(LogLevels.INFO, receivePacket);

        // attempt to wait 100ms.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // create a new socket to send data on.
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }


        System.out.println("Server: Sending packet:");
        logger.printPacket(LogLevels.INFO, receivePacket);
        String str = new String(receivePacket.getData(), 0, receivePacket.getLength());

        Matcher m1 = readRequest.matcher(str);
        Matcher m2 = writeRequest.matcher(str);
        if (m1.matches()) {

            String fileName = m1.group(2);
            System.out.println(fileName);
            buildDataPackets(fileName);
            sendPackets();
        } else if (m2.matches()) {
            String fileName = m1.group(2);
            System.out.println(fileName);

        } else {
            System.out.println("Recived Invalid Data");
            System.exit(1);
        }

        sendReceiveSocket.close();

    }


    /**
     * Read a file from disk, into an array of datagram packets to be send to the client.
     *
     * @param fileName The name of the file to read from
     */
    public void buildDataPackets(String fileName) {
        file = new ArrayList<DatagramPacket>();


        byte[] fileBytes = null;
        boolean numberOfByteCheck = false;
        long blockNumber = 0;

        try {
            File fileItem = new File(fileName);
            FileInputStream reader = new FileInputStream(fileItem);

            fileBytes = new byte[(int) fileItem.length()];
            reader.read(fileBytes);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        if (fileBytes.length % 512 == 0) {
            numberOfByteCheck = true;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int x = 0; x < fileBytes.length; x++) {


                if (x == 0 || x % 512 != 0) {
                    outputStream.write(fileBytes[x]);
                } else {
                    blockNumber++;
                    file.add(PacketConstructor.createDatapackets(readResponse, longToBytes(blockNumber), outputStream.toByteArray(), address, port));
                    outputStream.reset();

                    outputStream.write(fileBytes[x]);
                }
            }

            if (outputStream.size() != 0) {
                blockNumber++;
                file.add(PacketConstructor.createDatapackets(readResponse, longToBytes(blockNumber), outputStream.toByteArray(), address, port));
            } else {
                file.add(PacketConstructor.createDatapackets(readResponse, longToBytes(blockNumber), outputStream.toByteArray(), address, port));
            }


            //If the file is exactly length of around 512 or factor of 512 create a packet that closes connection


            if (outputStream.size() != 0) {
                blockNumber++;
                file.add(PacketConstructor.createDatapackets(readResponse, longToBytes(blockNumber), outputStream.toByteArray(), address, port));
            } else {
                file.add(PacketConstructor.createEmptyPacket(address, port));
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allocate a byte array of a specific size
     *
     * @param x length of the byte array to return to the client
     * @return An empty byte array of the given length
     */
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Send the datagram packets read from a file, to the client.
     */
    public void sendPackets() {

        byte[] temp = new byte[100];
        DatagramPacket recivePkt = new DatagramPacket(temp, temp.length);
        for (int x = 0; x < file.size(); x++) {
            try {
                sendReceiveSocket.send(file.get(x));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                System.out.println("Waiting");
                sendReceiveSocket.receive(recivePkt);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String pktString = new String(receivePacket.getData(), 0, receivePacket.getLength());
            AcknowledgementPacket writeSucc = null;
            try {
                writeSucc = (AcknowledgementPacket) Packet.parse(pktString);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        }


    }

}
