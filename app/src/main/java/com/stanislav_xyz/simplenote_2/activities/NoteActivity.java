package com.stanislav_xyz.simplenote_2.activities;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.ActivityStarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {

    private static final int MAX_TITLE_LENGTH = 20;

    private EditText mNoteEditText;
    private Note mNote;
    private NoteViewModel mNoteViewModel;
    private String mFolder;
    private boolean mRequireSaving = true;
    private int mEtTextColor;

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
        mEtTextColor = mNoteEditText.getCurrentTextColor();


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Возвращает EditText функцию редактирования
                setEditableET(true);
                // Показывает клавиатуру
                InputMethodManager methodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                fab.hide();
            }
        });

        if (getIntent().hasExtra(ActivityStarter.EXTRA_NOTE)) {
            mNote = getIntent().getParcelableExtra(ActivityStarter.EXTRA_NOTE);
            mNoteEditText.setText(mNote.getBody());
            // Превращает EditText в TextView
            setEditableET(false);
        }

        if (getIntent().hasExtra(ActivityStarter.EXTRA_FOLDER)) {
            mFolder = getIntent().getStringExtra(ActivityStarter.EXTRA_FOLDER);
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
                finish();
                break;
            case R.id.action_note_delete:
                if (mNote != null) {
                    new DeleteDialog(this, new DeleteDialog.DeleteDialogListener() {
                        @Override
                        public void onDeleteConfirm() {
                            mNoteViewModel.deleteNote(mNote);
                            finish();
                        }
                    }).show(getSupportFragmentManager(), null);
                    break;
                }
        }
        return true;
    }

    // Убирает или восстанавливает функцию редактировния у EditText
    private void setEditableET(boolean editable) {
        mNoteEditText.setEnabled(editable);
        mNoteEditText.setCursorVisible(editable);
        if (!editable)
            mNoteEditText.setTextColor(mEtTextColor);
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
        if (mRequireSaving) saveNote();
        super.onStop();
    }
}
