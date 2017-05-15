package com.bahaso.logindaftar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.City;
import com.bahaso.model.Country;
import com.bahaso.util.CountryHelper;
import com.bahaso.util.CountryPickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterLocation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Context applicationContext;
    private String URL_REGISTER,URL_CITY, firstnameuser, lastnameuser, emailuser, password_user, confirmationPassword_user, usergender;
    private String usercountrycode="";
    private TextView tv_country_code, tv_country;
    private Bundle bundle;
    private LinearLayout vgCountry;
    private AppCompatRadioButton rbMale, rbFemale;
    private TextInputLayout layoutUserGender, layoutCountry;
    private CountryPickerDialog mCountryDialog;
    private Country selectedCountry = null;
    private int mSelectedIndex = -1;
    private List<City> mCityList = new ArrayList<>();
    private Spinner spinnerCity;

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
        URL_CITY = global.getBaseURLpath() + "cities/";

        bundle = getIntent().getExtras();
        firstnameuser = bundle.getString("firstname");
        lastnameuser = bundle.getString("lastname");
        emailuser = bundle.getString("email");
        password_user = bundle.getString("password");
        confirmationPassword_user = bundle.getString("confirmpassword");

        rbFemale = (AppCompatRadioButton) findViewById(R.id.rb_female_register_location);
        rbMale = (AppCompatRadioButton) findViewById(R.id.rb_male_register_Location);
        layoutUserGender = (TextInputLayout) findViewById(R.id.tin_gender_register_location);
        vgCountry = (LinearLayout)findViewById(R.id.vg_input_country_register_location);
        tv_country = (TextView)findViewById(R.id.tv_input_country_register_location);
        tv_country_code = (TextView)findViewById(R.id.tv_calling_code_register_location);
        layoutCountry = (TextInputLayout) findViewById(R.id.tin_country_register_location);
        spinnerCity = (Spinner) findViewById(R.id.sp_input_city_register_location);

        spinnerCity.setOnItemSelectedListener(this);

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


    }

    public void getCityName(){

    }

    public void request_getCity(final String token){
        StringRequest request = new StringRequest(Request.Method.GET, URL_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    if(status.equals("true")){
                        JSONArray jsonCityArray = jsonResponse.getJSONArray("data");
                        for(int i = 0; i< jsonCityArray.length(); i++){
                            JSONObject jsonCityObject = jsonCityArray.getJSONObject(i);
                            String countryId = jsonCityObject.getString("country_iso_3166_2");
                            int cityId = jsonCityObject.getInt("raw_city_id");
                            String cityType = jsonCityObject.getString("type");
                            String cityName = jsonCityObject.getString("name");
                            City city = new City(cityName, cityId, cityType, countryId);
                            mCityList.add(city);
                        }
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
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.error_server),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.error_internet_access),
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public void onClick_registerLocation(View view){
        switch (view.getId()){
            case R.id.layout_btn_register_location:

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
                }

                break;
            case R.id.vg_input_country_register_location:
                onClickCountry();
                break;
        }
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
            layoutCountry.setVisibility(View.GONE);
        } else {
            tv_country_code.setText("+00");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
