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
import tools.*;

public class Connection extends Thread {
    private Logger logger;
    private DatagramPacket receivePacket;
    private ArrayList<DatagramPacket> file;
    private int port;
    private InetAddress address;

    public Connection(Logger logger, DatagramPacket receivePacket) {
        this.receivePacket = receivePacket;
        this.logger = logger;

    }

    private DatagramSocket sendReciveSocket;

    final byte readResponse[] = {0, 3};
    final byte writeResponse[] = {0, 4};

    private Pattern readRequest = Pattern.compile("^\\x00([\\x01])(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");
    private Pattern writeRequest = Pattern.compile("^\\x00([\\x02])(.+?)([\\x00]+)(.+?)([^\\x00]+)\\x00+$");

    public void run(){
        System.out.println("Server: Packet received:");
        logger.printPacket(LogLevels.INFO, receivePacket);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            sendReciveSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }


        System.out.println("Server: Sending packet:");
        logger.printPacket(LogLevels.INFO, receivePacket);
        String str = new String(receivePacket.getData(), 0, receivePacket.getLength());

        Matcher m1 = readRequest.matcher(str);
        Matcher m2 = writeRequest.matcher(str);
        if(m1.matches()){

            String fileName = m1.group(2);
            System.out.println(fileName);
            buildDataPackets("MobyDick.txt");
            sendPackets();
        }else if(m2.matches()){
            String fileName = m1.group(2);
            System.out.println(fileName);

        }else{
            System.out.println("Recived Invalid Data");
            System.exit(1);
        }



        System.out.println("Server: packet sent");

        sendReciveSocket.close();
    }


    public void buildDataPackets(String fileName) {
        file = new ArrayList<DatagramPacket>();




        byte[] fileBytes = null;
        boolean numberOfByteCheck = false;
        long blockNumber = 0;

        try {
            File fileItem = new File(fileName);
            FileInputStream reader = new FileInputStream(fileItem);
            fileBytes = reader.readAllBytes();


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
            for (int x = 0; x < fileBytes.length; x++) {
                DatagramPacket tempPacket = null;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


                if (x == 0) {
                    outputStream.write(fileBytes[x]);
                } else if (x % 512 == 0) {
                    blockNumber++;

                    file.add(PacketConstructor.createDatapackets(readResponse, longToBytes(x), outputStream.toByteArray()));

                    outputStream.reset();

                    outputStream.write(fileBytes[x]);
                } else {
                    outputStream.write(fileBytes[x]);
                }
            }




        //If the file is exactly lenght of around 512 or factor of 512 create a packet that closes connection


        if(numberOfByteCheck){

        }

    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public void sendPackets(){

    }

}
