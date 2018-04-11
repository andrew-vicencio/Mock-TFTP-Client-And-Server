package ErrorSimulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private DatagramSocket sendReceiveSocket, newTransferIDSocket;
    private boolean newConnection, gotServerAddress = false, gotClientAddress = false;
    private DatagramPacket receiveClientPacket, receiveServerPacket, sendPacket;
    private int clientPort, connectionPort;
    private InetAddress clientAddress, serverAddress;
    private ErrorSimCommandLine cl;
    private int testModeID = 2; // 0 : normal operation; 1 : lose a packet; 2 : delay a packet, 3 : duplicate a packet, 5 : Invalid transfer ID -- SELECT WHICH ERROR TO SIMULATE
    private int errorPacketID = 0; // 0 : None; 1: 1st WRQ/RRQ, 2: 2nd WRQ/RRQ, 3: 1st Data, 4: 2nd Data, 5: 1st ACK, 6: 2nd Ack -- SELECT WHICH PACKET TO LOSE/DELAY/DUPLICATE

    private int RRQCount = 0;
    private int dataCount = 0;
    private int ackCount = 0;

    /**
     *
     */
    public ErrorSimulator(ErrorSimCommandLine cl) {
        try {
            //Error simulator will use port 23
            sendReceiveSocket = new DatagramSocket(23);
            newTransferIDSocket = new DatagramSocket();
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
        if (!gotClientAddress) {
            clientAddress = receiveClientPacket.getAddress();
            gotClientAddress = true;
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
        if (!gotServerAddress) {
            gotServerAddress = true;
            serverAddress = receiveServerPacket.getAddress();
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
        checkNetworkErrorsAndSend(sendPacket);
    }

    /**
     * sendServerPacket is used to send packet to server
     */
    public void sendServerPacket() {
        //send to port 69 (server)
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
                receiveClientPacket.getAddress(), 69);
        checkNetworkErrorsAndSend(sendPacket);
        newConnection = false;
    }

    /**
     * sendServerPacket is used to send packet to connection's random port
     */
    public void sendResponsePacket() {
        sendPacket = new DatagramPacket(receiveClientPacket.getData(), receiveClientPacket.getLength(),
                receiveClientPacket.getAddress(), connectionPort);
        checkNetworkErrorsAndSend(sendPacket);
    }

    public void delay(int timeDelay) {
        try {
            Thread.sleep(timeDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void checkNetworkErrorsAndSend(DatagramPacket datagramPacket) {
        Packet packet;
        try {
            packet = Packet.parse(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(133);
            return;
        }

        incrementPacketCounter(packet);

        Packet afterPacket = modifyPacket(cl.getErrors(), packet);
        if (afterPacket != null) {
            sendPacket(afterPacket);
        }
    }

    private void incrementPacketCounter(Packet packet) {
        if (packet instanceof ReadPacket || packet instanceof WritePacket) {
            RRQCount++;
        } else if (packet instanceof AcknowledgementPacket) {
            ackCount++;
        } else if (packet instanceof DataPacket) {
            dataCount++;
        }
    }

    private int getPacketCounter(Packet packet) {
        if (packet instanceof ReadPacket || packet instanceof WritePacket) {
            return RRQCount;
        } else if (packet instanceof AcknowledgementPacket) {
            return ackCount;
        } else if (packet instanceof DataPacket) {
            return dataCount;
        }
        return 0;
    }

    private boolean isPacketOfType(PacketType type, Packet packet) {
        if (type.equals(PacketType.RRQ)) {
            return packet instanceof ReadPacket || packet instanceof WritePacket;
        } else if (type.equals(PacketType.ACK)) {
            return packet instanceof AcknowledgementPacket;
        } else if (type.equals(PacketType.DATA)) {
            return packet instanceof DataPacket;
        }
        return false;
    }

    public void sendPacket(Packet packet) {
        try {
            if (packet.getSendPort() == 0) {
                sendReceiveSocket.send(packet.toDataGramPacket());
                System.out.println("Sending packet to server from normal port");
            } else {
                DatagramSocket newSocket = new DatagramSocket();
                newSocket.send(packet.toDataGramPacket());
                System.out.println("Sending packet to server from wrong port");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public Packet modifyPacket(List<PacketError> errors, Packet packet) {
        Packet afterPacket = packet;

        for (PacketError error : errors) {
            if (!isPacketOfType(error.getPacketType(), packet)) {
                continue;
            }

            if (getPacketCounter(packet) != error.getPacketIndex()) {
                continue;
            }

            switch (error.getPacketOperation()) {
                case DELAY:
                    System.out.println("Delaying packet");
                    delay(error.getPacketDelay());
                    break;
                case LOSE:
                    System.out.println("Modifying packet");
                    afterPacket = null;
                    break;
                case DUPLICATE:
                    System.out.println("Duplicating packet");
                    if (afterPacket != null) {
                        sendPacket(afterPacket);
                    }
                    break;
                case CHANGETRANSFERID:
                    System.out.println("Changing transfer id");
                    if (afterPacket != null) {
                        packet.setSendPort(2000);
                    }
                    break;
                case MODIFY:
                    System.out.println("Modifying packet");
                    if (afterPacket != null) {
                        afterPacket = modifyPacketData(error, packet);
                    }
                    break;
            }
        }

        return afterPacket;
    }

    private Packet modifyPacketData(PacketError error, Packet packet) {

        byte[] bytes;
        try {
            bytes = packet.getCacheAwareByteData();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(23);
            return null;
        }

        int modificationIndex = error.getPacketModificationIndex();

        switch (error.getPacketModification()) {
            case REMOVE: {

                byte[] newBytes = new byte[bytes.length - 1];

                System.arraycopy(bytes, 0, newBytes, 0, modificationIndex);
                System.arraycopy(bytes, modificationIndex + 1, newBytes, modificationIndex, bytes.length - modificationIndex - 1);

                bytes = newBytes;
                break;
            }
            case EDIT:
                bytes[modificationIndex] = error.getNewData();
                break;
            case ADD: {
                byte[] newBytes = new byte[bytes.length + 1];

                System.arraycopy(bytes, 0, newBytes, 0, modificationIndex);
                System.arraycopy(bytes, modificationIndex, newBytes, modificationIndex + 1, bytes.length - modificationIndex);

                bytes[modificationIndex] = error.getNewData();

                bytes = newBytes;
                break;
            }

        }

        packet.cacheByteData(bytes);

        return packet;
    }
}


