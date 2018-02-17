package tools;

import java.io.*;
import java.util.*;

public abstract class CommandLine extends Thread { //TODO: Does not need to extend thread
	private boolean exit;
	private boolean verbose;
	private boolean test;
	protected Scanner in;
	
	public CommandLine() {
		exit = false;
		verbose = true;
		test = true;
		in = new Scanner(new BufferedInputStream(System.in));
	}
	
	
	/** The next four abstract methods are for each component -
	 *  client, error simulator, or server - to create their own implementations.
	 */
	public abstract void send();
	
	public abstract void receive();
	
	public abstract void print(); //TODO: Change to a synchronized method so that only one thread can run it at a time

	/**
	 * isTest checks if the test has been toggled on
	 * 
	 * @return boolean
	 */
	public boolean isTest() {
		return test;
	}
	
	/**
	 * toggleTest will toggle test on or off
	 * 
	 * @return boolean
	 */
	public boolean toggleTest() {
		test = !test;
		return test;
	}
	
	/**
	 * isVerbose checks if the user has been toggled on
	 * 
	 * @return
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
	/**
	 * toggleVerbose will toggle test on or off
	 * 
	 * @return
	 */
	public boolean toggleVerbose() {
		verbose = !verbose;
		return verbose;
	}
	
	/**
	 * run is the main logic
	 */
	public void run() { //TODO: Make quit handel exiting server and
        //TODO: Make this class for all mains for exiting
		while(!exit) {
			System.out.println("What would you like to do?");
			String input = in.next();				//User input
			String[] token = input.split("\\s");	//Input split into tokens
			int argSize = token.length;				//Amount of arguments in command
			
			if (argSize > 3) { 						//For now, max 3 arguments (send/receive, verbose toggle, test toggle)
				System.out.println("Error: Too many arguments in command.");
				System.exit(1);
			} else if (argSize <= 0) { 				//Must have at least one argument
				System.out.println("Error: too few arguments in command.");
				System.exit(1);
			}
			
			if (argSize > 1) { 						//Check for test and verbose only if there are more than one arguments
				//Check for verbose toggle
				if (token[1].contentEquals("-v") || token[2].contentEquals("-v")) {
					toggleVerbose();
					System.out.print("\nVerbose mode is:");
					if (verbose) {
						System.out.print("On.\n");
					} else {
						System.out.println("Off.\n");
					}
				}
				
				//Check for test toggle
				if (token[1].contentEquals("-t") || token[2].contentEquals("-t")) {
					toggleTest();
					System.out.print("\nTest mode is:");
					if (test) {
						System.out.print("On.\n");
					} else {
						System.out.println("Off.\n");
					}
				}
			}
			
			//Check for send or receive, only commands for now
			if (token[0].equalsIgnoreCase("send")) {
				send();
			} else if (token[0].equalsIgnoreCase("receive")) {
				receive();
			} else if(token[0].equalsIgnoreCase("quit")) {
			    System.exit(1);
            }else{
				System.out.println("Error: There is no such command. Please type send, receive, or quit.");
			}
		}
		in.close();
	}
}
