package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.TypeConverter;

import com.example.nickolasmorrison.mynote.Constants;

import java.util.Date;

public class TypeDBConverters {

    @TypeConverter
    public static Long fromDate(Date date) {
        if(date == null){
            return null;
        }
        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long millis) {
        if(millis == null){
            return null;
        }
        return new Date(millis);
    }

    @TypeConverter
    public static String fromStringArray(String[] array) {
        String re = "";
        for(String s : array) {
            re += s + Constants.StringDelim;
        }
        return re.substring(0,re.length()-Constants.StringDelim.length());
    }

    @TypeConverter
    public static String[] toStringArray(String str) {
        return str.split(Constants.StringDelim);
    }

}
