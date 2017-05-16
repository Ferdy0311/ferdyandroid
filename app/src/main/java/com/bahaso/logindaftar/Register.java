package com.bahaso.logindaftar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.bahaso.Confirm;
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.util.ValidatorHelper;
import com.maksim88.passwordedittext.PasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    Context applicationContext;
    private ProgressDialog pDialog;
    private EditText editTextfirstnameuser, editTextlastnameuser, editTextemail_user;
    private PasswordEditText editTextpassworduser, editTextconfirmationPassworduser;
    private String URL_REGISTER, firstnameuser, lastnameuser, emailuser, password_user, confirmationPassword_user;
    private SharedPreferences sharedpref;
    private TextInputLayout layout_namadepan, layout_namabelakang, layout_email, layout_password, layout_confirmationPassword;
    private static final String SCREEN_NAME = "Registration";
    private GoogleAnalyticsHelper GoogleHelper;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentApiVersion = Build.VERSION.SDK_INT;
        setContentView(R.layout.activity_register);

        InitGoogleAnalytics();
        applicationContext = getApplicationContext();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final GlobalVar global = (GlobalVar)applicationContext;
        URL_REGISTER = global.getBaseURLpath() + "signup/";

        editTextfirstnameuser = (EditText)findViewById(R.id.input_nama_depan);
        editTextlastnameuser = (EditText)findViewById(R.id.input_nama_belakang);
        editTextemail_user = (EditText)findViewById(R.id.input_email_username);
        editTextpassworduser = (PasswordEditText)findViewById(R.id.input_password);
        editTextconfirmationPassworduser = (PasswordEditText) findViewById(R.id.input_confirm_password);

        layout_namadepan = (TextInputLayout)findViewById(R.id.input_layout_namadepan);
        layout_namabelakang = (TextInputLayout)findViewById(R.id.input_layout_namabelakang);
        layout_email = (TextInputLayout)findViewById(R.id.input_layout_email);
        layout_password = (TextInputLayout)findViewById(R.id.input_layout_password);
        layout_confirmationPassword = (TextInputLayout)findViewById(R.id.input_layout_confirm_password);

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

    public void onClick_register(View v){
        switch (v.getId()){
            case R.id.layout_btn_register:
                SendEventGoogleAnalytics("Register", "BTN_REGISTER", "BTN_REGISTER_CLICKED");
                firstnameuser = editTextfirstnameuser.getText().toString();
                lastnameuser = editTextlastnameuser.getText().toString();
                emailuser = editTextemail_user.getText().toString();
                password_user = editTextpassworduser.getText().toString();
                confirmationPassword_user = editTextconfirmationPassworduser.getText().toString();

                if(firstnameuser.isEmpty()){
                    layout_namadepan.setError(getString(R.string.err_first_name_must_be_filled));
                } else if (!ValidatorHelper.isValidCharFirstName(firstnameuser)){
                    layout_namadepan.setError(getString(R.string.err_first_name_invalid_char));
                } else if(firstnameuser.length() < 3) {
                    layout_namadepan.setError(getString(R.string.err_first_name_min));
                } else if (firstnameuser.length() > 15) {
                    layout_namadepan.setError(getString(R.string.err_first_name_max));
                } else {
                    layout_namadepan.setErrorEnabled(false);
                }

                if (lastnameuser.isEmpty()){
                    layout_namabelakang.setError(getString(R.string.err_last_name_must_be_filled));
                } else if (!ValidatorHelper.isValidCharLastName(lastnameuser)) {
                    layout_namabelakang.setError(getString(R.string.err_last_name_invalid_char));
                } else if (lastnameuser.length() < 2) {
                    layout_namabelakang.setError(getString(R.string.err_last_name_min));
                } else if (lastnameuser.length() > 20) {
                    layout_namabelakang.setError(getString(R.string.err_last_name_max));
                } else {
                    layout_namabelakang.setErrorEnabled(false);
                }

                if (emailuser.equals("")) {
                    layout_email.setError(getString(R.string.err_email_must_be_filled));
                } else if (!ValidatorHelper.isValidEmailAddress(emailuser)) {
                    layout_email.setError(getString(R.string.err_email_invalid));
                } else {
                    layout_email.setErrorEnabled(false);
                }

                if (password_user.equals("")) {
                    layout_password.setError(getString(R.string.err_password_must_be_filled));
                } else if (password_user.length() < 6) {
                    layout_password.setError(getString(R.string.err_password_min));
                } else {
                    layout_password.setErrorEnabled(false);
                }

                if(!confirmationPassword_user.equals(password_user)){
                    layout_confirmationPassword.setError("Konfirmasi kata sandi tidak sama");
                } else if(confirmationPassword_user.equals("")){
                    layout_confirmationPassword.setError("Konfirmasi kata sandi harus diisi");
                }
                else {
                    layout_confirmationPassword.setErrorEnabled(false);
                }

                if((!firstnameuser.isEmpty() && (firstnameuser.length() >= 3 && firstnameuser.length() <= 15))
                        && (!lastnameuser.isEmpty() && (lastnameuser.length() >=2 && lastnameuser.length() <= 20))
                        && (!emailuser.isEmpty() && ValidatorHelper.isValidEmailAddress(emailuser)) && (!password_user.isEmpty() && password_user.length() >= 6)
                        && (!confirmationPassword_user.isEmpty()&& confirmationPassword_user.equals(password_user))) {
//                    register_new_user_to_server();
                    toRegisterLocation(firstnameuser, lastnameuser, emailuser, password_user, confirmationPassword_user);
                }

                break;
        }
    }
    private void toRegisterLocation(String fname, String lname, String email, String password, String cpassword){
        Intent i = new Intent(this, RegisterLocation.class);

        i.putExtra("firstname", fname);
        i.putExtra("lastname", lname);
        i.putExtra("email", email);
        i.putExtra("password", password);
        i.putExtra("confirmpassword", cpassword);
        startActivity(i);
    }

    private void showPDialog(){
        pDialog = new ProgressDialog(this, R.style.MyProgressTheme);
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

    private void register_new_user_to_server(){
        showPDialog();
        final String client_secret = "client1secret";
        final String grant_type = "password";
        final String client_id = "client1id";

        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            Log.i("message tes1", message);

                            if (status.equals("true")) {
                                JSONObject jsonData = new JSONObject(response).getJSONObject("data");
                                JSONObject jsonOAUTH = jsonData.getJSONObject("oauth");
                                String token_regis = jsonOAUTH.getString("access_token");
                                getSharedPref().edit().putString(getString(R.string.tokenregis), token_regis).apply();

                                JSONObject jsonUser = jsonData.getJSONObject("user");
                                getSharedPref().edit().putString(getString(R.string.userid), jsonUser.getString("id")).apply();
                                getSharedPref().edit().putString(getString(R.string.userfirstname), jsonUser.getString("firstname")).apply();
                                getSharedPref().edit().putString(getString(R.string.userlastname), jsonUser.getString("lastname")).apply();
                                getSharedPref().edit().putString(getString(R.string.useremail), jsonUser.getString("email")).apply();
                                getSharedPref().edit().putString(getString(R.string.username), jsonUser.getString("username")).apply();
                                getSharedPref().edit().putString(getString(R.string.useravatar), jsonUser.getString("avatar")).apply();
                                getSharedPref().edit().putInt(getString(R.string.usercoin), jsonUser.getInt("coin")).apply();
                                getSharedPref().edit().putString(getString(R.string.isConfirmed), jsonUser.getString("confirmed")).apply();
                                getSharedPref().edit().putString(getString(R.string.isProfilComplete), jsonUser.getString("profile_completed")).apply();

                                hidePDialog();
                                Intent RegisterLocation = new Intent(Register.this, RegisterLocation.class);
                                startActivity(RegisterLocation);
                                finish();

                            } else {
                                hidePDialog();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this, R.style.MyProgressTheme);
                                dialog.setMessage(message);
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog alertDialog = dialog.create();
                                alertDialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidePDialog();
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
                        hidePDialog();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", grant_type);
                params.put("client_secret", client_secret);
                params.put("client_id", client_id);
                params.put("email",emailuser);
                params.put("firstname", firstnameuser);
                params.put("lastname", lastnameuser);
                params.put("password", password_user);
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
