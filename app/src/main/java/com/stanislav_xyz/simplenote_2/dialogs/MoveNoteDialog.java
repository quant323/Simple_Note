package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MoveNoteDialog extends AppCompatDialogFragment {

    private String[] folderNames;
    private MoveDialogListener dialogListener;

    // Конструктор
    public MoveNoteDialog(String[] folderNames, MoveDialogListener dialogListener) {
        this.folderNames = folderNames;
        this.dialogListener = dialogListener;
    }

//    public static MoveNoteDialog create(String[] folderNames, MoveDialogListener dialogListener) {
//
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
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
