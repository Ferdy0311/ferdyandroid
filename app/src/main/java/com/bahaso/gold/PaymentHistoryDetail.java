package com.bahaso.gold;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.bahaso.model.ModelPaymentHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class PaymentHistoryDetail extends AppCompatActivity {

    private String paymentHistoryStatus, paymentHistoryDate, paymentHistoryAmountGold, paymentHistoryTotalPrice,
            paymentHistoryTransactionID, paymentHistoryType, paymentHistoryVA, URL_DETAIL_PAYMENT;
    private TextView txtpaymentHistoryDate, txtpaymentHistoryTypePay, txtpaymentHistroyVirtualAcc;
    private SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_DETAIL_PAYMENT = global.getBaseURLpath() + "payment_transaction/";

        getIntentData();
        requestDetailPayment();

        txtpaymentHistoryDate = (TextView)findViewById(R.id.txt_payment_history_date);
        TextView txtpaymentHistoryStatus = (TextView)findViewById(R.id.txt_payment_history_status);
        TextView txtpaymentHistoryAmountGold = (TextView)findViewById(R.id.txt_payment_history_amount_gold);
        TextView txtpaymentHistoryTotalPrice = (TextView)findViewById(R.id.txt_payment_history_total_price);
        TextView txtpaymentHistoryPemilikRekening = (TextView)findViewById(R.id.txt_nama_pemilik_rekening);
        txtpaymentHistoryTypePay = (TextView)findViewById(R.id.txt_payment_type);
        txtpaymentHistroyVirtualAcc = (TextView)findViewById(R.id.txt_va_number);

        if (paymentHistoryStatus.equals(getString(R.string.payment_wait_confirm))){
            Log.i("waitKonfirm tes1", "masuk");
            txtpaymentHistoryStatus.setTextColor(getResources().getColor(R.color.bahaso_dark_gray));
        }
        if (paymentHistoryStatus.equals(getString(R.string.payment_success))){
            Log.i("suksesKonfirm tes1", "masuk");
            txtpaymentHistoryStatus.setTextColor(getResources().getColor(R.color.bahaso_blue));
        }
        if (paymentHistoryStatus.equals(getString(R.string.payment_fail))){
            Log.i("failKonfirm tes1", " masuk");
            txtpaymentHistoryStatus.setTextColor(getResources().getColor(R.color.bahaso_red));
        }


        txtpaymentHistoryStatus.setText(paymentHistoryStatus);
        txtpaymentHistoryAmountGold.setText(String.valueOf(paymentHistoryAmountGold));
        txtpaymentHistoryTotalPrice.setText(String.valueOf(paymentHistoryTotalPrice));

        String namaPemilikRekening = getSharedPref().getString(getString(R.string.userfirstname), "") + " " +
                getSharedPref().getString(getString(R.string.userlastname), "");
        txtpaymentHistoryPemilikRekening.setText(namaPemilikRekening);


    }

    private void getIntentData(){
        Intent in = getIntent();
        paymentHistoryStatus = in.getStringExtra("TAG_PAY_HISTORY_STATUS");
        paymentHistoryAmountGold = in.getStringExtra("TAG_PAY_HISTORY_AMOUNT_GOLD");
        paymentHistoryTotalPrice = in.getStringExtra("TAG_PAY_HISTORY_TOTAL_PRICE");
        paymentHistoryTransactionID = in.getStringExtra("TAG_PAY_HISTORY_TRANS_ID");
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void requestDetailPayment(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DETAIL_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")){
                                Log.i("if TRUE tes1", "masuk");
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                paymentHistoryDate = jsonData.getString("date");
                                Log.i("date tes1", paymentHistoryDate);

                                if (jsonData.getString("type").contains("_")){
                                    paymentHistoryType = jsonData.getString("type").replace("_", " ");
                                    Log.i("type1 tes1", paymentHistoryType);
                                } else {
                                    paymentHistoryType = jsonData.getString("type");
                                    Log.i("type2 tes1", paymentHistoryType);
                                }
                                paymentHistoryVA = jsonData.getString("va_number");
                                Log.i("va number tes1", paymentHistoryVA);

                                txtpaymentHistoryTypePay.setText(paymentHistoryType);
                                txtpaymentHistroyVirtualAcc.setText(paymentHistoryVA);
                                txtpaymentHistoryDate.setText(paymentHistoryDate);
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
                params.put("Authorization", "Bearer " + getSharedPref().getString(getString(R.string.tokenlogin), ""));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("transaction_id", paymentHistoryTransactionID);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
