package com.example.nickolasmorrison.mynote.storage;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class AppRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    AppRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        noteDao = db.noteDao();
        allNotes = noteDao.getAll();
    }

}
