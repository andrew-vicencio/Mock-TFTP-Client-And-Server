package client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import Packet.AcknowledgementPacket;
import Packet.*;
import tools.PacketConstructor;
import tools.*;

public class ClientThread extends ToolThreadClass {
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket;
    private boolean write;
    private String fileName;
    private int port;
    private byte[] receivedData;
    private InetAddress address;
    private ClientCommandLine cl;

    final byte[] readResponse = {0, 3};
    final byte[] writeResponse = {0, 4};

    /*
     * Public constructor initializes the socket used to send and receive packets.
     * This constructor looks for write, filename, address, and port.
     * Chose to store write request, filename, and port. This is for future functionality
     * where it retries a few requests before stopping.
     */
    public ClientThread(boolean write, String filename, InetAddress address, int port, ClientCommandLine cl) {
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
     */
    public ClientThread(boolean write, String filename, int port) throws UnknownHostException {
        this(write, filename, InetAddress.getLocalHost(), port, null);
    }


    /**
     * run is used to create packets and send them then wait for confirmation from the server
     * that it has been received.
     */
    public void run() {
        try {
            sendPacket = PacketConstructor.createPacket(write, fileName, port);
        } catch (IOException e) {
            cl.print("Error: Packet creation has failed.");
            e.printStackTrace();
            System.exit(1);
        }
        sendPackets();
        receivePackets();
    }
    
    public void receivePackets() {
    	if(!write) {
    		receiveFilePackets();
    		System.out.println("File received.");
    		return;
    	}
    	
    	DatagramPacket receivePacket = new DatagramPacket(new byte[100], 100);
    	try {
    		sendReceiveSocket.receive(receivePacket);
    	} catch (IOException e) {
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
    	sendFilePackets();
    }


    /**
     * receivePacket is used to wait for confirmation packets from the host. This method will block until it
     * receives a packet.
     */
    public void receiveFilePackets() {
        int blockNumber = 0;
        boolean fileComplete = false;
        receivedData = new byte[516];
        FileWriter filewriter = null;
        File temp = new File("receivedFile.txt");
        DatagramPacket receivePacket = new DatagramPacket(new byte[522], 522);

        while (!fileComplete) {
            blockNumber++;

            //Try and receive from server
            try {
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
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
            String dataString = new String(receivedData, 0, receivedData.length);
            try {
                filewriter = new FileWriter(temp, true);
                filewriter.write(dataString);
                filewriter.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            //Send Response Packet to server
            ByteArrayOutputStream ack = new ByteArrayOutputStream();
            try {
                ack.write(writeResponse);
                ack.write(blockNumber);
                byte[] ackPacket = ack.toByteArray();
                String test = new String(ackPacket, 0, ackPacket.length);
                System.out.println(test);
                sendPacket = PacketConstructor.createPacket(ackPacket, blockNumber);

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendPackets();

            if (receivedData.length < 512) {
                fileComplete = true;
            }

        }
        sendReceiveSocket.close();
    }

    /**
     * sendPacket is used to send DatagramPacket sendPacket to the specified address and port
     */
    public void sendPackets() { //TODO: Breakdown to handle acknowledgments and sendFilePackets
        if (sendPacket == null) {
            System.out.println("Error: No packet to be sent.");
            System.exit(1);
        }

        System.out.println("Client - Sending packet to " + sendPacket.getAddress() + " Port " + sendPacket.getPort());

        try {
        	sendReceiveSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Client - Packet sent.");
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

    
    public void sendFilePackets() {
    	ArrayList<DatagramPacket> file = buildDataPackets(fileName, address, port);
        byte[] temp = new byte[100];
        DatagramPacket recivePkt = new DatagramPacket(temp, temp.length);
    	for (int i = 0; i < file.size(); i++) {
            try {
                sendReceiveSocket.send(file.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                System.out.println("Waiting2.0");
                sendReceiveSocket.receive(recivePkt); //TODO: Call receivePacket()
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
    }
}
