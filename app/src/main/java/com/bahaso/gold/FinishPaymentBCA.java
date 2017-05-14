package com.bahaso.gold;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GoogleAnalyticsHelper;

public class FinishPaymentBCA extends AppCompatActivity {

    private GoogleAnalyticsHelper GoogleHelper;
    private ImageView imgFinishPay;
    private TextView tvFinishPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_payment_bca);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        getIntentData();

        imgFinishPay = (ImageView)findViewById(R.id.iv_bca_finish_pay);
        tvFinishPay = (TextView)findViewById(R.id.txt_finish_bca_pay);

        sendScreenImageName();

    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(this);
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(this,iCategoryId,iActionId,iLabelId );
    }

    private void sendScreenImageName() {
        String SCREEN_NAME = "BCA FINISH PAYMENT";
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private void getIntentData(){
        Intent out = getIntent();
        String titleFinishPay = out.getStringExtra("ATM_TRANSFER");
        getSupportActionBar().setTitle(titleFinishPay);
    }

    public void onClick_finishPayBCA(View v){
        switch (v.getId()){
            case R.id.layout_btn_finish_bca:
                SendEventGoogleAnalytics("BCA FINISH", "BTN BCA FINISH", "BTN BCA FINISH CLICKED");
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                finish();
                break;
        }
    }

}
