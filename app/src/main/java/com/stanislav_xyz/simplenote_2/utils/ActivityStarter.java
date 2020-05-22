package com.stanislav_xyz.simplenote_2.utils;

import android.app.Activity;
import android.content.Intent;

import com.stanislav_xyz.simplenote_2.activities.NoteActivity;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;


/**
 * Запускает NoteActivity
 */
public class ActivityStarter {

    public static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";
    public static final String EXTRA_FOLDER = "NoteActivity.EXTRA_FOLDER";

//    public static void startNoteActivity(Activity activity, Note note) {
//        Intent intent = new Intent(activity, NoteActivity.class);
//        intent.putExtra(EXTRA_NOTE, note);
//        activity.startActivity(intent);
//    }
//
//    public static void startNoteActivity(Activity activity, String folderName) {
//        Intent intent = new Intent(activity, NoteActivity.class);
//        intent.putExtra(EXTRA_FOLDER, folderName);
//        activity.startActivity(intent);
//    }

    public static void startNoteActivity(Activity activity, Folder folder, Note note) {
        Intent intent = new Intent(activity, NoteActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        intent.putExtra(EXTRA_NOTE, note);
        activity.startActivity(intent);
    }

}
