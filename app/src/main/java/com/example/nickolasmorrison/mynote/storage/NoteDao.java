package com.example.nickolasmorrison.mynote.storage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters(TypeDBConverters.class)
public interface NoteDao {

    @Query( "SELECT * FROM note" )
    LiveData<List<Note>> getAll();

    @Query( "SELECT * FROM note WHERE id = (:noteId)" )
    LiveData<Note> getById(int noteId);

    @Query( "SELECT * FROM note WHERE date = (:date)")
    LiveData<List<Note>> getAllByDate(Date date);

    @Query( "SELECT * FROM note WHERE text LIKE (:substring)")
    LiveData<List<Note>> getAllByTextContaining(String substring);

    @Insert
    void insertAll(Note ...notes);

    @Delete
    void delete(Note note);
}
