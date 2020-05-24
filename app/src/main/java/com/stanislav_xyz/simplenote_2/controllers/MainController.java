package com.stanislav_xyz.simplenote_2.controllers;

import android.app.Activity;
import android.content.Intent;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.activities.NoteActivity;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.dialogs.AboutDialog;
import com.stanislav_xyz.simplenote_2.dialogs.DeleteDialog;
import com.stanislav_xyz.simplenote_2.dialogs.MoveNoteDialog;
import com.stanislav_xyz.simplenote_2.dialogs.NewFolderDialog;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainController {

    private static final String TAG = "myTag";
    public static final int INITIAL_FOLDER_ID = 0;
    public static final String EXTRA_FOLDER = "NoteActivity.EXTRA_FOLDER";
    public static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";

    private MainInterface mMainInterface;
    private FragmentActivity mActivity;
    private NoteViewModel mNoteViewModel;

    private List<Note> mNotes;
    private List<Note> mNotesInCurFolder = new ArrayList<>();
    private List<Folder> mFolderList;
    private Folder mCurFolder;
    private static Folder mBinFolder;

    // Конструктор
    public MainController(FragmentActivity activity, final MainInterface mainInterface, int curFolderId) {
        mActivity = activity;
        mMainInterface = mainInterface;
        mNoteViewModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        mBinFolder = new Folder(mActivity.getString(R.string.nav_bin), 0, R.id.nav_bin);
        initFolderList();
        if (curFolderId != mBinFolder.getId())
            mCurFolder = Utils.getFolderById(curFolderId, mFolderList);
        else mCurFolder = mBinFolder;
        initDrawerMenu();
        mNoteViewModel.getAllNotes().observe(activity, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mNotes = notes;
                updateNoteList();
                setNoteAmountToDrawerMenuItem(mFolderList, mNotes);
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

    // Устанавливает в DrawerMenu item's кол-во заметок в папках
    private void setNoteAmountToDrawerMenuItem(List<Folder> folderList, List<Note> noteList) {
        int curAmount;
        for (Folder folder : folderList) {
            curAmount = Utils.getNotesFromFolder(noteList, folder).size();
            mMainInterface.setExtraTextToDrawerMenuItem(folder, curAmount);
        }
    }

    public void onFabPressed() {
        startNoteActivity(mActivity, mCurFolder, null);
    }

    public void onMenuDelPressed() {
        if (mNotesInCurFolder.size() < 1)
            deleteFolder(mCurFolder);
        else Utils.showToast(mActivity, R.string.mes_folder_is_not_empty);
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
        } else mMainInterface.showSnack(R.string.mes_bin_is_empty);

    }

    public void onContextOpenPressed(Note note) {
        startNoteActivity(mActivity, mCurFolder, note);
    }

    public void onContextDelPressed(final Note note) {
        if (mCurFolder != mBinFolder) {
            new DeleteDialog(mActivity, DeleteDialog.ACTION_DELETE_NOTE,
                    new DeleteDialog.DeleteDialogListener() {
                        @Override
                        public void onDeleteConfirm() {
                            note.setFolderName(mBinFolder.getName());
                            mNoteViewModel.update(note);
                            mMainInterface.showSnack(R.string.mes_note_moved_to_bin);
                        }
                    }).show(mActivity.getSupportFragmentManager(), null);
        } else {
            mNoteViewModel.deleteNote(note);
            mMainInterface.showSnack(R.string.mes_note_deleted);
        }
    }

    public void onContextMovePressed(final Note note) {
        new MoveNoteDialog(mActivity, Utils.getFolderNames(mFolderList),
                new MoveNoteDialog.MoveDialogListener() {
                    @Override
                    public void onMoveConfirmed(int folderIndex) {
                        moveNote(folderIndex, note);
                    }
                }).show(mActivity.getSupportFragmentManager(), null);
    }

    public void onNavBinPressed() {
        mCurFolder = mBinFolder;
        updateNoteList();
    }

    public void onNavSettingsPressed() {
        Utils.showToast(mActivity, "Settings folder is pressed!");
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
        mCurFolder = Utils.getFolderById(id, mFolderList);
        updateNoteList();
        mMainInterface.setEnableMenuItems(mCurFolder.getId() != INITIAL_FOLDER_ID);
    }

    public void onItemNotePressed(Note note) {
        startNoteActivity(mActivity, mCurFolder, note);
    }

    public int getCurFolderId() {
        return mCurFolder.getId();
    }

    public static Folder getBinFolder() {
        return mBinFolder;
    }

    private void createNewFolder(String name) {
        if (!Utils.isFolderNameExists(name, mFolderList) && !name.equals(mBinFolder.getName())) {
            // Находим папку, находящуюся в списке последней
            Folder lastFolder = mFolderList.get(mFolderList.size() - 1);
            // Создаем новую папку. В качестве id указываем id последней папки, увеличенную на 1
            mCurFolder = new Folder(name, System.currentTimeMillis(), (lastFolder.getId() + 1));
            mFolderList = mNoteViewModel.insertFolder(mCurFolder);
            mMainInterface.addDrawerMenuItem(mCurFolder, R.drawable.ic_folder);
            mMainInterface.setCheckedDrawerMenuItem(mCurFolder);
            mMainInterface.setEnableMenuItems(mCurFolder.getId() != INITIAL_FOLDER_ID);
            updateNoteList();
        } else Utils.showToast(mActivity, R.string.mes_folder_exists);
    }

    private void deleteFolder(Folder folder) {
        mFolderList = mNoteViewModel.deleteFolder(folder);
        mMainInterface.deleteDrawerMenuItem(folder);
        mCurFolder = mFolderList.get(0);
        mMainInterface.setCheckedDrawerMenuItem(mCurFolder);
        mMainInterface.setEnableMenuItems(mCurFolder.getId() != INITIAL_FOLDER_ID);
        updateNoteList();
    }

    private void renameFolder(String name, Folder folder) {
        folder.setName(name);
        mFolderList = mNoteViewModel.updateFolder(folder);
        // Обновляем Notes в БД
        for (Note note : mNotesInCurFolder) {
            note.setFolderName(folder.getName());
            mNoteViewModel.update(note);
        }
        mMainInterface.renameDrawerMenuItem(folder);
        mMainInterface.setToolbarTitle(folder.getName());
    }

    private void moveNote(int folderIndex, Note note) {
        Folder folder = mFolderList.get(folderIndex);
        note.setFolderName(folder.getName());
        mNoteViewModel.update(note);
    }

    private void updateNoteList() {
        mNotesInCurFolder = Utils.getNotesFromFolder(mNotes, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
    }

    private static void startNoteActivity(Activity activity, Folder folder, Note note) {
        Intent intent = new Intent(activity, NoteActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        intent.putExtra(EXTRA_NOTE, note);
        activity.startActivity(intent);
    }

}
