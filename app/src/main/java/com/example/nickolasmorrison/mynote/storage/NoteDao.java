package com.example.nickolasmorrison.mynote.storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface NoteDao {

    @Query( "SELECT * FROM note" )
    LiveData<List<Note>> getAll();

    @Query( "SELECT * FROM note WHERE id in (:noteIds)" )
    List<Note> getAllById(int[] noteIds);

    @Query( "SELECT * FROM note WHERE date = (:date)")
    List<Note> getAllByDate(Date date);

    @Query( "SELECT * FROM note WHERE text LIKE (:substring)")
    List<Note> getAllByTextContaining(String substring);

    @Insert
    void insertAll(Note ...notes);

    @Delete
    void delete(Note note);


}
