package com.amazon.aws;

import java.time.Duration;
import java.time.Instant;

/**
 * It store information of each piece of code to be measured (process, method,
 * snippet, etc.)
 */
public class ProcessInfo {
    private String name;
    private Instant start;
    private Instant end;

    /**
     * Constructor with the process name to be measured by starting its start and
     * end.
     * 
     * Once you create a new object by giving process name it automatically stores
     * the start time.
     * 
     * @param name Name of the process
     */
    public ProcessInfo(String name) {
        this.name = name;
        this.start = Instant.now();
    }

    /**
     * Once pprocess finish call this method to store its end time.
     * 
     * @return {@link Instant} with the end process time.
     */
    public Instant finish() {
        this.end = Instant.now();
        return this.end;
    }

    /**
     * Returns the start time.
     * 
     * @return {@link Instant} object with the start time
     */
    public Instant getStart() {
        return start;
    }

    /**
     * Returns the end time.
     * 
     * @return {@link Instant} object with the end time
     */
    public Instant getEnd() {
        return end;
    }

    /**
     * Return the name of the process.
     * 
     * @return {@link String} with the name of the process
     */
    public String getName() {
        return name;
    }

    /**
     * Calculate duration of the process as duration = end - start.
     * 
     * @return {@link Duration} object with the calculated duration
     */
    public Duration getDuration() {
        return Duration.between(start, end);
    }

    /**
     * Calculate duration of the process as duration = end - start.
     * 
     * @param format Custom format @see
     *               {@link java.lang.String#format(String, Object...)} where
     *               Object part are: hours, minutes, seconds and milliseconds
     * @return New {@link String} object with the value of formatted
     *         duration
     */
    public String getFormattedDuration(String format) {
        return DurationFormatter.getFormattedDuration(getDuration(), format);
    }

    /**
     * Convert duration (object of type {@link java.time.Duration}) into a
     * {@link java.lang.String} by formatting the given duration with default
     * format @see {@link com.amazon.aws.DurationFormatter#DEFAULT_DURATION_FORMAT}
     * 
     * @param duration {@link Duration} object to be converted
     * @return New {@link String} object with the value of formatted
     *         durationS
     */
    public String getDurationString() {
        return DurationFormatter.getDurationString(getDuration());
    }
}