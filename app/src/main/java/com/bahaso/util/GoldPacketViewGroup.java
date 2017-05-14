package com.bahaso.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bahaso.R;

public class GoldPacketViewGroup extends LinearLayout {
    TextView tvGoldNominal;
    TextView tvPriceNominal;
    String goldNominal;
    String priceNominal;
    public GoldPacketViewGroup(Context context) {
        this(context, null);
    }

    public GoldPacketViewGroup(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvGoldNominal = (TextView) findViewById(R.id.tv_gold_nominal);
        tvPriceNominal = (TextView) findViewById(R.id.tv_price_nominal);
        if (!TextUtils.isEmpty(goldNominal)) {
            tvGoldNominal.setText(goldNominal);
        }
        if (!TextUtils.isEmpty(priceNominal)) {
            tvPriceNominal.setText(priceNominal);
        }
    }

    public void setText(String goldNominal, String priceNominal){
        this.goldNominal = goldNominal;
        this.priceNominal = priceNominal;
        if (null!= tvGoldNominal) {
            tvGoldNominal.setText(goldNominal);
        }
        if (null!= tvPriceNominal) {
            tvPriceNominal.setText(priceNominal);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tvGoldNominal.setEnabled(enabled);
        tvPriceNominal.setEnabled(enabled);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
        //return super.onInterceptTouchEvent(ev);
    }
}
