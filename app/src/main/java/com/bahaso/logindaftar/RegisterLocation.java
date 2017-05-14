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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.Country;
import com.bahaso.util.CountryHelper;
import com.bahaso.util.CountryPickerDialog;

import java.util.List;

public class RegisterLocation extends AppCompatActivity{

    Context applicationContext;
    private String URL_REGISTER, firstnameuser, lastnameuser, emailuser, password_user, confirmationPassword_user, usergender;
    private String usercountrycode="";
    private TextView tv_country_code, tv_country;
    private Bundle bundle;
    private LinearLayout vgCountry;
    private AppCompatRadioButton rbMale, rbFemale;
    private TextInputLayout layoutUserGender, layoutCountry;
    private CountryPickerDialog mCountryDialog;
    private Country selectedCountry = null;
    private int mSelectedIndex = -1;

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
}
