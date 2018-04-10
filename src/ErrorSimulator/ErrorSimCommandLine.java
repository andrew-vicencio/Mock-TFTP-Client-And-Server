package ErrorSimulator;

import java.util.Arrays;

import tools.*;

public class ErrorSimCommandLine extends CommandLine {
	private int RRQ;
	private String packetType;
	private int packetNum;
	private int errorCode;
	private int delayAmt;
	private int currentMode;

	private final String[] types = {"data", "ack", "err", "read", "write"};
	
	public ErrorSimCommandLine() {
		super("Error Simulator");
		setErrorCode(0);
		RRQ = 0;
		packetType = null;
		errorCode = 0;
		delayAmt = 0;
		currentMode = 0;
	}

	@Override
	public void interpret() {
		System.out.print("Current mode ");
		switch(currentMode) {
			case 0:
				System.out.print("0: Normal Operations (i.e. Choosing packets to be lost/duplicated/delayed.)\n");
				break;
			case 1:
				System.out.print("1: Lose a packet.\n");
				break;
			case 2:
				System.out.print("2: Delay a packet.\n");
				break;
            case 3:
                System.out.print("3: Duplicate Packet.\n");
                break;
           /* case 4:
                System.out.print("4: Invalid TFTP packet.\n");
                break;*/
            case 5:
                System.out.print("5: Invalid transfer ID.\n");
                break;

		}
		
		print("What mode would you like to operate in? (0, 1, 2, 3, 5)");
		String mode = in.next();
		
		switch(mode) {
			case "0":
				interpretZero();
				break;
			case "1":
				interpretOne();
				break;
			case "2":
				interpretTwo();
				break;
            case "3":
                interpretThree();
                break;
            case "5":
                interpretFive();
                break;
			default:
				print("This mode is invalid. Please try again.");
				return;
		}
	}
	
	private void interpretZero() {
		currentMode = 0;
		print("Q1: Would you like to tamper with a [read] or [write] request?");
		String write = in.next();
		
		if (write.equalsIgnoreCase("read")){
			RRQ = 1;
		} else if(write.equalsIgnoreCase("write")) {
			RRQ = -1;
		} else if (write.equalsIgnoreCase("exit")) {
			System.exit(1);
		}	else {
			print("Invalid command. PLease try again.");
			return;
		}
		
		print("Q2: What packet type would you like to tamper with? (i.e. DATA, ACK, etc.");
		String type = in.next();
		
		if (!Arrays.asList(types).contains(type.toLowerCase())) {
			print("Invalid Packet type. Please try again.");
			return;
		} else if (write.equalsIgnoreCase("exit")) {
			System.exit(1);
		}
		
		print("Q3: Which packet would you like to choose? (i.e. Choosing DATA and 3 chooses the 3rd DATA packet.)");
		String num = in.next();
		
		if (write.equalsIgnoreCase("exit")) {
				System.exit(1);
		}
		
		try {
			packetNum = Integer.parseInt(num);
		} catch (Exception e) {
			packetNum = 3;
			print("Invalid delay amount. Default amount (3) will be used.");
		}
		
		print("Q4: Would you like to [lose], [delay], or [duplicate] this packet?");
		String errCode = in.next();
		
		if (errCode.equalsIgnoreCase("lose")) {
			errorCode = 1;
		} else if (errCode.equalsIgnoreCase("delay")) {
			errorCode = 2;
			print("Q5: How long would you like to delay (in seconds)?");
			String delay = in.next();
			try {
				delayAmt = Integer.parseInt(delay);
			} catch (Exception e) {
				delayAmt = 2;
				print("Invalid delay amount. Default amount (2s) will be used.");
			}
			if (delayAmt >= 5) {
				delayAmt = 2;
				print("Delay amount may be too long. File transfer may have already completed."
						+ "\nDefault amount (2s) will be used.");
			}
		} else if (errCode.equalsIgnoreCase("duplicate")) {
			errorCode = 3;
		} else if (write.equalsIgnoreCase("exit")) {
			System.exit(1);
		}

	}
	
	private void interpretOne() {
		currentMode = 1;
	}
    private void interpretTwo() {
        currentMode = 2;
    }
	
	private void interpretThree() {
        currentMode = 3;
    }
    private void interpretFour() {
        currentMode = 4;
    }
    private void interpretFive() {
        currentMode = 5;
    }


	@Override
	public void helpCommand() {
		print("Follow the prompts.");
		System.out.print("Supported modes are:\n"
				+ "0: Normal Operations.\n"
				+ "1: Lose a Packet.\n"
				+ "2: Delay a Packet."
                + "3: Duplicate a Packet"
                + "5: Invalid TFTP Packet\n");
		print("Valid commands for Q2 are: data, ack, err, read, write");
		print("Valid commands for Q3 are integers less than 5.");
	}

	public int getRRQ() {
		return RRQ;
	}

	public void setRRQ(int rRQ) {
		RRQ = rRQ;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getDelayAmt() {
		return delayAmt;
	}

	public void setDelayAmt(int delayAmt) {
		this.delayAmt = delayAmt;
	}
	
	public int getPacketNum() {
		return packetNum;
	}

	public void setPacketNum(int packetNum) {
		this.packetNum = packetNum;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

}
