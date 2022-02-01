package ca.waterloo.dsg.graphflow.util;

public class Timer {
    private static final long MILLIS_PER_SEC = 1000;
    private static final long MICROS_PER_SEC = 1000000;
    private static final long NANOS_PER_SEC = 1000000000;
    private static final long NANOS_PER_MILLI = 1000000;
    private static final long NANOS_PER_MICRO = 1000;
    private static final long MILLIS_PER_MICRO = 1000;
    private long beginTime;

    public Timer() {
        beginTime = System.nanoTime();
    }

    public static String nanoToSeconds(long durationNano) {
        var fraction = String.valueOf(durationNano % NANOS_PER_SEC);
        return String
                .format("%d.%s s", durationNano / NANOS_PER_SEC, fraction.substring(0, Math.min(6, fraction.length())));
    }

    public static String elapsedDurationString(long milliDuration) {
        var fraction = String.format("%03d", milliDuration % MILLIS_PER_SEC);
        return String.format("%d.%s s ( %d ms )", milliDuration / MILLIS_PER_SEC,
                fraction.substring(0, Math.min(6, fraction.length())), milliDuration);
    }

    public static String elapsedDurationNanoString(long nanoDuration) {
        return String.format("%.3f s ( %d ns )", nanoDuration / (double) NANOS_PER_SEC, nanoDuration);
    }

    public static String elapsedMicroToMilliString(long microDuration) {
        return String.format("%.3f s ( %.3f ms )", microDuration / (double) MICROS_PER_SEC,
                microDuration / (double) MILLIS_PER_MICRO);
    }

    public static String elapsedDurationMicroString(long microDuration) {
        var fraction = String.format("%03d", microDuration % MICROS_PER_SEC);
        return String.format("%d.%s s ( %d us )", microDuration / MICROS_PER_SEC,
                fraction.substring(0, Math.min(6, fraction.length())), microDuration);
    }

    public String elapsedDurationString() {
        return elapsedDurationString(elapsedMillis());
    }

    public long elapsedMillis() {
        return (System.nanoTime() - this.beginTime) / NANOS_PER_MILLI;
    }

    public long elapsedNanos() {
        return (System.nanoTime() - this.beginTime);
    }

    public long elapsedMicros() {
        return (System.nanoTime() - this.beginTime) / NANOS_PER_MICRO;
    }

    public String elapsedSeconds() {
        return nanoToSeconds(System.nanoTime() - this.beginTime);
    }

    public void reset() {
        this.beginTime = System.nanoTime();
    }
}
