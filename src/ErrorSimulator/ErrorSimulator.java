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

    /**
     * 
     */
    public ErrorSimulator() {
        try {
            //Error simulator will use port 23
            sendReceiveSocket = new DatagramSocket(23);
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
        byte data[] = new byte[512];
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
                receiveClientPacket.getAddress(), 69);
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

}
