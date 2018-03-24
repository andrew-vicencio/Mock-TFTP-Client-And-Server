package client;

import java.io.IOException;

import tools.CommandLine;

public class ClientCommandLine extends CommandLine {
    /*
     * Public constructor for Client Command Line
     */
    public ClientCommandLine() {
        super("Client");
    }

	@Override
	public void interpret() {
		boolean writeBool = false;
		print("Would you like to read or write?");
		String write = in.next();
		
		if(write.equals("write")) {
			writeBool = true;
		} else if (write.equalsIgnoreCase("exit")) {
			System.exit(1);
		}
		
		print("What file would you like to " + write + "?");
		String fileNameIn = in.next();
		
		if (write.equalsIgnoreCase("exit")) {
				System.exit(1);
		}

		int port = 69;
		if (isTest()) {
			port = 23;
		}
			
        try {
            Thread thread = new Thread(new ClientThread(writeBool, fileNameIn, port), "Client");
            thread.start();
        } catch (IOException e) {
            print("Error: Client thread not created successfully.");
            e.printStackTrace();
            System.exit(1);
        }
	}

	@Override
	public void helpCommand() {
		print("Follow the prompts.");
	}
}
