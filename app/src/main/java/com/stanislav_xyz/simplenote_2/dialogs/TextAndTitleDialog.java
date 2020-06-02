package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TextAndTitleDialog extends AppCompatDialogFragment {

    private String title;
    private String message;

    public TextAndTitleDialog(String  title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}
