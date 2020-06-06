package com.stanislav_xyz.simplenote_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "myTag";
    private static final String MAIN_FOLDER = "Simple Note";
    private static final String BACKUP_FILE_NAME = "note_backup1";
    private static final String BACKUP_FOLDER = "backup";
    private static final String MIME_TEXT = "txt";
    private static final String MIME_BACKUP_FILE = "bac";

    private static final int REQUEST_EXPORT_TO_TEXT = 1;
    private static final int REQUEST_EXPORT_TO_FILE = 2;
    private static final int REQUEST_IMPORT_FROM_FILE = 3;

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

        Switch animSwitch = findViewById(R.id.animation_switch);
        animSwitch.setChecked(getIntent().getBooleanExtra(MainPresenter.EXTRA_ANIM, true));
        animSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = MainActivity.getSharedSettings().edit();
                editor.putBoolean(MainActivity.APP_PREFERENCES_ANIMATION, isChecked);
                editor.apply();
            }
        });

        mFileManager = new NoteFileManager(this);
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mFolderList = mNoteViewModel.getAllFolders();
        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mNoteList = notes;
                Log.d(TAG, "onChanged: ");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (isExternalStorageWritable()) {
            switch (v.getId()) {
                case R.id.settings_export_item:
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        exportNotesToFile();
                    else ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXPORT_TO_FILE);
                    break;

                case R.id.settings_import_item:
                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                        importNotesFromFile();
                    else ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMPORT_FROM_FILE);
                    break;

                case R.id.settings_export_to_text:
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        exportNotesToText();
                    else ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXPORT_TO_TEXT);
                    break;

                default: break;
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.d_fail_to_read_external_storage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

    private void exportNotesToFile() {
        final String folderPath = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER) + "/" + BACKUP_FOLDER;
        final File file = new File(folderPath + "/" + BACKUP_FILE_NAME + "." + MIME_BACKUP_FILE);
        if (file.exists()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.d_attention_title)
                    .setMessage(R.string.d_attention_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createBackupFile(file, folderPath);
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            new File(folderPath).mkdirs();
            createBackupFile(file, folderPath);
        }
    }

    private void importNotesFromFile() {
        String folderPath = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER) + "/" + BACKUP_FOLDER;
        final File file = new File(folderPath + "/" + BACKUP_FILE_NAME + "." + MIME_BACKUP_FILE);
        if (file.exists()) {
            Object[] data = mFileManager.readObjectsFromFile(file);
            if (data != null) {
                List<Note> importedNotes = restoreNotesFromString((String) data[0]);
                List<Folder> importedFolders = restoreFoldersFromString((String)data[1]);
                int uniqueNotes = mergeNotes(mNoteList, importedNotes);
                int uniqueFolders = mergeFolders(mFolderList, importedFolders);
                createSimpleDialog(getString(R.string.d_notes_imported_title),
                        getString(R.string.d_notes_folders_imported, uniqueNotes, uniqueFolders))
                        .show();
            } else createSimpleDialog(getString(R.string.d_import_failed_title),
                    getString(R.string.d_import_failed_message_1)).show();
        } else createSimpleDialog(getString(R.string.d_import_failed_title),
                getString(R.string.d_import_failed_message_2)).show();
    }

    private void exportNotesToText() {
            File mainFolderPath = Environment.getExternalStoragePublicDirectory(MAIN_FOLDER);
            if(!mainFolderPath.exists())
                mainFolderPath.mkdirs();
            for (Folder folder : mFolderList) {
                File folderPath = new File(mainFolderPath + "/" + folder.getName());
                folderPath.mkdirs();
                List<Note> notesInFolder = Utils.getNotesFromFolder(mNoteList, folder);
                for(Note note : notesInFolder) {
                    File file = new File(folderPath + "/" + note.getTitle() + "." + MIME_TEXT);
                    mFileManager.exportTextToFile(file, note.getBody());
                }
            }
            createSimpleDialog(getString(R.string.d_export_finished_title),
                    getString(R.string.d_export_notes_as_text_path, mainFolderPath))
                    .show();
    }

    // Сохраняет заметки и папки в файл
    private void createBackupFile(File file, String folderPath) {
        String notesAsString = convertListToString(mNoteList);
        String foldersAsString = convertListToString(mFolderList);
        mFileManager.writeObjectsToFile(file, notesAsString, foldersAsString);
        createSimpleDialog(getString(R.string.d_export_finished_title),
                getString(R.string.d_export_backup_path, folderPath))
                .show();
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

    // Проверяет, содержится ли уже такая заметка в листе. Если нет - добавляет заметку в хранилище.
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

    // Проверяет, содержится ли уже такая папка в листе. Если нет - добавляет папку в хранилище.
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

    private boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {

                case REQUEST_EXPORT_TO_TEXT:
//                    Toast.makeText(this, "Write Text Granted!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Write Text Granted!");
                    break;

                case REQUEST_EXPORT_TO_FILE:
//                    Toast.makeText(this, "Write File Granted!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Write File Granted!");
                    break;

                case REQUEST_IMPORT_FROM_FILE:
//                    Toast.makeText(this, "Import Granted!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Import Granted!");
                    break;

                default: break;
            }
        } else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
