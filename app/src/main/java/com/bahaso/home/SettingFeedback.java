package com.bahaso.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.profile.EditProfil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingFeedback extends AppCompatActivity {

    private SharedPreferences sharedpref;
    private String title, message, location, phone, URL_FEEDBACK;
    private EditText feedbackTitle, feedbackMessage, feedbackLocation, feedbackPhone;
    private TextInputLayout tilFeedTitle, tilFeedMessage, tilFeedLocation, tilFeedPhone;
    private GoogleAnalyticsHelper GoogleHelper;
    private static final String TAG0 = "Feedback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_feedback);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        feedbackTitle = (EditText)findViewById(R.id.et_feedback_title);
        feedbackMessage = (EditText)findViewById(R.id.et_feedback_message);
        feedbackLocation = (EditText)findViewById(R.id.et_location);
        feedbackPhone = (EditText)findViewById(R.id.et_cellphone_number);
        tilFeedTitle = (TextInputLayout)findViewById(R.id.til_feedback_title);
        tilFeedMessage = (TextInputLayout)findViewById(R.id.til_feedback_message);
        tilFeedLocation = (TextInputLayout)findViewById(R.id.til_location);
        tilFeedPhone = (TextInputLayout)findViewById(R.id.til_cellphone_number);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_FEEDBACK = global.getBaseURLpath() + "feedback/";
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
        GoogleHelper.SendScreenNameGoogleAnalytics(TAG0, getApplicationContext());
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    public void onClick_feedback(View v) {
        switch (v.getId()){
            case R.id.vg_submit_feedback:
                Log.i("btnClicked", "CLICKED!!");
                SendEventGoogleAnalytics("FEEDBACK", "BTN SEND FEEDBACK", "BTN FEEDBACK CLICKED");
                title = feedbackTitle.getText().toString();
                message = feedbackMessage.getText().toString();
                location = feedbackLocation.getText().toString();
                phone = feedbackPhone.getText().toString();

                if(!title.isEmpty() && !message.isEmpty() && !location.isEmpty()) {
                    sendFeedback(title, message, location, phone);
                }

                if(title.isEmpty()){
                    tilFeedTitle.setError(getString(R.string.err_feedback_title_must_filled));
                }

                if (message.isEmpty()){
                    tilFeedMessage.setError(getString(R.string.err_feedback_message_must_filled));
                }

                if(location.isEmpty()){
                    tilFeedLocation.setError(getString(R.string.err_feedback_location_must_filled));
                }
                break;
        }
    }

    private void sendFeedback(final String feedtitle, final String feedmessage,
                              final String feedlocation, final String feedphone) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("respon tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");
                            Log.i("status profil tes1", status);
                            String message = jsonResponse.getString("message");

                            if(status.equals("true")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingFeedback.this,
                                        R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingFeedback.this,
                                        R.style.MyProgressTheme);
                                alertDialogBuilder.setMessage(message);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

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
                params.put("Authorization", "Bearer " + getSharedPref().getString(getString(R.string.tokenlogin), ""));
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("type", "General");
                params.put("title", feedtitle);
                params.put("message", feedmessage);
                params.put("cellphonenumber", feedphone);
                params.put("location", feedlocation);
                params.put("device", Build.MANUFACTURER + "-" + Build.MODEL + "-" + Build.DEVICE + "-" + Build.PRODUCT);
                params.put("os", "Android OS " + Build.VERSION.RELEASE + " / API-" + Build.VERSION.SDK_INT);
                params.put("version", "34" + "-" + "1.2.8");
                params.put("useragent", "phone");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

}
