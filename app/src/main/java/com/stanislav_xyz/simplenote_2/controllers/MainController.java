package com.stanislav_xyz.simplenote_2.controllers;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.dialogs.AboutDialog;
import com.stanislav_xyz.simplenote_2.dialogs.DeleteDialog;
import com.stanislav_xyz.simplenote_2.dialogs.MoveNoteDialog;
import com.stanislav_xyz.simplenote_2.dialogs.NewFolderDialog;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.ActivityStarter;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainController {

    private static final String TAG = "myTag";
    public static final int INITIAL_FOLDER_ID = 0;

    private MainInterface mMainInterface;
    private FragmentActivity mActivity;

    private NoteViewModel mNoteViewModel;

    private List<Note> mNotes;
    private List<Note> mNotesInCurFolder = new ArrayList<>();
    private List<Folder> mFolderList;
    private Folder mCurFolder;
    private Folder mBinFolder;

    // Конструктор
    public MainController(FragmentActivity activity, MainInterface mainInterface) {
        mActivity = activity;
        mMainInterface = mainInterface;
        mNoteViewModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        initFolderList();
        initDrawerMenu();
        mBinFolder = new Folder(mActivity.getString(R.string.nav_bin), 0, R.id.nav_bin);
        mNoteViewModel.getAllNotes().observe(activity, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mNotes = notes;
                mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
                mMainInterface.updateNoteResView(mNotesInCurFolder);
                mMainInterface.setToolbarTitle(mCurFolder.getName());
            }
        });
    }

    private void initFolderList() {
        mFolderList = mNoteViewModel.getAllFolders();
        if (mFolderList == null) {
            Folder folder = new Folder(mActivity.getString(R.string.default_folder_name),
                    System.currentTimeMillis(), INITIAL_FOLDER_ID);
            mNoteViewModel.insertFolder(folder);
            mFolderList = mNoteViewModel.getAllFolders();
        }
        mCurFolder = mFolderList.get(0);
 //       mMainInterface.setToolbarTitle(mCurFolder.getName());
    }

    private void initDrawerMenu() {
        for (int i = 0; i < mFolderList.size(); i++) {
            if (i != 0) {
                mMainInterface.addDrawerMenuItem(mFolderList.get(i), R.drawable.ic_folder);
            } else {
                mMainInterface.addDrawerMenuItem(mFolderList.get(i), R.drawable.ic_home);
                mMainInterface.setCheckedDrawerMenuItem(mFolderList.get(i));
            }
        }
    }

    public void onFabPressed() {
        ActivityStarter.startNoteActivity(mActivity, mCurFolder.getName());
    }

    public void onMenuDelPressed() {
        if (mNotesInCurFolder.size() < 1) {
            deleteFolder(mCurFolder);
        } else {
            mMainInterface.showToast(R.string.mes_folder_is_not_empty);
        }
    }

    public void onMenuRenamePressed() {
        String message = mActivity.getString(R.string.action_rename_folder);
        new NewFolderDialog(mActivity, message, mCurFolder.getName(),
                new NewFolderDialog.FolderDialogListener() {
                    @Override
                    public void onFolderConfirm(String name) {
                        renameFolder(name, mCurFolder);
                    }
                }).show(mActivity.getSupportFragmentManager(), null);
    }

    public void onMenuCleanBinPressed() {
        if (mNotesInCurFolder.size() > 0) {
            new DeleteDialog(mActivity, DeleteDialog.ACTION_EMPTY_BIN, new DeleteDialog.DeleteDialogListener() {
                @Override
                public void onDeleteConfirm() {
                    if (mCurFolder == mBinFolder)
                        for (Note note : mNotesInCurFolder)
                            mNoteViewModel.deleteNote(note);
                    mMainInterface.showSnack(R.string.mes_bin_is_empty);
                }
            }).show(mActivity.getSupportFragmentManager(), null);
        } else {
            mMainInterface.showSnack(R.string.mes_bin_is_empty);
        }
    }

    public void onContextOpenPressed() {
        ActivityStarter.startNoteActivity(mActivity, mCurFolder.getName());
    }

    public void onContextDelPressed(final int position) {
        if (mCurFolder != mBinFolder) {
            new DeleteDialog(mActivity, DeleteDialog.ACTION_DELETE_NOTE,
                    new DeleteDialog.DeleteDialogListener() {
                        @Override
                        public void onDeleteConfirm() {
                            Note note = mNotesInCurFolder.get(position);
                            note.setFolder(mBinFolder.getName());
                            mNoteViewModel.update(note);
                            mMainInterface.showSnack(R.string.mes_note_moved_to_bin);
                        }
                    }).show(mActivity.getSupportFragmentManager(), null);
        } else {
            mNoteViewModel.deleteNote(mNotesInCurFolder.get(position));
            mMainInterface.showSnack(R.string.mes_note_deleted);
        }
    }

    public void onContextMovePressed(final int position) {
        new MoveNoteDialog(mActivity, Utils.getFolderNames(mFolderList),
                new MoveNoteDialog.MoveDialogListener() {
                    @Override
                    public void onMoveConfirmed(int folderIndex) {
                        moveNote(folderIndex, position);
                    }
                }).show(mActivity.getSupportFragmentManager(), null);
    }

    public void onNavBinPressed() {
        mCurFolder = mBinFolder;
        mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
    }

    public void onNavSettingsPressed() {
        mMainInterface.showToast("Settings folder is pressed!");
        mMainInterface.setToolbarTitle(mActivity.getString(R.string.nav_settings));
    }

    public void onNavAboutPressed() {
        new AboutDialog(mActivity).show(mActivity.getSupportFragmentManager(), null);
    }

    public void onNavAddFolderPressed() {
        String message = mActivity.getString(R.string.nav_add_new_folder);
        new NewFolderDialog(mActivity, message, new NewFolderDialog.FolderDialogListener() {
            @Override
            public void onFolderConfirm(String name) {
                createNewFolder(name);
            }
        }).show(mActivity.getSupportFragmentManager(), null);
    }

    public void onNavNormalFolderPressed(int id) {
        mCurFolder = Utils.getPressedFolder(id, mFolderList);
        mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
        mMainInterface.enableDelMenu(mCurFolder.getId() != INITIAL_FOLDER_ID);
    }

    private void createNewFolder(String name) {
        // Находим папку, находящуюся в списке последней
        Folder lastFolder = mFolderList.get(mFolderList.size() - 1);
        // Создаем новую папку. В качестве id указываем id последней папки, увеличенную на 1
        mCurFolder = new Folder(name, System.currentTimeMillis(), (lastFolder.getId() + 1));
        mNoteViewModel.insertFolder(mCurFolder);
        mFolderList = mNoteViewModel.getAllFolders();
 //       mNotesInCurFolder.clear();
        mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
        mMainInterface.addDrawerMenuItem(mCurFolder, R.drawable.ic_folder);
        mMainInterface.setCheckedDrawerMenuItem(mCurFolder);
        mMainInterface.enableDelMenu(mCurFolder.getId() != INITIAL_FOLDER_ID);
    }

    private void deleteFolder(Folder folder) {
        mNoteViewModel.deleteFolder(folder);
        mFolderList = mNoteViewModel.getAllFolders();
        mMainInterface.deleteDrawerMenuItem(folder);
        mCurFolder = mFolderList.get(0);
        mMainInterface.setCheckedDrawerMenuItem(mCurFolder);
        mMainInterface.enableDelMenu(mCurFolder.getId() != INITIAL_FOLDER_ID);
        mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
    }

    private void renameFolder(String name, Folder folder) {
        folder.setName(name);
        mNoteViewModel.updateFolder(folder);
        mFolderList = mNoteViewModel.getAllFolders();
        // Обновляем Notes в БД
        for (Note note : mNotesInCurFolder) {
            note.setFolder(folder.getName());
            mNoteViewModel.update(note);
        }
        mMainInterface.renameDrawerMenuItem(folder);
        mMainInterface.setToolbarTitle(folder.getName());
    }

    // Перемещает заметку из одной папки в другую
    private void moveNote(int folderIndex, int notePosition) {
        Folder folder = mFolderList.get(folderIndex);
        Note note = mNotesInCurFolder.get(notePosition);
        note.setFolder(folder.getName());
        mNoteViewModel.update(note);
    }

}
