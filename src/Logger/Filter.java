package Logger;

public class Filter {
    LogLevels printLogLevel;

    Filter(LogLevels printLogLevel) {
        this.printLogLevel = printLogLevel;
    }

    boolean checkLevel(LogLevels level) {
        return printLogLevel.compareTo(level) >= 0;
    }
}
