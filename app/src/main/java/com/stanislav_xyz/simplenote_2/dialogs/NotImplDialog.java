package com.stanislav_xyz.simplenote_2.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.stanislav_xyz.simplenote_2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NotImplDialog extends AppCompatDialogFragment {

    private Context context;

    public NotImplDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.mes_not_yet_implemented)
                .setPositiveButton(R.string.ok, null)
                .create();
    }

}
