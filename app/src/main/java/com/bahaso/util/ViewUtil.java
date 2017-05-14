package com.bahaso.util;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bahaso.globalvar.GlobalVar;

//import com.bahaso.R;
//import com.bahaso.application.MyApplication;
//import com.bahaso.widget.StateImageView;

public class ViewUtil {
//    public static final int SPACING2 = (int) MyApplication.getContext().getResources().getDimension(R.dimen.spacing_2);
//    public static final int ELEVATION2 = (int) MyApplication.getContext().getResources().getDimension(R.dimen.elevation_2);
    //public static final int mSlop = 1;
    public static final int mSlop = ViewConfiguration.get(GlobalVar.getContext()).getScaledTouchSlop();

//    public static final int ALIGN_CENTER = 0;
//    public static final int ALIGN_JUSTIFY = 1;
//    public static final int ALIGN_LEFT = 2;
//    public static final int ALIGN_RIGHT = 3;
//
//    public static final int FONTSIZE_SMALL = 0;
//    public static final int FONTSIZE_MEDIUM = 1;
//    public static final int FONTSIZE_LARGE = 2;

//    private static Typeface mThinTypeface;
    private static Typeface mPrimaryTypeface;
    private static Typeface mBoldTypeface;
//    private static Typeface mBolderTypeface;
//    private static int mediumFontSize = -1;
//    private static int largeFontSize = -1;
//    private static int smallFontSize = -1;

    public static Typeface getRegularTypeface (){
        if (mPrimaryTypeface == null){
            mPrimaryTypeface = Typeface.createFromAsset(GlobalVar.getInstance().getApplicationContext().getAssets(), "fonts/LatoMedium.ttf");
        }
        return mPrimaryTypeface;
    }

//    public static Typeface getThinTypeface (){
//        if (mThinTypeface == null){
//            mThinTypeface = Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/LatoLight.ttf");
//        }
//        return mThinTypeface;
//    }

    public static Typeface getBoldTypeface (){
        if (mBoldTypeface == null){
            mBoldTypeface = Typeface.createFromAsset(GlobalVar.getInstance().getApplicationContext().getAssets(), "fonts/LatoBold.ttf");
        }
        return mBoldTypeface;
    }

//    public static Typeface getBolderTypeface (){
//        if (mBolderTypeface == null){
//            mBolderTypeface = Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/LatoBlack.ttf");
//        }
//        return mBolderTypeface;
//    }

//    public static Typeface getTrirongTypeface (){
//        return Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/TrirongItalic.ttf");
//    }

//    public static String getWrappedHtml(String content, int alignEnum, int fontSizeEnum) {
//        String alignString;
//        switch (alignEnum){
//            case ALIGN_CENTER: alignString = "center";
//                break;
//            case ALIGN_JUSTIFY: alignString = "justify";
//                break;
//            case ALIGN_LEFT: alignString = "left";
//                break;
//            case ALIGN_RIGHT: alignString = "right";
//                break;
//            default: alignString = "center";
//                break;
//        }
//        int fontSize;
//        switch (fontSizeEnum){
//            case FONTSIZE_SMALL: fontSize = getSmallFontSize();
//                break;
//            case FONTSIZE_MEDIUM: fontSize = getMediumFontSize();
//                break;
//            case FONTSIZE_LARGE: fontSize = getLargeFontSize();
//                break;
//            default: fontSize = getMediumFontSize();
//                break;
//        }
//        String begin = "<head>" +
//                "<style type=\"text/css\">" +
//                "    @font-face { font-family: 'Lato'; src: url('file:///android_asset/fonts/LatoMedium.ttf');}" +
//                "    body {" +
//                "    font-family: Lato;" +
//                "    font-size:"+ fontSize +"px;" +
//                "    text-align:"+ alignString+";" +
//                "}" +
//                "</style>" +
//                "</head>" +
//                "<body style=\"margin: 0; padding: 0\">";
//        String end = "</body>" +
//                "</html>";
//        return begin + content + end;
//    }

//    public static void setBackgroundColor(View v, int color){
//        Drawable background = v.getBackground();
//        if (background instanceof ShapeDrawable) {
//            // cast to 'ShapeDrawable'
//            ShapeDrawable shapeDrawable = (ShapeDrawable)background;
//            shapeDrawable.getPaint().setColor(color);
//        } else if (background instanceof GradientDrawable) {
//            // cast to 'GradientDrawable'
//            GradientDrawable gradientDrawable = (GradientDrawable)background;
//            gradientDrawable.setColor(color);
//        }
//    }

    public static void setColorFilter(ImageView v, int color){
        Drawable drawable = v.getDrawable();
        if (null!= drawable){
            ColorFilter cf = new PorterDuffColorFilter(color
                    , PorterDuff.Mode.MULTIPLY);
            drawable.setColorFilter(cf);
            if (v instanceof StateImageView) {
                ((StateImageView) v ).setDrawableColorFilter(cf);
            }
        }
    }

    public static void setColorFilter(ImageView v, int color, PorterDuff.Mode mode){
        Drawable drawable = v.getDrawable();
        if (null!= drawable){
            ColorFilter cf = new PorterDuffColorFilter(color
                    , mode);
            drawable.setColorFilter(cf);
            if (v instanceof StateImageView) {
                ((StateImageView) v ).setDrawableColorFilter(cf);
            }
        }
    }

    public static void setColorFilterGrayScale(ImageView imageView){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public static void clearColorFilter(ImageView v){
        Drawable drawable = v.getDrawable();
        if (null!= drawable){
            drawable.clearColorFilter();
        }
    }

//    public static int getSmallFontSize(){
//        if (smallFontSize < 0){
//            smallFontSize = spFromPx(MyApplication.getContext(),
//                    MyApplication.getContext().getResources().getDimension(R.dimen.font_size));
//        }
//        return smallFontSize;
//    }

//    public static int getMediumFontSize(){
//        if (mediumFontSize < 0){
//            mediumFontSize = spFromPx(MyApplication.getContext(),
//                    MyApplication.getContext().getResources().getDimension(R.dimen.medium_font_size));
//        }
//        return mediumFontSize;
//    }

//    public static int getLargeFontSize(){
//        if (largeFontSize < 0){
//            largeFontSize = spFromPx(MyApplication.getContext(),
//                    MyApplication.getContext().getResources().getDimension(R.dimen.large_font_size));
//        }
//        return largeFontSize;
//    }

    public static int pxFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int dpFromPx(final Context context, final float dimensionPx) {
        return (int) (dimensionPx / context.getResources().getDisplayMetrics().density);
    }

    public static int spFromPx(Context context, float dimensionPx) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dimensionPx/scaledDensity);
    }

    public static int pxFromSp(Context context, float sp) {
        final float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }

    static private void getRelativePosition(View myView, ViewParent root, int[] location) {
        location[0] = 0;
        location[1] = 0;
        getRelativePositionRec(myView, root, location);
    }

    static private void getRelativePositionRec(View myView, ViewParent root, int[] location) {
        if (myView.getParent() == root) {
            location[0] += myView.getLeft();
            location[1] += myView.getTop();
        } else {
            location[0] += myView.getLeft();
            location[1] += myView.getTop();
            getRelativePositionRec((View) myView.getParent(), root, location);
        }
    }

    public static boolean isView50PercentVisibleInScrollView(View view, ScrollView scrollView) {
        Rect parentRect = new Rect();
        scrollView.getLocalVisibleRect(parentRect);
        int[] location = new int[2];
        ViewUtil.getRelativePosition(view, scrollView, location);
        int bottom = location[1] +view.getHeight();
        int middleVertical = (location[1] + bottom) / 2;
        if (middleVertical >= parentRect.top && middleVertical <= parentRect.bottom) {
            return true;
        }
        return false;
    }
}
