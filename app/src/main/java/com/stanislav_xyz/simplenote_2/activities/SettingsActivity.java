package com.stanislav_xyz.simplenote_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.dialogs.NotImplDialog;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

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
    }

    @Override
    public void onClick(View v) {
        new NotImplDialog(this).show(getSupportFragmentManager(), null);
    }
}
