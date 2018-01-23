import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendSocket, receiveSocket;

    Pattern p = Pattern.compile("^\\x00([\\x01\\x02])([^\\x00]+)\\x00([^\\x00]+)\\x00+$");
    final byte readResponse[] = {0, 3, 0, 1};
    final byte writeResponse[] = {0, 4, 0, 0};


    public Server() {
        try {
            receiveSocket = new DatagramSocket(69);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public void receiveAndEcho() {


        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Server: Waiting for Packet.\n");


        try {
            System.out.println("Waiting...");
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }


        System.out.println("Server: Packet received:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("Host port: " + receivePacket.getPort());
        int len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");

        String received = new String(data, 0, len);
        System.out.println(received + "\n");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                receivePacket.getAddress(), receivePacket.getPort());

        System.out.println("Server: Sending packet:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort());
        len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        String str = new String(sendPacket.getData(), 0, len);
        System.out.println(str);

        Matcher m = p.matcher(str);
        boolean b = m.matches();
        System.out.println(b ? "matches" : "does not match");
        if (!b) {
            System.err.println("Received invalid data");
            System.exit(1);
        }
        boolean write = "\u0002".equals(m.group(1));
        System.out.println(Arrays.toString(str.getBytes()));
        System.out.println(Arrays.toString(m.group(0).getBytes()));
        System.out.println();

        try {
            byte msg[] = write ? writeResponse : readResponse;
            sendSocket.send(new DatagramPacket(msg, msg.length, receivePacket.getAddress(), receivePacket.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server: packet sent");

        sendSocket.close();

    }

    public void closeRecieve() {
        receiveSocket.close();
    }

    public static void main(String args[]) {
        Server c = new Server();
        while (true) {
            c.receiveAndEcho();
        }
        // c.closeRecieve();
    }
}

