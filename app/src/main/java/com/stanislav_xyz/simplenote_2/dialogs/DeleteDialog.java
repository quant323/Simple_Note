package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteDialog extends AppCompatDialogFragment {

    public static final int ACTION_DELETE_NOTE = 0;
    public static final int ACTION_EMPTY_BIN = 1;

    private Context context;
    private DeleteDialogListener dialogListener;
    private int titleId;
    private int messageId;

    // Конструктор
    public DeleteDialog(Context context, int action, DeleteDialogListener dialogListener) {
        this.context = context;
        this.dialogListener = dialogListener;
        setAction(action);
    }

    private void setAction(int action) {
        switch (action) {
            case ACTION_DELETE_NOTE:
                titleId = R.string.d_delete_title;
                messageId = R.string.d_delete_message;
                break;
            case ACTION_EMPTY_BIN:
                titleId = R.string.d_empty_title;
                messageId = R.string.d_empty_message;
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDeleteConfirm();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    // Интерфейс
    public interface DeleteDialogListener {
        void onDeleteConfirm();
    }

}
