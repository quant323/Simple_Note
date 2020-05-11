package com.stanislav_xyz.simplenote_2.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class FolderDialog extends AppCompatDialogFragment {

    private Context context;
    private FolderDialogListener folderDialogListener;

    // Конструктор
    public FolderDialog(Context context, FolderDialogListener folderDialogListener) {
        this.context = context;
        this.folderDialogListener = folderDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText newFolderName_ET = new EditText(context);
        newFolderName_ET.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newFolderName_ET.setHint("Name");

        return new AlertDialog.Builder(context)
                .setMessage("Add New Folder")
                .setView(newFolderName_ET)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        folderDialogListener.onFolderConfirm(newFolderName_ET.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


    public interface FolderDialogListener {
        void onFolderConfirm(String name);
    }

}
