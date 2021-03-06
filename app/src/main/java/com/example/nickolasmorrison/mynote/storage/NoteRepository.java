package com.example.nickolasmorrison.mynote.storage;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public static final int QUERY_TYPE_BY_ID = 0, QUERY_TYPE_BY_DATE = 1;

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

    public void update(Note... note) { new updateAsyncTask(noteDao).execute(note);}

    public LiveData<Note> getById(int id){
        return noteDao.getById(id);
    }

    private static class updateAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao dao;
        public updateAsyncTask(NoteDao dao){ this.dao = dao;}

        @Override
        protected Void doInBackground(Note... notes) {
            dao.updateNotes(notes);
            return null;
        }
    }

//    private static class getByQuery extends AsyncTask<Object, Void,  LiveData<List<Note>> > {
//
//        private NoteDao dao;
//        final int queryType;
//
//        public getByQuery(NoteDao dao, int queryType){
//            this.dao = dao;
//            this.queryType = queryType;
//        }
//
//        @Override
//        protected  LiveData<List<Note>>  doInBackground(Object... data) {
//            switch (queryType) {
//                case QUERY_TYPE_BY_ID:
//                return dao.getAllById(new int[]{(int) data[0]});
//            }
//            return null;
//        }
//    }

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
