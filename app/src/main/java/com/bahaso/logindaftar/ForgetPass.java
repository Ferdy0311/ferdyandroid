package com.bahaso.logindaftar;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPass extends AppCompatActivity {

    private EditText editTextEmail;
    private String emailuser, URL_FORGET;
    private static final String SCREEN_NAME = "ForgetPass";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextEmail = (EditText)findViewById(R.id.edit_text_email);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_FORGET = global.getBaseURLpath() + "password/forget/";

        sendScreenImageName();

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

    public void onClick_forgetpass(View v){
        switch (v.getId()){
            case R.id.layout_btn_submit_email_reset:
                SendEventGoogleAnalytics("FORGET_PASSWORD", "BTN_FORGET_PASS", "BTN_FORGET_CLICKED");
                emailuser = editTextEmail.getText().toString();
                if(emailuser.isEmpty()){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgetPass.this, R.style.MyProgressTheme);
                    alertDialogBuilder.setMessage(getString(R.string.err_email_must_be_filled));
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //do nothing
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    request_reset_password();
                }
                break;
        }
    }

    private void request_reset_password(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_FORGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("respon forget tes1", String.valueOf(jsonResponse));

                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if(status.equals("true")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgetPass.this, R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
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
                        } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.error_internet_access),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email",emailuser);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
