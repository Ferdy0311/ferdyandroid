package com.bahaso.gold;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.util.ActivityRequestCode;
import com.bahaso.util.BahasoAlertDialogGold;
import com.bahaso.util.CreditCardUtils;
import com.bahaso.widget.CVCreditCard;
import com.google.android.gms.analytics.Tracker;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.veritrans.sdk.coreflow.core.Constants;
import id.co.veritrans.sdk.coreflow.core.SdkCoreFlowBuilder;
import id.co.veritrans.sdk.coreflow.core.TransactionRequest;
import id.co.veritrans.sdk.coreflow.core.VeritransSDK;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBusProvider;
import id.co.veritrans.sdk.coreflow.eventbus.callback.TokenBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.events.GeneralErrorEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetTokenFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetTokenSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.NetworkUnavailableEvent;
import id.co.veritrans.sdk.coreflow.models.CardTokenRequest;
import id.co.veritrans.sdk.coreflow.models.CustomerDetails;
import id.co.veritrans.sdk.coreflow.models.TokenDetailsResponse;

public class PayTopUpWithCC extends AppCompatActivity implements TokenBusCallback{

    private GlobalVar global;
    private VeritransSDK mVeritransSDK;
    private CVCreditCard cvCreditCard;
    private boolean isFrontCardShow, mIsSuccess3DSorNotEnrolled;
    private SharedPreferences sharedpref;
    protected TextWatcher mCcNumberTextWatcher, mCcExpiryDateWatcher, mCcCvvTextWatcher, mCcNameTextWatcher;
    private String paketGold, paketPrice, paketCurrency, mTokenID, nominalGold, URL_TOPUP_CC;
    private View.OnFocusChangeListener OnEditTextFocusChangeListener;
    private TextInputEditText editTextCreditCardNumber, editTextCreditCardExpired, editTextCreditCardCVV;
    private TextInputLayout tilCreditCardNumber, tilCreditCardExpired, tilCreditCardCVV;
    private Tracker Tracker;
    private static final String SCREEN_NAME = "InsertDataCreditCard";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_with_cc);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        global = (GlobalVar)getApplicationContext();
        URL_TOPUP_CC = global.getBaseURLpath() + "cc_topup/";

        initVeritransSDK();
        registerVTBus();
        getIntentShowData();

        cvCreditCard = (CVCreditCard)findViewById(R.id.cv_credit_card);
        isFrontCardShow = cvCreditCard.isShowFront();
        tilCreditCardNumber = (TextInputLayout)findViewById(R.id.til_credit_card_number);
        tilCreditCardExpired = (TextInputLayout)findViewById(R.id.til_expiry_date);
        tilCreditCardCVV = (TextInputLayout)findViewById(R.id.til_cvv);
        editTextCreditCardNumber = (TextInputEditText)findViewById(R.id.et_credit_card_number);
        editTextCreditCardExpired = (TextInputEditText)findViewById(R.id.et_expiry_date);
        editTextCreditCardCVV = (TextInputEditText)findViewById(R.id.et_cvv);

        View.OnFocusChangeListener OnEditTextFocusChangeListener = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (view == editTextCreditCardCVV ) { // if EtCvv is focused
                        if (isFrontCardShow) {
                            cvCreditCard.flip(false, false); // flip to back animate
                            isFrontCardShow = false;
                        }
                        // else already in back, do nothing
                    } else {
                        if (! isFrontCardShow) {
                            cvCreditCard.flip(true, false); // flip to front animate
                            isFrontCardShow = true;
                        }
                    }
                }
            }
        };

        editTextCreditCardNumber.setOnFocusChangeListener(OnEditTextFocusChangeListener);
        editTextCreditCardExpired.setOnFocusChangeListener(OnEditTextFocusChangeListener);
        editTextCreditCardCVV.setOnFocusChangeListener(OnEditTextFocusChangeListener);

        mCcNumberTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int cursorPosition = editTextCreditCardNumber.getSelectionEnd();
                int previousLength = editTextCreditCardNumber.getText().length();

                String rawCardNumber = editable.toString().replace(" ", "");;
                String cardNumber = CreditCardUtils.handleCardNumber(rawCardNumber);
                int modifiedLength = cardNumber.length();

                editTextCreditCardNumber.removeTextChangedListener(this);
                editTextCreditCardNumber.setText(cardNumber);
                if(modifiedLength <= previousLength && cursorPosition < modifiedLength) {
                    if (cursorPosition == 5 ||
                            cursorPosition == 10 ||
                            cursorPosition == 15) {
                        cursorPosition-=1;
                    }
                    editTextCreditCardNumber.setSelection(cursorPosition);
                }
                else {
                    editTextCreditCardNumber.setSelection(cardNumber.length()
                            > 19 ? 19 : cardNumber.length());
                }
                editTextCreditCardNumber.addTextChangedListener(this);

                cvCreditCard.setCreditCardNumber(rawCardNumber);
            }
        };
        editTextCreditCardNumber.addTextChangedListener(mCcNumberTextWatcher);

        mCcExpiryDateWatcher = new TextWatcher() {
            String textBefore;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textBefore = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int currCursorPos = editTextCreditCardExpired.getSelectionEnd();

                String currStringb4edit = editable.toString();
                int currLengthB4Edit = currStringb4edit.length();
                // logic to remove separator and char before separator when backspace
                if (textBefore.contains("/")
                        && !currStringb4edit.contains("/")
                        && textBefore.length() - currStringb4edit.length() == 1
                        && currCursorPos == 2) {
                    if (currStringb4edit.length() > 3) {
                        currStringb4edit = currStringb4edit.substring(0, 1) +
                                currStringb4edit.substring(2);
                    } else {
                        currStringb4edit = currStringb4edit.substring(0, 1);
                    }
                    currCursorPos--;
                }
                String monthYear = currStringb4edit.replace(
                        CreditCardUtils.SLASH_SEPERATOR, "");

                String month, year="";
                if(monthYear.length() >= 2) {
                    month = monthYear.substring(0, 2);
                    if(monthYear.length() > 2) {
                        year = monthYear.substring(2);
                    }
                } else {
                    month = monthYear;
                }

                // logic to parse month and year;
                String monthYearAEdit = "";
                int monthInt = 0;
                int yearInt = 0;
                if (! TextUtils.isEmpty(month)) {
                    monthInt = Integer.parseInt(month);
                    String monthEdit;
                    if (monthInt > 1 && monthInt < 10) {
                        monthEdit = "0"+monthInt;
                    } else if (monthInt > 12){
                        monthEdit = "12";
                    } else {
                        monthEdit = month;
                    }

                    if (monthEdit.length() == 2) {
                        monthEdit+= "/";
                    }

                    try {
                        yearInt = Integer.parseInt(year);
                    } catch (Exception e) {
                        yearInt = 0;
                    }
                    monthYearAEdit = monthEdit + year;
                }
                // end logic to parse month and year;

                int currLengthAEdit = monthYearAEdit.length();

                editTextCreditCardExpired.removeTextChangedListener(this);
                editTextCreditCardExpired.setText(monthYearAEdit);
                if(currLengthAEdit <= currLengthB4Edit && currCursorPos < currLengthAEdit) {
                    editTextCreditCardExpired.setSelection(currCursorPos);
                } else {
                    editTextCreditCardExpired.setSelection(monthYearAEdit.length());
                }
                editTextCreditCardExpired.addTextChangedListener(this);
                cvCreditCard.setCardExpiry(monthInt, yearInt);
            }
        };
        editTextCreditCardExpired.addTextChangedListener(mCcExpiryDateWatcher);

        mCcCvvTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String cVV = editable.toString();
                int cVVInt = 0;
                if (!TextUtils.isEmpty(cVV)) {
                    cVVInt = Integer.parseInt(cVV);
                }
                cvCreditCard.setCVV(cVVInt);
            }
        };
        editTextCreditCardCVV.addTextChangedListener(mCcCvvTextWatcher);

        mCcNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString();
                cvCreditCard.setName(name);
            }
        };

        sendScreenImageName();
    }

    private void registerVTBus(){
        if (!VeritransBusProvider.getInstance().isRegistered(this)) {
            VeritransBusProvider.getInstance().register(this);
        }
    }

    private void unRegisterVTBus(){
        if (VeritransBusProvider.getInstance().isRegistered(this)) {
            VeritransBusProvider.getInstance().unregister(this);
        }
    }

    private void sendScreenImageName() {
//        String name = SCREEN_NAME;
//        // [START screen_view_hit]
//        Log.i(SCREEN_NAME, "Setting screen name: " + name);
//        Tracker.setScreenName("Android~" + name);
//        Tracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
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
        paketPrice = in.getStringExtra("PAKET_PRICE");
        paketCurrency = in.getStringExtra("PAKET_CURRENCY");

        View vgTopUpInfo = findViewById(R.id.vg_topup_info);
        ((TextView)vgTopUpInfo.findViewById(R.id.tv_gold)).setText(paketGold);
        ((TextView)vgTopUpInfo.findViewById(R.id.tv_price)).setText(paketCurrency);

        nominalGold = paketGold.substring(0,2);
        Log.i("nominalGold tes1", nominalGold);
    }

    private void initVeritransSDK(){
        mVeritransSDK = new SdkCoreFlowBuilder(this,
                global.getVT_CLIENT_KEY_PROD(),
                global.getBaseURL())
                .enableLog(true)
                .setDefaultText("open_sans_regular.ttf")
                .setSemiBoldText("open_sans_semibold.ttf")
                .setBoldText("open_sans_bold.ttf")
                .setMerchantName("Bahaso.com")
                .buildSDK();
    }

    private void initTransactionRequest(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        Log.i("timeStamp tes 2", ts);

        String TRANSACTION_ID = "BAHASO " + paketGold + " " + ts; //add timestamp hh:mm:ss
        TransactionRequest transactionRequest = new TransactionRequest(TRANSACTION_ID,
                Double.parseDouble(paketPrice));

        CustomerDetails customerDetails = new CustomerDetails(getSharedPref().getString(getString(R.string.userfirstname),"")
                , getSharedPref().getString(getString(R.string.userlastname),"")
                , getSharedPref().getString(getString(R.string.useremail),"")
                , getSharedPref().getString(getString(R.string.userphonenumber),""));

        transactionRequest.setCustomerDetails(customerDetails);
        VeritransSDK.getVeritransSDK().setTransactionRequest(transactionRequest);

    }

    public void userPaymentID(){
        int month = cvCreditCard.getExpiryMonth();
        int year = cvCreditCard.getExpiryYear();
        int cvv = cvCreditCard.getCvv();
        String ccNumber = cvCreditCard.getCreditCardNumber();

        boolean isValid = true;
        if (ccNumber.length() == 0) {
            isValid = false;
            tilCreditCardNumber.setError(getString(R.string.credit_card_number_must_be_filled));
//            editTextCreditCardNumber.setError(getString(R.string.credit_card_number_must_be_filled));

        }
        else if (ccNumber.length() < 16) {
            isValid = false;
            tilCreditCardNumber.setError(getString(R.string.credit_card_must_be_16_digits));
        }

        if (month == 0 || year < 1000) {
            if (isValid) {
                isValid = false;
            }
            tilCreditCardExpired.setError(getString(R.string.expiry_date_must_be_filled));
        }

        if (cvv == 0) {
            if (isValid) {
                isValid = false;
            }
            tilCreditCardCVV.setError(getResources().getString(R.string.cvv_must_be_filled));
        }
        if (cvv < 100) {
            if (isValid) {
                isValid = false;
            }
            tilCreditCardCVV.setError(getString(R.string.cvv_must_be_minimum_3_digits));
        }

        if(isValid) {

            CardTokenRequest cardTokenRequest = new CardTokenRequest(ccNumber,
                    String.valueOf(cvv),
                    String.valueOf(month),
                    String.valueOf(year),
                    mVeritransSDK.getClientKey());
            cardTokenRequest.setIsSaved(false);
            cardTokenRequest.setSecure(true);
            cardTokenRequest.setGrossAmount(Double.parseDouble(paketPrice));

            mVeritransSDK.getToken(cardTokenRequest);

            Log.i("clientKeyCC tes1", mVeritransSDK.getClientKey());
            // lanjut ke onEvent(GetTokenSuccessEvent getTokenSuccessEvent)
            // atau ke onEvent(GetTokenFailedEvent getTokenFailedEvent)
        }
    }

    public void onClick_payCC(View v){
        switch (v.getId()){
            case R.id.btn_paynow:
                SendEventGoogleAnalytics("CREDIT CARD", "BTN BAYAR CC", "BTN BAYAR CC CLICKED");
                initTransactionRequest();
                userPaymentID();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterVTBus();
    }

    @Subscribe
    @Override
    public void onEvent(GetTokenSuccessEvent getTokenSuccessEvent) {
        Log.i("tokenSukses tes1", "SUCCESS");
        TokenDetailsResponse tokenDetailsResponse = getTokenSuccessEvent.getResponse();
        if (tokenDetailsResponse == null || TextUtils.isEmpty(tokenDetailsResponse.getRedirectUrl())) {
            BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                    getApplicationContext(), null,
                    getString(R.string.err_verify_cc),
                    R.drawable.fail, null);
            alertDialog.show();
        }
        else {
            mTokenID = tokenDetailsResponse.getTokenId();
            Log.d("tokenCC tes1", mTokenID);
            Intent intentPaymentWeb = new Intent(this, PaymentWebActivity.class);
            intentPaymentWeb.putExtra(Constants.WEBURL, tokenDetailsResponse.getRedirectUrl());
            startActivityForResult(intentPaymentWeb, ActivityRequestCode.PAYMENT_WEB_INTENT);
        }
    }

    @Subscribe
    @Override
    public void onEvent(GetTokenFailedEvent getTokenFailedEvent) {
        Log.i("tokenFAIL tes1", "FAIL");
        BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                getApplicationContext(), null,
                getString(R.string.err_verify_cc),
                R.drawable.fail, null);
        alertDialog.show();
    }

    @Subscribe
    @Override
    public void onEvent(NetworkUnavailableEvent networkUnavailableEvent) {
        Log.i("tokenNoNetwork tes1", "No Network");
        BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                getApplicationContext(), null,
                getString(R.string.error_no_network),
                R.drawable.fail, null);
        alertDialog.show();
    }

    @Subscribe
    @Override
    public void onEvent(GeneralErrorEvent generalErrorEvent) {
        Log.i("tokenERROR tes1", "ERROR");
        BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                getApplicationContext(), null,
                getString(R.string.err_verify_cc),
                R.drawable.fail, null);
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ActivityRequestCode.PAYMENT_WEB_INTENT:
                if(resultCode == RESULT_OK) { // 3DS Success
                    mIsSuccess3DSorNotEnrolled = true;
                    // continue in onResume --- lansung saja
                    Log.i("resulOK tes1", "3DSECURE");
                    requestTopUpCC();
                } else {
                    mIsSuccess3DSorNotEnrolled = false;
                    Log.i("resulFAIL tes1", "3DSECURE");
                }
                break;
        }
    }


    private void requestTopUpCC(){
        StringRequest request = new StringRequest(Request.Method.GET, URL_TOPUP_CC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("jsonResponCC tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");
//                            String message = jsonResponse.getString("message");
//                            Log.i("message tes1", message);

                            if (status.equals("true")) {
                                //TODO if user hasGold before, add it
                                Object obj = jsonResponse.get("data");
                                Log.i("payCCSukses tes1", String.valueOf(obj));
                                if (obj instanceof Integer){
                                    int isHasGold = getSharedPref().getInt(getString(R.string.usercoin), 0);
                                    if (isHasGold == 0){

                                    }
                                }
                                BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                                        getApplicationContext(), null,
                                        "Top up berhasil",
                                        R.drawable.top_up, null);
                                alertDialog.show();
                            } else {
                                Log.i("jsonFailResponCC tes1", String.valueOf(jsonResponse));
                                AlertDialog.Builder alertadd = new AlertDialog.Builder(PayTopUpWithCC.this, R.style.MyProgressTheme);
                                LayoutInflater factory = LayoutInflater.from(PayTopUpWithCC.this);
                                final View view = factory.inflate(R.layout.dialog_gold_fail, null);
                                alertadd.setView(view);
                                alertadd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alertadd.show();

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
                                    getString(R.string.error_network_timeout),
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
                params.put("Authorization", "Bearer " + getSharedPref().getString(getString(R.string.tokenlogin),""));
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("token_id", mTokenID );
                params.put("amount", nominalGold);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

}
