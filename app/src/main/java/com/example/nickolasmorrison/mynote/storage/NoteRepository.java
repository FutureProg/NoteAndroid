package com.example.nickolasmorrison.mynote.storage;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        noteDao = db.noteDao();
        allNotes = noteDao.getAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insert(Note note) {
        new insertAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new deleteAsyncTask(noteDao).execute(note);
    }

    private static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao asyncTaskDao;

        public deleteAsyncTask(NoteDao dao) { this.asyncTaskDao = dao;}

        @Override
        protected Void doInBackground(Note... notes) {
            asyncTaskDao.delete(notes[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao asyncTaskDao;

        public insertAsyncTask(NoteDao dao) {
            this.asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            asyncTaskDao.insertAll(notes);
            return null;
        }
    }

}
