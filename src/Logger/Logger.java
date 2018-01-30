package Logger;

import java.net.DatagramPacket;

public class Logger {
    private Logger.Printer printer;
    private Filter filter;

    public Logger(Logger.LogLevels logLevel) {
        printer = new Logger.Printer();
        filter = new Filter(logLevel);
    }

    public void printPacket(Logger.LogLevels logLevel, DatagramPacket datagramPacket) {
        if (filter.checkLevel(logLevel)) {
            printer.printPacket(datagramPacket);
        }
    }

    public void print(Logger.LogLevels logLevel, String string) {
        if (filter.checkLevel(logLevel)) {
            printer.printString(string);
        }
    }
}
