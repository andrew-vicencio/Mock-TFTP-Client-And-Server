package Logger;

public class Filter {
    Logger.LogLevels printLogLevel;

    Filter(Logger.LogLevels printLogLevel) {
        this.printLogLevel = printLogLevel;
    }

    boolean checkLevel(Logger.LogLevels level) {
        return printLogLevel.compareTo(level) >= 0;
    }
}
