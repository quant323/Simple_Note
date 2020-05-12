package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;

import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mRepository;
    private LiveData<List<Note>> mAllNotes;

    // Конструкотр
    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public LiveData<List<Note>> getNotesFromFolderLive(String folder) {
        return mRepository.getNotesFromFolderLive(folder);
    }

//    public List<Note> getNotesFromFolder (String folder) throws ExecutionException, InterruptedException {
//        return mRepository.getNotesFromFolder(folder);
//    }

    public void insert(Note note) {
        mRepository.insert(note);
    }

    public void deleteNote(Note note) {
        mRepository.delete(note);
    }

    public void update(Note note) {
        mRepository.update(note);
    }

}
