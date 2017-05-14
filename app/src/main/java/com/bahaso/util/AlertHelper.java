package com.bahaso.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Hendry on 8/26/2015.
 */
public class AlertHelper {

    public static void createDialog(Context context, String title,
                                    String message,
                                    boolean cancelable, String positiveText,
                                    DialogInterface.OnClickListener onPositiveClick,
                                    String negativeText,
                                    DialogInterface.OnClickListener onNegativeClick,
                                    View customView,
                                    DialogInterface.OnCancelListener onCancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(Html.fromHtml(message))
                .setCancelable(cancelable);
        if (!TextUtils.isEmpty(positiveText)){
            builder.setTitle(title);
        }
        if (null!= customView) {
            builder.setView(customView);
        }
        if (!TextUtils.isEmpty(positiveText)) {
            builder.setPositiveButton(positiveText, onPositiveClick);
        }
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, onNegativeClick);
        }
        if (null!= onCancelListener) {
            builder.setOnCancelListener(onCancelListener);
        }
        AlertDialog dialog = builder.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        TextView titleView  = (TextView) dialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);
        if (null!= textView) {
            textView.setTypeface(ViewUtil.getRegularTypeface());
        }
        if (null!= titleView) {
            titleView.setTypeface(ViewUtil.getBoldTypeface());
        }
    }

    public static void createDialog(Context context, String title,
                                    String message,
                                    boolean cancelable, String positiveText,
                                    DialogInterface.OnClickListener onPositiveClick,
                                    String negativeText,
                                    DialogInterface.OnClickListener onNegativeClick,
                                    View customView){
        createDialog (context, title,message, cancelable, positiveText, onPositiveClick,
                negativeText, onNegativeClick, customView, null);
    }
}
