package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;
import com.stanislav_xyz.simplenote_2.model.Note;
import java.util.List;
import androidx.lifecycle.LiveData;

public class NoteRepository {

    private NoteDao mNoteDao;
    private LiveData<List<Note>> mAllNotes;


    // Конструктор
    public NoteRepository(Application application) {
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteDao = db.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
    }

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
}
