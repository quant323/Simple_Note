package com.stanislav_xyz.simplenote_2.data;

import android.app.Application;
import android.os.AsyncTask;

import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public LiveData<List<Note>> getNotesFromFolderLive(java.lang.String folder) {
        return null;
    }

//    public List<Note> getNotesFromFolder(java.lang.String folder) throws ExecutionException, InterruptedException {
//        return new getNotesFromFolderAsync(mNoteDao).execute(folder).get();
//    }

    public void insert(Note note) {
        new insertAsyncTask(mNoteDao).execute(note);
    }

    public void delete(Note note) {
        new deleteAsyncTask(mNoteDao).execute(note);
    }

    public void update(Note note) {
        new updateAsyncTask(mNoteDao).execute(note);
    }


    private static class getNotesFromFolderAsync extends AsyncTask<String, Void, List<Note>> {
        private NoteDao mAsyncTaskDao;

        // Конструктор
        public getNotesFromFolderAsync(NoteDao noteDao) {
            mAsyncTaskDao = noteDao;
        }


        @Override
        protected List<Note> doInBackground(String... strings) {
            return mAsyncTaskDao.getNotesFromFolder(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
        }
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

        // Конструктор
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
