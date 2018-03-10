package client;

import java.io.IOException;
import java.util.Arrays;

import tools.CommandLine;

public class ClientCommandLine extends CommandLine {
    /*
     * Public constructor for Client Command Line
     */
    public ClientCommandLine() {
        super("Client");
    }

	@Override
	public void interpret(String[] cmds) {
		if(cmds.length >= 2) {
			//By default, writeBool is set to read
			boolean writeBool = false;
			if (Arrays.asList(cmds).contains("write")) {
				writeBool = true;
			}
			
			String fileNameIn = cmds[1];
			
			//Check for port
			int port = 69;
			if (isTest()) {
				port = 23;
			}
			
	        try {
	            Thread thread = new Thread(new ClientThread(writeBool, fileNameIn, port), "Client");
	            thread.start();
	        } catch (IOException e) {
	            System.out.println("Error: Client thread not created successfully.");
	            e.printStackTrace();
	            System.exit(1);
	        }
		} else {
			print("Invalid command.");
		}
	}

	@Override
	public void helpCommand() {
		
	}
}
