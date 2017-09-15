package com.mrerror.tm.models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kareem on 9/15/2017.
 */

public class TimeAndDate {
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MMM yyyy");


    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long _2DAYS_MILLIS=48*HOUR_MILLIS;



    public static String timeHandeler(String dateFromJson){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date="";
        try {
            Date mydate = sdf.parse(dateFromJson);
            long millis = mydate.getTime();
            date=currentDate(millis);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return date;

    }

    private static String currentDate(Long timeInMs) {
        long dateMillis = timeInMs;
        String date = "";
        long now = System.currentTimeMillis();

        // Change how the date is displayed depending on whether it was written in the last minute,
        // the hour, etc.
        if (now - dateMillis < (DAY_MILLIS)) {
            if (now - dateMillis < (HOUR_MILLIS)) {
                long minutes = Math.round((now - dateMillis) / MINUTE_MILLIS);
                date = String.valueOf(minutes) + "m";
            } else {
                long minutes = Math.round((now - dateMillis) / HOUR_MILLIS);
                date = String.valueOf(minutes) + "h";
            }
        }else if(now -dateMillis <(_2DAYS_MILLIS)){
            date="Yesterday";
        }
        else {
            Date dateDate = new Date(dateMillis);
            date = sDateFormat.format(dateDate);
        }


        return date;
    }


}
