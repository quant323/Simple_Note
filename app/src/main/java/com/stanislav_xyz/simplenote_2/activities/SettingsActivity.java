package com.stanislav_xyz.simplenote_2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.presenters.MainPresenter;
import com.stanislav_xyz.simplenote_2.utils.NoteFileManager;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import java.lang.reflect.Type;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "myTag";
    private static final String MAIN_FOLDER = "Simple Note";
    private static final String BACKUP_FILE_NAME = "note_backup1";
    private static final String BACKUP_FOLDER = "backup";

    public static final String MIME_TEXT = "txt";
    public static final String MIME_FILE = "bac";
    public static final String EXTRA_FOLDERS = "extra_folders";
    public static final String EXTRA_NOTES = "extra_notes";

    private List<Folder> mFolderList;
    private List<Note> mNoteList;

    private NoteFileManager mFileManager;

    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.settings_export_item).setOnClickListener(this);
        findViewById(R.id.settings_import_item).setOnClickListener(this);
        findViewById(R.id.settings_export_to_text).setOnClickListener(this);

        mFolderList = getIntent().getParcelableArrayListExtra(MainPresenter.EXTRA_FOLDER_LIST);
        mNoteList = getIntent().getParcelableArrayListExtra(MainPresenter.EXTRA_NOTE_LIST);

        mFileManager = new NoteFileManager(this);
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_export_item:
                Toast.makeText(this, "Export files", Toast.LENGTH_SHORT).show();
                exportNotesToFile();
                break;
            case R.id.settings_import_item:
                Toast.makeText(this, "Import files", Toast.LENGTH_SHORT).show();
                importNotesFromFile();
                break;
            case R.id.settings_export_to_text:
                exportNotesToText();
                break;
        }
    }

    // Экспортирует заметки в текстовые файлы
    private void exportNotesToText() {
        String folderPath = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER).toString();
        for (Folder folder : mFolderList) {
            String path = folderPath + "/" + folder.getName();
            List<Note> notesInFolder = Utils.getNotesFromFolder(mNoteList, folder);
            for(Note note : notesInFolder)
                mFileManager.writeTextFile(path, note.getTitle() + "." + MIME_TEXT, note.getBody());
        }
        createSimpleDialog(getString(R.string.d_export_finished_title),
                getString(R.string.d_export_notes_as_text_path, folderPath))
                .show();
    }

    private void exportNotesToFile() {
        String path = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER) + "/" + BACKUP_FOLDER;
        String notesAsString = convertListToString(mNoteList);
        String foldersAsString = convertListToString(mFolderList);
        mFileManager.writeFile(path, BACKUP_FILE_NAME + "." + MIME_FILE,
                notesAsString, foldersAsString);
        createSimpleDialog(getString(R.string.d_export_finished_title),
                getString(R.string.d_export_backup_path, path))
                .show();
    }

    private void importNotesFromFile() {
        String path = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER) + "/" + BACKUP_FOLDER;
        String[] data = mFileManager.readFile(path, BACKUP_FILE_NAME + "." + MIME_FILE);
        List<Note> importedNotes;
        List<Folder> importedFolders;
        int uniqueNotes;
        int uniqueFolders;
        if (data != null) {
            importedNotes = restoreNotesFromString(data[0]);
            importedFolders = restoreFoldersFromString(data[1]);
            uniqueNotes = mergeNotes(mNoteList, importedNotes);
            uniqueFolders = mergeFolders(mFolderList, importedFolders);
//            createSimpleDialog(getString(R.string.d_notes_imported_title),
//                    getString(R.string.d_notes_folders_imported, uniqueNotes, uniqueFolders))
//                    .show();
            createSimpleDialog(getString(R.string.d_import_failed_title), null).show();
        } else createSimpleDialog(getString(R.string.d_import_failed_title), null).show();
 //           Utils.showToast(this, R.string.mes_import_failed);
    }

    // Работа с Gson
    private String convertListToString(List<?> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    private List<Note> restoreNotesFromString(String notes) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Note>>(){}.getType();
        return gson.fromJson(notes, type);
    }

    private List<Folder> restoreFoldersFromString(String folders) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Folder>>(){}.getType();
        return gson.fromJson(folders, type);
    }


    // Приверяет, содержится ли уже такая заметка в листе. Если нет - добавляет заметку в хранилище.
    // Заметки считаются одниковыми, если равны их id (см. метод equals() клааса Note)
    // Возвращаемое значение - кол-во уникальных заметок
    private int mergeNotes(List<Note> existingNotes, List<Note> importedNotes) {
        int uniqueNotes = 0;
        for (Note note : importedNotes) {
            if (!existingNotes.contains(note)) {
                mNoteViewModel.insert(note);
                uniqueNotes++;
                Log.d(TAG, "missing note - " + note.getTitle());
            }
        }
        return uniqueNotes;
    }

    // Приверяет, содержится ли уже такая папка в листе. Если нет - добавляет папку в хранилище.
    // Папки считаются одниковыми, если равны их имена (см. метод equals() клааса Folder).
    // Возвращаемое значение - кол-во уникальных папок
    private int mergeFolders(List<Folder> existingFolders, List<Folder> importedFolders) {
        int uniqueFolders = 0;
        for (Folder folder : importedFolders) {
            if (!existingFolders.contains(folder)) {
                Folder lastFolder = existingFolders.get(existingFolders.size() - 1);
                folder.setId(lastFolder.getId() + 1);
                mNoteViewModel.insertFolder(folder);
                uniqueFolders++;
                Log.d(TAG, "missing folder - " + folder.getName());
            }
        }
        return uniqueFolders;
    }

    private AlertDialog.Builder createSimpleDialog(String title, String message) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null);
    }

}
