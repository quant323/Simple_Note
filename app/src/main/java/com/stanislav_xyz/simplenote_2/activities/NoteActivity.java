package com.stanislav_xyz.simplenote_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.model.Note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";
    private TextView noteTextView;

    // Метод для вызова этого Активити
    public static void startThisActivity(Activity callerActivity, Note note) {
        Intent intent = new Intent(callerActivity, NoteActivity.class);
        if (note != null)
            intent.putExtra(EXTRA_NOTE, note);
        callerActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // нажатие на стрелку "назад" на Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noteTextView = findViewById(R.id.note_textView);

        if (getIntent().hasExtra(EXTRA_NOTE)) {
            Note note = getIntent().getParcelableExtra(EXTRA_NOTE);
            noteTextView.setText(note.body);
        }




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
