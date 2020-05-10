package com.stanislav_xyz.simplenote_2.data;

import android.content.Context;
import android.os.AsyncTask;

import com.stanislav_xyz.simplenote_2.model.Note;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    private static NoteRoomDatabase INSTANCE;

    // Создаем экземпляр Database
    public static NoteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, "note_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract NoteDao noteDao();

    // Callback, который вызывается при открытии базы данных
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
//            new PopulateDbAsync(INSTANCE).execute();
        }
    };


    // Первичное заполнение базы данных
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private NoteDao mNoteDao;
        String[] titles = {"first note", "second note", "third note"};
        long[] dates = {23423, 23423423, 234324};

        PopulateDbAsync(NoteRoomDatabase db) {
            mNoteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < titles.length; i++) {
                Note note = new Note();
                note.title = titles[i];
                note.body = titles[i];
                note.date = dates[i];
                mNoteDao.insert(note);
            }
            return null;
        }
    }

}
