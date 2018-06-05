package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Note {

    @PrimaryKey
    int id;

    @ColumnInfo( name = "text" )
    String text;

    @ColumnInfo( name = "images" )
    String[] images;

    @ColumnInfo( name = "date" )
    Date date;

}
