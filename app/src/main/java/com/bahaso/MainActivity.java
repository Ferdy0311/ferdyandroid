package com.bahaso;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import com.bahaso.adapter.ViewPagerAdapterHome;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.home.SettingFeedback;
import com.bahaso.home.SettingsActivity;
import com.bahaso.profile.ProfilActivity;
import com.bahaso.util.ActivityRequestCode;
import com.bahaso.util.ViewUtil;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String SCREEN_NAME = "Home";
    private GoogleAnalyticsHelper GoogleHelper;
    private AppCompatImageView compatImageView;

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

