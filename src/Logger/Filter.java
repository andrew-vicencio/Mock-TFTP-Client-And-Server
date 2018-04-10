package Logger;

public class Filter {
    private LogLevels printLogLevel;

    /**
     * @param printLogLevel
     */
    Filter(LogLevels printLogLevel) {
        this.printLogLevel = printLogLevel;
    }

    public void setLogLevel(LogLevels logLevel) {
        printLogLevel = logLevel;
    }

    /**
     * @param level
     * @return
     */
    boolean checkLevel(LogLevels level) {
        return printLogLevel.compareTo(level) <= 0;
    }
}
