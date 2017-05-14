package com.bahaso.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bahaso.MainActivity;
import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.home.SettingsActivity;
import com.bahaso.model.Country;
import com.bahaso.util.CountryHelper;
import com.bahaso.util.CountryPickerDialog;
import com.bahaso.util.ValidatorHelper;
import com.squareup.haha.perflib.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfil extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText editTextFirstName, editTextLastName, editTextPhoneNumber, editTextJOB, editTextAboutMe;
    private static EditText editTextDOB;
    private TextView tv_country, tv_country_code;
    private AppCompatRadioButton rbMale, rbFemale;
    private String userfirstname, userlastname, usergender = "", userDOB, usercountrycode = ""
            , userphone, userjob, userabout, URL_EDIT_PROFIL, token;
    private CountryPickerDialog mCountryDialog;
    private LinearLayout vgCountry;
    private int mSelectedIndex = -1, year, month, day, flagFromSettingorProfil;
    private Country selectedCountry = null;
    private SharedPreferences sharedpref;
    private ProgressDialog pDialog;
    private TextInputLayout layoutFirstName, layoutLastName, layoutDOB, layoutPhoneNumber, layoutUserGender, layoutCountry;
    private static final String SCREEN_NAME = "EditProfile";
    private GoogleAnalyticsHelper GoogleHelper;
    private Intent flagIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_EDIT_PROFIL = global.getBaseURLpath() + "profile/";
        token = getSharedPref().getString(getString(R.string.tokenlogin), "");

        layoutFirstName = (TextInputLayout)findViewById(R.id.input_layout_firstname);
        layoutLastName = (TextInputLayout)findViewById(R.id.input_layout_lastname);
        layoutDOB = (TextInputLayout)findViewById(R.id.input_layout_dob);
        layoutPhoneNumber = (TextInputLayout)findViewById(R.id.input_layout_phone);
        editTextFirstName = (EditText)findViewById(R.id.input_firstname);
        editTextLastName = (EditText)findViewById(R.id.input_lastname);
        editTextDOB = (EditText)findViewById(R.id.input_dob);
        editTextPhoneNumber = (EditText)findViewById(R.id.et_phone_number);
        editTextJOB = (EditText)findViewById(R.id.input_job);
        editTextAboutMe = (EditText)findViewById(R.id.input_about_me);
        vgCountry = (LinearLayout)findViewById(R.id.vg_input_country);
        tv_country = (TextView)findViewById(R.id.tv_input_country);
        tv_country_code = (TextView)findViewById(R.id.tv_calling_code);
        rbFemale = (AppCompatRadioButton)findViewById(R.id.rb_female);
        rbMale = (AppCompatRadioButton)findViewById(R.id.rb_male);
        AppCompatImageView compatImageView = (AppCompatImageView)findViewById(R.id.iv_calendar_icon);
        layoutUserGender = (TextInputLayout)findViewById(R.id.tin_gender);
        layoutCountry = (TextInputLayout)findViewById(R.id.tin_country);

        editTextFirstName.setText(getSharedPref().getString(getString(R.string.userfirstname), ""));
        editTextLastName.setText(getSharedPref().getString(getString(R.string.userlastname),""));
        userDOB = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        editTextDOB.setText(userDOB);

        compatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getSharedPref().getString(getString(R.string.userbirthday), "").isEmpty() ) {
                    String dob = getSharedPref().getString(getString(R.string.userbirthday), "");

                    String [] dateParts = dob.split("/");
                    String day = dateParts[0];
                    String month = dateParts[1];
                    String year = dateParts[2];

                    DatePickerDialog dialog = new DatePickerDialog(EditProfil.this, EditProfil.this,
                            Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
                    dialog.show();
                } else {
                    DatePickerDialog dialog = new DatePickerDialog(EditProfil.this, EditProfil.this, 1990, 0, 1);
                    dialog.show();
                }

            }
        });

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

        getDataSharePref();
        getDataIntent();
        sendScreenImageName();

    }

    private void getDataSharePref() {
        if (!getSharedPref().getString(getString(R.string.usergender), "").isEmpty()){
            if (getSharedPref().getString(getString(R.string.usergender), "").equals("male")){
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
        }
        if (!getSharedPref().getString(getString(R.string.userbirthday),"").isEmpty()){
            editTextDOB.setText(getSharedPref().getString(getString(R.string.userbirthday), ""));
        }
        if (!getSharedPref().getString(getString(R.string.userphonenumber), "").isEmpty()) {
            editTextPhoneNumber.setText(getSharedPref().getString(getString(R.string.userphonenumber),""));
        }
        if (!getSharedPref().getString(getString(R.string.userjob),"").isEmpty()){
            editTextJOB.setText(getSharedPref().getString(getString(R.string.userjob), ""));
        }
        if (!getSharedPref().getString(getString(R.string.userabout), "").isEmpty()){
            editTextAboutMe.setText(getSharedPref().getString(getString(R.string.userabout),""));
        }
        if (!getSharedPref().getString(getString(R.string.usercountryid), "").isEmpty()){
            usercountrycode = getSharedPref().getString(getString(R.string.usercountryid), "");
            onPopulateCountry();
            onAfterClickCountry();
        }
    }

    private void getDataIntent(){
        flagIntent = getIntent();
        flagFromSettingorProfil = flagIntent.getIntExtra(getString(R.string.flag_edit_profile), 0);
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
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

    private void saveEditedProfile(String firstname, String lastname, String gender, String phone, String job, String about, String dob){
        getSharedPref().edit().putString(getString(R.string.userfirstname), firstname).apply();
        getSharedPref().edit().putString(getString(R.string.userlastname), lastname).apply();
        getSharedPref().edit().putString(getString(R.string.userphonenumber), phone).apply();
        getSharedPref().edit().putString(getString(R.string.userjob), job).apply();
        getSharedPref().edit().putString(getString(R.string.userabout),about).apply();
        getSharedPref().edit().putString(getString(R.string.userbirthday), dob).apply();
        getSharedPref().edit().putString(getString(R.string.usergender), gender).apply();
    }

    public void onClick_editProfil(View v){
        switch (v.getId()){
            case R.id.layout_btn_simpan_edit_profile:
                int errFlagFirstName = 0 , errFlagLastName = 0;
                SendEventGoogleAnalytics("EDIT PROFILE", "BTN SAVE PROFILE", "BTN SAVE PROFILE");
                userfirstname = editTextFirstName.getText().toString();
                userlastname = editTextLastName.getText().toString();
                if (rbMale.isChecked() ){
                    usergender = "male";
                    Log.i("male tes1", usergender);
                } else if (rbFemale.isChecked()){
                    usergender = "female";
                    Log.i("female tes1", usergender);
                }
                userphone = editTextPhoneNumber.getText().toString();
                userjob = editTextJOB.getText().toString();
                userabout = editTextAboutMe.getText().toString();
                userDOB = editTextDOB.getText().toString();

                if(userfirstname.isEmpty()){
                    layoutFirstName.setError(getString(R.string.err_first_name_must_be_filled));
                } else if (!ValidatorHelper.isValidCharFirstName(userfirstname)){
                    layoutFirstName.setError(getString(R.string.err_first_name_invalid_char));
                } else if(userfirstname.length() < 3) {
                    errFlagFirstName = 1;
                    layoutFirstName.setError(getString(R.string.err_first_name_min));
                } else if (userfirstname.length() > 15) {
                    errFlagFirstName = 1;
                    layoutFirstName.setError(getString(R.string.err_first_name_max));
                } else {
                    layoutFirstName.setErrorEnabled(false);
                }

                if(userlastname.isEmpty()){
                    layoutLastName.setError(getString(R.string.err_last_name_must_be_filled));
                } else if (!ValidatorHelper.isValidCharFirstName(userlastname)){
                    layoutLastName.setError(getString(R.string.err_last_name_invalid_char));
                } else if(userlastname.length() < 3) {
                    errFlagLastName = 1;
                    layoutLastName.setError(getString(R.string.err_last_name_min));
                } else if (userlastname.length() > 15) {
                    errFlagLastName = 1;
                    layoutLastName.setError(getString(R.string.err_last_name_max));
                } else {
                    layoutLastName.setErrorEnabled(false);
                }

                if(userDOB.isEmpty() || userDOB.equals("0/0/0")){
                    layoutDOB.setError(getString(R.string.err_dob_must_be_filled));
                }
                if (usergender.isEmpty()){
                    layoutUserGender.setVisibility(View.VISIBLE);
                    layoutUserGender.setError(getString(R.string.err_gender_invalid));
                } else {
                    layoutUserGender.setVisibility(View.GONE);
                }

                if(usercountrycode.isEmpty()){
                    layoutCountry.setVisibility(View.VISIBLE);
                    layoutCountry.setError(getString(R.string.err_country_invalid));
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

                if(!userfirstname.isEmpty() && errFlagFirstName != 1 && ValidatorHelper.isValidCharFirstName(userfirstname)
                        && !userlastname.isEmpty() && errFlagLastName != 1 && ValidatorHelper.isValidCharFirstName(userlastname)
                        && !usergender.isEmpty() && !userphone.isEmpty() && !userDOB.equals("0/0/0") && !usercountrycode.isEmpty()) {
                    saveEditedProfile(userfirstname, userlastname, usergender, userphone, userjob, userabout, userDOB);
                    request_edit_profil();
                }

                break;

            case R.id.vg_input_country:
                onClickCountry();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i("yearEdit tes1", String.valueOf(year));
        Log.i("monthEdit tes1", String.valueOf(month));
        Log.i("dayEdit tes1", String.valueOf(dayOfMonth));
        String DOB = String.valueOf(dayOfMonth) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
        editTextDOB.setText(DOB);
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
                            ((ImageView) vgCountry.findViewById(R.id.iv_country_icon))
                                    .setImageResource(getResources().
                                            getIdentifier("flag_" + usercountrycode.toLowerCase(),
                                                    "drawable",
                                                    getPackageName()));
                            ((TextView) vgCountry.findViewById(R.id.tv_input_country))
                                    .setText(country.getName());
                            tv_country_code.setText(country.getCallingCodeString());

                        }
                    },
                    mSelectedIndex);
        }
        mCountryDialog.show();
        onAfterClickCountry();
    }

    private void onAfterClickCountry(){
        if (selectedCountry != null) {
            ((ImageView) vgCountry.findViewById(R.id.iv_country_icon))
                    .setImageResource(getApplicationContext().getResources().
                            getIdentifier("flag_" + selectedCountry.getCode().toLowerCase(),
                                    "drawable",
                                    getApplicationContext().getPackageName()));
            ((TextView) vgCountry.findViewById(R.id.tv_input_country))
                    .setText(selectedCountry.getName());
            tv_country_code.setText(selectedCountry.getCallingCodeString());
        } else {
            tv_country_code.setText("+00");
        }
    }

    private void request_edit_profil(){
        showPDialog();
        StringRequest request = new StringRequest(Request.Method.POST, URL_EDIT_PROFIL,
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
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                getSharedPref().edit().putString(getString(R.string.usercoin), jsonData.getString("coin")).apply();
                                getSharedPref().edit().putString(getString(R.string.userabout), jsonData.getString("aboutme")).apply();
                                getSharedPref().edit().putString(getString(R.string.usercountryid),jsonData.getString("country_id")).apply();
                                getSharedPref().edit().putString(getString(R.string.userfirstname),jsonData.getString("firstname")).apply();
                                getSharedPref().edit().putString(getString(R.string.userlastname),jsonData.getString("lastname")).apply();
                                getSharedPref().edit().putString(getString(R.string.userphonenumber),jsonData.getString("cellphonenumber")).apply();
                                getSharedPref().edit().putString(getString(R.string.userbirthday),jsonData.getString("birthday")).apply();
                                getSharedPref().edit().putString(getString(R.string.username), jsonData.getString("username")).apply();
                                getSharedPref().edit().putString(getString(R.string.useremail), jsonData.getString("email")).apply();
                                getSharedPref().edit().putString(getString(R.string.userjob), jsonData.getString("job")).apply();
                                getSharedPref().edit().putString(getString(R.string.usergender), jsonData.getString("gender")).apply();
                                //TODO user avatar belum di get

                                JSONArray jsonCourse = jsonData.getJSONArray("courses");
                                if(jsonCourse != null) {
                                    Log.d("null editCourse tes1", "bla bla bla");
                                    getSharedPref().edit().putInt(getString(R.string.flag_current_course), 1).apply(); // 1= blm pilih, 2 = udh pilih
                                } else {
                                    Log.d("null editCourse tes1", "bla bla bla");
                                    int lengthCourse = jsonCourse.length();
                                    for (int i = 0; i < lengthCourse; i++) {
                                        JSONObject jsonObject = jsonCourse.getJSONObject(i);
                                        getSharedPref().edit().putString(getString(R.string.courseid), jsonObject.getString("id")).apply();
                                        getSharedPref().edit().putString(getString(R.string.coursename), jsonObject.getString("name")).apply();
                                        getSharedPref().edit().putString(getString(R.string.userscore), jsonObject.getString("score")).apply();
                                        getSharedPref().edit().putString(getString(R.string.userpoint), jsonObject.getString("point")).apply();
                                        getSharedPref().edit().putString(getString(R.string.userjob), jsonObject.getString("job")).apply();
                                        float userprogress = (float) jsonObject.getDouble("progress");
                                        getSharedPref().edit().putFloat(getString(R.string.userprogress), userprogress).apply();
                                        //TODO course avatar belum di get
                                    }
                                }

                                hidePDialog();
                                //TODO just refresh UI/reload whole profileFragment
                                Intent in = new Intent(getApplicationContext(), ProfilActivity.class);
//                                in.putExtra(getString(R.string.flag_edit_profile), 1);
                                startActivity(in);
                                finish();
                                Log.i("diEDIT tes1", "bla bla bla");

                            } else {
                                Toast.makeText(EditProfil.this, message, Toast.LENGTH_LONG).show();
                                hidePDialog();
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
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("firstname", userfirstname);
                params.put("lastname", userlastname);
                params.put("gender", usergender);
                params.put("cellphonenumber", userphone);
                params.put("country_id", usercountrycode);
                params.put("birthday", userDOB);
                params.put("job", userjob);
                params.put("aboutme", userabout);
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
                if (flagFromSettingorProfil == 0) {
                    Intent in = new Intent(this, ProfilActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(this, SettingsActivity.class);
                    startActivity(in);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (flagFromSettingorProfil == 0) {
            Intent in = new Intent(this, ProfilActivity.class);
            startActivity(in);
            finish();
        } else {
            Intent in = new Intent(this, SettingsActivity.class);
            startActivity(in);
            finish();
        }
    }
}
