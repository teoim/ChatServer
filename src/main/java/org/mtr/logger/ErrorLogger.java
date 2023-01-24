package org.mtr.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ErrorLogger {
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    static LocalDateTime now;

    static Date date;
    public static void log(Exception e, String className, String methodName) {
        //now = LocalDateTime.now();
        date = new java.util.Date();

        System.err.println( date + ": " + className + " - " + methodName + " error: " + e.toString());
    }
}
