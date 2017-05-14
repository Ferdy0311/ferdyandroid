package com.bahaso.gold;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.model.PayBankGroup;
import com.bahaso.util.Fx;
import com.bahaso.widget.CVBankGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PayTopUpWithATM extends AppCompatActivity /*implements View.OnClickListener*/{

    private int mSelectedItem = -1;
    private long paketPrice = 0;
    private TextView tv_vAccount, tvPricePaket, tvDatetime, deskripsiContentProcedurePayment,
            tvTotalPembayaranGold, tvComfortFee, tvTotal_KodeUnik, tvHeader1, tvHeader2, txtBtnTransfer;
    protected ViewGroup vgExpandleBank;
    private SharedPreferences sharedpref;
    protected List<PayBankGroup> mBankGroupList = new ArrayList<>();;
    private String URL_BANK_TRANSFER, paketGold, paketCurrency, amountParam, virtualAccount, titleATMTransfer,
            URL_BCA_TRANSFER, URL_INDOMARET_TRANSFER, flag_bank;
    private String SCREEN_NAME = "PaymentMethodTransfer";
    private GoogleAnalyticsHelper GoogleHelper;
    private ImageView ivFlag;
    private LinearLayout layoutContent, layoutBCA;
    private RelativeLayout layoutParentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_topup_with_atm);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_BANK_TRANSFER = global.getBaseURLpath() + "bank_transfer/";
        URL_BCA_TRANSFER = global.getBaseURLpath() + "bca_transfer/";
        URL_INDOMARET_TRANSFER = global.getBaseURLpath() + "indomaret/";

        tv_vAccount = (TextView)findViewById(R.id.tv_v_account);
        tvPricePaket = (TextView)findViewById(R.id.tv_pricePay);
        tvDatetime = (TextView)findViewById(R.id.tv_datetime);
        ivFlag = (ImageView)findViewById(R.id.iv_flag);

        deskripsiContentProcedurePayment = (TextView)findViewById(R.id.deskripsi_content);
        layoutContent = (LinearLayout)findViewById(R.id.layout_content);
        layoutContent.setVisibility(View.GONE);

        layoutParentContent = (RelativeLayout)findViewById(R.id.expandHighlight);
        layoutBCA = (LinearLayout)findViewById(R.id.layout_khusus_bca);
        tvTotalPembayaranGold = (TextView)findViewById(R.id.total_pembayaran_gold);
        tvComfortFee = (TextView)findViewById(R.id.biaya_kenyamanan);
        tvTotal_KodeUnik = (TextView)findViewById(R.id.total_dan_kode_unik);

        tvHeader1 = (TextView)findViewById(R.id.txt_header_transfer1);
        tvHeader2 = (TextView)findViewById(R.id.txt_header_transfer2);
        txtBtnTransfer = (TextView)findViewById(R.id.txt_btn_transfer);

        getIntentShowData();

        if (!flag_bank.equals("bca") && !flag_bank.equals("indomaret")) {
            requestBankTransfer(getSharedPref().getString(getString(R.string.tokenlogin), ""), amountParam);
        } else if (flag_bank.equals("bca")){
            requestBcaTransfer(getSharedPref().getString(getString(R.string.tokenlogin), ""), amountParam);
        } else if (flag_bank.equals("indomaret")){
            requestIndomaretTransfer(getSharedPref().getString(getString(R.string.tokenlogin), ""), amountParam);
        }

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
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void getIntentShowData(){
        Intent in = getIntent();
        titleATMTransfer = in.getStringExtra("ATM_TRANSFER");
        paketGold = in.getStringExtra("PAKET_GOLD");
        paketPrice = in.getLongExtra("PAKET_PRICE", 0);
        paketCurrency = in.getStringExtra("PAKET_CURRENCY");
        flag_bank = in.getStringExtra("flag_bank");

        DecimalFormat formatter = new DecimalFormat("###,###,###,###");
        long LongNum = Long.valueOf(String.valueOf(paketPrice));
        String priceGoldIDR = "IDR " + formatter.format(LongNum);

        tvPricePaket.setText(priceGoldIDR);
        tvTotalPembayaranGold.setText(priceGoldIDR);

        amountParam = String.valueOf(paketPrice/10000);

        getSupportActionBar().setTitle(titleATMTransfer);

        switch (flag_bank){
            case "permata":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.permata));
                String[] str_permata_info_array = getResources().getStringArray(R.array.permata_info_array);
                for (int i = 0; i < str_permata_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_permata_info_array[i] + "\n\n");
                }
                break;

            case "prima":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.prima));
                String[] str_prima_info_array = getResources().getStringArray(R.array.prima_info_array);
                for (int i = 0; i < str_prima_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_prima_info_array[i] + "\n\n");
                }
                break;

            case "bersama":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.bersama));
                String[] str_bersama_info_array = getResources().getStringArray(R.array.bersama_info_array);
                for (int i = 0; i < str_bersama_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_bersama_info_array[i] + "\n\n");
                }
                break;

            case "alto":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.alto));
                String[] str_alto_info_array = getResources().getStringArray(R.array.alto_info_array);
                for (int i = 0; i < str_alto_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_alto_info_array[i] + "\n\n");
                }
                break;

            case "indomaret":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.indomaret));
                String[] str_indomaret_info_array = getResources().getStringArray(R.array.indomaret_info_array);
                for (int i = 0; i < str_indomaret_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_indomaret_info_array[i] + "\n\n");
                }
                break;

            case "bca":
                layoutParentContent.setVisibility(View.GONE);
                layoutBCA.setVisibility(View.VISIBLE);
                tvHeader1.setText(getString(R.string.txt_make_sure_nominal_payment));
                tvHeader2.setText(getString(R.string.txt_nama_dan_nomor_rekening));
                String rekBCA = "764 089 8376 \n PT Bahaso Intermedia Cakrawala";
                tvPricePaket.setText(rekBCA);
                txtBtnTransfer.setText(getString(R.string.txt_already_paid));
                break;

            case "mandiri":
                ivFlag.setImageDrawable(getResources().getDrawable(R.drawable.mandiri));
                String[] str_mandiri_info_array = getResources().getStringArray(R.array.mandiri_info_array);
                for (int i = 0; i < str_mandiri_info_array.length; i++){
                    deskripsiContentProcedurePayment.append(i + 1 + "." + str_mandiri_info_array[i] + "\n\n");
                }
                break;
        }

    }

    private void notifyBankGroupChanges (){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int i = 0, sizei=mBankGroupList.size(); i<sizei; i++) {
            PayBankGroup payBankGroup = mBankGroupList.get(i);
            final CVBankGroup cvBankGroup = (CVBankGroup) inflater.inflate(
                    R.layout.single_generic_bank_group, vgExpandleBank, false);
            cvBankGroup.setId(i);
            cvBankGroup.setProperties(payBankGroup.getName(),
                    payBankGroup.getImageRes());

            final ViewGroup childViewGroup = (ViewGroup) inflater.inflate(
                    R.layout.linear_layout, vgExpandleBank, false);
            // to enable search by tag
            childViewGroup.setTag(i);

            int no = 1;
            for (int j = 0; j < payBankGroup.getStepInfoCount(); j++) {
                String stepInfo = payBankGroup.getStepInfo(j);
                if (stepInfo.startsWith("~")) {
                    String newStepInfo = stepInfo.substring(1);
                    // it is not a numbered list
                    final View childSingleStepItem = inflater.inflate(
                            R.layout.single_step_item, childViewGroup, false);
                    ((TextView)childSingleStepItem.findViewById(R.id.tv_step_info))
                            .setText(Html.fromHtml(newStepInfo));
                    childViewGroup.addView(childSingleStepItem);
                }
                else {
                    // it is a numbered list
                    final View childSingleStepItem = inflater.inflate(
                            R.layout.single_step_item_no, childViewGroup, false);
                    ((TextView)childSingleStepItem.findViewById(R.id.tv_number))
                            .setText(String.valueOf(no));
                    no++;
                    // always input argument even though it might not need it
                    ((TextView)childSingleStepItem.findViewById(R.id.tv_step_info))
                            .setText(Html.fromHtml(
                                    String.format(stepInfo, virtualAccount)));
                    childViewGroup.addView(childSingleStepItem);
                }

            }
            if (mSelectedItem == i) {
                cvBankGroup.setExpand(true, false);
            }

            vgExpandleBank.addView(cvBankGroup);
            vgExpandleBank.addView(childViewGroup);

            vgExpandleBank.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("ObsoleteSdkInt")
                        @Override
                        public void onGlobalLayout() {
                            // int height = v.getHeight();
                            childViewGroup.getLayoutParams().height = childViewGroup.getHeight();
                            if ((int)childViewGroup.getTag() == mSelectedItem) {
                                childViewGroup.setVisibility(View.VISIBLE );
                            }
                            else {
                                childViewGroup.setVisibility(View.GONE);
                            }
                            if (Build.VERSION.SDK_INT < 16) {
                                //noinspection deprecation
                                vgExpandleBank.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                vgExpandleBank.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

//                            cvBankGroup.setOnClickListener(PayTopUpWithATM.this); // onclick disable
                        }
                    });
        }
    }

    private void requestBankTransfer(final String token, final String amountParam){
        StringRequest request = new StringRequest(Request.Method.POST, URL_BANK_TRANSFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("jsonResponse tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                virtualAccount = jsonData.getString("va-number");
                                String price2Pay = jsonData.getString("price");
                                String expiredPay = jsonData.getString("expired");

                                @SuppressWarnings("deprecation") DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                                        getApplicationContext().getResources().getConfiguration().locale);
                                try {
                                    Date dt = df.parse(expiredPay);
                                    @SuppressWarnings("deprecation") SimpleDateFormat dateFormat =
                                            new SimpleDateFormat(getString(R.string.date_hour_format),
                                            getApplicationContext().getResources().getConfiguration().locale);
                                    String str = dateFormat.format(dt);
                                    tvDatetime.setText(str);
                                }
                                catch (Exception e) {

                                }

                                tv_vAccount.setText(virtualAccount);

                            } else {
                                Log.i("responGold tes1", "FALSE");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("Authorization", "Bearer " + token );
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("amount", amountParam);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void requestBcaTransfer(final String token, final String amountParam){
        StringRequest request = new StringRequest(Request.Method.POST, URL_BCA_TRANSFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("jsonResponse tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                int kode_unik = jsonData.getInt("unique_code");
                                int price = jsonData.getInt("price");
                                String expiredPay = jsonData.getString("expired");

                                String forComfortFee = "-IDR " + String.valueOf(kode_unik);
                                tvComfortFee.setText(forComfortFee);

                                String forTotal_KodeUnik = "IDR " + String.valueOf(price);
                                tvTotal_KodeUnik.setText(forTotal_KodeUnik);

                                tv_vAccount.setText(forTotal_KodeUnik);

                                @SuppressWarnings("deprecation") DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                                        getApplicationContext().getResources().getConfiguration().locale);
                                try {
                                    Date dt = df.parse(expiredPay);
                                    @SuppressWarnings("deprecation") SimpleDateFormat dateFormat =
                                            new SimpleDateFormat(getString(R.string.date_hour_format),
                                                    getApplicationContext().getResources().getConfiguration().locale);
                                    String str = dateFormat.format(dt);
                                    tvDatetime.setText(str);
                                }
                                catch (Exception e) {}

                            } else {
                                Log.i("responGold tes1", "FALSE");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("Authorization", "Bearer " + token );
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("amount", amountParam);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void requestIndomaretTransfer(final String token, final String amountParam){
        StringRequest request = new StringRequest(Request.Method.POST, URL_BCA_TRANSFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("jsonResponse tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                int kode_unik = jsonData.getInt("unique_code");
                                int price = jsonData.getInt("price");
                                String expiredPay = jsonData.getString("expired");

                                String forComfortFee = "-IDR " + String.valueOf(kode_unik);
                                tvComfortFee.setText(forComfortFee);

                                String forTotal_KodeUnik = "IDR " + String.valueOf(price);
                                tvTotal_KodeUnik.setText(forTotal_KodeUnik);

                                tv_vAccount.setText(forTotal_KodeUnik);

                                @SuppressWarnings("deprecation") DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                                        getApplicationContext().getResources().getConfiguration().locale);
                                try {
                                    Date dt = df.parse(expiredPay);
                                    @SuppressWarnings("deprecation") SimpleDateFormat dateFormat =
                                            new SimpleDateFormat(getString(R.string.date_hour_format),
                                                    getApplicationContext().getResources().getConfiguration().locale);
                                    String str = dateFormat.format(dt);
                                    tvDatetime.setText(str);
                                }
                                catch (Exception e) {}

                            } else {
                                Log.i("responGold tes1", "FALSE");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("Authorization", "Bearer " + token );
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("amount", amountParam);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public void onClick_payATM(View v){
        switch (v.getId()) {
            case R.id.expandProcedurePayment:
                if (layoutContent.isShown()) {
                    layoutContent.setVisibility(View.GONE);
                    Fx.slide_up(this, layoutContent);
                    RelativeLayout background = (RelativeLayout) findViewById(R.id.expandHighlight);
                    int tenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            (float) 10, getResources().getDisplayMetrics());
                    int sevenDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            (float) 7, getResources().getDisplayMetrics());
                    background.setPadding(tenDip, tenDip, tenDip, sevenDip);
                    AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_cc);
                    detailArrow.setRotation(360);
                } else {
                    layoutContent.setVisibility(View.VISIBLE);
                    AppCompatImageView detailArrow = (AppCompatImageView) findViewById(R.id.down_arrow_cc);
                    detailArrow.setRotation(180);
                    Fx.slide_down(this, layoutContent);
                }
                break;

            case R.id.layout_btn_pay_atm:
                if (!flag_bank.equals("bca")) {
                    Intent in = new Intent(this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(this, FinishPaymentBCA.class);
                    in.putExtra("ATM_TRANSFER", titleATMTransfer);
                    startActivity(in);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        finish();
    }


//    @Override
//    public void onClick(View view) {
//        int clickedId = view.getId();
//        if (mSelectedItem > -1) { // have previous Item selected
//            if (mSelectedItem == clickedId) {
//                // it click the same selected item
//                // just collapse it
//                ((CVBankGroup)view).setExpand(false, true);
//                vgExpandleBank.findViewWithTag(clickedId)
//                        .setVisibility(View.GONE);
//
//                mSelectedItem = -1;
//            }
//            else { // selected item and current item is different
//                // minimixe the previous first, then expand the current
//                ((CVBankGroup)vgExpandleBank.
//                        findViewById(mSelectedItem)).setExpand(false,true);
//                vgExpandleBank.findViewWithTag(mSelectedItem)
//                        .setVisibility(View.GONE);
//                ((CVBankGroup)view).setExpand(true, true);
//                vgExpandleBank.findViewWithTag(clickedId)
//                        .setVisibility(View.VISIBLE);
//
//                mSelectedItem = clickedId;
//            }
//        }
//        else { // no previous item selected, just expand the current Item
//            ((CVBankGroup)view).setExpand(true, true);
//            vgExpandleBank.findViewWithTag(clickedId)
//                    .setVisibility(View.VISIBLE);
//
//            mSelectedItem = clickedId;
//        }
//    }
}
