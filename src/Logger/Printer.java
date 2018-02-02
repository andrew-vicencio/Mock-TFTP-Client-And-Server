package Logger;

import java.net.DatagramPacket;
import java.util.Arrays;

public class Printer {

    public void printPacket(DatagramPacket datagramPacket) {
        System.out.println("Server: Packet received:");
        System.out.println("From host: " + datagramPacket.getAddress());
        System.out.println("Host port: " + datagramPacket.getPort());
        int len = datagramPacket.getLength();
        System.out.println("Length: " + len);
        String received = new String(datagramPacket.getData(), 0, len);
        System.out.print("Containing: ");
        System.out.println(Arrays.toString(received.getBytes()));
        System.out.print("            ");
        System.out.println(received + "\n");
    }

    public void printString(String string) {
        System.out.println(string);
    }
}
