package Server;

import java.util.Arrays;

import tools.CommandLine;

public class ServerCommandLine extends CommandLine {
	private Listener listen;
	private Thread thread;
	public ServerCommandLine(Listener listen, Thread thread) {
		super("Server");
		this.listen = listen;
		this.thread = thread;
	}

	@Override
	public void interpret(String[] tokens) {
	    if(Arrays.asList(tokens).contains("exit")){
	        listen.interrupt();
        }
	}

	@Override
	public void helpCommand() {
		print("Exit is the only command");
	}
	
}
