package Logger;

import java.net.DatagramPacket;

public class Logger {
    private Printer printer;
    private Filter filter;

    public Logger(LogLevels logLevel) {
        printer = new Printer();
        filter = new Filter(logLevel);
    }

    public void printPacket(LogLevels logLevel, DatagramPacket datagramPacket) {
        if (filter.checkLevel(logLevel)) {
            printer.printPacket(datagramPacket);
        }
    }

    public void print(LogLevels logLevel, String string) {
        if (filter.checkLevel(logLevel)) {
            printer.printString(string);
        }
    }
}
