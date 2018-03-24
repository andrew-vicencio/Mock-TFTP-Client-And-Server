package Server;

import java.util.Arrays;

import tools.CommandLine;

public class ServerCommandLine extends CommandLine {
	private Listener listen;
	public ServerCommandLine(Listener listen) {
		super("Server");
		this.listen = listen;
	}

	@Override
	public void interpret() {
	}

	@Override
	public void helpCommand() {
		print("Exit is the only command");
	}
	
}
