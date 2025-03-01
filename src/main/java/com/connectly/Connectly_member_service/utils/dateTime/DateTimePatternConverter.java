package com.connectly.Connectly_member_service.utils.dateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateTimePatternConverter {

    public static String getCurrentDate(){
        Date date = new Date();
        String str = date.toString();
        LocalDateTime parse = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return parse.format(dateTimeFormatter);
    }
}
