package Server;

import Logger.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import Packet.*;
import tools.*;

public class Connection extends ToolThreadClass {
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
        //Send data gram packets
            String fileName = m1.group(2);

           file = buildDataPackets(fileName, address, port);
            sendPackets();
        } else if (m2.matches()) {
            //Write acknolagement packets
            receivePackets();

        } else {
            System.out.println("Recived Invalid Data");
            System.exit(1);
        }

        sendReceiveSocket.close();

    }


    /**
     * Send the datagram packets read from a file, to the client.
     */
    @Override
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
                sendReceiveSocket.receive(recivePkt);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try{
                AcknowledgementPacket test = (AcknowledgementPacket)Packet.parse(recivePkt);

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}

    @Override
    public void receivePackets() {
        //TODO: build first response packet
        DatagramPacket recivedDataPacket = new DatagramPacket(new byte[522], 522);



        DatagramPacket sendPacket = null;
        AcknowledgementPacket reviedResponse = new AcknowledgementPacket(address, port, 0);
        sendPacket = reviedResponse.toDataGramPacket();
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean fileComplete = false;

        while (!fileComplete) {


            //Try and receive from server
            try {
                sendReceiveSocket.receive(recivedDataPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //Write out where the packet came from
            System.out.println("Server - Packet received from " + recivedDataPacket.getAddress() + " Port " + recivedDataPacket.getPort());

            //Write to file
            DataPacket recivedData = null;
            try {
                recivedData = (DataPacket) (Packet.parse(recivedDataPacket));
            }catch (Exception e ){
                e.printStackTrace();
            }
            fileComplete = writeRecivedDataPacket(recivedData);

            //Send Response Packet to server

            //build response packet constrontur
            reviedResponse = new AcknowledgementPacket(recivedData.getAddress(), recivedData.getPort(), recivedData.getBlockNumber());


            try {
                sendPacket = reviedResponse.toDataGramPacket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //send the packet on the way
            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}
