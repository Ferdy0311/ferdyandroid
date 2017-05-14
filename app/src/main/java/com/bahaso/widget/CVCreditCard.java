package com.bahaso.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.animation.FlipAnimator;
import com.bahaso.util.CreditCardUtils;


/**
 * Created by hendrysetiadi on 28/07/2016.
 */
public class CVCreditCard extends FrameLayout {

    private int mCardSide;

    View mVgOutline;
    View mVgContent;
    View vFrontOutline, vBackOutline,
            vFrontContent, vBackContent;

    String mCardHolderName;
    int mMonth;
    int mYear;
    String mRawCardNumber;
    int mCvv;

    int mCreditCardType;
    ImageView mIvLogo;

    TextView mTvHolderName;
    TextView mTvCreditCardNumber;
    TextView mTvExpireDate;
    TextView mTvCvv;

    public CVCreditCard(Context context) {
        super(context);
        init (context, null);
    }

    public CVCreditCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init (context, attrs);
    }

    public CVCreditCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init (context, attrs);
    }

    @SuppressWarnings("ResourceType")
    private void init (Context context, AttributeSet attrs) {
        mRawCardNumber = "";
        mCreditCardType = 0;

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.creditcard, this, true);

        mVgOutline = findViewById(R.id.vg_outline_container);
        vFrontOutline = mVgOutline.findViewById(R.id.v_front_outline);
        vBackOutline = mVgOutline.findViewById(R.id.v_back_outline);

        mVgContent = findViewById(R.id.vg_content_container);
        vFrontContent = mVgContent.findViewById(R.id.v_front_content);
        vBackContent = mVgContent.findViewById(R.id.v_back_content);

        mTvHolderName = (TextView) vFrontContent.findViewById(R.id.tv_holder_name);
        mTvCreditCardNumber = (TextView) vFrontContent.findViewById(R.id.tv_credit_card_number);
        mTvExpireDate = (TextView) vFrontContent.findViewById(R.id.tv_expiry_date);
        mTvCvv = (TextView) vBackContent.findViewById(R.id.tv_cvv);

        mIvLogo = (ImageView) vFrontOutline.findViewById(R.id.iv_logo);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.creditcard, 0, 0);

        mCardHolderName = a.getString(R.styleable.creditcard_card_holder_name);
        mMonth = a.getInt(R.styleable.creditcard_card_expiration_month, 0);
        mYear = a.getInt(R.styleable.creditcard_card_expiration_year, 0);
        mRawCardNumber = a.getString(R.styleable.creditcard_card_number);
        mCvv = a.getInt(R.styleable.creditcard_cvv, 0);
        mCardSide = a.getInt(R.styleable.creditcard_card_side, CreditCardUtils.CARD_SIDE_FRONT);

        setCreditCardNumber(mRawCardNumber);
        setCVV(mCvv);
        setCardExpiry(mMonth, mYear);
        setName(mCardHolderName);

        setCreditCardIcon(CreditCardUtils.verifyCreditCardType(mRawCardNumber));
//
//
//        paintCard();

        a.recycle();

        if(mCardSide == CreditCardUtils.CARD_SIDE_BACK) {
            flip(false,true);
        }
    }

    public void setCreditCardIcon(int creditCardIcon){
        switch (creditCardIcon) {
            case CreditCardUtils.VISA:
                mIvLogo.setImageResource(R.drawable.visa);
                break;
            case CreditCardUtils.MASTER_CARD:
                mIvLogo.setImageResource(R.drawable.mastercard);
                break;
            default:
                mIvLogo.setImageDrawable(null);
                break;
        }
    }

    public boolean isShowFront(){
        return (mCardSide == CreditCardUtils.CARD_SIDE_FRONT);
    }

    public void setCreditCardNumber(String rawCardNumber) {
        this.mRawCardNumber = rawCardNumber == null ? "" : rawCardNumber;

        String newCardNumber = mRawCardNumber;
        for(int i=mRawCardNumber.length();i<16;i++) {
            newCardNumber +="X";
        }

        String cardNumber = CreditCardUtils.handleCardNumber(newCardNumber, CreditCardUtils.DOUBLE_SPACE_SEPERATOR);
        mTvCreditCardNumber.setText(cardNumber);

        int creditCardType = CreditCardUtils.verifyCreditCardType(mRawCardNumber);
        if ( mCreditCardType != creditCardType){
            setCreditCardIcon(creditCardType);
            mCreditCardType = creditCardType;
        }
    }

    public void setCardExpiry(int month, int year) {
        mMonth = month;
        mYear = year;
        String monthEdit = "";
        if (month == 0) {
            monthEdit += "MM/";
        }
        else {
            if (month > 1 && month < 10) {
                monthEdit = "0" + month;
            } else if (month > 12) {
                monthEdit = "12";
            } else {
                monthEdit = String.valueOf(month);
            }

            if (monthEdit.length() == 2) {
                monthEdit += "/";
            } else {
                monthEdit += "M/";
            }
        }
        String yearString;
        if (year == 0) {
            yearString = "";
        }
        else {
            yearString = String.valueOf(year);
        }
        String modMonthYearPlusHint = monthEdit + yearString;
        int length = modMonthYearPlusHint.length();
        while (length < 7) {
            modMonthYearPlusHint+= "Y";
            length = modMonthYearPlusHint.length();
        }
        mTvExpireDate.setText(modMonthYearPlusHint);
    }

    public void setCVV(int cVV) {
        mCvv = cVV;
        if (mCvv == 0) {
            mTvCvv.setText(null);
        }
        else {
            mTvCvv.setText(String.valueOf(cVV));
        }
    }

    public void setName(String name) {
        mCardHolderName = (name == null)? null: name.trim();
        mTvHolderName.setText(mCardHolderName);
    }

    public String getCardHolderName() {
        return mCardHolderName;
    }

    public int getCvv() {
        return mCvv;
    }

    public int getExpiryMonth() {
        return mMonth;
    }

    public int getExpiryYear() {
        return mYear;
    }

    public int getCreditCardType() {
        return mCreditCardType;
    }

    public String getCreditCardNumber() {
        return mRawCardNumber;
    }

    public void flip(final boolean isShowFront, boolean isImmediate) {
//        if (isShowFront() == isShowFront) return;
//        mCardSide = 1-mCardSide;
        if(isImmediate) {
            vFrontContent.setVisibility(isShowFront?VISIBLE:GONE);
            vFrontOutline.setVisibility(isShowFront?VISIBLE:GONE);

            vBackContent.setVisibility(isShowFront?GONE:VISIBLE);
            vBackOutline.setVisibility(isShowFront?GONE:VISIBLE);
        }
        else {
            int duration =  600;

            FlipAnimator flipAnimator = new FlipAnimator(vFrontOutline, vBackOutline,
                    vFrontOutline.getWidth() / 2, /*vBackOutline.getHeight()*/  getHeight()/ 2);
            flipAnimator.setInterpolator(new OvershootInterpolator(0.5f));
            flipAnimator.setDuration(duration);

            if (isShowFront) {
                flipAnimator.reverse();
            }

            flipAnimator.setTranslateDirection(FlipAnimator.DIRECTION_Z);
            flipAnimator.setRotationDirection(FlipAnimator.DIRECTION_Y);
            mVgOutline.startAnimation(flipAnimator);

            FlipAnimator flipAnimator1 = new FlipAnimator(vFrontContent, vBackContent,
                    vFrontContent.getWidth() / 2, /*vBackContent.getHeight()*/
                    getHeight() / 2);
            flipAnimator1.setInterpolator(new OvershootInterpolator(0.5f));
            flipAnimator1.setDuration(duration);

            if (isShowFront) {
                flipAnimator1.reverse();
            }

            flipAnimator1.setTranslateDirection(FlipAnimator.DIRECTION_Z);
            flipAnimator1.setRotationDirection(FlipAnimator.DIRECTION_Y);

            mVgContent.startAnimation(flipAnimator1);
        }

    }
}
