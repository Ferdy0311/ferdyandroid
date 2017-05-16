package com.bahaso.logindaftar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.bahaso.model.City;
import com.bahaso.model.Country;
import com.bahaso.util.CountryHelper;
import com.bahaso.util.CountryPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterLocation extends AppCompatActivity implements TextWatcher {

    Context applicationContext;
    private ProgressDialog pDialog;
    private String URL_REGISTER,URL_CITY, firstnameuser, lastnameuser, emailuser, password_user, usergender;
    private String usercountrycode="", usercity="", userphone="";
    private TextView tv_country_code, tv_country;
    private Bundle bundle;
    private LinearLayout vgCountry;
    private AppCompatRadioButton rbMale, rbFemale;
    private TextInputLayout layoutUserGender, layoutCountry, layoutCity, layoutPhoneNumber;
    private CountryPickerDialog mCountryDialog;
    private Country selectedCountry = null;
    private int mSelectedIndex = -1;
    private List<City> mCityList = new ArrayList<>();
    private Spinner spinnerCity;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView autoCompleteTextViewCity;
    private SharedPreferences sharedpref;
    private EditText et_userPhone;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_location);

        int currentApiVersion = Build.VERSION.SDK_INT;
        applicationContext = getApplicationContext();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView terms = (TextView)findViewById(R.id.txt_terms);
        if(currentApiVersion >= Build.VERSION_CODES.N) {
            terms.setText(Html.fromHtml(getString(R.string.terms),Html.FROM_HTML_MODE_LEGACY));
            terms.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
        //noinspection deprecation
            terms.setText(Html.fromHtml(getString(R.string.terms)));
            terms.setMovementMethod(LinkMovementMethod.getInstance());
        }

        final GlobalVar global = (GlobalVar)applicationContext;
        URL_REGISTER = global.getBaseURLpath() + "signup/";
        URL_CITY = "https://dc33c8b0a252f4219c9698652d4e4708.bahaso.com/cities/";
//        URL_CITY = global.getBaseURLpath() + "cities/";



        bundle = getIntent().getExtras();
        firstnameuser = bundle.getString("firstname");
        lastnameuser = bundle.getString("lastname");
        emailuser = bundle.getString("email");
        password_user = bundle.getString("password");

        rbFemale = (AppCompatRadioButton) findViewById(R.id.rb_female_register_location);
        rbMale = (AppCompatRadioButton) findViewById(R.id.rb_male_register_Location);
        layoutUserGender = (TextInputLayout) findViewById(R.id.tin_gender_register_location);
        vgCountry = (LinearLayout)findViewById(R.id.vg_input_country_register_location);
        tv_country = (TextView)findViewById(R.id.tv_input_country_register_location);
        tv_country_code = (TextView)findViewById(R.id.tv_calling_code_register_location);
        layoutCountry = (TextInputLayout) findViewById(R.id.tin_country_register_location);
        autoCompleteTextViewCity = (AutoCompleteTextView) findViewById(R.id.ac_input_city_register_location);
        layoutCity = (TextInputLayout) findViewById(R.id.tin_city_register_location);
        layoutPhoneNumber = (TextInputLayout) findViewById(R.id.input_layout_phone_register_location);
        et_userPhone = (EditText) findViewById(R.id.et_phone_number_register_location);

        autoCompleteTextViewCity.addTextChangedListener(this);

        rbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbMale.setChecked(true);
            }
        });
        rbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbFemale.setChecked(true);
            }
        });

        request_getCity();
    }


    public void request_getCity(){
        StringRequest request = new StringRequest(Request.Method.GET, URL_CITY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("msg");

                    if(status.equals("true")){
                        JSONArray jsonCityArray = jsonResponse.getJSONArray("data");
                        ArrayList<String> arrayList = new ArrayList<String>();
                        for(int i = 0; i< jsonCityArray.length(); i++){
                            JSONObject jsonCityObject = jsonCityArray.getJSONObject(i);
//                            String countryId = jsonCityObject.getString("country_iso_3166_2");
//                            int cityId = jsonCityObject.getInt("raw_city_id");
//                            String cityType = jsonCityObject.getString("type");
                            String cityName = jsonCityObject.getString("name");
                            arrayList.add(cityName);
                        }

                        adapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                arrayList);

                        autoCompleteTextViewCity.setAdapter(adapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
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
                    Log.i("err21",error.getMessage() + " sss");
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.error_server),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.error_internet_access),
                            Toast.LENGTH_LONG).show();
                }
            }
        })
        {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<>();
//                // the POST parameters:
//                params.put("","");
//                return params;
//            }
        }
        ;
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    public void onClick_registerLocation(View view){
        switch (view.getId()){
            case R.id.layout_btn_register_location:
                usercity = autoCompleteTextViewCity.getText().toString();
                usercountrycode = tv_country.getText().toString();
                userphone = et_userPhone.getText().toString();

                if(rbMale.isChecked()){
                    usergender = "male";
                } else if(rbFemale.isChecked()){
                    usergender = "female";
                } else {
                    usergender = "";
                }

                if(usergender.isEmpty()){
                    layoutUserGender.setVisibility(View.VISIBLE);
                    layoutUserGender.setError(getString(R.string.err_gender_invalid));
                } else {
                    layoutUserGender.setVisibility(View.GONE);
                }

                if(usercountrycode.isEmpty()){
                    layoutCountry.setVisibility(View.VISIBLE);
                    layoutCountry.setError(getString(R.string.err_country_invalid));
                } else {
                    layoutCountry.setVisibility(View.GONE);
                }

                if(usercity.isEmpty()){
                    layoutCity.setVisibility(View.VISIBLE);
                    layoutCity.setError("Kota harus diisi");
                } else {
                    layoutCity.setVisibility(View.GONE);
                }

                if(userphone.isEmpty()){
                    layoutPhoneNumber.setError(getString(R.string.err_invalid_phone_number));
                } else if(userphone.length() < 3) {
                    layoutPhoneNumber.setError(getString(R.string.err_invalid_phone_number));
                } else if (userphone.length() > 15) {
                    layoutPhoneNumber.setError(getString(R.string.err_invalid_phone_number));
                } else {
                    layoutPhoneNumber.setErrorEnabled(false);
                }

                if(!usergender.isEmpty() && !usercountrycode.isEmpty() && !usercity.isEmpty()
                        && (userphone.length() >= 3 && userphone.length() <=15)) {
                    register_new_user_to_server();
                }

                break;
            case R.id.vg_input_country_register_location:
                onClickCountry();
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
                                Intent confirm = new Intent(RegisterLocation.this, Confirm.class);
                                startActivity(confirm);
                                finish();

                            } else {
                                hidePDialog();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterLocation.this, R.style.MyProgressTheme);
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

    private void onClickCountry() {
        onPopulateCountry();
        if (null == mCountryDialog) {
            mCountryDialog = new CountryPickerDialog(this,
                    R.style.Bahaso_AlertDialog,
                    new CountryPickerDialog.OnCountrySetListener() {
                        @Override
                        public void onCountrySet(Country country) {
                            usercountrycode = country.getCode();
                            Log.i("userCountryCode tes1", usercountrycode);
                            ((ImageView) vgCountry.findViewById(R.id.iv_country_icon_register_location))
                                    .setImageResource(getResources().
                                            getIdentifier("flag_" + usercountrycode.toLowerCase(),
                                                    "drawable",
                                                    getPackageName()));
                            ((TextView) vgCountry.findViewById(R.id.tv_input_country_register_location))
                                    .setText(country.getName());
                            tv_country_code.setText(country.getCallingCodeString());

                        }
                    },
                    mSelectedIndex);
        }
        mCountryDialog.show();
        onAfterClickCountry();

    }
    private void onPopulateCountry(){
        if (!TextUtils.isEmpty(usercountrycode)) {
            List<Country> countryList= CountryHelper.getCountryList(getApplicationContext());
            for (int i = 0, sizei = countryList.size(); i < sizei; i++){
                if (countryList.get(i).getCode().equals(usercountrycode)){
                    selectedCountry = countryList.get(i);
                    mSelectedIndex = i;
                    break;
                }
            }
        }
    }
    private void onAfterClickCountry(){
        if (selectedCountry != null) {
            ((ImageView) vgCountry.findViewById(R.id.iv_country_icon_register_location))
                    .setImageResource(getApplicationContext().getResources().
                            getIdentifier("flag_" + selectedCountry.getCode().toLowerCase(),
                                    "drawable",
                                    getApplicationContext().getPackageName()));
            ((TextView) vgCountry.findViewById(R.id.tv_input_country_register_location))
                    .setText(selectedCountry.getName());
            tv_country_code.setText(selectedCountry.getCallingCodeString());

        } else {
            tv_country_code.setText("+00");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
