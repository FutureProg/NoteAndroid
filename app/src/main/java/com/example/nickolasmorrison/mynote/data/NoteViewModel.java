package com.example.nickolasmorrison.mynote.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.nickolasmorrison.mynote.storage.NoteRepository;
import com.example.nickolasmorrison.mynote.storage.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel{

    NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public LiveData<Note> getNoteByid(int id) {
        return repository.getById(id);
    }

    public void update(Note... note) { repository.update(note);}

    public void delete(Note note) { repository.delete(note); }
}
