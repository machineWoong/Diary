package com.example.jeon.diary;

import java.io.Serializable;

/**
 * Created by JEON on 2018-01-27.
 */

public class AlaramContent implements Serializable {

    int year;
    int month;
    int day;
    int hour;
    int min;
    String message;

    public AlaramContent(int year, int month, int day, int hour, int min, String message){
        this.year = year;
        this.month =month;
        this.day =day;
        this.hour = hour;
        this.min = min;
        this.message =message;
    }



}
