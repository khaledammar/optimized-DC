package ca.waterloo.dsg.graphflow.util;

/**
 * This is a class to report different comments/debug notifications or error messages to standard output.
 * <p>
 * It needs to be initialized once and could be muted anytime
 */
public class Report {
    public enum Level {
        DEBUG,
        INFO,
        ERROR
    }

    public static Report INSTANCE = new Report(Level.ERROR);
    public Level appReportingLevel;

    public Report(Level l) {
        this.appReportingLevel = l;
    }

    public void setLevel(Level l) {
        appReportingLevel = l;
    }

    public void debug(String s, Object... args) {
        if (appReportingLevel == Level.DEBUG) {
            System.out.printf(s + "\n", args);
            System.out.flush();
        }
    }

    public void info(String s, Object... args) {
        if (appReportingLevel != Level.ERROR) {
            System.out.printf(s + "\n", args);
        }
    }

    public void error(String s, Object... args) {
        System.out.printf(s + "\n", args);
    }

    public void print(String s, Object... args) {
        System.out.printf(s + "\n", args);
    }
}
