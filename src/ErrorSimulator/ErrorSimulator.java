package ErrorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Geoffrey Scornaienchi
 * <p>
 * Iteration 3
 * <p>
 * Acts as an intermediate host between the client and server
 * Processes one client request at a time
 */
public class ErrorSimulator {

    private DatagramSocket sendReceiveSocket;
    private DatagramPacket receiveClientPacket, receiveServerPacket, sendPacket;
    private int clientPort, connectionPort;
    private ErrorSimCommandLine cl;
    
    private int testModeID = 0; // 0 : normal operation; 1 : lose a packet; 2 : delay a packet, 3 : duplicate a packet -- SELECT WHICH ERROR TO SIMULATE
    private int errorPacketID = 0; // 0 : None; 1: 1st WRQ/RRQ, 2: 2nd WRQ/RRQ, 3: 1st Data, 4: 2nd Data, 5: 1st ACK, 6: 2nd Ack -- SELECT WHICH PACKET TO LOSE/DELAY/DUPLICATE
    private int timeDelay = 1000; //How much time between delays or sending duplicates (in MILLISECONDS)
    
    /**
     * 
     */
    public ErrorSimulator(ErrorSimCommandLine cl) {
        try {
            //Error simulator will use port 23
            sendReceiveSocket = new DatagramSocket(23);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        this.cl = cl;
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
        checkNetworkErrorsAndSend(testModeID, sendPacket);
    }

     /**
     * sendServerPacket is used to send packet to server
     */
    public void sendServerPacket() {
        //send to port 69 (server)
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
        receiveClientPacket.getAddress(), 69);
        checkNetworkErrorsAndSend(testModeID, sendPacket);
        
    }

    /**
     * sendServerPacket is used to send packet to connection's random port
     */
    public void sendResponsePacket(){
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
        receiveClientPacket.getAddress(), connectionPort);
        checkNetworkErrorsAndSend(testModeID, sendPacket);
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
        System.out.println("ErrorSimulator: Sending duplicate packet with delay:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
        receiveClientPacket = null;
    }
    
    public void checkNetworkErrorsAndSend(int testModeID, DatagramPacket packet){
    	switch (testModeID){
        case 0: //No network error	
        	 try {
                 sendReceiveSocket.send(packet);
             } catch (IOException e) {
                 e.printStackTrace();
                 System.exit(1);
             }

             System.out.println("ErrorSimulator: Sending packet");
             System.out.println("To host: " + sendPacket.getAddress());
             System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
             receiveClientPacket = null;
        	break;
        	
        case 1: //Lose a packet
        	break;
        	
        case 2: //Delay a packet
        	delay();
        	try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("ErrorSimulator: Sending packet with delay:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
            receiveClientPacket = null;
        	break;
        	
        case 3: //Duplicate a packet
        	try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("ErrorSimulator: Sending packet before duplicate:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("Destination host port: " + sendPacket.getPort() + "\n");       
            receiveClientPacket = null;
            delayAndSendDuplicate(sendPacket);
        	break;
        }
    }
    
    
    
    
    
}


