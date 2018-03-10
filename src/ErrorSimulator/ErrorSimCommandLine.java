package ErrorSimulator;

import java.util.Arrays;

import tools.*;

public class ErrorSimCommandLine extends CommandLine {
	private int RRQ;
	private String packetType;
	private int errorCode;
	private int delayAmt;
	
	public ErrorSimCommandLine() {
		super("Error Simulator");
		setErrorCode(0);
		RRQ = 0;
		packetType = null;
		errorCode = 0;
		delayAmt = 0;
	}

	@Override
	public void interpret(String[] cmds) {
		if (Arrays.asList(cmds).contains("rrq")) {
			RRQ = 1;
		}
		
		if (Arrays.asList(cmds).contains("wrq")) {
			RRQ = -1;
		}
		
		packetType = cmds[1];
		
		if (Arrays.asList(cmds).contains("lose")) { //Check for lose
			errorCode = 1;
		} else if (Arrays.asList(cmds).contains("delay")) { //Check for delay
			errorCode = 2;
			if (cmds.length >= 5) { //Check for delay amount
				try {
					delayAmt = Integer.parseInt(cmds[4]);
				} catch (Exception e) {
					print("Invalid delay amount. Default amount (2s) will be used");
				}
			} else {
				delayAmt = 2;
			}
		} else if (Arrays.asList(cmds).contains("duplicate")) { //Check for duplicate
			errorCode = 3;
		}
	}

	@Override
	public void helpCommand() {
		print("RRQ <Packet Type> <Packet#> <lose/delay/duplicate> [delay amount]\n"
				+ "RRQ tells the error simulator to simulate an error from a read request\n\n"
				+ "\tPacket Type \t The type of packet (i.e. DATA, ACK, etc)\n"
				+ "\tPacket# \t The nth packet of the specified type (i.e. Packet# is 3. Packet Type is DATA. Select 3rd DATA packet.)\n"
				+ "\tlose \t Lose the specified packet.\n"
				+ "\tdelay \t Delay the specified packet.\n"
				+ "\tduplicate \t Duplicate the specified packet.\n"
				+ "\tdelay amount \t Amount of seconds the specified packet will be delayed.\n\n");
		print("WRQ <Packet Type> <Packet#> <lose/delay/duplicate> [delay amount]\n"
				+ "WRQ tells the error simulator to simulate an error from a read request\n\n"
				+ "\tPacket Type \t The type of packet (i.e. DATA, ACK)\n"
				+ "\tPacket# \t The nth packet of the specified type (i.e. Packet# is 3. Packet Type is DATA. Select 3rd DATA packet.)\n"
				+ "\tlose \t Lose the specified packet.\n"
				+ "\tdelay \t Delay the specified packet.\n"
				+ "\tduplicate \t Duplicate the specified packet.\n"
				+ "\tdelay amount \t Amount of seconds the specified packet will be delayed.\n");
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
}
