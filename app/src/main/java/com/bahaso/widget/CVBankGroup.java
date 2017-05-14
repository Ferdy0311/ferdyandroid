package com.bahaso.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.animation.AnimationHelper;

/**
 * Created by hendrysetiadi on 26/07/2016.
 */
public class CVBankGroup extends LinearLayout {

    String mName;
    int mImageRes;
    TextView mtvBankGroup;
    ImageView mIvBankGroupName;
    ImageView mIvIndicator;
    boolean mIsExpanded;

    public CVBankGroup(Context context) {
        super(context);
        init (context, null);
    }

    public CVBankGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init (context, attrs);
    }

    public CVBankGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init (context, attrs);
    }

    @SuppressWarnings("ResourceType")
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CVBankGroup, 0, 0);
        mName = a.getString(R.styleable.CVBankGroup_cvbankgroup_name);
        mImageRes = a.getResourceId(R.styleable.CVBankGroup_cvbankgroup_src, -1);
        mIsExpanded = a.getBoolean(R.styleable.CVBankGroup_cvbankgroup_isExpanded, false);
        a.recycle();

        // http://stackoverflow.com/questions/19034597/
        // get-multiple-style-attributes-with-obtainstyledattributes
        int[] attrsArray = {
                android.R.attr.background,
                android.R.attr.paddingBottom,
                android.R.attr.paddingLeft,
                android.R.attr.paddingRight,
                android.R.attr.paddingTop
        };

        // http://stackoverflow.com/questions/36254914/
        // error-expected-resource-of-type-styleable-resourcetype-error
        a = context.obtainStyledAttributes(attrs, attrsArray,0,0);
        int background = a.getResourceId(0, -1);
        int paddingBottom = a.getDimensionPixelSize(1, 0);
        int paddingLeft = a.getDimensionPixelSize(2, 0);
        int paddingRight = a.getDimensionPixelSize(3, 8 * 2);
        int paddingTop = a.getDimensionPixelSize(4, 0);
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClickable(true);
        setPadding(paddingLeft,paddingTop,paddingRight, paddingBottom);

        if (background > 0) {
            setBackground(ContextCompat.getDrawable(context, background));
        }
        else {
            // API always higher 11
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            setBackgroundResource(outValue.resourceId);
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cv_item_bank_group, this, true);

        mtvBankGroup = (TextView) v.findViewById(R.id.tv_bank_group);
        mtvBankGroup.setText(mName);

        mIvBankGroupName = (ImageView) v.findViewById(R.id.iv_bank_group_name);
        if (mImageRes > 0) {
            mIvBankGroupName.setImageResource(mImageRes);
        }

        mIvIndicator = (ImageView) v.findViewById(R.id.iv_indicator);

        if (mIsExpanded) {
            mIvIndicator.setImageResource(R.drawable.svg_continue_down);
        }
        else {
            mIvIndicator.setImageResource(R.drawable.svg_continue);
        }
        mIvIndicator.getDrawable().setColorFilter(
                ContextCompat.getColor(getContext(),
                        R.color.bahaso_dark_blue),
                PorterDuff.Mode.SRC_ATOP
        );
    }

    public void setExpand(boolean isExpanded, boolean useAnimation) {
        if (isExpanded) { // right to down
            AnimationHelper.startAnimatedImageView(
                    mIvIndicator,
                    R.drawable.animated_expand_right_2_down_new,
                    R.drawable.svg_continue_down,
                    ContextCompat.getColor(getContext(),
                            R.color.bahaso_dark_blue),
                    PorterDuff.Mode.SRC_ATOP,
                    useAnimation
            );
        }
        else { // down to right
            AnimationHelper.startAnimatedImageView(
                    mIvIndicator,
                    R.drawable.animated_expand_down_2_right_new,
                    R.drawable.svg_continue,
                    ContextCompat.getColor(getContext(),
                            R.color.bahaso_dark_blue),
                    PorterDuff.Mode.SRC_ATOP,
                    useAnimation
            );
        }
        mIsExpanded = isExpanded;
    }

    public void setProperties (String name, int imageRes){
        this.mName = name;
        this.mImageRes = imageRes;
        mtvBankGroup.setText(mName);
        mIvBankGroupName.setImageResource(imageRes);
        invalidate();
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }
}
