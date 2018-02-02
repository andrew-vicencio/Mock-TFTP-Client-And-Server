package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tools.PacketConstructor;

public class ClientThread implements Runnable{
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private boolean write;
	private String filename;
	private int port;
	
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
	
	/*
	 * Public constructor initializes the socket used to send and receive packets.
	 * This constructor only looks for write, filename, and port as it defaults the address
	 * to the local host.
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
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Client - Packet received from " + receivePacket.getAddress() + " Port " + receivePacket.getPort());
		
		byte[] data = receivePacket.getData();
		String received = new String(data, 0, data.length);
		System.out.println(received);
		
		sendReceiveSocket.close();
	}
	
	/*
	 * sendPacket is used to send DatagramPacket sendPacket to the specified address and port
	 */
	public void sendPacket()
	{
		if (sendPacket != null) {
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
