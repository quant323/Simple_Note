package com.stanislav_xyz.simplenote_2.controllers;

import android.content.Intent;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.dialogs.DeleteDialog;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

public class NoteController {

    private static final int MAX_TITLE_LENGTH = 25;

    private NoteInterface mNoteInterface;
    private FragmentActivity mActivity;
    private NoteViewModel mNoteViewModel;
    private Intent mIntent;
    private Note mNote;
    private Folder mFolder;

    // Конструктор
    public NoteController (FragmentActivity activity, NoteInterface noteInterface, Intent intent) {
        mActivity = activity;
        mNoteInterface = noteInterface;
        mIntent = intent;
        mNoteViewModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        initialisation();
    }

    private void initialisation() {
        mFolder = mIntent.getParcelableExtra(MainController.EXTRA_FOLDER);
        mNote = mIntent.getParcelableExtra(MainController.EXTRA_NOTE);
        if (mNote != null) {
            mNoteInterface.setTextEditText(mNote.getBody());
            // Превращает EditText в TextView
            mNoteInterface.setEditToEditText(false);
        } else {
            mNoteInterface.showKeyBoard(false);
            mNoteInterface.hideFab();
        }
    }

    public void onFabPressed() {
        mNoteInterface.setEditToEditText(true);
        mNoteInterface.hideFab();
        mNoteInterface.showOptionDelItem(false);
        mNoteInterface.showKeyBoard(true);
    }

    public void onMenuDelPressed() {
        if (mFolder.getId() != MainController.getBinFolder().getId()) {
            if (mNote != null) {
                new DeleteDialog(mActivity, DeleteDialog.ACTION_DELETE_NOTE,
                        new DeleteDialog.DeleteDialogListener() {
                            @Override
                            public void onDeleteConfirm() {
                                mNote.setFolderName(MainController.getBinFolder().getName());
                                mNoteViewModel.update(mNote);
                                Utils.showToast(mActivity, R.string.mes_note_moved_to_bin);
                                mActivity.finish();
                            }
                        }).show(mActivity.getSupportFragmentManager(), null);
            }
        } else {
            mNoteViewModel.deleteNote(mNote);
            Utils.showToast(mActivity, R.string.mes_note_deleted);
            mActivity.finish();
        }
    }

    public void optionsMenuCreated() {
        mNoteInterface.showOptionDelItem(mNote != null);
    }

    public void onStop(boolean requireSaving) {
        if (requireSaving) saveNote();
    }

    // Сохраняет новую, либо обновляет существующую заметку
    private void saveNote() {
        String body = mNoteInterface.getTextFromEditText();
        if (mNote != null) {
            if (!mNote.getBody().equals(body)) {
                mNoteViewModel.update(setNewValues(mNote, body));
            }
        } else {
            if (!body.equals("")) {
                mNote = new Note(body, Utils.getTitle(body, MAX_TITLE_LENGTH),
                        System.currentTimeMillis(), mFolder.getName());
                mNoteViewModel.insert(mNote);
            }
        }
    }

    private Note setNewValues(Note note, String body) {
        note.setBody(body);
        note.setTitle(Utils.getTitle(body, MAX_TITLE_LENGTH));
        note.setDate(System.currentTimeMillis());
        return note;
    }

}
