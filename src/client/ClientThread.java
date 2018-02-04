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
import java.util.Arrays;

import tools.PacketConstructor;

public class ClientThread implements Runnable {
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private boolean write;
    private boolean fileComplete;
    private String filename;
    private int port;
    private byte[] receivedData;

    final byte[] ackBytes = {0, 4};

    /*
     * Public constructor initializes the socket used to send and receive packets.
     * This constructor looks for write, filename, address, and port.
     * Chose to store write request, filename, and port. This is for future functionality
     * where it retries a few requests before stopping.
     */
    public ClientThread(boolean write, String filename, InetAddress address, int port) {
        try {
            sendReceiveSocket = new DatagramSocket();
            this.write = write;
            this.filename = filename;
            this.port = port;
            sendPacket = null;
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
     * @param write true if the packet is a write packet
     * @param filename filename to write
     * @param port port to listen on.
     */
    public ClientThread(boolean write, String filename, int port) throws UnknownHostException {
        this(write, filename, InetAddress.getLocalHost(), port);
    }


    /**
     * run is used to create packets and send them then wait for confirmation from the server
     * that it has been received.
     */
    public void run() {
        try {
            sendPacket = PacketConstructor.createPacket(write, filename, port);
        } catch (IOException e) {
            System.out.println("Error: Packet creation has failed.");
            e.printStackTrace();
            System.exit(1);
        }
        sendPacket();
        receivePacket();
    }


    /**
     * receivePacket is used to wait for confirmation packets from the host. This method will block until it
     * receives a packet.
     */
    public void receivePacket() {
        int blockNumber = 0;
        fileComplete = false;
        receivedData = new byte[516]; 
        FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(new File("receivedFile.txt"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

        while (!fileComplete) {
            blockNumber++;
            try {
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }


            System.out.println("Client - Packet received from " + receivePacket.getAddress() + " Port " + receivePacket.getPort());
            
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            try {
                data.write(Arrays.copyOfRange(receivePacket.getData(), 4, receivePacket.getLength()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            receivedData = data.toByteArray();
            
            String dataString = new String(receivedData, 0, receivedData.length);
            try {
				filewriter.write(dataString);
				filewriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            ByteArrayOutputStream ack = new ByteArrayOutputStream();
            try {
                ack.write(ackBytes);
                ack.write(blockNumber);
                byte[] ackPacket = ack.toByteArray();
                String test = new String(ackPacket, 0 , ackPacket.length);
                System.out.println(test);
                sendPacket = PacketConstructor.createPacket(ackPacket, blockNumber);
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (receivedData.length < 512) {
                fileComplete = true;
            }

        }
        sendReceiveSocket.close();
    }

    /**
     * sendPacket is used to send DatagramPacket sendPacket to the specified address and port
     */
    public void sendPacket() {
        if (sendPacket == null) {
            System.out.println("Error: No packet to be sent.");
            System.exit(1);
        }

        System.out.println("Client - Sending packet to " + sendPacket.getAddress() + " Port " + sendPacket.getPort());

        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Client - Packet sent.");
        receivePacket = new DatagramPacket(new byte[100], 100);
    }
}
