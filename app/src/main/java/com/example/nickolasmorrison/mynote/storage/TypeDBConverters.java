package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.example.nickolasmorrison.mynote.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        if(array == null) return null;
        String re = "";
        for(String s : array) {
            re += s + Constants.StringDelim;
        }
        return re.substring(0,re.length()-Constants.StringDelim.length());
    }

    @TypeConverter
    public static String[] toStringArray(String str) {
        if(str == null) return null;
        return str.split(Constants.StringDelim);
    }

    @TypeConverter
    public static String fromStringListToString(List<String> list) {
        if(list == null) return null;
        String re = "";
        for(String s : list) {
            re += s + Constants.StringDelim;
        }
        return re.substring(0,re.length()-Constants.StringDelim.length());
    }

    @TypeConverter
    public static List<String> fromStringToStringList(String str) {
        if(str == null) return null;
        return new ArrayList<>(Arrays.asList(str.split(Constants.StringDelim)));
    }


}
