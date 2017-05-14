package com.bahaso.voucher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.bahaso.util.BahasoAlertDialogGold;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoucherActivity extends Fragment implements View.OnClickListener {

    private String voucherCode, URL_REDEEM_VOUCHER;
    private EditText editTextVoucherCode;
    private ProgressDialog pDialog;
    private GoogleAnalyticsHelper GoogleHelper;
    private SharedPreferences sharedpref;
    private String patternNumber = "[^0-9]";
    private String patternString = "[^a-z A-Z]";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_voucher, container, false);
        InitGoogleAnalytics();

        GlobalVar global = (GlobalVar)getActivity().getApplicationContext();
        URL_REDEEM_VOUCHER = global.getBaseURLpath() + "redeem_scholarship/";

        editTextVoucherCode = (EditText)v.findViewById(R.id.et_voucher);
        editTextFilter();

        LinearLayout layoutBtnRedeemVoucher = (LinearLayout)v.findViewById(R.id.layout_btn_payment_method);

        layoutBtnRedeemVoucher.setOnClickListener(this);

        sendScreenImageName();

        return v;
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

    private void sendScreenImageName() {
        String SCREEN_NAME = "Voucher";
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getContext());
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(getContext(),iCategoryId,iActionId,iLabelId );
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void editTextFilter(){
        editTextVoucherCode.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                        for (int start = i; start < i1; start++) {
                            if (Character.isLetterOrDigit(charSequence.charAt(start))) {
                                return charSequence;
                            }
                            if (Character.isWhitespace(charSequence.charAt(start))){
                                return "";
                            }
                        }
                        return "";
                    }
                }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_btn_payment_method:
                SendEventGoogleAnalytics("GOLD REDEEM", "BTN REDEEM", "BTN REDEEM CLICKED");
                if (!editTextVoucherCode.getText().toString().
                        equals(editTextVoucherCode.getText().toString().toUpperCase())) {
                    voucherCode = editTextVoucherCode.getText().toString().toUpperCase();
                    Log.i("voucher tes1", voucherCode);
                } else {
                    voucherCode = editTextVoucherCode.getText().toString();
                    Log.i("voucher tes1", voucherCode);
                }
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
        }
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

}
