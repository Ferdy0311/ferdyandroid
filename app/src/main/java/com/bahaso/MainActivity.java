package com.bahaso;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.BuildConfig;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.bahaso.adapter.ViewPagerAdapterHome;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.home.SettingFeedback;
import com.bahaso.home.SettingsActivity;
import com.bahaso.profile.CompleteActivity;
import com.bahaso.profile.ProfilActivity;
import com.bahaso.util.ActivityRequestCode;
import com.bahaso.util.ViewUtil;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String SCREEN_NAME = "Home";
    private GoogleAnalyticsHelper GoogleHelper;
    private AppCompatImageView compatImageView;
    private SharedPreferences sharedpref;
    private String URL_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitGoogleAnalytics();

        compatImageView = (AppCompatImageView)findViewById(R.id.icon_setting);

        int NumTabs = 3;
        int[] tabIcons = new int[] {
                R.drawable.svg_gold_icon,
                R.drawable.svg_home_icon,
                R.drawable.svg_voucher_icon};

        String[] tabTitles = new String[] {getResources().getString(R.string.tab_gold),
                getResources().getString(R.string.tab_home),
                getResources().getString(R.string.tab_voucher)};

        ViewPagerAdapterHome adapter = new ViewPagerAdapterHome(getSupportFragmentManager(),tabTitles, NumTabs);

        ViewPager pager = (ViewPager)findViewById(R.id.pagerlayout);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(NumTabs);

        TabLayout tabs = (TabLayout)findViewById(R.id.tablayout);
        tabs.setupWithViewPager(pager);

        setTabLayoutContent(tabs, tabIcons, tabTitles);
        Intent in = getIntent();
        if(in.getIntExtra(getString(R.string.flag_edit_gold), 0) == 0){
            pager.setCurrentItem(1);
        } else if(in.getIntExtra(getString(R.string.flag_edit_gold), 0) == 1){
            pager.setCurrentItem(0);
        }

        sendScreenImageName();

        compatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(in);
            }
        });

        request_getProfile(getSharedPref().getString(getString(R.string.tokenlogin),""));

        //|| getSharedPref().getString(getString(R.string.usercity), "").isEmpty()
        if(getSharedPref().getString(getString(R.string.usergender), "").isEmpty()
                || getSharedPref().getString(getString(R.string.userbirthday), "").isEmpty()
                || getSharedPref().getString(getString(R.string.userphonenumber), "").isEmpty()) {
            startActivity(new Intent(this, CompleteActivity.class));
        }
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void request_getProfile(final String token) {
        StringRequest request = new StringRequest(Request.Method.GET, URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");

                    if(status.equals("true")){
                        JSONObject jsonData = jsonResponse.getJSONObject("data");

                        getSharedPref().edit().putString(getString(R.string.usergender), jsonData.getString("gender")).apply();
                        getSharedPref().edit().putString(getString(R.string.userbirthday), jsonData.getString("birthday")).apply();
                        //getSharedPref().edit().putString(getString(R.string.usercity), jsonData.getString("city")).apply();
                        getSharedPref().edit().putString(getString(R.string.usercountryid), jsonData.getString("country_id")).apply();
                        getSharedPref().edit().putString(getString(R.string.userphonenumber), jsonData.getString("cellphonenumber")).apply();

                    }

                } catch (JSONException e) {
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
        }){
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

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(this);
    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private void setTabLayoutContent(TabLayout tabLayout, int[] tabIcons, String[]tabTitles) {
        LayoutInflater inflater = LayoutInflater.from(this);
        int tabIconsLength = tabIcons.length;
        for (int i = 0; i < tabIconsLength ; i++) {
            View tabView = inflater.inflate(R.layout.custom_main_tab, null);
            TextView tvTab = ((TextView) tabView.findViewById(R.id.tv_tab_text));
            AppCompatImageView ivTabIcon = (AppCompatImageView) tabView.findViewById(R.id.iv_tab_icon);
            ivTabIcon.setImageResource(tabIcons[i]);
            ViewUtil.setColorFilter(ivTabIcon,
                    ContextCompat.getColor(this, R.color.bahaso_dark_blue),
                    PorterDuff.Mode.SRC_ATOP);
            tvTab.setText(tabTitles[i].toUpperCase());
            tvTab.setTextColor(ContextCompat.getColor(this, R.color.bahaso_dark_blue));
            tabLayout.getTabAt(i).setCustomView(tabView);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int color = ContextCompat.getColor(MainActivity.this, R.color.bahaso_white_gray);

                View tabView = tab.getCustomView();
                TextView tvTab = ((TextView) tabView.findViewById(R.id.tv_tab_text));
                tvTab.setTextColor(color);
                AppCompatImageView ivTabIcon = (AppCompatImageView) tabView.findViewById(R.id.iv_tab_icon);
                ViewUtil.setColorFilter(ivTabIcon,
                        color,
                        PorterDuff.Mode.SRC_ATOP);
                 Log.i("Test", "selected pos: " + String.valueOf(tab.getPosition()));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int color = ContextCompat.getColor(MainActivity.this, R.color.bahaso_dark_blue);
                View tabView = tab.getCustomView();
                TextView tvTab = ((TextView) tabView.findViewById(R.id.tv_tab_text));
                tvTab.setTextColor(color);
                AppCompatImageView ivTabIcon = (AppCompatImageView) tabView.findViewById(R.id.iv_tab_icon);
                ViewUtil.setColorFilter(ivTabIcon,
                        color,
                        PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if(BuildConfig.DEBUG) {
                    Log.i("Test", "reselected pos: " + pos);
                }
            }
        });
    }

    public void onClick_main(View v) {
        switch (v.getId()){
            case R.id.icon_setting:
                Log.i("setupClick tes1", "CLICKED");
                break;

            case R.id.menu_overflow:
                Log.i("menuOver tes1", "CLICKED");
                menuOverFlow(v);
                break;
        }
    }

    private void menuOverFlow(View v){
        Context wrapper = new ContextThemeWrapper(MainActivity.this, null);
        PopupMenu popup = new PopupMenu(wrapper, v);
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.profile:
                        Intent profile = new Intent(MainActivity.this, ProfilActivity.class);
                        startActivity(profile);
                        break;

                    case R.id.feedback:
                        Intent feedback = new Intent(MainActivity.this, SettingFeedback.class);
                        startActivity(feedback);
                        break;

                    case R.id.settings:
                        Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;

                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() { finish(); }

}

