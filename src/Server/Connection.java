package Server;

import Logger.LogLevels;
import Logger.Logger;
import Packet.*;
import tools.ToolThreadClass;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Connection extends ToolThreadClass {
    private Logger logger;
    private DatagramPacket receivePacket;
    private ArrayList<DatagramPacket> file;
    private int port;
    private InetAddress address;
    private ErrorPacket errorPkt;
    // socket to be used to send / receive data.
    private DatagramPacket prvsPkt;

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

    /**
     * Method called when thread is initialized to handle the packet it was created to handle.
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

        System.out.println("Server:/n Sending packet:");
        logger.printPacket(LogLevels.INFO, receivePacket);

        //Parse Packet that was received
        Packet packet = null;
        try {
            packet = Packet.parse(receivePacket);
        }catch (IllegalArgumentException e1){
            errorPkt = new ErrorPacket(address, port, 4);
            ifErrorPrintAndExit(errorPkt);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(133);
            return;
        }

        //Setting TID from the first packet received from client side
        ifInvalidTIDPrintAndExit(receivePacket);



        //Check which packet has been given to us
        if (packet instanceof ReadPacket) {
            //Send data gram packets
            ReadPacket readPacket = (ReadPacket) packet;

            try {
                file = buildDataPackets(readPacket.getFileName(), address, port);
            } catch (IOException e) {
                errorPkt = ErrorCodeHandler(address, port, e);
                ifErrorPrintAndExit(errorPkt);
            }
            sendPackets();
        } else if (packet instanceof WritePacket) {
            //Write acknowledgement packets
            receivePackets();
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

            //Try and send first Packet
            try {
                prvsPkt = file.get(x);
                sendReceiveSocket.send(file.get(x));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Try and receive first Ack Packet
            try {
                sendReceiveSocket.receive(recivePkt);
            } catch (SocketTimeoutException e){
                recivePkt = timeout(prvsPkt,0,sendReceiveSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ifDataPacketErrorPrintAndExit(recivePkt);
            ifInvalidTIDPrintAndExit(recivePkt);
            //TODO: What should we do with ack packet
            try {
                AcknowledgementPacket test = (AcknowledgementPacket) Packet.parse(recivePkt);
            } catch (Exception e) {

                errorPkt = new ErrorPacket(address, port, 4);
                ifErrorPrintAndExit(errorPkt);

            }
        }
    }

    /**
     * When the connection gets a write request get all datagram values from client
     */
    @Override
    public void receivePackets() {
        DatagramPacket recivedDataPacket = new DatagramPacket(new byte[1024], 1024);
        boolean fileComplete = false;

        //Build First Acknowledgement Packet and send off
        AcknowledgementPacket reviedResponse = new AcknowledgementPacket(address, port, 0);
        DatagramPacket sendPacket = reviedResponse.toDataGramPacket();
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (!fileComplete) {

            //Try and receive from Client
            try {
                sendReceiveSocket.receive(recivedDataPacket);
            } catch (SocketTimeoutException e){
                recivedDataPacket =  timeout(prvsPkt,0, sendReceiveSocket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            ifDataPacketErrorPrintAndExit(recivedDataPacket);
            ifInvalidTIDPrintAndExit(recivedDataPacket);
            //Write out where the packet came from
            System.out.println("Server - Packet received from " + recivedDataPacket.getAddress() + " Port " + recivedDataPacket.getPort());

            //Get received Data and parse and check for duplecets and valid op code
            DataPacket recivedData = null;
            try {
                recivedData = (DataPacket) (Packet.parse(recivedDataPacket));
                if (shouldDiscardPacket(recivedData)) {
                    System.out.println("[debug]: Dropping packet index " + recivedData.getBlockNumber());
                    continue;
                }
                setLastBlockNumber(recivedData.getBlockNumber());
            } catch (Exception e) {
                //Send off invalid opcode error packet
                errorPkt = new ErrorPacket(address, port, 4);
                ifErrorPrintAndExit(errorPkt);
            }

            //Try and write data packets
            try {
                fileComplete = writeRecivedDataPacket(recivedData);
            } catch (IOException e) {
                //Caught error, try and create error data packet
                errorPkt = ErrorCodeHandler(address, port, e);

                ifErrorPrintAndExit(errorPkt);
            }

            //Set response packet object
            reviedResponse = new AcknowledgementPacket(recivedData.getAddress(), recivedData.getPort(), recivedData.getBlockNumber());

            //Build Datagram response packet
            try {
                sendPacket = reviedResponse.toDataGramPacket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Send the packet on the way
            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        //Finished receiving the file
        System.out.println("Received file.");

    }


}
