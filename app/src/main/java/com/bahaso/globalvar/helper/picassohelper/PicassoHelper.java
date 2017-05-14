package com.bahaso.globalvar.helper.picassohelper;

import android.content.Context;
import android.widget.ImageView;

import com.bahaso.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Hendry on 1/28/2016.
 */
public class PicassoHelper {
    public static final int DEFAULT_ERROR_PIC = R.drawable.image_placeholder;
    public static final int DEFAULT_PLACEHOLDER = R.drawable.image_placeholder;

    private static Transformation mTransformation;

    public static void loadImagePlaceholder(Context context, String imageSrc, ImageView targetImageView){
        Picasso.with(context).load(imageSrc)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR_PIC)
                .into(targetImageView);
    }

    public static void loadImagePlaceholder(Context context, String imageSrc, ImageView targetImageView,
                                 com.squareup.picasso.Callback callback){
        Picasso.with(context).load(imageSrc)
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR_PIC)
                .into(targetImageView, callback);
    }

    public static void loadImage(Context context, String imageSrc, ImageView targetImageView){
        Picasso.with(context).load(imageSrc)
                .into(targetImageView);
    }

    public static void loadImage(Context context, String imageSrc, ImageView targetImageView,
                                 com.squareup.picasso.Callback callback){
        Picasso.with(context).load(imageSrc)
                .into(targetImageView, callback);
    }

    public static void loadRoundedImage(Context context, String imageSrc, ImageView targetImageView){
        Picasso.with(context)
                .load(imageSrc)
                .fit()
                .transform(getTransformation())
                .into(targetImageView);
    }

    public static void loadRoundedImage(Context context, String imageSrc, ImageView targetImageView,
                                        com.squareup.picasso.Callback callback){
        Picasso.with(context)
                .load(imageSrc)
                .fit()
                .transform(getTransformation())
                .into(targetImageView, callback);
    }

//    public static void setIndicatorsEnabled (boolean indicatorEnabled) {
//        Picasso.with(MyApplication.getContext()).setIndicatorsEnabled(indicatorEnabled);
//    }

    private static Transformation getTransformation(){
        if (null == mTransformation) {
            mTransformation = new RoundedTransformationBuilder()
//                    .cornerRadiusDp(30)
//                    .borderColor(Color.BLACK)
//                    .borderWidthDp(MyApplication.getContext().getResources().
//                            getDimension(R.dimen.unselected_stroke_width))
                    .oval(true)
                    .build();
        }
        return mTransformation;
    }
}
