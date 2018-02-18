package Logger;

public class Filter {
    LogLevels printLogLevel;

    /**
     * @param printLogLevel
     */
    Filter(LogLevels printLogLevel) {
        this.printLogLevel = printLogLevel;
    }

    /**
     * @param level
     * @return
     */
    boolean checkLevel(LogLevels level) {
        return printLogLevel.compareTo(level) <= 0;
    }
}
