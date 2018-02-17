package tools;

import Packet.DataPacket;
import Packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ToolThreadClass implements Runnable {
    // pre created read and write response headers
    final byte dataResponse[] = {0, 3};
    final byte acknowledgeResponse[] = {0, 4};

    /**
     * Read a file from disk, into an array of datagram packets to be send to the client.
     *
     * @param fileName The name of the file to read from
     */

    public  ArrayList<DatagramPacket> buildDataPackets(String fileName, InetAddress address, int port) {
        ArrayList<DatagramPacket> file = new ArrayList<DatagramPacket>();

        byte[] fileBytes = null;
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

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int x = 0; x < fileBytes.length; x++) {


                if (x == 0 || x % 512 != 0) {
                    outputStream.write(fileBytes[x]);
                } else {
                    blockNumber++;
                    file.add(PacketConstructor.createDatapackets(dataResponse, longToBytes(blockNumber), outputStream.toByteArray(), address, port));
                    outputStream.reset();

                    outputStream.write(fileBytes[x]);
                }
            }

            //If the file is exactly length of around 512 or factor of 512 create a packet that closes connection

            if (outputStream.size() != 0) {
                blockNumber++;
                file.add(PacketConstructor.createDatapackets(dataResponse, longToBytes(blockNumber), outputStream.toByteArray(),address, port));
            } else {
                file.add(Packet.createEmptyPacket(address, port));
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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


    public boolean writeRecivedDataPacket(DataPacket receivePacket){
        //TODO: Fix to be more integrated with packet classes
        FileWriter filewriter = null;
        File temp = new File("receivedFile.txt");
        byte[] receivedData = new byte[512];

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            data.write(Arrays.copyOfRange(receivePacket.getData(), 4, receivePacket.getData().length));
        } catch (IOException e) {
            e.printStackTrace();
        }

        receivedData = data.toByteArray();

        //Try and write to file from the new data string
        String dataString = new String(receivedData, 0, receivedData.length);
        try {
            filewriter = new FileWriter(temp, true);
            filewriter.write(dataString);
            filewriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (receivedData.length < 511) {
           return true;
        }else{
            return false;
        }
    }


    /**
     * Every class that inherits should be sending packets no matter what that is this method for sending packets
     */
    public abstract void sendPackets();

    /*
    this method is for waiting to receive packets from either client or server they are specialized for each
     */
    public abstract void receivePackets();



}
