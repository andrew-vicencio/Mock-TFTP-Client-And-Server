package Server;

import Logger.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection extends Thread {
    private Logger logger;
    private DatagramPacket receivePacket;
    private ArrayList<DatagramPacket> file;
    private int port, connectionID;
    private InetAddress address;

    public Connection(Logger logger, DatagramPacket receivePacket, int x) {
        this.receivePacket = receivePacket;
        this.logger = logger;
        this.connectionID = x;
    }

    private DatagramSocket sendReciveSocket;

    final byte readResponse[] = {0, 3, 0, 1};
    final byte writeResponse[] = {0, 4, 0, 0};

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
        int blockNumber = 0;


        try {
            FileInputStream reader = new FileInputStream(new File(fileName));
            fileBytes = reader.readAllBytes();
            if (fileBytes.length % 512 == 0) {
                numberOfByteCheck = true;
            }
            for (int x = 0; x < fileBytes.length; x++) {
                DatagramPacket tempPacket = null;
                ArrayList<Byte> tempArray = new ArrayList<Byte>();


                if (x == 0) {
                    tempArray.add(fileBytes[x]);
                } else if (x % 512 == 0) {

                    byte[] temp = {(byte)connectionID, ((byte)blockNumber)};
                    System.arraycopy(tempArray.toArray(), 0, temp,4,tempArray.size());
                            tempPacket = new DatagramPacket(temp, temp.length, address, port);
                    tempArray.clear();

                    blockNumber++;
                    tempArray.add(fileBytes[x]);
                } else {
                    tempArray.add(fileBytes[x]);
                }


            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        //If the file is exactly lenght of around 512 or factor of 512 create a packet that closes connection


        if(numberOfByteCheck){

        }

    }

    public void sendPackets(){

    }

}
