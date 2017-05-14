package com.bahaso.gold;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.util.Fx;
import com.google.android.gms.analytics.Tracker;

import java.text.DecimalFormat;

public class TopUpGoldActivity extends AppCompatActivity {

    private int flag = 0;
    private long paketPrice;
    private TextView deskripsi_indomaret, deskripsi_bca, deskripsi_mandiri, deskripsi_permata,
            deskripsi_prima, deskripsi_bersama, deskripsi_alto;
    private LinearLayout layout_content_indomaret, layout_content_cc, layout_content_bca, layout_content_mandiri, layout_content_permata, layout_content_prima,
            layout_content_bersama, layout_content_alto, layoutBtnPayIndomaret, layoutBtnPayCC, layoutBtnPayBCA, layoutBtnPayMandiri,
            layoutBtnPayPermata, layoutBtnPayPrima, layoutBtnPayBersama, layoutBtnPayAlto;
    private String paketGold, paketCurrency, str_indomaret_info_array[], str_bca_info_array[], str_mandiri_info_array[]
            , str_permata_info_array[], str_prima_info_array[], str_bersama_info_array[], str_alto_info_array[];
    private static final String SCREEN_NAME = "PaymentMethod";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_gold_detail);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        deskripsi_indomaret = (TextView)findViewById(R.id.deskripsi_indomaret);
//        deskripsi_indomaret.setVisibility(View.GONE);

        deskripsi_bca = (TextView)findViewById(R.id.deskripsi_bca);
//        deskripsi_bca.setVisibility(View.GONE);

        deskripsi_mandiri = (TextView)findViewById(R.id.deskripsi_mandiri);
//        deskripsi_mandiri.setVisibility(View.GONE);

        deskripsi_permata = (TextView)findViewById(R.id.deskripsi_permata);
//        deskripsi_permata.setVisibility(View.GONE);

        deskripsi_prima = (TextView)findViewById(R.id.deskripsi_prima);
//        deskripsi_prima.setVisibility(View.GONE);

        deskripsi_alto = (TextView)findViewById(R.id.deskripsi_alto);
//        deskripsi_alto.setVisibility(View.GONE);

        deskripsi_bersama = (TextView)findViewById(R.id.deskripsi_bersama);
//        deskripsi_bersama.setVisibility(View.GONE);

        layout_content_indomaret = (LinearLayout)findViewById(R.id.layout_content_indomaret);
        layout_content_indomaret.setVisibility(View.GONE);

        layout_content_cc = (LinearLayout)findViewById(R.id.layout_content_cc);
        layout_content_cc.setVisibility(View.GONE);

        layout_content_bca = (LinearLayout)findViewById(R.id.layout_content_bca);
        layout_content_bca.setVisibility(View.GONE);

        layout_content_mandiri = (LinearLayout)findViewById(R.id.layout_content_mandiri);
        layout_content_mandiri.setVisibility(View.GONE);

        layout_content_permata = (LinearLayout)findViewById(R.id.layout_content_permata);
        layout_content_permata.setVisibility(View.GONE);

        layout_content_prima = (LinearLayout)findViewById(R.id.layout_content_prima);
        layout_content_prima.setVisibility(View.GONE);

        layout_content_bersama = (LinearLayout)findViewById(R.id.layout_content_bersama);
        layout_content_bersama.setVisibility(View.GONE);

        layout_content_alto = (LinearLayout)findViewById(R.id.layout_content_alto);
        layout_content_alto.setVisibility(View.GONE);

        sendScreenImageName();

        getIntentShowData();
    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(this);
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(this,iCategoryId,iActionId,iLabelId );
    }

    private void getIntentShowData(){
        Intent in = getIntent();
        paketGold = in.getStringExtra("PAKET_GOLD");
        paketPrice = in.getLongExtra("PAKET_PRICE", 0);
        paketCurrency = in.getStringExtra("PAKET_CURRENCY_PRICE");

        DecimalFormat formatter = new DecimalFormat("###,###,###,###");
        long LongNum = Long.valueOf(String.valueOf(paketPrice));
        String priceGoldIDR = "IDR " + formatter.format(LongNum);

        View vgTopUpInfo = findViewById(R.id.vg_topup_info);
        ((TextView)vgTopUpInfo.findViewById(R.id.tv_gold)).setText(paketGold);
        ((TextView)vgTopUpInfo.findViewById(R.id.tv_price)).setText(priceGoldIDR);

        getStringArrayContent("indomaret");
        getStringArrayContent("creditcard");
        getStringArrayContent("bca");
        getStringArrayContent("mandiri");
        getStringArrayContent("permata");
        getStringArrayContent("prima");
        getStringArrayContent("bersama");
        getStringArrayContent("alto");
    }

    private void getStringArrayContent(String caseString){
        switch (caseString){
            case "indomaret":
                str_indomaret_info_array = getResources().getStringArray(R.array.indomaret_info_array);
                for(int i = 0; i < str_indomaret_info_array.length; i++) {
                    deskripsi_indomaret.append(i + 1 + "." + str_indomaret_info_array[i] + "\n\n");
                }
                break;

            case "bca":
                str_bca_info_array = getResources().getStringArray(R.array.bca_info_array);
                for (int i = 0; i < str_bca_info_array.length; i++) {
                    deskripsi_bca.append(i + 1 + "." + str_bca_info_array[i] + "\n\n");
                }
                break;

            case "mandiri":
                str_mandiri_info_array = getResources().getStringArray(R.array.mandiri_info_array);
                for (int i = 0; i < str_mandiri_info_array.length; i++){
                    deskripsi_mandiri.append(i + 1 + "." + str_mandiri_info_array[i] + "\n\n");
                }
                break;

            case "permata":
                str_permata_info_array = getResources().getStringArray(R.array.permata_info_array);
                for (int i = 0; i < str_permata_info_array.length; i++){
                    deskripsi_permata.append(i + 1 + "." + str_permata_info_array[i] + "\n\n");
                }
                break;

            case "prima":
                str_prima_info_array = getResources().getStringArray(R.array.prima_info_array);
                for (int i = 0; i < str_prima_info_array.length; i++){
                    deskripsi_prima.append(i + 1 + "." + str_prima_info_array[i] + "\n\n");
                }
                break;

            case "bersama":
                str_bersama_info_array = getResources().getStringArray(R.array.bersama_info_array);
                for (int i = 0; i < str_bersama_info_array.length; i++){
                    deskripsi_bersama.append(i + 1 + "." + str_bersama_info_array[i] + "\n\n");
                }
                break;

            case "alto":
                str_alto_info_array = getResources().getStringArray(R.array.alto_info_array);
                for (int i = 0; i < str_alto_info_array.length; i++){
                    deskripsi_alto.append(i + 1 + "." + str_alto_info_array[i] + "\n\n");
                }
                break;
        }
    }

    public void onClick_topupgold(View v){
        switch(v.getId()){
            case R.id.expandIndomaret:
                toggleDesk("indomaret");
                break;

            case R.id.expandCreditCard:
                toggleDesk("creditcard");
                break;

            case R.id.expandBCA:
                toggleDesk("bca");
                break;

            case R.id.expandMandiri:
                toggleDesk("mandiri");
                break;

            case R.id.expandPermata:
                toggleDesk("permata");
                break;

            case R.id.expandPrima:
                toggleDesk("prima");
                break;

            case R.id.expandBersama:
                toggleDesk("bersama");
                break;

            case R.id.expandAlto:
                toggleDesk("alto");
                break;

            case R.id.layout_btn_pay_cc:
                SendEventGoogleAnalytics("CREDIT CARD", "BTN CREDIT CARD", "BTN CREDIT CARD CLICKED");
                Intent cc = new Intent(this, PayTopUpWithCC.class);
                cc.putExtra("PAKET_GOLD", paketGold);
                cc.putExtra("PAKET_PRICE", paketPrice);
                cc.putExtra("PAKET_CURRENCY", paketCurrency);
                startActivity(cc);
                finish();
                break;

            case R.id.layout_btn_permata:
                SendEventGoogleAnalytics("ATM TRANSFER", "BTN ATM PERMATA", "BTN ATM PERMATA CLICKED");
                Intent atm1 = new Intent(this, PayTopUpWithATM.class);
                atm1.putExtra("ATM_TRANSFER", "TRANSFER PERMATA");
                atm1.putExtra("PAKET_GOLD", paketGold);
                atm1.putExtra("PAKET_PRICE", paketPrice);
                atm1.putExtra("PAKET_CURRENCY", paketCurrency);
                atm1.putExtra("flag_bank", "permata");
                startActivity(atm1);
                finish();
                break;
            case R.id.layout_btn_prima:
                SendEventGoogleAnalytics("ATM TRANSFER", "BTN ATM PRIMA", "BTN ATM PRIMA CLICKED");
                Intent atm2 = new Intent(this, PayTopUpWithATM.class);
                atm2.putExtra("ATM_TRANSFER", "TRANSFER PRIMA");
                atm2.putExtra("PAKET_GOLD", paketGold);
                atm2.putExtra("PAKET_PRICE", paketPrice);
                atm2.putExtra("PAKET_CURRENCY", paketCurrency);
                atm2.putExtra("flag_bank", "prima");
                startActivity(atm2);
                finish();
                break;

            case R.id.layout_btn_bersama:
                SendEventGoogleAnalytics("ATM TRANSFER", "BTN ATM BERSAMA", "BTN ATM BERSAMA CLICKED");
                Intent atm3 = new Intent(this, PayTopUpWithATM.class);
                atm3.putExtra("ATM_TRANSFER", "TRANSFER BERSAMA");
                atm3.putExtra("PAKET_GOLD", paketGold);
                atm3.putExtra("PAKET_PRICE", paketPrice);
                atm3.putExtra("PAKET_CURRENCY", paketCurrency);
                atm3.putExtra("flag_bank", "bersama");
                startActivity(atm3);
                finish();
                break;

            case R.id.layout_btn_alto:
                SendEventGoogleAnalytics("ATM TRANSFER", "BTN ATM ALTO", "BTN ATM ALTO CLICKED");
                Intent atm4 = new Intent(this, PayTopUpWithATM.class);
                atm4.putExtra("ATM_TRANSFER", "TRANSFER ALTO");
                atm4.putExtra("PAKET_GOLD", paketGold);
                atm4.putExtra("PAKET_PRICE", paketPrice);
                atm4.putExtra("PAKET_CURRENCY", paketCurrency);
                atm4.putExtra("flag_bank", "alto");
                startActivity(atm4);
                finish();
                break;

            case R.id.layout_btn_bca:
                SendEventGoogleAnalytics("ATM TRANSFER", "BTN ATM BCA", "BTN ATM BCA CLICKED");
                Intent atm5 = new Intent(this, PayTopUpWithATM.class);
                atm5.putExtra("ATM_TRANSFER","TRANSFER BCA");
                atm5.putExtra("PAKET_GOLD", paketGold);
                atm5.putExtra("PAKET_PRICE", paketPrice);
                atm5.putExtra("PAKET_CURRENCY", paketCurrency);
                atm5.putExtra("flag_bank", "bca");
                startActivity(atm5);
                finish();
                break;

            case R.id.layout_btn_indomaret:
                SendEventGoogleAnalytics("INDOMARET", "BTN PAYMENT VIA INDOMARET", "BTN PAYMENT INDOMARET CLICKED");
                Intent atm6 = new Intent(this, PayTopUpWithATM.class);
                atm6.putExtra("ATM_TRANSFER","INDOMARET");
                atm6.putExtra("PAKET_GOLD", paketGold);
                atm6.putExtra("PAKET_PRICE", paketPrice);
                atm6.putExtra("PAKET_CURRENCY", paketCurrency);
                atm6.putExtra("flag_bank", "indomaret");
                startActivity(atm6);
                finish();
                break;

            case R.id.layout_btn_mandiri:
                SendEventGoogleAnalytics("MANDIRI", "BTN PAYMENT MANDIRI", "BTN PAYMENT MANDIRI CLICKED");
                Intent atm7 = new Intent(this, PayTopUpWithATM.class);
                atm7.putExtra("ATM_TRANSFER","MANDIRI");
                atm7.putExtra("PAKET_GOLD", paketGold);
                atm7.putExtra("PAKET_PRICE", paketPrice);
                atm7.putExtra("PAKET_CURRENCY", paketCurrency);
                atm7.putExtra("flag_bank", "mandiri");
                startActivity(atm7);
                finish();
                break;
        }
    }

    private void toggleDesk(String flag){
        switch (flag){
            case "indomaret":
                toggleContent(flag);
                break;

            case "creditcard":
                toggleContent(flag);
                break;

            case "bca":
                toggleContent(flag);
                break;

            case "mandiri":
                toggleContent(flag);
                break;

            case "permata":
                toggleContent(flag);
                break;

            case "prima":
                toggleContent(flag);
                break;

            case "bersama":
                toggleContent(flag);
                break;

            case "alto":
                toggleContent(flag);
                break;

        }
    }

    private void toggleContent(String flag){
        switch (flag){
            case "indomaret":
                toggleContentIndomaret();
                break;

            case "creditcard":
                toggleContentCreditCard();
                break;

            case "bca":
                toggleContentBCA();
                break;

            case "mandiri":
                toggleContentMandiri();
                break;

            case "permata":
                toggleContentPermata();
                break;

            case "prima":
                toggleContentPrima();
                break;

            case "bersama":
                toggleContentBersama();
                break;

            case "alto":
                toggleContentAlto();
                break;
        }
    }

    private void toggleContentIndomaret(){
        if(layout_content_indomaret.isShown()){
            layout_content_indomaret.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_indomaret);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_indomaret);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_indomaret.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_indomaret);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_indomaret);
            flag = 1;

        }
    }

    private void toggleContentCreditCard(){
        if(layout_content_cc.isShown()){
            layout_content_cc.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_cc);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight2);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_cc);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_cc.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_cc);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_cc);
            flag = 2;

        }
    }

    private void toggleContentBCA(){
        if(layout_content_bca.isShown()){
            layout_content_bca.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_bca);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight3);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_bca);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_bca.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_bca);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_bca);
            flag = 3;

        }
    }

    private void toggleContentMandiri(){
        if(layout_content_mandiri.isShown()){
            layout_content_mandiri.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_mandiri);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight4);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_mandiri);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_mandiri.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_mandiri);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_mandiri);
            flag = 4;

        }
    }

    private void toggleContentPermata(){
        if(layout_content_permata.isShown()){
            layout_content_permata.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_permata);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight5);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_permata);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_permata.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_permata);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_permata);
            flag = 5;

        }
    }

    private void toggleContentPrima(){
        if(layout_content_prima.isShown()){
            layout_content_prima.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_permata);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight6);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_prima);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_prima.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_prima);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_prima);
            flag = 6;

        }
    }

    private void toggleContentBersama(){
        if(layout_content_bersama.isShown()){
            layout_content_bersama.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_bersama);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight7);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_bersama);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_bersama.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_bersama);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_bersama);
            flag = 7;

        }
    }

    private void toggleContentAlto(){
        if(layout_content_alto.isShown()){
            layout_content_alto.setVisibility(View.GONE);
            Fx.slide_up(this, layout_content_alto);
            RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight8);
            int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 10, getResources().getDisplayMetrics());
            int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) 7, getResources().getDisplayMetrics());
            background.setPadding(tenDip, tenDip, tenDip, sevenDip);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_alto);
            detailArrow.setRotation(360);
        } else {
            dropupdownlayout(flag);
            layout_content_alto.setVisibility(View.VISIBLE);
            AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_alto);
            detailArrow.setRotation(180);
            Fx.slide_down(this, layout_content_alto);
            flag = 8;

        }
    }


    private void dropupdownlayout(int flagdropdown){
        switch (flagdropdown){
            case 1:
                layout_content_indomaret.setVisibility(View.GONE);
                Fx.slide_up(this, layout_content_indomaret);
                flag = 0;
                AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_indomaret);
                detailArrow.setRotation(360);
                break;

            case 2:
                layout_content_cc.setVisibility(View.GONE);
                Fx.slide_up(this, layout_content_cc);
                flag = 0;
                AppCompatImageView detailArrow2 = (AppCompatImageView) findViewById(R.id.down_arrow_cc);
                detailArrow2.setRotation(360);
                break;

            case 3:
                layout_content_bca.setVisibility(View.GONE);
                Fx.slide_up(this, layout_content_bca);
                flag = 0;
                AppCompatImageView detailArrow3 = (AppCompatImageView) findViewById(R.id.down_arrow_bca);
                detailArrow3.setRotation(360);
                break;

            case 4:
                layout_content_mandiri.setVisibility(View.GONE);
                Fx.slide_up(this,layout_content_mandiri);
                flag = 0;
                AppCompatImageView detailArrow4 = (AppCompatImageView) findViewById(R.id.down_arrow_mandiri);
                detailArrow4.setRotation(360);
                break;

            case 5:
                layout_content_permata.setVisibility(View.GONE);
                Fx.slide_up(this,layout_content_permata);
                flag = 0;
                AppCompatImageView detailArrow5 = (AppCompatImageView) findViewById(R.id.down_arrow_permata);
                detailArrow5.setRotation(360);
                break;

            case 6:
                layout_content_prima.setVisibility(View.GONE);
                Fx.slide_up(this,layout_content_prima);
                flag = 0;
                AppCompatImageView detailArrow6 = (AppCompatImageView) findViewById(R.id.down_arrow_prima);
                detailArrow6.setRotation(360);
                break;

            case 7:
                layout_content_bersama.setVisibility(View.GONE);
                Fx.slide_up(this,layout_content_bersama);
                flag = 0;
                AppCompatImageView detailArrow7 = (AppCompatImageView) findViewById(R.id.down_arrow_bersama);
                detailArrow7.setRotation(360);
                break;

            case 8:
                layout_content_alto.setVisibility(View.GONE);
                Fx.slide_up(this,layout_content_alto);
                flag = 0;
                AppCompatImageView detailArrow8 = (AppCompatImageView) findViewById(R.id.down_arrow_alto);
                detailArrow8.setRotation(360);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent in = new Intent(this, MainActivity.class);
                in.putExtra(getString(R.string.flag_edit_gold), 1);
                startActivity(in);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        in.putExtra(getString(R.string.flag_edit_gold), 1);
        startActivity(in);
        finish();
    }
}
