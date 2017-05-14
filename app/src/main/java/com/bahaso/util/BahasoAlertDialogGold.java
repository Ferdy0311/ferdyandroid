package com.bahaso.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.globalvar.helper.picassohelper.PicassoHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by hendrysetiadi on 10/08/2016.
 */

/* use this just to show warning to user
   and no action when click OK
   It will be destroyed on rotate*/
public class BahasoAlertDialogGold extends AlertDialog {

    public BahasoAlertDialogGold(Context context) {
        this(context, null, null, 0, null, null);
    }

    public BahasoAlertDialogGold(Context context, String title, String message,
                                 int imageRes, String imageSrc, final EditText editText) {
        super(context, R.style.Bahaso_AlertDialog_Transparent);

        // final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.bahaso_alert_dialog, null);
        setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
        ImageView ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        TextView tvMessage = (TextView) v.findViewById(R.id.tv_message);
        tvTitle.setText(title);

        if (!TextUtils.isEmpty( imageSrc) ) {
            PicassoHelper.loadImage(getContext(), imageSrc, ivIcon);
        }
        else if (imageRes > 0) {
            ivIcon.setImageResource(imageRes);
        }
        tvMessage.setText(message);

//        View tvOk =  v.findViewById(R.id.tv_ok);
        View btnOK = v.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editText.getText().clear();
                cancel();
            }
        });

    }

    public BahasoAlertDialogGold(Context context, String title, String message,
                                 int imageRes, String imageSrc) {
        super(context, R.style.Bahaso_AlertDialog_Transparent);

        // final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.bahaso_alert_dialog, null);
        setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
        ImageView ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        TextView tvMessage = (TextView) v.findViewById(R.id.tv_message);
        tvTitle.setText(title);

        if (!TextUtils.isEmpty( imageSrc) ) {
            PicassoHelper.loadImage(getContext(), imageSrc, ivIcon);
        }
        else if (imageRes > 0) {
            ivIcon.setImageResource(imageRes);
        }
        tvMessage.setText(message);

//        View tvOk =  v.findViewById(R.id.tv_ok);
        View btnOK = v.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

    }


}
