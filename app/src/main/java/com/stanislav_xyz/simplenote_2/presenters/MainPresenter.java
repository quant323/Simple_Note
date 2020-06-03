package com.stanislav_xyz.simplenote_2.presenters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.activities.NoteActivity;
import com.stanislav_xyz.simplenote_2.activities.SettingsActivity;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainPresenter {

    private static final String TAG = "myTag";

    public static final int INITIAL_FOLDER_ID = 0;
    public static final String EXTRA_FOLDER = "NoteActivity.EXTRA_FOLDER";
    public static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";

    private MainInterface mMainInterface;
    private FragmentActivity mActivity;
    private NoteViewModel mNoteViewModel;

    private List<Note> mNoteList;
    private List<Note> mNotesInCurFolder = new ArrayList<>();
    private List<Folder> mFolderList;
    private Folder mCurFolder;
    private static Folder mBinFolder;

    // Конструктор
    public MainPresenter(FragmentActivity activity, final MainInterface mainInterface, int curFolderId) {
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
                mNoteList = notes;
                updateNoteList();
                setNoteAmountToDrawerMenuItem(mFolderList, mNoteList);
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
        startNoteActivity(mCurFolder, null);
    }

    public void onMenuDelPressed() {
        if (mNotesInCurFolder.size() < 1)
            deleteFolder(mCurFolder);
        else Utils.showToast(mActivity, R.string.mes_folder_is_not_empty);
    }

    public void onMenuRenamePressed() {
        final EditText folderName_ET = Utils.createEditText(mActivity,
                mCurFolder.getName(), null);
        new AlertDialog.Builder(mActivity)
                .setMessage(R.string.action_rename_folder)
                .setView(folderName_ET)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        renameFolder(folderName_ET.getText().toString(), mCurFolder);
                    }
                }).show();
    }

    public void onMenuCleanBinPressed() {
        if (mNotesInCurFolder.size() > 0) {
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.d_empty_title)
                    .setMessage(R.string.d_empty_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCurFolder == mBinFolder)
                                for (Note note : mNotesInCurFolder)
                                    mNoteViewModel.deleteNote(note);
                            mMainInterface.showSnack(R.string.mes_bin_is_empty);
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        } else mMainInterface.showSnack(R.string.mes_bin_is_empty);
    }

    public void onContextOpenPressed(Note note) {
        startNoteActivity(mCurFolder, note);
    }

    public void onContextDelPressed(final Note note) {
        if (mCurFolder != mBinFolder) {
           new AlertDialog.Builder(mActivity)
                   .setTitle(R.string.d_delete_title)
                   .setMessage(R.string.d_delete_message)
                   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           note.setFolderName(mBinFolder.getName());
                           mNoteViewModel.update(note);
                           mMainInterface.showSnack(R.string.mes_note_moved_to_bin);
                       }
                   }).setNegativeButton(R.string.cancel, null)
                   .show();
        } else {
            mNoteViewModel.deleteNote(note);
            mMainInterface.showSnack(R.string.mes_note_deleted);
        }
    }

    public void onContextMovePressed(final Note note) {
        new AlertDialog.Builder(mActivity).setTitle(R.string.action_context_move)
                .setItems(Utils.getFolderNames(mFolderList), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveNote(which, note);
                    }
                }).show();
    }

    public void onNavBinPressed() {
        mCurFolder = mBinFolder;
        updateNoteList();
    }

    public void onNavSettingsPressed() {
        startSettingsActivity();
    }

    public void onNavAboutPressed() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.nav_about)
                .setMessage(R.string.d_about_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public void onNavAddFolderPressed() {
        final EditText folderName_ET = Utils.createEditText(mActivity, null,
                mActivity.getString(R.string.d_create_folder_hint));
        new AlertDialog.Builder(mActivity)
                .setMessage(R.string.action_rename_folder)
                .setView(folderName_ET)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNewFolder(folderName_ET.getText().toString());
                        mMainInterface.setItemsVisibility(true);
                    }
                }).show();
    }

    public void onNavNormalFolderPressed(int id) {
        mCurFolder = Utils.getFolderById(id, mFolderList);
        updateNoteList();
        mMainInterface.setEnableMenuItems(mCurFolder.getId() != INITIAL_FOLDER_ID);
    }

    public void onItemNotePressed(Note note) {
        startNoteActivity(mCurFolder, note);
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
        mNotesInCurFolder = Utils.getNotesFromFolder(mNoteList, mCurFolder);
        mMainInterface.updateNoteResView(mNotesInCurFolder);
        mMainInterface.setToolbarTitle(mCurFolder.getName());
    }

    private void startNoteActivity(Folder folder, Note note) {
        Intent intent = new Intent(mActivity, NoteActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        intent.putExtra(EXTRA_NOTE, note);
        mActivity.startActivity(intent);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        mActivity.startActivity(intent);
    }

}
