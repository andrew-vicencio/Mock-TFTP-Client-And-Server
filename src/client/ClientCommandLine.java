package client;

import java.io.IOException;

import tools.CommandLine;

public class ClientCommandLine extends CommandLine {
	/*
	 * Public constructor for Client Command Line
	 */
	public ClientCommandLine() {
		super();
	}
	
	/*
	 * Asks user for input whether they want read or write, and what they want to send.
	 * Also will check for test mode automatically to check where to send the packet.
	 */
	@Override
	public void send() {
		System.out.println("Would you like to read or write?");
		String writeIn = in.next();
		System.out.println("What file would you like to send?");
		String fileNameIn = in.next();
		
		//Checks for whether read or right was chosen
		boolean writeBool = false;
		if (writeIn.equalsIgnoreCase("write")) {
			writeBool = true;
		} else if (writeIn.equalsIgnoreCase("read")) {
			writeBool = false;
		} else {
			System.out.println("Error: Read or write not selected.");
			System.exit(1);
		}
		
		//Check for port 
		int port = 23;

		
		//Dynamically create client thread
		try {
			Thread thread = new Thread(new ClientThread(writeBool, fileNameIn, port), "Client");
			thread.start();
		} catch (IOException e) {
			System.out.println("Error: Client thread not created successfully.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//Not needed for client CommandLine
	@Override
	public void receive() {
		// TODO Auto-generated method stub
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void exit() {
		//TODO Auto-generated method stub
	}
}
