package com.stanislav_xyz.simplenote_2.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MoveNoteDialog extends AppCompatDialogFragment {

    private Context context;
    String[] folderNames;

    // Конструктор
    public MoveNoteDialog(Context context, String... folderNames) {
        this.context = context;
        this.folderNames = folderNames;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.action_context_move)
                .setItems(folderNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }
}
