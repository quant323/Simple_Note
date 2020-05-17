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

    private Context context;
    private DeleteDialogListener dialogListener;

    // Конструктор
    public DeleteDialog(Context context, DeleteDialogListener dialogListener) {
        this.context = context;
        this.dialogListener = dialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.d_delete_title)
                .setMessage(R.string.d_delete_message)
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
