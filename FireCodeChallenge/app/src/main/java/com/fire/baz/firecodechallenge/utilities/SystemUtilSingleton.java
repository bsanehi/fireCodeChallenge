package com.fire.baz.firecodechallenge.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtilSingleton {

    private static SystemUtilSingleton instance = null;

    private SystemUtilSingleton() { // private constructor
        // Exists only to defeat instantiation.
    }

    public static synchronized SystemUtilSingleton getInstance() {

        if (instance == null) {
            instance = new SystemUtilSingleton();
        }
        return instance;
    }


    public static String getDate(long milliSeconds) {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm aa");

        Date date = new Date(milliSeconds);

        return sdf.format(date);

    }


    public String getDate() {
        return cal_weekday() + " " + cal_month_day() + ", " + cal_month();
    }


    public String cal_weekday() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        Date d = new Date();

        return sdf.format(d);

    }


    public String cal_month() {

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        Date d = new Date();

        return sdf.format(d);

    }

    public String cal_month_day() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd");

        Date d = new Date();

        return sdf.format(d);

    }

}