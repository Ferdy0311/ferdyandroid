package com.bahaso.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bahaso.R;

public class StateImageView extends AppCompatImageView {
    int mMode;
    PorterDuff.Mode mPorterDuffMode;
    ColorFilter mColorFilterTemp;
    public StateImageView(Context context) {
        this(context, null);
    }

    public StateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StateImageView,0,0);
        mMode = a.getInt(R.styleable.StateImageView_siv_disabled_mode,
                1);
        a.recycle();
        switch (mMode) {
            case 1:
                mPorterDuffMode = PorterDuff.Mode.SRC_ATOP;
                break;
            case 2:
                mPorterDuffMode = PorterDuff.Mode.MULTIPLY;
                break;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (isEnabled()) {
//            ViewUtil.clearColorFilter(this);
            clearColorFilter();
        }
        else {
            // ViewUtil.setColorFilter(this, Color.LTGRAY, mPorterDuffMode);
            setColorFilter(Color.LTGRAY, mPorterDuffMode);
        }
    }

    public void setDrawableColorFilter (ColorFilter colorFilter) {
        mColorFilterTemp = colorFilter;
        getDrawable().setColorFilter(mColorFilterTemp);
        invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        if (isEnabled()) {
            if (pressed) {
                if (null == mColorFilterTemp) {
                    setColorFilter(0x55000000, PorterDuff.Mode.SRC_ATOP);
                }
//                setColorFilter(Color.LTGRAY, mPorterDuffMode);
            } else {
                if (null == mColorFilterTemp ) {
                    clearColorFilter();
                }
                else {
                    getDrawable().setColorFilter(mColorFilterTemp);
                }
            }
        }
        else{
            setColorFilter(Color.LTGRAY, mPorterDuffMode);
        }
        super.setPressed(pressed);
    }
}
