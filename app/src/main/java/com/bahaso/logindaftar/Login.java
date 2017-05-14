package com.bahaso.logindaftar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.design.widget.TextInputLayout;
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
import com.bahaso.Confirm;
import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.maksim88.passwordedittext.PasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private TextInputLayout layoutEmail, layoutPass;
    private EditText editTextemailuser;
    private PasswordEditText editTextpassword;
    private String emailuserlogin, passuserlogin, URL_LOGIN;
    private ProgressDialog pDialog;
    private SharedPreferences sharedpref;
    private static final String SCREEN_NAME = "Login";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView forget_pass = (TextView)findViewById(R.id.txt_forget_pass);
        forget_pass.setPaintFlags(forget_pass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        layoutEmail = (TextInputLayout)findViewById(R.id.input_layout_email);
        layoutPass = (TextInputLayout)findViewById(R.id.input_layout_password);
        editTextemailuser = (EditText)findViewById(R.id.input_email_username);
        editTextpassword = (PasswordEditText)findViewById(R.id.input_password);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_LOGIN = global.getBaseURLpath() + "signin/";

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

    public void onClick_login(View v){
        switch(v.getId()){
            case R.id.layout_btn_login_email:
                SendEventGoogleAnalytics("LOGIN", "BTN_LOGIN", "BTN_LOGIN_CLICKED");
                emailuserlogin = editTextemailuser.getText().toString().trim();
                passuserlogin = editTextpassword.getText().toString().trim();

                if(emailuserlogin.isEmpty()){
                    layoutEmail.setError(getString(R.string.err_username_or_email_must_be_filled));
                } else {
                    layoutEmail.setErrorEnabled(false);
                }

                if(passuserlogin.isEmpty()){
                    layoutPass.setError(getString(R.string.err_password_must_be_filled));
                } else {
                    layoutPass.setErrorEnabled(false);
                }

                if(!emailuserlogin.isEmpty() && !passuserlogin.isEmpty()){
                    user_login_to_server();
                }
                break;

            case R.id.btn_forget_password:
                SendEventGoogleAnalytics("FORGET PASS", "BTN_FORGET_PASS", "BTN_FORGET_CLICKED");
                Intent forget = new Intent(this, ForgetPass.class);
                forget.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(forget);
                break;
        }
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

    private void loginTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String format = simpleDateFormat.format(new Date());
        Log.i("loginTime tes1", format);
        getSharedPref().edit().putString(getString(R.string.logintimestamp), format).apply();
        getSharedPref().edit().putString("dateTemp", format).apply();
    }

    private void user_login_to_server(){
        showPDialog();
        final String client_secret = "client1secret";
        final String grant_type = "password";
        final String client_id = "client1id";

        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if (status.equals("true")) {
                                loginTimeStamp();
                                JSONObject jsonData = new JSONObject(response).getJSONObject("data");
                                JSONObject jsonOAUTH = jsonData.getJSONObject("oauth");
                                getSharedPref().edit().putString(getResources().getString(R.string.tokenlogin), jsonOAUTH.getString("access_token")).apply();

                                JSONObject jsonUser = jsonData.getJSONObject("user");
                                getSharedPref().edit().putString(getString(R.string.userid), jsonUser.getString("id")).apply();
                                getSharedPref().edit().putString(getString(R.string.usercoin), jsonUser.getString("coin")).apply();
                                String isConfirmed = jsonUser.getString("confirmed");
                                getSharedPref().edit().putString(getString(R.string.isConfirmed), jsonUser.getString("confirmed")).apply();
                                getSharedPref().edit().putString(getString(R.string.userfirstname), jsonUser.getString("firstname")).apply();
                                getSharedPref().edit().putString(getString(R.string.userlastname), jsonUser.getString("lastname")).apply();
                                getSharedPref().edit().putString(getString(R.string.useremail), jsonUser.getString("email")).apply();
                                getSharedPref().edit().putString(getString(R.string.useravatar), jsonUser.getString("avatar")).apply();
                                getSharedPref().edit().putString(getString(R.string.isProfilComplete), jsonUser.getString("profile_completed")).apply();
                                getSharedPref().edit().putInt(getString(R.string.flag_expire_token), 1).apply();

                                if(JSONObject.NULL.equals(jsonUser.get("current_course"))) {
                                    Log.i("login tes1", "currentCourse null");
                                    getSharedPref().edit().putInt("flag_current_course", 1).apply(); // 1= blm pilih, 2 = udh pilih
                                } else {
                                    Log.i("login tes1", "currentCourse not null");
                                    JSONObject jsonCurrentcourse = jsonUser.getJSONObject("current_course");
                                    getSharedPref().edit().putString(getString(R.string.isPremium), jsonCurrentcourse.getString("is_premium")).apply();
                                    getSharedPref().edit().putString(getString(R.string.courseid), jsonCurrentcourse.getString("id")).apply();
                                    getSharedPref().edit().putString(getString(R.string.coursename), jsonCurrentcourse.getString("name")).apply();
                                    getSharedPref().edit().putInt(getString(R.string.userpoint), jsonCurrentcourse.getInt("point")).apply();
                                    getSharedPref().edit().putInt(getString(R.string.userscore), jsonCurrentcourse.getInt("score")).apply();
                                    float userprogress = (float) jsonCurrentcourse.getDouble("progress");
                                    getSharedPref().edit().putFloat(getString(R.string.userprogress), userprogress).apply();
                                    getSharedPref().edit().putString(getString(R.string.usernativelang), jsonCurrentcourse.getString("native_lang")).apply();
                                    getSharedPref().edit().putString(getString(R.string.userlearnlang), jsonCurrentcourse.getString("learn_lang")).apply();

                                    JSONObject jsonLevel = jsonCurrentcourse.getJSONObject("level");
                                    String id_level = jsonLevel.getString("id");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlevelcourseid), id_level).apply();
                                    String name_level = jsonLevel.getString("name");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlevelcoursename), name_level).apply();
                                    String urllevelimg = jsonLevel.getString("icon_src");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlevelcourseimg), urllevelimg).apply();
                                    String color_level = jsonLevel.getString("color");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlevelcoursecolor), color_level).apply();
                                }

                                hidePDialog();
                                if (isConfirmed.equals("false")){
                                    Log.i("confirm false tes1", "FALSE");
                                    Intent confirm = new Intent(Login.this, Confirm.class);
                                    startActivity(confirm);
                                    finish();
                                } else {
                                    Log.i("confirm true tes1", "TRUE");
                                    Intent home = new Intent(Login.this, MainActivity.class);
                                    home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(home);
                                    finish();
                                }
                            } else {
                                hidePDialog();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this, R.style.MyProgressTheme);
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
                params.put("username",emailuserlogin);
                params.put("password", passuserlogin);
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
