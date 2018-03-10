package tools;

import Packet.DataPacket;
import Packet.ErrorPacket;
import Packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ToolThreadClass implements Runnable {
    // pre created read and write response headers
    final byte dataResponse[] = {0, 3};
    final byte acknowledgeResponse[] = {0, 4};

    /**
     * Read a file from disk, into an array of datagram packets to be send to the client.
     *
     * @param fileName The name of the file to read from
*/

    public  ArrayList<DatagramPacket> buildDataPackets(String fileName, InetAddress address, int port) throws IOException {


        ArrayList<DatagramPacket> file = new ArrayList<DatagramPacket>();

        byte[] fileBytes = null;
        long blockNumber = 0;
        
        File fileItem = new File(fileName);
        FileInputStream reader = new FileInputStream(fileItem);

        fileBytes = new byte[(int) fileItem.length()];
        reader.read(fileBytes);
        reader.close();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int x = 0; x < fileBytes.length; x++) {


            if (x == 0 || x % 512 != 0) {
                outputStream.write(fileBytes[x]);
            } else {

                blockNumber++;
                file.add((new DataPacket(address, port, blockNumber, outputStream.toByteArray())).toDataGramPacket());
                outputStream.reset();

                outputStream.write(fileBytes[x]);
            }
        }

        //If the file is exactly length of around 512 or factor of 512 create a packet that closes connection

        if (outputStream.size() != 0) {
            blockNumber++;
            file.add((new DataPacket(address, port, blockNumber, outputStream.toByteArray())).toDataGramPacket());
        } else {
            file.add((new DataPacket(address, port, blockNumber, new byte[1])).toDataGramPacket());
        }

        outputStream.close();
        return file;
    }

    public boolean writeRecivedDataPacket(DataPacket receivePacket){

        FileWriter filewriter = null;
        File temp = new File("receivedFile.txt");
        byte[] receivedData = new byte[512];

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            data.write(Arrays.copyOfRange(receivePacket.getData(), 4, receivePacket.getData().length));
        } catch (IOException e) {
            e.printStackTrace();
        }

        receivedData = data.toByteArray();

        //Try and write to file from the new data string
        String dataString = new String(receivedData, 0, receivedData.length);
        try {
            filewriter = new FileWriter(temp, true);
            filewriter.write(dataString);
            filewriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }


        if (receivedData.length < 511) {
           return true;
        }else{
            return false;
        }
    }

    /**
     * Every class that inherits should be sending packets no matter what that is this method for sending packets, reciving packets, and handleing errors
     */
    public abstract void sendPackets();

    public abstract void receivePackets();

    public abstract void ifErrorPrintAndExit(ErrorPacket errorPacket);

    /**
     * Determines what error packet to create
     * @param address
     * @param port
     * @param e
     * @return Error Packet with correct error code or nothing
     */
    public ErrorPacket ErrorCodeHandler(InetAddress address, int port, Exception e){
    	if (e instanceof FileNotFoundException){
    		//Error Code 1 - File not found.
    		return new ErrorPacket(address, port, 1);
    	} else if (e instanceof AccessViolationException){
    		//Error Code 2 - Access Violation
    		return new ErrorPacket(address, port, 2);
    	} else if (e instanceof DiskFullException){
    		//Error Code 3 - Disk full or allocation exceeded.
    		return new ErrorPacket(address, port, 3);
    	} else if (e instanceof FileAlreadyExistsException){
    		//Error Code 6 - File Already Exists
    		return new ErrorPacket(address, port, 6);
    	}
    	return null;
    }

    public class AccessViolationException extends IOException{}
    public class FileAlreadyExistsException extends IOException{}
    public class DiskFullException extends IOException{}
    

}
