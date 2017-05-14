package com.bahaso.util;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bahaso.R;

/**
 * Created by shiperus on 3/10/2017.
 */

public class AlertDialogYesNoSimple extends AlertDialog {
    static AlertDialog alertDialogYesNo ;

    public AlertDialogYesNoSimple(@NonNull final Context context, String title, String message, String backMsg, String nextMsg) {
        super(context);
        // final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.yes_no_simple_alert_dialog, null);
        setView(v);
        alertDialogYesNo = this;
        TextView yesNoSimpleTitle = (TextView) v.findViewById(R.id.yesNoSimpleDialogTitle);
        TextView yesNoSimpleMessage = (TextView) v.findViewById(R.id.yesNoSimpleDialogMessage);
        TextView yesNoSimpleBackTxt= (TextView) v.findViewById(R.id.backDialogYesNoSimple);
        TextView yesNoSimpleNextTxt = (TextView) v.findViewById(R.id.nextDialogYesNoSimple);

        yesNoSimpleTitle.setText(title);
        yesNoSimpleMessage.setText(message);
        yesNoSimpleBackTxt.setText(backMsg);
        yesNoSimpleNextTxt.setText(nextMsg);

        yesNoSimpleBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
                alertDialogYesNo.hide();
            }
        });

        yesNoSimpleNextTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "Next", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
