package com.jorotayo.algorubickrevamped.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomDialogs {
    public void createNotificationDialog(Context context, String title, String message, String positiveText) {
        new MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message).setPositiveButton(positiveText, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void createConfirmationDialog(final Context context, String title, String message, String positiveText, String negativeText) {
        new MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message).setPositiveButton(positiveText, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Closes without saving", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(negativeText, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
