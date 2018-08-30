package com.android.accenture.knowme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import static com.android.accenture.knowme.Constant.CANCEL_BUTTON;
import static com.android.accenture.knowme.Constant.MESSAGE_CONST;

/**
 * Created by ykashiwagi on 7/25/17.
 */

public class CallDialogActivity extends Activity {
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            message = intent.getStringExtra(MESSAGE_CONST);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setNegativeButton(CANCEL_BUTTON,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
