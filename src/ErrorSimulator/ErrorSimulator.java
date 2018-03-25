package ErrorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import Packet.*;

/**
 * @author Geoffrey Scornaienchi
 * <p>
 * Iteration 3
 * <p>
 * Acts as an intermediate host between the client and server
 * Processes one client request at a time
 */
public class ErrorSimulator {
	private boolean newConnection;
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket receiveClientPacket, receiveServerPacket, sendPacket;
    private int clientPort, connectionPort;
    private ErrorSimCommandLine cl;
    
    private int testModeID = 2; // 0 : normal operation; 1 : lose a packet; 2 : delay a packet, 3 : duplicate a packet -- SELECT WHICH ERROR TO SIMULATE
    private int errorPacketID = 0; // 0 : None; 1: 1st WRQ/RRQ, 2: 2nd WRQ/RRQ, 3: 1st Data, 4: 2nd Data, 5: 1st ACK, 6: 2nd Ack -- SELECT WHICH PACKET TO LOSE/DELAY/DUPLICATE
    private int timeDelay = 1000; //How much time between delays or sending duplicates (in MILLISECONDS)
    
    private int rwCount = 0;
    private int dataCount = 0;
    private int ackCount = 0;
    
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
        newConnection = true;
    }
    
    /**
     * start is used begins to receive and send
     * packets between the client and server
     *
     */
    public void start() {
        while (true) {
            this.receiveClientPacket();
            if (newConnection) {
            	this.sendServerPacket();
            } else {
            	this.sendResponsePacket();
            }
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
        
        Packet pkt = null;
        try {
        	pkt = Packet.parse(receiveClientPacket);
        } catch (Exception e) {
        	System.out.println("Invalid packet");
        } finally {
        	if (pkt instanceof ReadPacket || pkt instanceof WritePacket) {
        		newConnection = true;
        	}
        }
        
        System.out.println("ErrorSimulator: Received packet from client:");
        System.out.println("From host: " + receiveClientPacket.getAddress());
        System.out.println("Host port: " + receiveClientPacket.getPort() + "\n");
        clientPort = receiveClientPacket.getPort();

    }

    /**
     * receiveServerPacket is used to receive packet from server
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
        newConnection = false;
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
    	if(isErrorPacket(packet)) {
	    	switch (testModeID){
	    		case 0: //No network error
	        	System.out.println("Case 0");
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
	        	System.out.println("Case 1");
	        	break;
	        	
	        case 2: //Delay a packet
	        	System.out.println("Case 2");
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
	        	System.out.println("Case 3");
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
    		} else {
    			try {
    				sendReceiveSocket.send(sendPacket);
    			} catch (IOException e) {
    				e.printStackTrace();
    				System.exit(1);
    			}
    		}
    }
    
    public boolean isErrorPacket(DatagramPacket receivePacket) {
    		Packet packet;
    		try {
            packet = Packet.parse(receivePacket);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(133);
            return false;
        }


        //Check which packet has been given to us
        if (packet instanceof ReadPacket || packet instanceof WritePacket) {
            rwCount++;
            if(errorPacketID % 2 == 0) {
            		if(rwCount == 2) {
            			return true;
            		}
            } else{
	            	if(rwCount == 1) {
	        			return true;
	        		}
            }
        } else if (packet instanceof DataPacket) {
        		dataCount++;
        		if(errorPacketID % 2 == 0) {
            		if(dataCount == 2) {
            			return true;
            		}
            } else{
	            	if(dataCount == 1) {
	        			return true;
	        		}
            }
        } else if (packet instanceof AcknowledgementPacket) {
        	  	ackCount++;
        	  	if(ackCount % 2 == 0) {
            		if(rwCount == 2) {
            			return true;
            		}
            } else{
	            	if(ackCount == 1) {
	        			return true;
	        		}
            }
        } else {
            return false;
        }
        
        return false;
    }
}


