package client;

import java.io.*;
import java.net.*;
import java.util.*;


import Logger.LogLevels;
import Packet.*;
import Logger.Logger;
import tools.ToolThreadClass;

public class ClientThread extends ToolThreadClass {
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket;
    private boolean write;
    private String fileName;
    private int port;
    private byte[] receivedData;
    private Logger logger;
    private InetAddress address;
    private ClientCommandLine cl;
    private DatagramPacket prvsPkt;

    /*
     * Public constructor initializes the socket used to send and receive packets.
     * This constructor looks for write, filename, address, and port.
     * Chose to store write request, filename, and port. This is for future functionality
     * where it retries a few requests before stopping.
     *
     * @param write
     * @param filename
     * @param address
     * @param port
     * @param cl
     */
    public ClientThread(boolean write, String filename, InetAddress address, int port, ClientCommandLine cl) {
        logger = new Logger(LogLevels.ALL);
        try {
            sendReceiveSocket = new DatagramSocket();
            this.write = write;
            this.fileName = filename;
            this.port = port;
            sendPacket = null;
            this.address = address;
            this.cl = cl;
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Client socket created.");
    }

    /**
     * Public constructor initializes the socket used to send and receive packets.
     * This constructor only looks for write, filename, and port as it defaults the address
     * to the local host.
     *
     * @param write    true if the packet is a write packet
     * @param filename filename to write
     * @param port     port to listen on.
     * @throws UnknownHostException
     */
    public ClientThread(boolean write, String filename, int port) throws UnknownHostException {
        this(write, filename, InetAddress.getLocalHost(), port, null);
    }


    /**
     * run is used to create packets and send them then wait for confirmation from the server
     * that it has been received.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            if(write){
                sendPacket = (new WritePacket(InetAddress.getLocalHost(), port, fileName, "")).toDataGramPacket();
            }else{
                sendPacket = (new ReadPacket(InetAddress.getLocalHost(), port, fileName, "")).toDataGramPacket();
            }

        } catch (IOException e) {
            cl.print("Error: Packet creation has failed.");
            e.printStackTrace();
            System.exit(1);
        }
        send(sendPacket);

        if(!write) {
            receivePackets();
            System.out.println("File received.");
            return;
        }else{
            sendPackets();
        }
        sendReceiveSocket.close();
    }
    
    /* (non-Javadoc)
     * @see tools.ToolThreadClass#receivePackets()
     */
    public void receivePackets() {
        int blockNumber = 0;
        boolean fileComplete = false;
        receivedData = new byte[522];
        DatagramPacket receivePacket = new DatagramPacket(new byte[522], 522);

        while (!fileComplete) {
            blockNumber++;

            //Try and receive from server
            try {
                sendReceiveSocket.receive(receivePacket);
            }catch (SocketTimeoutException e){
                receivePacket = timeout(prvsPkt, 0, sendReceiveSocket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //Check for ErrorPacket
            Packet pkt = null;
            try {
                pkt = Packet.parse(receivePacket);
                if(pkt instanceof ErrorPacket) {
                    System.out.println("Error: Serverside.");
                    System.exit(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Write out where the packet came from
            System.out.println("Client - Packet received from " + receivePacket.getAddress() + " Port " + receivePacket.getPort());

            ByteArrayOutputStream data = new ByteArrayOutputStream();
            try {
                data.write(Arrays.copyOfRange(receivePacket.getData(), 4, receivePacket.getLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            receivedData = data.toByteArray();

            //Try and write to file from the new data string
            try {
                if(pkt instanceof DataPacket) {
                    DataPacket dataPacket = (DataPacket) pkt;
                    if (shouldDiscardPacket(dataPacket)) {
                        System.out.println("[debug]: Dropping packet index " + dataPacket.getBlockNumber());
                        continue;
                    }
                    setLastBlockNumber(dataPacket.getBlockNumber());
                    writeRecivedDataPacket(dataPacket);
                }
            } catch (IOException e2) {
                ErrorPacket errPkt = ErrorCodeHandler(address, port, e2);
                send(errPkt.toDataGramPacket());
                e2.printStackTrace();
                System.exit(1);
            } catch (Exception e) {

            }

            //Send Response Packet to server
            try {

                sendPacket = (new AcknowledgementPacket(InetAddress.getLocalHost(),port, blockNumber)).toDataGramPacket();
                prvsPkt = sendPacket;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            send(sendPacket);

            //Check if file transhpher is complete
            if (receivedData.length < 512) {
                fileComplete = true;
            }

        }

    }


    /**
     * sendPackets passes the first request packet to sendPackets.
     *
     * (non-Javadoc)
     * @see tools.ToolThreadClass#sendPackets()
     */
    public void sendPackets() {


        DatagramPacket receivePacket = new DatagramPacket(new byte[100], 100);
        ArrayList<DatagramPacket> file = null;

        try {
            sendReceiveSocket.receive(receivePacket);

        }catch(IOException e) {
            System.out.println("Error in receiving first packet.");
            e.printStackTrace();
            System.exit(1);
        }

        Packet ack = null;
        try {
            ack = Packet.parse(receivePacket);
        } catch (Exception e) {
            System.out.println("Packet could not be parsed");
        }

        if(ack instanceof ErrorPacket) {
            System.out.println("Error in write request.");
            System.exit(1);
        }

        System.out.println("Starting file transfer.");


        try {

            file = buildDataPackets(fileName, address, port);
        } catch (IOException e1) {
            System.out.println("made it");
            ErrorPacket errPkt = ErrorCodeHandler(address, port, e1);
            send(errPkt.toDataGramPacket());
            e1.printStackTrace();
            System.exit(1);
        }


        byte[] temp = new byte[100];
        DatagramPacket recivePkt = new DatagramPacket(temp, temp.length);
        for (int i = 0; i < file.size(); i++) {
            try {
                prvsPkt = file.get(i);
                sendReceiveSocket.send(file.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                System.out.println("Waiting2.0"); // TODO: output more / better information
                sendReceiveSocket.receive(recivePkt);
            }catch (SocketTimeoutException e) {
                recivePkt = timeout( prvsPkt,0, sendReceiveSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try{
                AcknowledgementPacket test = (AcknowledgementPacket)Packet.parse(recivePkt);
            } catch (Exception e) {
                System.out.println("Error in Acknowledgement Packet.");
                e.printStackTrace();
                System.exit(1);
            }
        }

        System.out.println("Send finished.");
    }

    /**
     * send is used to send any DatagramPacket to the address and port that is saved by
     * the current ClientThread.
     *
     * @param sndPkt Send Packet
     */
    public void send(DatagramPacket sndPkt) { //TODO: Breakdown to handle acknowledgments and sendFilePackets
        if (sendPacket == null) {
            System.out.println("Error: No packet to be sent.");
            System.exit(1);
        }

        System.out.println("Client - Sending packet to " + sendPacket.getAddress() + " Port " + sendPacket.getPort());

        try {
            sendReceiveSocket.send(sndPkt);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Client - Packet sent.");
    }
}
