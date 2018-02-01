import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Client {
	DatagramSocket sendReceiveSocket;
	DatagramPacket sendPacket;
	DatagramPacket receivePacket;
	String mode = "netascii";
	final byte writeHeader[] = {0, 2};
	final byte readHeader[] = {0, 1};
	
	/**
	 * 
	 * Public constructor initializes the socket used to send and receive packets
	 * 
	 */
	public Client()
	{
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client socket created.");
	}
	
    private byte[] getHeader(boolean write, String filename) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (write) {
            outputStream.write(writeHeader);
        } else {
            outputStream.write(readHeader);
        }

        outputStream.write(filename.getBytes());
        outputStream.write(0);
        outputStream.write(mode.getBytes());
        outputStream.write(0);

        return outputStream.toByteArray();
    }
	
	
	/**
	 * 
	 * run is used to run the receivePacket and sendPacket methods 11 times. Can also be used in future
	 * iterations in case the client class must be multi-threaded, if client implements the runnable interface.
	 * 
	 */
	public void run() {
		for(int i = 0; i < 11; i++) {
			sendPacket(i, "test.txt"); //todo: input from commandline
			receivePacket();
		}
	}
	
	
	/**
	 * 
	 * receivePacket is used to wait for confirmation packets from the host. This method will block until it
	 * receives a packet.
	 * 
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
	
	public void sendPacket(int i, String filename)
	{
		byte header[] = null;
		try {
			if(i % 2 == 0) {
				header = getHeader(false, filename); //Read request
			} else {
				header = getHeader(true, filename); //Write request
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Client - Sending following message: ");
		System.out.println("String: " + header.toString());
		System.out.println("Byte: " + Arrays.toString(header));
		
		try {
			sendPacket = new DatagramPacket(header, header.length, InetAddress.getLocalHost(), 23);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Client - Packet created successfully.");
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
	
   public static void main(String args[])
   {
	      Client c = new Client();
	      c.run();
   }
	

}
