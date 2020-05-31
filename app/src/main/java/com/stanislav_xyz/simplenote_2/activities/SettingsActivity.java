package com.stanislav_xyz.simplenote_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.presenters.MainPresenter;
import com.stanislav_xyz.simplenote_2.utils.NoteFileManager;
import com.stanislav_xyz.simplenote_2.utils.Utils;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "myTag";
    private static final String APP_NAME = "Simple Note";
    private static final String MIME_TEXT = ".txt";
    private static final String FILE_NAME = "note_backup1.sno";

    private List<Folder> mFolderList;
    private List<Note> mNoteList;

    private NoteFileManager mFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Нажатие на стрелку "назад" на Toolbar
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
        for (Folder folder : mFolderList) {
            String path = Environment.getExternalStoragePublicDirectory(APP_NAME) + "/" + folder.getName();
            List<Note> notesInFolder = Utils.getNotesFromFolder(mNoteList, folder);
            for(Note note : notesInFolder)
                mFileManager.writeTextFile(path, note.getTitle() + MIME_TEXT, note.getBody());
        }
        Snackbar.make(findViewById(R.id.coordinator_settings),
                R.string.mes_export_finished, Snackbar.LENGTH_LONG).show();
    }

    private void exportNotesToFile() {
        String path = Environment.getExternalStoragePublicDirectory(APP_NAME) + "/" + "note_backup";
        mFileManager.writeFile(path, FILE_NAME, mNoteList.get(0));
    }

    private void importNotesFromFile() {
        String path = Environment.getExternalStoragePublicDirectory(APP_NAME) + "/" + "note_backup";
        mFileManager.readFile(path, FILE_NAME);
    }

}
