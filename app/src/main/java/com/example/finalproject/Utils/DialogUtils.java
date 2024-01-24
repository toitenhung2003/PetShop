package com.example.finalproject.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.finalproject.R;

public class DialogUtils {

    public static Dialog showDialog(Context context, @Nullable String title, String message,
                                    String yesTitle, @Nullable DialogInterface.OnClickListener yesListener,
                                    @Nullable String cancelTitle,
                                    @Nullable DialogInterface.OnClickListener cancelListener) {
        Dialog dialog = new AlertDialog.Builder(context,R.style.CustomAlertDialog).setTitle(title)
                .setMessage(message)
                .setPositiveButton(yesTitle, yesListener)
                .setNegativeButton(cancelTitle, cancelListener)
                .create();

        dialog.setOnShowListener(arg0 -> {
            Button positiveButton = dialog.findViewById(android.R.id.button1);
            positiveButton.setTextColor(context.getColor(R.color.black));
        });

        dialog.show();

        return dialog;
    }

    public static Dialog showDialog(Context context, @Nullable String title, String message,
                                    String yesTitle, @Nullable DialogInterface.OnClickListener yesListener,
                                    @Nullable String cancelTitle, @Nullable DialogInterface.OnClickListener cancelListener,
                                    boolean cancelable) {
        Dialog dialog = new AlertDialog.Builder(context,R.style.CustomAlertDialog)
                .setTitle(CommonActivity.isNullOrEmpty(title) ? context.getString(R.string.notifications) : title)
                .setMessage(message)
                .setPositiveButton(yesTitle, yesListener)
                .setNegativeButton(cancelTitle, cancelListener)
                .setCancelable(cancelable)
                .create();

        dialog.setOnShowListener(arg0 -> {
            Button positiveButton = dialog.findViewById(android.R.id.button1);
            Button negativeButton = dialog.findViewById(android.R.id.button2);
            positiveButton.setTextColor(context.getColor(R.color.myColor));
            negativeButton.setTextColor(context.getColor(R.color.myColor));
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showDialog(Context context, @Nullable String title, String message,
                                    @Nullable DialogInterface.OnClickListener yesListener) {
        return showDialog(context,
                title == null ? context.getResources().getString(R.string.notifications) : title,
                message,
                context.getString(R.string.ok),
                yesListener,
                null,
                null);
    }

    public static Dialog showDialog(Context context, @Nullable String title, String message,
                                    @Nullable DialogInterface.OnClickListener yesListener, boolean cancelable) {
        return showDialog(context,
                title == null ? context.getResources().getString(R.string.notifications) : title,
                message,
                context.getResources().getString(R.string.ok),
                yesListener,
                context.getResources().getString(R.string.no),
                (dialog, which) -> dialog.dismiss(),
                cancelable);
    }

    public static Dialog showDialog(Context context, String message) {
        return showDialog(context,
                context.getResources().getString(R.string.notifications),
                message,
                null);
    }

    public static Dialog showDialog(Context context, String message, DialogInterface.OnClickListener yesListener, boolean cancelable) {
        return showDialog(context, context.getResources().getString(R.string.notifications), message, context.getResources().getString(R.string.ok), yesListener, null, null, cancelable);
    }
}
