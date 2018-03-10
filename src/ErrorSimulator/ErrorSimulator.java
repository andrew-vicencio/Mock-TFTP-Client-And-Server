package ErrorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Geoffrey Scornaienchi
 * <p>
 * Iteration 1
 * <p>
 * Acts as an intermediate host between the client and server
 * Processes one client request at a time
 */
public class ErrorSimulator {

    private DatagramSocket sendReceiveSocket;
    private DatagramPacket receiveClientPacket, receiveServerPacket, sendPacket;
    private int clientPort, connectionPort;
    
    private int testModeID = 0; // 0 : normal operation; 1 : lose a packet; 2 : delay a packet, 3 : duplicate a packet -- SELECT WHICH ERROR TO SIMULATE
    private int errorPacketID = 0; // 0 : None; 1: 1st WRQ/RRQ, 2: 2nd WRQ/RRQ, 3: 1st Data, 4: 2nd Data, 5: 1st ACK, 6: 2nd Ack -- SELECT WHICH PACKET TO LOSE/DELAY/DUPLICATE
    private int timeDelay = 100; //How much time between delays or sending duplicates (in MILLISECONDS)
    
    /**
     * 
     */
    public ErrorSimulator() {
        try {
            //Error simulator will use port 23
            sendReceiveSocket = new DatagramSocket(10023);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        ErrorSimulator host = new ErrorSimulator();
        host.start();
    }

    /**
     * start is used begins to receive and send
     * packets between the client and server
     *
     */
    public void start() {
        this.receiveClientPacket();
        this.sendServerPacket();
        this.receiveServerPacket();
        this.sendClientPacket();

        while (true) {

            this.receiveClientPacket();
            this.sendResponsePacket();
            this.receiveServerPacket();
            this.sendClientPacket();
        }
    }

    /**
     * receiveClientPacket is used to receive packet sent to
     * port 23 from client
     */
    /**
     * 
     */
    public void receiveClientPacket() {
        byte data[] = new byte[522];
        receiveClientPacket = new DatagramPacket(data, data.length);

        try {
            sendReceiveSocket.receive(receiveClientPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("ErrorSimulator: Received packet from client:");
        System.out.println("From host: " + receiveClientPacket.getAddress());
        System.out.println("Host port: " + receiveClientPacket.getPort() + "\n");
        clientPort = receiveClientPacket.getPort();

    }

    /**
     * receiveServerPacket is used to receive packet from server
     */
    /**
     * 
     */
    public void receiveServerPacket() {
        byte data[] = new byte[522];
        receiveServerPacket = new DatagramPacket(data, data.length);
        try {
            sendReceiveSocket.receive(receiveServerPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ErrorSimulator: Received packet from server:");
        System.out.println("From host: " + receiveServerPacket.getAddress());
        System.out.println("Host port: " + receiveServerPacket.getPort() + "\n");
        connectionPort = receiveServerPacket.getPort();
    }

    /**
     * sendClientPacket is used to send packet from server to client
     */
    /**
     * 
     */
    public void sendClientPacket() {
        sendPacket = new DatagramPacket(receiveServerPacket.getData(), receiveServerPacket.getLength(),
                receiveServerPacket.getAddress(), clientPort);
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ErrorSimulator: Sending packet to client:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
        receiveServerPacket = null;
    }

     /**
     * sendServerPacket is used to send packet to server
     */
    public void sendServerPacket() {
        //send to port 69 (server)
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
                receiveClientPacket.getAddress(), 10069);
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ErrorSimulator: Sending packet to server:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
        receiveClientPacket = null;
    }

    /**
     * sendServerPacket is used to send packet to connection's random port
     */
    public void sendResponsePacket(){
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
                receiveClientPacket.getAddress(), connectionPort);
        switch (testModeID){
        case 0: //No network error	
        	 try {
                 sendReceiveSocket.send(sendPacket);
             } catch (IOException e) {
                 e.printStackTrace();
                 System.exit(1);
             }

             System.out.println("ErrorSimulator: Sending packet to server:");
             System.out.println("To host: " + sendPacket.getAddress());
             System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
             receiveClientPacket = null;
        	break;
        	
        case 1: //Lose a packet
        	break;
        case 2: //Delay a packet
        	break;
        case 3: //Duplicate a packet
        	break;
        }
       
       
    }
    
    public void delay(){
    	try {
			Thread.sleep(timeDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void delayAndSendDuplicate(DatagramPacket packet){
    	this.delay();
        try {
        	sendReceiveSocket.send(packet);
        } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
        }
    }
    
    
    
    
    
}


