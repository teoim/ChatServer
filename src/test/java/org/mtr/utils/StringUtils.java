package org.mtr.utils;

public class StringUtils {

    /**
     * Strips leading and trailing '%' characters from a string.
     * If the string doesn't start and end with '%', it's returned unchanged.
     */
    public static String stripPercentMarkers(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        if (input.startsWith("%") && input.endsWith("%")) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }
}