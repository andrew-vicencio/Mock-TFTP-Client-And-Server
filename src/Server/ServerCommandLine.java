package Server;

import java.util.Arrays;

import tools.CommandLine;

/**
 * Utilized for server input
 *
 * @author Andrew V.
 * @version 1.0
 * @since 2018
 */
public class ServerCommandLine extends CommandLine {
	private Listener listen;
	private Thread thread;
	public ServerCommandLine(Listener listen, Thread thread) {
		super("Server");
		this.listen = listen;
		this.thread = thread;
	}

	@Override
	public void interpret() {
	    if(Arrays.asList(token).contains("exit")){
	        listen.interrupt();
        }
	}

    /**
     * User helper class
     */
    @Override
	public void helpCommand() {
		print("Exit is the only command");
	}
	
}
