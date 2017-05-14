package com.bahaso;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Confirm extends AppCompatActivity {

    private String URL_RESEND_EMAIL, URL_CONFIRM_EMAIL, kodekonfirmasi;
    protected TextView textViewEmailUser;
    private EditText editTextConfirmCode;
    private SharedPreferences sharedpref;
    private static final String SCREEN_NAME = "AccountConfirmation";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_RESEND_EMAIL = global.getBaseURLpath() + "resend_confirmation/";
        URL_CONFIRM_EMAIL = global.getBaseURLpath() + "confirmation/";

        textViewEmailUser = (TextView)findViewById(R.id.text_email_user);
        textViewEmailUser.setText(String.format("Email: %1$s", getSharedPref().getString(getString(R.string.useremail),null)));

        editTextConfirmCode = (EditText)findViewById(R.id.edit_text_confirm_code);

        sendScreenImageName();

    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
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

    public void onClick_confirm(View v){
        switch(v.getId()){
            case R.id.layout_btn_confirm:
                SendEventGoogleAnalytics("CONFIRM ACCOUNT", "BTN CONFIRM", "BTN CONFIRM CLICKED");
                kodekonfirmasi = editTextConfirmCode.getText().toString();

                if(!kodekonfirmasi.isEmpty()) {
                    String token = getSharedPref().getString(getString(R.string.tokenlogin),"");
                    requestConfirm(token);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirm.this, R.style.MyProgressTheme);
                    alertDialogBuilder.setMessage(getString(R.string.err_confirm_code_must_filled));
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //do nothing
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;

            case R.id.text_resend_email_confirm:
                SendEventGoogleAnalytics("RESEND CONFIRM ACCOUNT", "BTN RESEND CONFIRM", "BTN RESEND CONFIRM CLICKED");
                String token = getSharedPref().getString(getString(R.string.tokenlogin),"");
                requestResendEmail(token);
                break;
        }
    }

    private void requestConfirm(final String token){
        StringRequest request = new StringRequest(Request.Method.POST, URL_CONFIRM_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("respon confirm tes1", String.valueOf(jsonResponse));

                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if(status.equals("true")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirm.this, R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        getSharedPref().edit().putBoolean(getString(R.string.isConfirmed), true).apply();
                                        Intent home = new Intent(Confirm.this, MainActivity.class);
                                        startActivity(home);
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirm.this, R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //do nothing
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
                params.put("email",getSharedPref().getString(getString(R.string.useremail),null));
                params.put("ccid",kodekonfirmasi);
                return params;
            }

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

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void requestResendEmail(final String token){
        StringRequest request = new StringRequest(Request.Method.POST, URL_RESEND_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("respon resend tes1", String.valueOf(jsonResponse));

                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if(status.equals("true")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirm.this, R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //do nothing
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirm.this, R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //do nothing
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
           public Map<String, String> getHeaders() {
               Map<String, String> params = new HashMap<>();
               // the POST parameters:
               params.put("Authorization", "Bearer " + token );
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
