package Server;

import Logger.LogLevels;
import Logger.Logger;
import Packet.*;
import tools.ToolThreadClass;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Connection extends ToolThreadClass {
    private Logger logger;
    private DatagramPacket receivePacket;
    private ArrayList<DatagramPacket> file;
    private int port;
    private InetAddress address;
    private ErrorPacket errorPkt;
    // socket to be used to send / receive data.
    private DatagramSocket sendReceiveSocket;


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

        Packet packet;
        try {
            packet = Packet.parse(receivePacket);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(133);
            return;
        }

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

        } else {
            System.out.println("Received Invalid Data");
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

            ifDataPacketErrorPrintAndExit(recivePkt);

            try {
                AcknowledgementPacket test = (AcknowledgementPacket) Packet.parse(recivePkt);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * When the connection gets a write request get all datagram values from client
     */
    @Override
    public void receivePackets() {
        //TODO: build first response packet
        DatagramPacket recivedDataPacket = new DatagramPacket(new byte[1024], 1024);

        AcknowledgementPacket reviedResponse = new AcknowledgementPacket(address, port, 0);
        DatagramPacket sendPacket = reviedResponse.toDataGramPacket();

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

            ifDataPacketErrorPrintAndExit(recivedDataPacket);

            //Write out where the packet came from
            System.out.println("Server - Packet received from " + recivedDataPacket.getAddress() + " Port " + recivedDataPacket.getPort());

            //Write to file
            DataPacket recivedData = null;
            try {
                recivedData = (DataPacket) (Packet.parse(recivedDataPacket));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Try and write data packets
            try {
                fileComplete = writeRecivedDataPacket(recivedData);
            } catch (IOException e) {
                //Caught error, try and create error data packet
                errorPkt = ErrorCodeHandler(address, port, e);

                ifErrorPrintAndExit(errorPkt);
            }

            //build response packet constructor
            reviedResponse = new AcknowledgementPacket(recivedData.getAddress(), recivedData.getPort(), recivedData.getBlockNumber());

            //Send Response Packet to server
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

    private void ifDataPacketErrorPrintAndExit(DatagramPacket receivedDataPacket) {
        try {
            Packet pkt = Packet.parse(receivedDataPacket);
            if (pkt instanceof ErrorPacket) {
                System.out.println("Error Received from client");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ifErrorPrintAndExit(ErrorPacket errorPacket) {
        if (errorPacket != null) {
            try {
                sendReceiveSocket.send(errorPacket.toDataGramPacket());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.exit(1);
        }
    }
}
