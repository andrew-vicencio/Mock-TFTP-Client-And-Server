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

    public ErrorSimulator() {
        try {
            //Error simulator will use port 23
            sendReceiveSocket = new DatagramSocket(10023);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        ErrorSimulator host = new ErrorSimulator();
        host.start();
    }

    /**
     * start is used begins to receive and send
     * packets between the client and server
     */
    public void start() {
        while (true) {
            this.receiveClientPacket();
            this.sendServerPacket();
            this.receiveServerPacket();
            this.sendClientPacket();
        }
    }

    /**
     * receiveClientPacket is used to receive packet sent to
     * port 23 from client
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
    }

    /**
     * sendClientPacket is used to send packet from server to client
     */
    public void sendClientPacket() {
        sendPacket = new DatagramPacket(receiveServerPacket.getData(), receiveServerPacket.getLength(),
                receiveClientPacket.getAddress(), receiveClientPacket.getPort());
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ErrorSimulator: Sending packet to client:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort() + "\n");
    }

    /**
     * receiveServerPacket is used to receive packet from server
     */
    public void receiveServerPacket() {
        byte data[] = new byte[512];
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

    }

}
