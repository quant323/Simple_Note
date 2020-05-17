package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;
import android.util.Log;

import com.stanislav_xyz.simplenote_2.activities.MainActivity;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.WorkWithSharedPref;

import java.util.List;
import androidx.lifecycle.LiveData;

public class NoteRepository {

    private NoteDao mNoteDao;
    private LiveData<List<Note>> mAllNotes;
    private WorkWithSharedPref mSharedPref;
    private List<Folder> mAllFolders;


    // Конструктор
    public NoteRepository(Application application) {
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteDao = db.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
        mSharedPref = new WorkWithSharedPref(application.getApplicationContext());
        mAllFolders = mSharedPref.loadFromSharedPref();
    }

    // Работа с заметками
    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public void insert(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void workInBackground() {
                mNoteDao.insert(note);
            }
        }).execute();
    }

    public void delete(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void workInBackground() {
                mNoteDao.delete(note);
            }
        }).execute();
    }

    public void update(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void workInBackground() {
                mNoteDao.update(note);
            }
        }).execute();
    }


    //  Работа с папками
    public List<Folder> getAllFolders() {
        return getFoldersFromSharedPref();
    }

    public void insertFolder(Folder folder) {
        insertFolderToSharedPref(folder);
    }

    public void deleteFolder(Folder folder) {
        deleteFolderFromSharedPref(folder);
    }

    public void updateFolder(Folder folder) {
        updateFolderInSharedPref(folder);
    }


    // Работа с SharedPreferences
    private List<Folder> getFoldersFromSharedPref() {
        return mAllFolders;
    }

    private void insertFolderToSharedPref(Folder folder) {
        mAllFolders.add(folder);
        mSharedPref.saveInSharedPref(mAllFolders);
    }

    private void deleteFolderFromSharedPref(Folder folder) {
        mAllFolders.remove(folder);
        mSharedPref.saveInSharedPref(mAllFolders);
    }

    private void updateFolderInSharedPref(Folder folder) {
        mAllFolders.set(mAllFolders.indexOf(folder), folder);
        mSharedPref.saveInSharedPref(mAllFolders);
    }

}
