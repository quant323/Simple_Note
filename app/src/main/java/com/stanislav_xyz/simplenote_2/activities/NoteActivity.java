package com.stanislav_xyz.simplenote_2.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.presenters.NotePresenter;
import com.stanislav_xyz.simplenote_2.presenters.NoteInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity implements NoteInterface {

    private EditText mNoteEditText;
    private boolean mRequireSaving = true;
    private int mEtTextColor;
    private NotePresenter mNotePresenter;
    private FloatingActionButton fab;
    private Menu mMenu;

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else finish();

            }
        });

        mNoteEditText = findViewById(R.id.note_editText);
        mEtTextColor = mNoteEditText.getCurrentTextColor();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotePresenter.onFabPressed();
            }
        });
        mNotePresenter = new NotePresenter(this, this, getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        mMenu = menu;
        mNotePresenter.optionsMenuCreated();
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
                mNotePresenter.onMenuDelPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        mNotePresenter.onStop(mRequireSaving);
        super.onStop();
    }

    // Убирает или восстанавливает функцию редактировния у EditText
    @Override
    public void setEditToEditText(boolean editable) {
        mNoteEditText.setEnabled(editable);
        mNoteEditText.setCursorVisible(editable);
        if (!editable)
            mNoteEditText.setTextColor(mEtTextColor);
    }

    @Override
    public void setTextEditText(String text) {
        mNoteEditText.setText(text);
    }

    @Override
    public String getTextFromEditText() {
        return mNoteEditText.getText().toString();
    }

    @Override
    public void showOptionDelItem(boolean show) {
        mMenu.findItem(R.id.action_note_delete).setVisible(show);
    }

    @Override
    public void hideFab() {
        fab.hide();
    }

    @Override
    public void showKeyBoard(boolean inFab) {
        if (inFab) {
            InputMethodManager methodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        } else getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

}
