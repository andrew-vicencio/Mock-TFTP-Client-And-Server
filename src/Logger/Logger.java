package Logger;

import java.net.DatagramPacket;

public class Logger {
    private Printer printer;
    private Filter filter;

    /**
     * @param logLevel
     */
    public Logger(LogLevels logLevel) {
        printer = new Printer();
        filter = new Filter(logLevel);
    }

    /**
     * @param logLevel
     * @param datagramPacket
     */
    public void printPacket(LogLevels logLevel, DatagramPacket datagramPacket) {
        if (filter.checkLevel(logLevel)) {
            printer.printPacket(datagramPacket);
        }
    }

    /**
     * @param logLevel
     * @param string
     */
    public void print(LogLevels logLevel, String string) {
        if (filter.checkLevel(logLevel)) {
            printer.printString(string);
        }
    }

    /**
     * @param logLevel
     * @param string
     */
    public void println(LogLevels logLevel, String string) {
        if (filter.checkLevel(logLevel)) {
            printer.printString(string + "\n");
        }
    }

    /**
     * @param logLevel
     * @param e
     */
    public void printException(LogLevels logLevel, Exception e) {
        if (filter.checkLevel(logLevel)) {
            printer.printException(e);
        }
    }
}
