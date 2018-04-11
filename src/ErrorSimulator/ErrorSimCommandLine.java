package ErrorSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.*;

import static java.lang.Integer.parseInt;

public class ErrorSimCommandLine extends CommandLine {
    private List<PacketError> errors;

    public ErrorSimCommandLine() {
        super("Error Simulator");
        errors = new ArrayList<>();
    }

    public List<PacketError> getErrors() {
        return errors;
    }

    private <E extends Enum<E>> E getInputWithinEnum(Class<E> enumType) {
        String userInput = in.next();

        try {
            return Enum.valueOf(enumType, userInput);
        } catch (IllegalArgumentException error) {
            return getInputWithinEnum(enumType);
        }
    }

    private int getInputInt() {
        String userInput = in.next();

        try {
            return parseInt(userInput);
        } catch (NumberFormatException error) {
            print("Please enter a valid number");
            return getInputInt();
        }
    }

    private char getInputChar() {
        String userInput = in.next();

        try {
            int inputNum = parseInt(userInput);
            if (inputNum < 0 || inputNum > 255) {
                print("Number is outside of bounds for a byte");
                return getInputChar();
            }

            return (char) inputNum;
        } catch (NumberFormatException error) {
            print("Please enter a valid number");
            return getInputChar();
        }
    }

    @Override
    public void interpret() {
        PacketError packetError = new PacketError();

        print("What packet type would you like to modify?");
        print("One of " + Stream.of(PacketType.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        packetError.setPacketType(getInputWithinEnum(PacketType.class));

        print("What would you like to do to it? ");
        print("One of " + Stream.of(ErrorOperation.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        packetError.setPacketOperation(getInputWithinEnum(ErrorOperation.class));

        print("How many packets of the given type should we ignore before we modify one?");
        packetError.setPacketIndex(getInputInt());

        if (packetError.getPacketOperation().equals(ErrorOperation.DELAY)) {
            print("How Long should we delay the packet? (ms)");
            packetError.setPacketDelay(getInputInt());
        }

        if (packetError.getPacketOperation().equals(ErrorOperation.MODIFY)) {
            print("How would you like to modify a byte within the packet? ");
            print("One of " + Stream.of(PacketModification.values()).map(Enum::toString).collect(Collectors.joining(", ")));
            packetError.setPacketModification(getInputWithinEnum(PacketModification.class));

            print("How far into the packet would you like to modify a byte? ");
            packetError.setPacketModificationIndex(getInputInt());

            if (packetError.getPacketModification().equals(PacketModification.EDIT)) {
                print("What would you like the new data to be? ");
                packetError.setNewData(getInputChar());
            }
        }

        errors.add(packetError);
    }

    @Override
    public void helpCommand() {
        print("Follow the prompts.");
    }

}
