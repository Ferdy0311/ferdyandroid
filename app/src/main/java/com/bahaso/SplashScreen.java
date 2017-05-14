package com.bahaso;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.logindaftar.Login;
import com.bahaso.logindaftar.Register;
import com.bahaso.util.ConnectionDetector;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Date dateTemp ;
    private LoginButton login_btn_fb_widget;
    private CallbackManager mCallbackManager;
    private CoordinatorLayout coordinatorLayout;
    private View btnFacebook, btnGoogle, btnRegisLogin;
    boolean isConnected = false, isInternetAvailable = false, isVisible = false;
    private ProgressDialog pDialog;
    private SharedPreferences sharedpref;
    private GoogleApiClient mGoogleApiClient;
    private String URL_GOOGLE, URL_FB;
    private static final int RC_SIGN_IN = 9001;
    private static final String SCREEN_NAME = "SplashScreen";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); //no need it on sdk 4.19.0
        mCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.bahaso_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setContentView(R.layout.splash_screen);

        transitionAnimation();
        InitGoogleAnalytics();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        isConnected = connection.isConnectingToInternet();
        isInternetAvailable = connection.isInternetAvailable();

        LinearLayout layoutSplash = (LinearLayout)findViewById(R.id.linearlayoutsplash);
        layoutSplash.getBackground().setAlpha((int)(0.4f*255 ));
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorlayout);

        login_btn_fb_widget = (LoginButton) findViewById(R.id.button_facebook_login_widget);
        login_btn_fb_widget.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));

        btnFacebook = layoutSplash.findViewById(R.id.layout_btn_login_fb);
        btnFacebook = getLayoutInflater().inflate(R.layout.single_btn_login_fb, layoutSplash, false);
        layoutSplash.addView(btnFacebook);

        btnGoogle = layoutSplash.findViewById(R.id.layout_btn_login_gplus);
        btnGoogle = getLayoutInflater().inflate(R.layout.single_btn_login_gplus, layoutSplash, false);
        layoutSplash.addView(btnGoogle);

        btnRegisLogin = layoutSplash.findViewById(R.id.layout_btn_regis_login);
        btnRegisLogin = getLayoutInflater().inflate(R.layout.btn_regis_login, layoutSplash, false);
        layoutSplash.addView(btnRegisLogin);

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_FB = global.getBaseURLpath() + "auth/facebook/callback/";
        URL_GOOGLE = global.getBaseURLpath() + "auth/google/callback/";

        if(!isConnected){
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getResources().getString(R.string.error_no_network), Snackbar.LENGTH_LONG);
            snackbar.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 3s = 3000ms
                    String lastLogin = getSharedPref().getString(getResources().getString(R.string.logintimestamp), "");
                    Log.i("lastLogin tes1", lastLogin);
                    if(!lastLogin.isEmpty()) {
                        compareDate(lastLogin);
                    }
                }
            }, 2000);
        } else {
            if(!isInternetAvailable){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getResources().getString(R.string.error_internet_access),
                                Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                sendScreenImageName();
                loginUsingFB();
                btnFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("fbClicked tes1", "Clicked");
                        if (null != AccessToken.getCurrentAccessToken()) {
                            LoginManager.getInstance().logOut();
                        }
                        login_btn_fb_widget.performClick();
                    }
                });

                btnGoogle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("googleClicked tes1", "Clicked");
                        boolean isServiceAvailable = checkPlayServices();
                        if (isServiceAvailable) {
                            // Google sign out
                            try {
                                Log.i("trySignOut tes1", "SIGN OUT");
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            } catch (Exception e) {
                                // illegal state exception caused by google api is not yet connected
                                Log.i("catch tes1", String.valueOf(e.getMessage()));
                            }
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, RC_SIGN_IN);
                        }
                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 3s = 3000ms
                        String lastLogin = getSharedPref().getString(getResources().getString(R.string.logintimestamp), "");
                        Log.i("lastLogin tes1", lastLogin);
                        if(!lastLogin.isEmpty()) {
                            compareDate(lastLogin);
                        }
                    }
                }, 2000);
            }
        }
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(this);
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(this,iCategoryId,iActionId,iLabelId );
    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private void loginTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String format = simpleDateFormat.format(new Date());
        Log.i("loginTime tes1", format);
        getSharedPref().edit().putString(getString(R.string.logintimestamp), format).apply();
        getSharedPref().edit().putString("dateTemp", format).apply();
    }

    public void compareDate(final String lastLogin) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String format = sdf.format(new Date());
            Date date2 = sdf.parse(format);
            Log.i("currDate tes1", String.valueOf(date2));
            Date date1 = sdf.parse(lastLogin);
            Log.i("lastDate tes1", String.valueOf(date1));

            if(date2.after(date1)) {
                Log.i("app tes1","Date2 is after Date1");
                String loginDateTemp = getSharedPref().getString("dateTemp", "");
                Log.i("loginDateTemp tes1",loginDateTemp);
                dateTemp = sdf.parse(loginDateTemp);
                Log.i("dateTemp1 tes1", String.valueOf(dateTemp));

                if (date2.equals(dateTemp)) {
                    Log.i("dateTemp tes1", "date2 is same dateTemp");
                    isLoginAlready();
                } else if (date2.after(dateTemp)) {
                    Log.i("dateTemp tes1", "date2 not same dateTemp");
                    String temp = sdf.format(date2);
                    getSharedPref().edit().putString("dateTemp", temp).apply();
                    int flag_expire = getSharedPref().getInt(getResources().getString(R.string.flag_expire_token), 0);
                    flag_expire += 1;
                    getSharedPref().edit().putInt(getResources().getString(R.string.flag_expire_token), flag_expire).apply();
                    if (flag_expire < 8) {
                        Log.i("flagEXP 1 tes1", String.valueOf(flag_expire));
                        isLoginAlready();
                    } else {
                        Log.i("flagEXP 2 tes1", String.valueOf(flag_expire));
                        getSharedPref().edit().clear().apply();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this, R.style.MyProgressTheme);
                        dialog.setMessage("Sesi login anda berakhir, Mohon untuk login kembali");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    }
                }

            } else if(date1.before(date2)){
                Log.i("app tes1","Date1 is before Date2");

            } else if(date1.equals(date2)){
                Log.i("app tes1","Date1 is equal to Date2");
                isLoginAlready();
            }
        } catch(Exception e) {
            Log.i("compareDate tes1", "ERROR");
        }
    }

    public void onClick_splash(View v) {
        switch (v.getId()) {
            case R.id.btn_email_login:
                SendEventGoogleAnalytics("SplashScreen", "BTN_LOGIN", "BTN_LOGIN_CLICKED");
                Intent email_login = new Intent(this, Login.class);
                startActivity(email_login);
                break;

            case R.id.btn_daftar_baru:
                SendEventGoogleAnalytics("SplashScreen", "BTN_REGISTER", "BTN_REGISTER_CLICKED");
                Intent daftar = new Intent(this, Register.class);
                startActivity(daftar);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i("resultG tes1", String.valueOf(result.isSuccess()));
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                String token = account.getIdToken();
                Log.i("google token tes1", token);
                loginGoogletoServer(token);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.i("google login tes1", "FAILED!!");
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this, R.style.MyProgressTheme);
                dialog.setMessage(getResources().getString(R.string.txt_failed_synchronize_google));
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Google Play Services bermasalah",Toast.LENGTH_LONG).show();
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
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

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if(result != ConnectionResult.SUCCESS) {
            Log.i("play service tes1", "FAIL");
            return false;
        } else {
            Log.i("play service tes1", "SUCCESS");
        }
        return true;
    }

    private void loginUsingFB(){
        login_btn_fb_widget.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("success tes1", "tes");
                getUserInfoFB(loginResult);
            }

            @Override
            public void onCancel() {
                Log.i("FBcancel tes1", "facebook:onCancel:");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("FBerror tes1", "facebook:onError:" + error);
            }
        });
    }

    private void getUserInfoFB(LoginResult loginResult){
        final String token = loginResult.getAccessToken().getToken();
        Log.i("token tes1", token);

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");

                            getSharedPref().edit().putString(getResources().getString(R.string.username), name).apply();
                            getSharedPref().edit().putString(getResources().getString(R.string.useremail), email).apply();

                            loginFBtoServer(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginFBtoServer(final String tokenFB){
        final String client_secret = "client1secret";
        final String grant_type = "social_login";
        final String client_id = "client1id";

        StringRequest request = new StringRequest(Request.Method.POST, URL_FB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.i("responFB tes1", String.valueOf(jsonResponse));
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                loginTimeStamp();
                                Log.i("fb login tes1", "respon true");
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                JSONObject jsonOAUTH = jsonData.getJSONObject("oauth");
                                getSharedPref().edit().putString(getResources().getString(R.string.tokenFB), jsonOAUTH.getString("access_token")).apply();

                                JSONObject jsonUser = jsonData.getJSONObject("user");
                                getSharedPref().edit().putString(getResources().getString(R.string.userid), jsonUser.getString("id")).apply();
                                getSharedPref().edit().putInt(getResources().getString(R.string.usercoin), jsonUser.getInt("coin")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.userfirstname), jsonUser.getString("firstname")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.userlastname),jsonUser.getString("lastname")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.useremail),jsonUser.getString("email")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.username),jsonUser.getString("username")).apply();
                                getSharedPref().edit().putString(getString(R.string.useravatar), jsonUser.getString("avatar")).apply();
                                getSharedPref().edit().putBoolean(getString(R.string.isConfirmed), jsonUser.getBoolean("confirmed")).apply();
                                Boolean isConfirmed = jsonUser.getBoolean("confirmed");

                                //********************** isCompleted ********************//
                                if(JSONObject.NULL.equals(jsonUser.get("profile_completed"))){
                                    Log.i("isCompleted tes1", "NULL");
                                    getSharedPref().edit().putBoolean(getString(R.string.userprofilecompleted), false).apply();
                                } else {
                                    Log.i("isCompleted tes1", "TRUE");
                                    getSharedPref().edit().putBoolean(getString(R.string.userprofilecompleted), true).apply();
                                }

                                //********************** User Current Course *********************//
                                if(JSONObject.NULL.equals(jsonUser.get("current_course"))){
                                    Log.i("fb login tes1", "course belum dipilih");
                                    getSharedPref().edit().putString(getResources().getString(R.string.isPremium),"false").apply();
                                    getSharedPref().edit().putInt(getResources().getString(R.string.flag_current_course), 1).apply(); // 1= blm pilih, 2 = udh pilih
                                } else {
                                    Log.i("fb login tes1", "course sudah dipilih");
                                    JSONObject jsonCurrentcourse = jsonUser.getJSONObject("current_course");

                                    String isPremium = jsonCurrentcourse.getString("is_premium");
                                    getSharedPref().edit().putString(getResources().getString(R.string.isPremium), isPremium).apply();
                                    String courseID = jsonCurrentcourse.getString("id");
                                    getSharedPref().edit().putString(getResources().getString(R.string.courseid), courseID).apply();
                                    String courseName = jsonCurrentcourse.getString("name");
                                    getSharedPref().edit().putString(getResources().getString(R.string.coursename), courseName).apply();
                                    int userpoint = jsonCurrentcourse.getInt("point");
                                    getSharedPref().edit().putInt(getResources().getString(R.string.userpoint), userpoint).apply();
                                    int userscore = jsonCurrentcourse.getInt("score");
                                    getSharedPref().edit().putInt(getResources().getString(R.string.userscore), userscore).apply();

                                    double userprogress = jsonCurrentcourse.getDouble("progress");
                                    float progress = (float) userprogress;
                                    Log.i("progressFB tes1", String.valueOf(progress));
                                    getSharedPref().edit().putFloat(getResources().getString(R.string.userprogress), progress).apply();

                                    String usernativelang = jsonCurrentcourse.getString("native_lang");
                                    getSharedPref().edit().putString(getResources().getString(R.string.usernativelang), usernativelang).apply();
                                    String userlearnlang = jsonCurrentcourse.getString("learn_lang");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlearnlang), userlearnlang).apply();
                                    String urlcourseimg = jsonCurrentcourse.getString("icon_src");
                                    getSharedPref().edit().putString(getResources().getString(R.string.courseimg), urlcourseimg).apply();

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
                                //********************** /User Current Course *********************//

                                hidePDialog();
                                if (isConfirmed) {
                                    Log.i("fb login tes1", "confirm true");
                                    Intent confirm = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(confirm);
                                    finish();
                                } else {
                                    Log.i("fb login tes1", "confirm false");
                                    Intent confirm = new Intent(SplashScreen.this, Confirm.class);
                                    startActivity(confirm);
                                    finish();
                                }

                            } else {
                                hidePDialog();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this, R.style.MyProgressTheme);
                                dialog.setMessage(getResources().getString(R.string.txt_failed_synchronize_facebook));
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
                params.put("fb_access_token", tokenFB);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void loginGoogletoServer(final String tokenGoogle){
        showPDialog();
        final String client_secret = "client1secret";
        final String grant_type = "social_login";
        final String client_id = "client1id";

        StringRequest request = new StringRequest(Request.Method.POST, URL_GOOGLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("true")) {
                                loginTimeStamp();
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                JSONObject jsonOAUTH = jsonData.getJSONObject("oauth");
                                getSharedPref().edit().putString(getResources().getString(R.string.tokenGoogle), jsonOAUTH.getString("access_token")).apply();

                                JSONObject jsonUser = jsonData.getJSONObject("user");
                                getSharedPref().edit().putString(getResources().getString(R.string.userid), jsonUser.getString("id")).apply();
                                getSharedPref().edit().putInt(getResources().getString(R.string.usercoin), jsonUser.getInt("coin")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.userfirstname), jsonUser.getString("firstname")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.userlastname), jsonUser.getString("lastname")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.useremail), jsonUser.getString("email")).apply();
                                getSharedPref().edit().putString(getResources().getString(R.string.username), jsonUser.getString("username")).apply();
                                getSharedPref().edit().putString(getString(R.string.useravatar), jsonUser.getString("avatar")).apply();
                                getSharedPref().edit().putBoolean(getString(R.string.isConfirmed), jsonUser.getBoolean("confirmed")).apply();
                                Boolean isConfirmed = jsonUser.getBoolean("confirmed");

                                //********************* isCompleted ******************//
                                if (jsonUser.getBoolean("profile_completed")) {
                                    Log.i("profile_completed tes1", "TRUE");
                                    getSharedPref().edit().putBoolean(getString(R.string.userprofilecompleted),
                                            jsonUser.getBoolean("profile_completed")).apply();
                                } else {
                                    Log.i("profile_completed tes1", "NULL/FALSE");
                                    getSharedPref().edit().putBoolean(getString(R.string.userprofilecompleted),
                                            false).apply();
                                }

                                //********************* User Current Course *********************//
                                if (JSONObject.NULL.equals(jsonUser.get("current_course"))) {
                                    Log.i("googleLogin tes1", "course belum dipilih");
                                    getSharedPref().edit().putString(getResources().getString(R.string.isPremium), "false").apply();
                                    getSharedPref().edit().putInt(getResources().getString(R.string.flag_current_course), 1).apply(); // 1= blm pilih, 2 = udh pilih
                                } else {
                                    Log.i("googleLogin tes1", "course sudah dipilih");
                                    JSONObject jsonCurrentcourse = jsonUser.getJSONObject("current_course");

                                    String isPremium = jsonCurrentcourse.getString("is_premium");
                                    getSharedPref().edit().putString(getResources().getString(R.string.isPremium), isPremium).apply();
                                    String courseID = jsonCurrentcourse.getString("id");
                                    getSharedPref().edit().putString(getResources().getString(R.string.courseid), courseID).apply();
                                    String courseName = jsonCurrentcourse.getString("name");
                                    getSharedPref().edit().putString(getResources().getString(R.string.coursename), courseName).apply();
                                    int userpoint = jsonCurrentcourse.getInt("point");
                                    getSharedPref().edit().putInt(getResources().getString(R.string.userpoint), userpoint).apply();
                                    int userscore = jsonCurrentcourse.getInt("score");
                                    getSharedPref().edit().putInt(getResources().getString(R.string.userscore), userscore).apply();

                                    double userprogress = jsonCurrentcourse.getDouble("progress");
                                    float progress = (float) userprogress;
                                    Log.i("progressG tes1", String.valueOf(progress));
                                    getSharedPref().edit().putFloat(getResources().getString(R.string.userprogress), progress).apply();

                                    String usernativelang = jsonCurrentcourse.getString("native_lang");
                                    getSharedPref().edit().putString(getResources().getString(R.string.usernativelang), usernativelang).apply();
                                    String userlearnlang = jsonCurrentcourse.getString("learn_lang");
                                    getSharedPref().edit().putString(getResources().getString(R.string.userlearnlang), userlearnlang).apply();
                                    String urlcourseimg = jsonCurrentcourse.getString("icon_src");
                                    getSharedPref().edit().putString(getResources().getString(R.string.courseimg), urlcourseimg).apply();

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
                                //********************* /User Current Course *********************//

                                hidePDialog();
                                if (isConfirmed) {
                                    Intent confirm = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(confirm);
                                    finish();
                                } else {
                                    Intent confirm = new Intent(SplashScreen.this, Confirm.class);
                                    startActivity(confirm);
                                    finish();
                                }

                            } else {
                                hidePDialog();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this, R.style.MyProgressTheme);
                                dialog.setMessage(getResources().getString(R.string.txt_failed_synchronize_google));
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
                params.put("token_id", tokenGoogle);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);

    }

    private void isLoginAlready(){
        if(!getSharedPref().getString(getResources().getString(R.string.userid),"").isEmpty()){
            if(getSharedPref().getString(getResources().getString(R.string.isConfirmed),"").equals("false")){
                //if not confirm yet
                Intent confirm = new Intent(SplashScreen.this, Confirm.class);
                startActivity(confirm);
                finish();
            } else {
                Intent main = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        } else {
            Log.i("userid tes1", "is empty");
        }
    }

    private void transitionAnimation(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 3s = 3000ms
                TransitionManager.beginDelayedTransition(coordinatorLayout);
                isVisible = !isVisible;
                btnFacebook.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                btnGoogle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                btnRegisLogin.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyProgressTheme);
        alertDialogBuilder.setMessage(getResources().getString(R.string.txt_b4_exit));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //***Change Here***
                startActivity(intent);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

}
