package org.mtr.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MessageLogger {
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    static LocalDateTime now;

    static Date date;

    public static void log(String msg){
        //now = LocalDateTime.now();
        date = new java.util.Date();

        System.out.println( date + ": " + msg);
    }
}
