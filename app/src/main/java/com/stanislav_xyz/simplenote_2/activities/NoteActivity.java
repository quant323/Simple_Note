package com.stanislav_xyz.simplenote_2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.model.Note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";
    private static final String EXTRA_FOLDER = "NoteActivity.EXTRA_FOLDER";
    private static final int MAX_TITLE_LENGTH = 20;

    private EditText mNoteEditText;
    private Note mNote;
    private NoteViewModel mNoteViewModel;
    private String mFolder;
    private InputMethodManager mMethodManager;
    private boolean mRequireSaving = true;

    // Метод для вызова этого Активити из других Activity
    public static void startThisActivity(Activity activity, Note note, String folder) {
        Intent intent = new Intent(activity, NoteActivity.class);
        if (note != null)
            intent.putExtra(EXTRA_NOTE, note);
        if (folder != null)
            intent.putExtra(EXTRA_FOLDER, folder);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Нажатие на стрелку "назад" на Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNoteEditText = findViewById(R.id.note_editText);
        mMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Возвращает EditText функцию редактирования
                mNoteEditText.setEnabled(true);
                mNoteEditText.setCursorVisible(true);
                // Показывает клавиатуру
                mMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                fab.hide();
            }
        });

        int textColor = mNoteEditText.getCurrentTextColor();
        if (getIntent().hasExtra(EXTRA_NOTE)) {
            mNote = getIntent().getParcelableExtra(EXTRA_NOTE);
            mNoteEditText.setText(mNote.getBody());
            // Превращает EditText в TextView
            mNoteEditText.setEnabled(false);
            mNoteEditText.setCursorVisible(false);
            mNoteEditText.setTextColor(textColor);
        }

        if (getIntent().hasExtra(EXTRA_FOLDER)) {
            mFolder = getIntent().getStringExtra(EXTRA_FOLDER);
            // Показывает клавиатуру
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            fab.hide();
        }
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        mRequireSaving = false;
        switch (id) {
            case R.id.action_not_save:
                break;
            case R.id.action_note_delete:
                if (mNote != null) {
                    mNoteViewModel.deleteNote(mNote);
                    break;
                }
        }
        finish();
        return true;
    }

    // Устанавливает новые данные для заметки
    private Note setNewValues(Note note, String body) {
        note.setBody(body);
        note.setTitle(getTitle(body));
        note.setDate(System.currentTimeMillis());
        return note;
    }

    // Сохраняет новую, либо обновляет существующую заметку
    private void saveNote() {
        String body = mNoteEditText.getText().toString();
        if (mNote != null) {
            if (!mNote.getBody().equals(body)) {
                mNoteViewModel.update(setNewValues(mNote, body));
            }
        } else {
            if (!body.equals("")) {
                mNote = new Note(body, getTitle(body), System.currentTimeMillis(), mFolder);
                mNoteViewModel.insert(mNote);
            }
        }
    }

    // Возвращает title из тела заметки
    private String getTitle(String body) {
        String[] arr = body.split("\n");
        if (arr[0].length() < MAX_TITLE_LENGTH) {
            return arr[0];
        } else {
            return arr[0].substring(0, MAX_TITLE_LENGTH);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequireSaving) saveNote();
    }
}
