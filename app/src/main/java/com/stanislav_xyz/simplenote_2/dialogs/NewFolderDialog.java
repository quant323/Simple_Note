package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NewFolderDialog extends AppCompatDialogFragment {

    private Context context;
    private FolderDialogListener folderDialogListener;
    private String message;
    private String curFolderName;

    // Конструктор для создания новой папки
    public NewFolderDialog(Context context, String message, FolderDialogListener folderDialogListener) {
        this.context = context;
        this.folderDialogListener = folderDialogListener;
        this.message = message;
    }

    // Конструктор для переименования папки
    public NewFolderDialog(Context context, String message, String curFolderName,
                           FolderDialogListener folderDialogListener) {
        this.context = context;
        this.folderDialogListener = folderDialogListener;
        this.message = message;
        this.curFolderName = curFolderName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText newFolderName_ET = new EditText(context);
        newFolderName_ET.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (curFolderName != null)
            newFolderName_ET.setText(curFolderName);
        else
            newFolderName_ET.setHint(R.string.d_create_folder_hint);

        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setView(newFolderName_ET)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        folderDialogListener.onFolderConfirm(newFolderName_ET.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    // Интерфейс
    public interface FolderDialogListener {
        void onFolderConfirm(String name);
    }

}
