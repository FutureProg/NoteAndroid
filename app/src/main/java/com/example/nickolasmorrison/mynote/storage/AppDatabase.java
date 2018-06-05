package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.nickolasmorrison.mynote.Constants;

@Database(entities = {Note.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase instance;

    public abstract  NoteDao noteDao();

    public static AppDatabase getDatabase(final Context context) {
        if(instance == null){
            synchronized (AppDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context,AppDatabase.class,
                            Constants.DatabaseName).build();
                }
            }
        }
        return instance;
    }
}
