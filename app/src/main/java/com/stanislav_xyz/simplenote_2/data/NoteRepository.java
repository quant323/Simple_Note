package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;
import android.os.AsyncTask;

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

    public void insert(Note note) {
        new insertAsyncTask(mNoteDao).execute(note);
    }

    public void delete(Note note) {
        new deleteAsyncTask(mNoteDao).execute(note);
    }

    public void update(Note note) {
        new updateAsyncTask(mNoteDao).execute(note);
    }


    private static class insertAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao mAsyncTaskDao;

        // Конструктор
        public insertAsyncTask(NoteDao noteDao) {
            mAsyncTaskDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mAsyncTaskDao.insert(notes[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao mAsyncTaskDao;

        // Конструктор
        public deleteAsyncTask(NoteDao noteDao) {
            mAsyncTaskDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mAsyncTaskDao.delete(notes[0]);
            return null;
        }
    }


    private static class updateAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao mAsyncTaskDao;

        // Консруктор
        public updateAsyncTask(NoteDao noteDao) {
            mAsyncTaskDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            mAsyncTaskDao.update(notes[0]);
            return null;
        }
    }

}
