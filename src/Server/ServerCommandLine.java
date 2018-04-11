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
	public ServerCommandLine(Listener listen) {
		super("Server");
		this.listen = listen;
	}

    /**
     * Not needed, used to interpret input for specific operations
     */
    @Override
	public void interpret() {
	}

    /**
     * User helper class
     */
    @Override
	public void helpCommand() {
		print("Exit is the only command");
	}
	
}
