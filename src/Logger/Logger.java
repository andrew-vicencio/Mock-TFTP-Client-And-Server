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

    public void println(LogLevels logLevel, String string) {
        if (filter.checkLevel(logLevel)) {
            printer.printString(string + "\n");
        }
    }

    public void printException(LogLevels logLevel, Exception e) {
        if (filter.checkLevel(logLevel)) {
            printer.printException(e);
        }
    }
}
