package com.stanislav_xyz.simplenote_2.dialogs;

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
    private String[] folderNames;
    private MoveDialogListener dialogListener;

    // Конструктор
    public MoveNoteDialog(Context context, String[] folderNames, MoveDialogListener dialogListener) {
        this.context = context;
        this.folderNames = folderNames;
        this.dialogListener = dialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.action_context_move)
                .setItems(folderNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onMoveConfirmed(which);
                    }
                }).create();
    }


    // Интерфейс
    public interface MoveDialogListener {
        void onMoveConfirmed(int index);
    }

}
