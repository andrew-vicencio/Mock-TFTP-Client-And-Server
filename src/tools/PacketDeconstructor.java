package tools;

import Packet.*;

import java.net.DatagramPacket;

public class PacketDeconstructor {
    public PacketDeconstructor() {
    }

    public static byte[] getHeader(DatagramPacket pkt) {
        return pkt.getData();
    }

    public static boolean isWrite(DatagramPacket pkt) {
        byte[] header = pkt.getData();
        if (header[1] == 2) {
            return true;
        }
        return false;
    }


    public static Packet deConstructPkt(DatagramPacket pkt){
        //TODO: make this return packet

        String str = new String();
        try {


            switch (str.charAt(1) * 255 + str.charAt(1)) {
                case 1:
                    return new ReadPacket(str.substring(2));
                case 2:
                    return new WritePacket(str.substring(2));
                case 3:
                    return new DataPacket(str.substring(2));
                case 4:
                    return new AcknowledgementPacket(str.substring(2));
                case 5:
                    return new ErrorPacket(str.substring(2));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getByteData(DatagramPacket pkt) {
        byte[] data = null;
        //TODO: Find data in header as byte array
        return data;
    }

    public static String getStringData(DatagramPacket pkt) {
        String data = null;
        //TODO: Find data in header as string
        return data;
    }

    public static byte[] getByteMode(DatagramPacket pkt) {
        byte[] mode = null;
        //TODO: Find mode in header as byte array
        return mode;
    }

    public static String getStringMode(DatagramPacket pkt) {
        String mode = null;
        //TODO: Find mode in header as string
        return mode;
    }

}
