package com.amazon.aws;

import java.time.Duration;

/**
 * Class to fromat objects of type {@link java.time.Duration}
 */
public class DurationFormatter {
    /**
     * Default format
     */
    public static final String DEFAULT_DURATION_FORMAT = "%d:%02d:%02d.%03d";

    /**
     * Convert duration (object of type {@link java.time.Duration}) into a
     * {@link java.lang.String} by formatting the given duration.
     * 
     * @param duration {@link Duration} object to be converted
     * @param format   Custom format @see
     *                 {@link java.lang.String#format(String, Object...)} where
     *                 Object part are: hours, minutes, seconds and milliseconds
     * @return New {@link String} object with the value of formatted
     *         duration
     */
    public static String getFormattedDuration(Duration duration, String format) {
        return String.format(format,
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                duration.toMillisPart());
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
    public static String getDurationString(Duration duration) {
        return getFormattedDuration(duration, DEFAULT_DURATION_FORMAT);
    }

}
