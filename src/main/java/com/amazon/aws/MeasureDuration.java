package com.amazon.aws;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility classs to measure any process duration by starting and finihig
 * manually.
 * Let you have a lot of process to be monitored.
 * Start by creating a new {@link MeasureDuration object and then start to
 * measure by calling the {@link #start(String)} method (to start monitoring)
 * and finally the
 * {@link #finish(String)} method (to stop monitoring), then duration can be got
 * using
 * {@link #getDuration(String)} getter.
 * 
 * <pre>
 * Example:
 * </pre>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     MeasureDuration md = new MeasureDuration();
 *     String processName = "getFactorial";
 *     md.start(processName);
 *     long factorial = getFactorial(10);
 *     Duration duration = md.getDuration(processName);
 *     String durationString = md.getDurationString(processName);
 *     System.out.println(Strinf.format("Method: %s has finished", processName));
 *     System.out.println(Strinf.format("Duration: %s (HH:MM:SS:MS))", durationString));
 * }
 * </pre>
 */
public class MeasureDuration {
    /**
     * Stores all followed processes to calculate duration.
     */
    private Map<String, ProcessInfo> processes = new HashMap<>();

    /**
     * Default constructor
     */
    public MeasureDuration() {
    }

    /**
     * Clear all monitors by deleting all elements
     */
    public void clear() {
        processes.clear();
    }

    /**
     * Start counting time for the given name of the process.
     * 
     * @param name Name of the process to calculate its duration
     * @return {@link Instant} object with the time process has started
     */
    public Instant start(String name) {
        ProcessInfo process = new ProcessInfo(name);
        processes.put(name, process);
        return process.getStart();
    }

    /**
     * Stop counting time for the given name of the process.
     * 
     * @param name Name of the process to calculate its duration
     * @return {@link Instant} object with the time process has finished
     */
    public Instant finish(String name) {
        return processes.getOrDefault(name, new ProcessInfo(name)).finish();
    }

    /**
     * Get the duretion of the given name of the process
     * 
     * @param name Name of the process to get its duration
     * @return {@link Duration} object with the calculated duration
     */
    public Duration getDuration(String name) {
        return processes.getOrDefault(name, new ProcessInfo(name)).getDuration();
    }

    /**
     * Get the duretion of the given name of the process using a custom format
     * 
     * @param name   Name of the process to get its duration
     * @param format custom format @see
     *               {@link java.lang.String#format(String, Object...)} where
     *               Object part are: hours, minutes, seconds and milliseconds
     * @return New {@link java.lang.String} object with the value of formatted
     *         duration
     */
    public String getFormattedDuration(String name, String format) {
        return processes.getOrDefault(name, new ProcessInfo(name)).getFormattedDuration(format);
    }

    /**
     * Convert duration (object of type {@link java.time.Duration}) into a
     * {@link java.lang.String} by formatting the given duration with default
     * format @see {@link com.amazon.aws.DurationFormatter#DEFAULT_DURATION_FORMAT}
     * 
     * @param name Name of the process to get its duration
     * @return New {@link java.lang.String} object with the value of formatted
     *         duration
     */
    public String getDurationString(String name) {
        return processes.getOrDefault(name, new ProcessInfo(name)).getDurationString();
    }
}
