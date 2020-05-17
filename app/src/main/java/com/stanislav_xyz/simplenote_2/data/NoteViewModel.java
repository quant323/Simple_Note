package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;

import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mRepository;
    private LiveData<List<Note>> mAllNotes;

    // Конструктор
    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNotes();
    }

    // Работа с заметками
    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public void insert(Note note) {
        mRepository.insert(note);
    }

    public void deleteNote(Note note) {
        mRepository.delete(note);
    }

    public void update(Note note) {
        mRepository.update(note);
    }


    // Работа с папками
    public List<Folder> getAllFolders() {
        return mRepository.getAllFolders();
    }

    public void insertFolder(Folder folder) {
        mRepository.insertFolder(folder);
    }

    public void deleteFolder(Folder folder) {
        mRepository.deleteFolder(folder);
    }

    public void updateFolder(Folder folder) {
        mRepository.updateFolder(folder);
    }

}
