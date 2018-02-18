package Server;

import Logger.*;
import client.ClientThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;


public class Listener {

    DatagramPacket receivePacket;
    DatagramSocket receiveSocket;

    Logger logger;

    /**
     * 
     */
    public Listener() {
        // initialise a logger with maximum verbosity
        logger = new Logger(LogLevels.ALL);
    }

    /**
     * Start a socket on provided port and handles connections.
     *
     * @param port
     */
    /**
     * @param port
     */
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

    /**
     * Wait for a packet from the client, and pass of handling it to a connection thread..
     */
    /**
     * 
     */
    public void waitForPacketAndHandle() {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        logger.println(LogLevels.INFO, "Server: Waiting for Packet.");

        try {
            logger.println(LogLevels.INFO, "Waiting...");
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            logger.println(LogLevels.ERROR, "IO Exception: likely: Receive Socket Timed Out.");
            logger.printException(LogLevels.ERROR, e);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Thread thread = new Thread(new Connection(logger, receivePacket), "Server Connection");
            thread.start();
        } catch (Exception e) {
            System.out.println("Error: Client thread not created successfully.");
            e.printStackTrace();
            System.exit(1);
        }

    }
}
