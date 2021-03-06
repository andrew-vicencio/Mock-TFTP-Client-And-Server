package tools;

import Logger.LogLevels;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Logger.*;

/**
 * Used to handle and make all classes implement handling user input
 *
 * @author Andrew V.
 * @since 2018
 * @version 1.0
 */
public abstract class CommandLine extends Thread { //TODO: Does not need to extend thread
	private final boolean VERBOSE_DEFAULT = true;
	private final boolean TEST_DEFAULT = false;
	private boolean exit;
	private boolean verbose;
	private boolean test;
	protected Scanner in;
	private String name;
	private Logger l;
	protected String[] token;

    /**
	 * 
	 */
	public CommandLine(String name) {
		exit = false;
		verbose = true;
		test = true;
		in = new Scanner(new BufferedInputStream(System.in));
		this.name = name;
		l = new Logger(LogLevels.ALL);
	}
	
	public abstract void interpret();
	
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

	public void checkFlags(String str){
	    System.out.println(str);
	    Pattern p = Pattern.compile("(?:\\s(?:--|-))(\\w+)(\\s\\w+)?");
	    Matcher m = p.matcher(str);
	    while(m.find()){
	        for (int i = 0; i <= m.groupCount(); i++){
	            if (m.group(i).equalsIgnoreCase("logger")) {
                    l.setLogLevel(getStringWithinEnum(LogLevels.class, m.group(i + 1).toUpperCase()));
                }

                if (m.group(i).toUpperCase().indexOf("v") != -1){
	                toggleVerbose();
                }

                if(m.group(i).toUpperCase().indexOf("t") != -1){
	                toggleTest();
                }
            }
        }

    }

    private <E extends Enum<E>> E getStringWithinEnum(Class<E> enumType, String userInput) {
        try {
            return Enum.valueOf(enumType, userInput);
        } catch (Error error) {
            return getStringWithinEnum(enumType, userInput);
        }
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
		    //Option2
		    //Add action for after seting test to reset test type test again
			print(name + " Command line ready.");
			print("Verbose: " + verbose);
			print("Test: " + test);
			print("[--verbose], [--test], [HELP], [EXIT], or [CONTINUE]");
			String input = "";
			while(input.isEmpty()){
                input = in.nextLine();
			}
			input = input.toLowerCase();			//User input to lower case
			token = input.split("\\s");	//Input split into tokens
			
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
				exit=true;
			}
			
			interpret();
		}
		in.close();
	}
}
