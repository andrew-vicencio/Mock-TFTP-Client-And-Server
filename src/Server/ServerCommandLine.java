package Server;

import java.util.Arrays;

import tools.CommandLine;

public class ServerCommandLine extends CommandLine {
	public ServerCommandLine() {
		super("Server");
	}

	@Override
	public void interpret(String[] cmds) {
		if (Arrays.asList(cmds).contains("read")) {
			helpCommand();
		}
		
		if (Arrays.asList(cmds).contains("write")) {
			helpCommand();
		}
	}

	@Override
	public void helpCommand() {
		print("Exit is the only command");
	}
	
}
