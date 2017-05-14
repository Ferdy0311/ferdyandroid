package com.bahaso.home;

import android.content.DialogInterface;
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
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingChangePassword extends AppCompatActivity {

    private SharedPreferences sharedpref;
    private String passwordLama, passwordBaru, confirmPasswordBaru, URL_CHANGE_PASS;
    private EditText oldpass, newpass, confirmNewPass;
    private TextInputLayout tilOldPass, tilNewPass, tilConfirmNewPass;
    private GoogleAnalyticsHelper GoogleHelper;
    private static final String TAG0 = "ChangePass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_change_password);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        oldpass = (EditText)findViewById(R.id.et_old_password);
        newpass = (EditText)findViewById(R.id.et_new_password);
        confirmNewPass = (EditText)findViewById(R.id.et_new_password_confirm);
        tilOldPass = (TextInputLayout)findViewById(R.id.til_old_password);
        tilNewPass = (TextInputLayout)findViewById(R.id.til_new_password);
        tilConfirmNewPass = (TextInputLayout)findViewById(R.id.til_new_password_confirm);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_CHANGE_PASS = global.getBaseURLpath() + "profile/password/change/";

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

    public void onClick_changePass(View v){
        switch (v.getId()){
            case R.id.vg_change_pass:
                Log.i("changePass tes1","CLICKED");
                SendEventGoogleAnalytics("CHANGE PASS", "BTN CHANGE PASS", "BTN CHANGE PASS CLICKED");
                passwordLama = oldpass.getText().toString();
                passwordBaru = newpass.getText().toString();
                confirmPasswordBaru = confirmNewPass.getText().toString();

                if (!passwordLama.isEmpty() && !passwordBaru.isEmpty() && !confirmPasswordBaru.isEmpty()){
                    changePassword(passwordLama, passwordBaru, confirmPasswordBaru);
                }

                if (passwordLama.isEmpty()){
                    tilOldPass.setError(getString(R.string.err_password_must_be_filled));
                }

                if (passwordBaru.isEmpty()){
                    tilNewPass.setError(getString(R.string.err_new_password_must_be_filled));
                }

                if (confirmPasswordBaru.isEmpty()){
                    tilConfirmNewPass.setError(getString(R.string.err_new_password_confirm_must_be_filled));
                }

                break;
        }
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void changePassword(final String oldPass, final String newPass, final String confirmNewPass){
        StringRequest request = new StringRequest(Request.Method.POST, URL_CHANGE_PASS,
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
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingChangePassword.this,
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
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingChangePassword.this,
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
                params.put("password", oldPass);
                params.put("new_password", newPass);
                params.put("new_password_confirmation", confirmNewPass);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

}
