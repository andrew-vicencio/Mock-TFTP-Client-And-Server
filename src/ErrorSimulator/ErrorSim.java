package ErrorSimulator;

public class ErrorSim {
	public static void main(String[] args) {
		ErrorSimCommandLine cl = new ErrorSimCommandLine();
		ErrorSimulator errSim = new ErrorSimulator(cl);
		cl.start();
		errSim.start();
	}

}
