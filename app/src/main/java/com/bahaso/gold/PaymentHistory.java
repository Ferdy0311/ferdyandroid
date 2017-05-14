package com.bahaso.gold;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.bahaso.adapter.PaymentHistoryAdapter;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.ModelPaymentHistory;
import com.bahaso.util.BahasoAlertDialogGold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentHistory extends AppCompatActivity implements ListView.OnItemClickListener{

    private String URL_HISTORY_PAYMENT;
    private SharedPreferences sharedpref;
    private ListView listPaymentHistory;
    private PaymentHistoryAdapter adapter;
    protected TextView noPaymentRecord;
    private List<ModelPaymentHistory>paymentHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        noPaymentRecord = (TextView)findViewById(R.id.txtNoPaymentHistory);
        listPaymentHistory = (ListView)findViewById(R.id.listpaymenthistory);
        adapter = new PaymentHistoryAdapter(this, paymentHistoryList);
        listPaymentHistory.setAdapter(adapter);
        listPaymentHistory.setOnItemClickListener(this);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_HISTORY_PAYMENT = global.getBaseURLpath() + "all_payment_transaction/";

        requestPaymentHistory();

    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void requestPaymentHistory(){
        StringRequest request = new StringRequest(Request.Method.GET, URL_HISTORY_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            Log.i("statusPayHistory tes1", status);

                            if (status.equals("true")){
                                Log.i("payHistory tes1", "TRUE");
                                JSONArray jsonArrData = jsonResponse.getJSONArray("data");
                                int dataLength = jsonArrData.length();
                                for (int i = 0; i < dataLength; i++){
                                    JSONObject jsonObject = jsonArrData.getJSONObject(i);

                                    ModelPaymentHistory mdlPaymentHistory = new ModelPaymentHistory();
                                    mdlPaymentHistory.setDateHistoryPayment(jsonObject.getString("date"));
                                    mdlPaymentHistory.setHistoryPaymentStatus(jsonObject.getString("payment_status"));
                                    mdlPaymentHistory.setHistoryAmountGold(jsonObject.getInt("amount"));
                                    mdlPaymentHistory.setHistoryTotalPrice(jsonObject.getInt("total"));
                                    mdlPaymentHistory.setHistoryTransactionID(jsonObject.getInt("_id"));
                                    paymentHistoryList.add(mdlPaymentHistory);
                                }
                            } else {
                                Log.i("payHistory tes1", "FALSE");
                                listPaymentHistory.setVisibility(View.GONE);
                                noPaymentRecord.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String paymentHistoryDate = ((TextView) view.findViewById(R.id.payment_history_date)).getText().toString();
        String paymentHistoryStatus = ((TextView) view.findViewById(R.id.payment_history_status)).getText().toString();
        String strPaymentHistoryAmountGold = ((TextView) view.findViewById(R.id.payment_history_amountgold)).getText().toString();
        String strPaymentHistoryTotalPrice = ((TextView) view.findViewById(R.id.payment_history_price)).getText().toString();
        String strPaymentHistoryTransactionID = ((TextView) view.findViewById(R.id.payment_history_transaction_id)).getText().toString();

        Intent in = new Intent(this, PaymentHistoryDetail.class);
        in.putExtra("TAG_PAY_HISTORY_DATE", paymentHistoryDate);
        in.putExtra("TAG_PAY_HISTORY_STATUS", paymentHistoryStatus);
        in.putExtra("TAG_PAY_HISTORY_AMOUNT_GOLD", strPaymentHistoryAmountGold);
        in.putExtra("TAG_PAY_HISTORY_TOTAL_PRICE", strPaymentHistoryTotalPrice);
        in.putExtra("TAG_PAY_HISTORY_TRANS_ID", strPaymentHistoryTransactionID);
        startActivity(in);
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
