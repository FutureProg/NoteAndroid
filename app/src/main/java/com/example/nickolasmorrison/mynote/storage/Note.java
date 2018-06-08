package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;
import java.util.List;

import io.reactivex.annotations.NonNull;

@Entity
@TypeConverters(TypeDBConverters.class)
public class Note {

    public Note() {
        this.date = new Date();
    }

    @PrimaryKey(autoGenerate =  true)
    @NonNull
    int id;

    @Ignore
    public int getId() {
        return id;
    }

    @Ignore
    @Override
    public String toString() {
        return "Note " + this.id + ": " + this.title;
    }


    @ColumnInfo( name = "title" )
    public String title;

    @ColumnInfo( name = "text" )
    public String text;

    @ColumnInfo( name = "images" )
    public List<String> images;



    @ColumnInfo( name = "date" )
    public Date date;


}
