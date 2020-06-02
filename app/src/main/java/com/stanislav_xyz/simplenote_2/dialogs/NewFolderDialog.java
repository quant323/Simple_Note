package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.Dialog;
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

    private FolderDialogListener folderDialogListener;
    private String message;
    private String curFolderName;

    // Конструктор для создания новой папки
    public NewFolderDialog(String message, FolderDialogListener folderDialogListener) {
        this.folderDialogListener = folderDialogListener;
        this.message = message;
    }

    // Конструктор для переименования папки
    public NewFolderDialog(String message, String curFolderName,
                           FolderDialogListener folderDialogListener) {
        this.folderDialogListener = folderDialogListener;
        this.message = message;
        this.curFolderName = curFolderName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText newFolderName_ET = new EditText(getContext());
        newFolderName_ET.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newFolderName_ET.setSingleLine(true);
        if (curFolderName != null)
            newFolderName_ET.setText(curFolderName);
        else
            newFolderName_ET.setHint(R.string.d_create_folder_hint);

        return new AlertDialog.Builder(requireContext())
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
