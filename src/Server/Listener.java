package Server;

import Logger.*;
import client.ClientThread;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;


public class Listener extends Thread {

    DatagramPacket receivePacket;
    DatagramSocket receiveSocket;
    private boolean stopRequested;

    Logger logger;


    /**
     * Constructor to create main logger for server
     */
    public Listener() {
        // initialize a logger with maximum Verboseness
        logger = new Logger(LogLevels.ALL);
        stopRequested = false;
    }

    /**
     * Start a socket on provided port and handles connections.
     * @param port
     */
    public void listen(int port) throws InterruptedException {
        try {
            System.out.println("2"); //TODO: remove
            receiveSocket = new DatagramSocket(port);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        while (!stopRequested) {
            waitForPacketAndHandle();
        }
    }

    /**
     * Starts the thread to listen on port 69
     */
    @Override
    public void run() {
        while (!stopRequested) {
            System.out.println("1"); //TODO: remove
            try {
                listen(69);
            } catch (InterruptedException ie) {
                System.out.println("Listener closed.");
                requestStop();
            }
        }
        System.out.println("Listener closed.");
    }

    public void requestStop() {
        System.out.println("Closing listener.");
        stopRequested = true;
    }

    /**
     * Wait for a packet from the client, and pass of handling it to a connection thread..
     */
    public void waitForPacketAndHandle() throws InterruptedException {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        logger.println(LogLevels.INFO, "Server: Waiting for Packet.");

        try {
            logger.println(LogLevels.INFO, "Waiting...");
            System.out.println("3"); //TODO: remove
            receiveSocket.receive(receivePacket);
            System.out.println("4"); //TODO: remove
        } catch (InterruptedIOException e) {
            System.out.println("Interrupt");
            throw new InterruptedException();
        } catch (SocketException e) {
            logger.println(LogLevels.INFO, "Socket Exception: Most likely means socket closed due to exit.");
            return;
        } catch (IOException e) {
            logger.println(LogLevels.FATAL, "IO Exception: likely: Receive Socket Timed Out.");
            logger.printException(LogLevels.FATAL, e);
            System.exit(1);
        }

        try {
            System.out.println("5");
            Thread thread = new Thread(new Connection(logger, receivePacket), "Server Connection");
            thread.start();
        } catch (Exception e) {
            logger.println(LogLevels.FATAL, "Error: Client thread not created successfully.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.receiveSocket.close();
        requestStop();
    }
}
