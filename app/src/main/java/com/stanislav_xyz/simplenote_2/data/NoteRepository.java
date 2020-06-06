package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.WorkWithSharedPref;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.LiveData;

class NoteRepository {

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
        mAllFolders = mSharedPref.getFromSharedPref();
    }

    // Работа с заметками
    LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    void insert(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void onWorkInBackground() {
                mNoteDao.insert(note);
            }
        }).execute();
    }

    void delete(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void onWorkInBackground() {
                mNoteDao.delete(note);
            }
        }).execute();
    }

    void update(final Note note) {
        new QueryAsyncTask(new QueryAsyncTask.AsyncBack() {
            @Override
            public void onWorkInBackground() {
                mNoteDao.update(note);
            }
        }).execute();
    }


    //  Работа с папками
    List<Folder> getAllFolders() {
        return getFoldersFromSharedPref();
    }

    List<Folder> insertFolder(Folder folder) {
        return insertFolderToSharedPref(folder);
    }

    List<Folder> deleteFolder(Folder folder) {
        return deleteFolderFromSharedPref(folder);
    }

    List<Folder> updateFolder(Folder folder) {
        return updateFolderInSharedPref(folder);
    }


    // Работа с SharedPreferences
    private List<Folder> getFoldersFromSharedPref() {
        return mAllFolders;
    }

    private List<Folder> insertFolderToSharedPref(Folder folder) {
        if (mAllFolders == null)
            mAllFolders = new ArrayList<>();
        mAllFolders.add(folder);
        mSharedPref.saveInSharedPref(mAllFolders);
        return mAllFolders;
    }

    private List<Folder> deleteFolderFromSharedPref(Folder folder) {
        mAllFolders.remove(folder);
        mSharedPref.saveInSharedPref(mAllFolders);
        return mAllFolders;
    }

    private List<Folder> updateFolderInSharedPref(Folder folder) {
        mAllFolders.set(mAllFolders.indexOf(folder), folder);
        mSharedPref.saveInSharedPref(mAllFolders);
        return mAllFolders;
    }

}
