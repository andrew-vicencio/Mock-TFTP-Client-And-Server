package tools;

import java.io.*;
import java.util.*;

public abstract class CommandLine extends Thread { //TODO: Does not need to extend thread
	private final boolean VERBOSE_DEFAULT = true;
	private final boolean TEST_DEFAULT = true;
	private boolean exit;
	private boolean verbose;
	private boolean test;
	protected Scanner in;
	private String name;
	
	/**
	 * 
	 */
	public CommandLine(String name) {
		exit = false;
		verbose = true;
		test = true;
		in = new Scanner(new BufferedInputStream(System.in));
		this.name = name;
	}
	
	public abstract void interpret(String[] cmds);
	
	public abstract void helpCommand();

	/**
	 * isTest checks if the test has been toggled on
	 * 
	 * @return boolean
	 */
	/**
	 * @return
	 */
	public boolean isTest() {
		return test;
	}
	
	/**
	 * toggleTest will toggle test on or off
	 * 
	 * @return boolean
	 */
	/**
	 * @return
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
	/**
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
	/**
	 * @return
	 */
	public boolean toggleVerbose() {
		verbose = !verbose;
		return verbose;
	}
	
	/**
	 * @param str
	 */
	public synchronized void print(String str) {
		System.out.println(str);
	}
	
	/**
	 * run is the main logic
	 */
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() { //TODO: Make quit handel exiting server and
        //TODO: Make this class for all mains for exiting
		while(!exit) {
			print(name + " Command line ready.");
			print("Verbose: " + verbose);
			print("Test: " + test);
			print("What would you like to do?");
			String input = in.nextLine();				//User input
			input = input.toLowerCase();			//User input to lower case
			print(input);
			String[] token = input.split("\\s");	//Input split into tokens
//			print(token.toString());
			int argSize = token.length;				//Amount of arguments in command
			
			print(Integer.toString(argSize));
			
			//Checks for verbose flag change
			if (Arrays.asList(token).contains("--verbose") || Arrays.asList(token).contains("-v")) {
				toggleVerbose();
			}
			
			//Checks for test flag change
			if (Arrays.asList(token).contains("--test") || Arrays.asList(token).contains("-t")) {
				toggleTest();
			}
			
			//Checks for help command
			if (Arrays.asList(token).contains("help")) {
				helpCommand();
			}
			
			if (Arrays.asList(token).contains("exit")) {
				System.exit(1);
			}
			
			interpret(token);
			test = TEST_DEFAULT;
			verbose = VERBOSE_DEFAULT;
		}
		in.close();
	}
}
