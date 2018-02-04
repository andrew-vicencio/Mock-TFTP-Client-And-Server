package Server;

import Logger.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;


public class Listener {

    private ArrayList<Connection> activeConnections;
    DatagramPacket receivePacket;
    DatagramSocket receiveSocket;

    Logger logger;

    public Listener() {
        logger = new Logger(LogLevels.ALL);
    }

    public void listen(int port) {
        try {
            receiveSocket = new DatagramSocket(port);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        while (true) {
            waitForPacketAndHandle();
        }


    }

    public void waitForPacketAndHandle() {
        activeConnections = new ArrayList<Connection>();
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

        Connection connection = new Connection(logger, receivePacket, activeConnections.size());
        connection.start();

    }
}
