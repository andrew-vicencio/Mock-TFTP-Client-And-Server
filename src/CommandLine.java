
public class CommandLine {
	private String commands[];
	private boolean verbose;
	
	public CommandLine(String[] commands) {
		this.commands = commands;
	}
	
	public CommandLine() {
		
	}
	
	public boolean isVerbose() {
		if(verbose) {
			return true;
		}
		return false;
	}
}
