package com.bahaso.gold;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.bahaso.model.Gold;
import com.bahaso.util.BahasoAlertDialogGold;
import com.bahaso.util.GoldPacketViewGroup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.igenius.customcheckbox.CustomCheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GoldActivity extends Fragment implements View.OnClickListener {

    private String URL_GETGOLD_PACKAGE, URL_REDEEM_VOUCHER, voucherCode, URL_USER_GOLD, amountGold = "";
    private EditText editTextVoucherCode, editTextAmountGold;
    private SharedPreferences sharedpref;
    private ViewGroup vgGoldPaket;
    private TextView textPrice;
    private ProgressDialog pDialog;
    private List<Gold> listGold = new ArrayList<>();
    private static final String SCREEN_NAME = "GoldTopUp";
    private GoogleAnalyticsHelper GoogleHelper;
    private long priceGold2IDR = 0;
    private CustomCheckBox customCheckBox;
    private LinearLayout layoutRedeemVoucher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_gold, container, false);
        InitGoogleAnalytics();

        editTextVoucherCode = (EditText)v.findViewById(R.id.et_voucher);
        editTextVoucherCode.setFilters(new InputFilter[] {
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(40)});

        LinearLayout layoutPaymentHistory = (LinearLayout)v.findViewById(R.id.layout_payment_history);
        LinearLayout layoutBtnRedeemVoucher = (LinearLayout)v.findViewById(R.id.layout_btn_redeem_voucher);
        LinearLayout layoutBtnPaymentMethod = (LinearLayout)v.findViewById(R.id.layout_btn_payment_method) ;
        layoutRedeemVoucher = (LinearLayout)v.findViewById(R.id.layout_redeem_voucher);
        editTextAmountGold = (EditText)v.findViewById(R.id.et_gold);
        textPrice = (TextView)v.findViewById(R.id.tv_price);
        customCheckBox = (CustomCheckBox)v.findViewById(R.id.checkbox_voucher);
        final TextView textUserGold = (TextView)v.findViewById(R.id.tv_gold);
        String totalCoinUser = String.valueOf(getSharedPref()
                .getInt(getString(R.string.usercoin), 0)) + " Gold";
        textUserGold.setText(totalCoinUser);

        GlobalVar global = (GlobalVar)getActivity().getApplicationContext();
        URL_USER_GOLD = global.getBaseURLpath() + "gold/";
        URL_GETGOLD_PACKAGE = global.getBaseURLpath() + "topup_packet/";
        URL_REDEEM_VOUCHER = global.getBaseURLpath() + "redeem_voucher/";

        String token = getSharedPref().getString(getString(R.string.tokenlogin),"");
        getUserGoldNow(token);

        layoutPaymentHistory.setOnClickListener(this);
        layoutBtnRedeemVoucher.setOnClickListener(this);
        layoutBtnPaymentMethod.setOnClickListener(this);

        sendScreenImageName();

        checkBoxChangeListener();
        editTextChangeListener();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return v;
    }

    private void checkBoxChangeListener(){
        customCheckBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                Log.i("cekbox tes1", String.valueOf(isChecked));
                if(isChecked){
                    layoutRedeemVoucher.setVisibility(View.VISIBLE);
                } else {
                    layoutRedeemVoucher.setVisibility(View.GONE);
                }
            }
        });
    }

    private void editTextChangeListener(){
        editTextAmountGold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String priceGoldIDR = "";
                amountGold = editTextAmountGold.getText().toString();
                if (!amountGold.matches("^0")) {
                    if (amountGold.isEmpty()) {
                        priceGold2IDR = 0;
                        textPrice.setVisibility(View.GONE);
                    } else {
                        priceGold2IDR = Long.parseLong(amountGold) * 10000;
                        DecimalFormat formatter = new DecimalFormat("###,###,###,###");
                        long LongNum = Long.valueOf(String.valueOf(priceGold2IDR));
                        priceGoldIDR = "IDR " + formatter.format(LongNum);
                        textPrice.setVisibility(View.VISIBLE);
                    }
                    textPrice.setText(priceGoldIDR);
                } else {
                    Toast.makeText(getContext(), getString(R.string.err_input_gold), Toast.LENGTH_LONG).show();
                    editTextAmountGold.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getContext());
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(getContext(),iCategoryId,iActionId,iLabelId );
    }

    private void showPDialog(){
        pDialog = new ProgressDialog(getContext(), R.style.MyProgressTheme);
        pDialog.setMessage("Loading... Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void inflateLayoutGoldPacket(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for(int i = 0, sizei = listGold.size(); i < sizei; i++){
            Gold goldPaket = listGold.get(i);
            GoldPacketViewGroup goldPacketViewGroup = (GoldPacketViewGroup)
                    inflater.inflate(R.layout.viewgroup_gold_packet_new, vgGoldPaket, false);
            goldPacketViewGroup.setText(goldPaket.getGoldString(),
                    goldPaket.getCurrency());
            goldPacketViewGroup.setTag(i);
            goldPacketViewGroup.setOnClickListener(this);
            vgGoldPaket.addView(goldPacketViewGroup);
        }
    }

    private void request_get_gold_paket(final String token){
        StringRequest request = new StringRequest(Request.Method.GET, URL_GETGOLD_PACKAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            Log.i("message tes1", message);

                            if (status.equals("true")) {
                                JSONArray jsonData = new JSONObject(response).getJSONArray("data");
                                for(int i = 0, dataAmount = jsonData.length(); i < dataAmount; i++) {
                                    Gold gold = new Gold();
                                    JSONObject object = jsonData.getJSONObject(i);
                                    gold.setID(object.getInt("id"));
                                    gold.setGold(object.getInt("gold"));
                                    gold.setPrice(object.getInt("price"));
                                    gold.setCurrency(object.getString("currency"));
                                    listGold.add(gold);
                                }
                                inflateLayoutGoldPacket();
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
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_network_timeout),
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
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }

    private void request_redeem_voucher(final String token, final String voucherCode){
        showPDialog();
        StringRequest request = new StringRequest(Request.Method.POST, URL_REDEEM_VOUCHER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                //TODO extract response if success redeem
                                getSharedPref().edit().putInt(getString(R.string.usercoin), jsonResponse.getInt("data")).apply();
                                Intent in = new Intent(getContext(), MainActivity.class);
                                in.putExtra(getString(R.string.flag_edit_gold), 1);
                                startActivity(in);
                                getActivity().finish();
                            } else {
                                Log.i("responGold tes1", "FALSE");
                                BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                                        getActivity(), null,
                                        getString(R.string.err_voucher_invalid),
                                        R.drawable.fail, null, editTextVoucherCode);
                                alertDialog.show();
                            }

                            hidePDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidePDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidePDialog();
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
                params.put("code", voucherCode );
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }

    private void getUserGoldNow(final String token){
        StringRequest request = new StringRequest(Request.Method.GET, URL_USER_GOLD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
//                                JSONArray jsonData = new JSONObject(response).getJSONArray("data");
                                getSharedPref().edit().putInt(getString(R.string.usercoin), jsonResponse.getInt("data")).apply();

                            } else {
                                Log.i("responGold tes1", "FALSE");
                                BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                                        getActivity(), null,
                                        getString(R.string.err_voucher_invalid),
                                        R.drawable.fail, null, editTextVoucherCode);
                                alertDialog.show();
                            }

                            hidePDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidePDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_server),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    getContext().getApplicationContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidePDialog();
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
                params.put("code", voucherCode);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_btn_redeem_voucher:
                SendEventGoogleAnalytics("GOLD REDEEM", "BTN REDEEM", "BTN REDEEM CLICKED");
                voucherCode = editTextVoucherCode.getText().toString();
                if(!voucherCode.isEmpty()){
                    request_redeem_voucher(getSharedPref().getString(getString(R.string.tokenlogin),""), voucherCode);
                } else {
                    BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                            getActivity(), null,
                            getString(R.string.err_voucher_empty),
                            R.drawable.fail, null, editTextVoucherCode);
                    alertDialog.show();
                }
                break;

            case R.id.layout_btn_payment_method:
//                int tag = (int) v.getTag();
//                Gold goldPacket = listGold.get(tag);
//                int paketPrice = goldPacket.getPrice();
//                String paketGold = goldPacket.getGoldString();
//                String paketCurrency = goldPacket.getCurrency();
                if(!amountGold.isEmpty()) {
                    long price = Long.parseLong(amountGold) * 10000;
                    SendEventGoogleAnalytics("TOP UP GOLD", amountGold + " GOLD", "PAYMENT " + amountGold + " GOLD");
                    Intent in = new Intent(getContext(), TopUpGoldActivity.class);
                    in.putExtra("PAKET_GOLD", amountGold + " GOLD");
                    in.putExtra("PAKET_PRICE", price);
                    in.putExtra("PAKET_CURRENCY_PRICE", "IDR " + String.valueOf(price));
                    startActivity(in);
                    getActivity().finish();
                } else {
                    BahasoAlertDialogGold alertDialog = new BahasoAlertDialogGold(
                            getActivity(), null,
                            getString(R.string.err_amount_gold_empty),
                            R.drawable.fail, null, editTextAmountGold);
                    alertDialog.show();
                }
                break;

            case R.id.layout_payment_history:
                Intent in = new Intent(getContext(), PaymentHistory.class);
                startActivity(in);
                break;

        }
    }

}
