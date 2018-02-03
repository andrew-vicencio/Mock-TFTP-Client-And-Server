package Server;

import Logger.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection extends Thread {
    private Logger logger;

    public Connection(Logger logger) {
        this.logger = logger;
    }

    private DatagramSocket sendSocket;

    final byte readResponse[] = {0, 3, 0, 1};
    final byte writeResponse[] = {0, 4, 0, 0};

    private Pattern p = Pattern.compile("^\\x00([\\x01\\x02])([^\\x00]+)\\x00([^\\x00]+)\\x00+$");

    void handlePacket(DatagramPacket receivePacket) {

        System.out.println("Server: Packet received:");
        logger.printPacket(LogLevels.INFO, receivePacket);

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

        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
                receivePacket.getAddress(), receivePacket.getPort());

        System.out.println("Server: Sending packet:");
        logger.printPacket(LogLevels.INFO, sendPacket);
        String str = new String(sendPacket.getData(), 0, sendPacket.getLength());

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

}
