package com.bahaso.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.SplashScreen;
import com.bahaso.adapter.MainSettingsBaseAdapter;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.model.BaseSettingsItem;
import com.bahaso.model.CategorySettingsItem;
import com.bahaso.model.DividerSettingsItem;
import com.bahaso.model.IconSettingsItem;
import com.bahaso.model.SwitchSettingsItem;
import com.bahaso.profile.EditProfil;
import com.bahaso.util.ActivityRequestCode;
import com.bahaso.util.AlertHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ListView mListView;
    private String URL_FAQ;
    private CompoundButton mTempCompoundButton;
    private SharedPreferences sharedpref;
    private boolean mIsLanguageSupported;
    MainSettingsBaseAdapter mMainSettingsListAdapter;
    List<BaseSettingsItem> mBaseSettingsItemList = new ArrayList<>();
    private static final String SCREEN_NAME = "GeneralSettings";
    private GoogleAnalyticsHelper GoogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adding2List();

        GlobalVar global = (GlobalVar)getApplicationContext();
        URL_FAQ = global.getBaseURLpath() + "faq/";
        sendScreenImageName();

    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(this);
    }


    private void sendScreenImageName() {
//        String name = SCREEN_NAME;
//        // [START screen_view_hit]
//        Log.i(SCREEN_NAME, "Setting screen name: " + name);
//        Tracker.setScreenName("Android~" + name);
//        Tracker.send(new HitBuilders.ScreenViewBuilder().build());
//        // [END screen_view_hit]
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void showDialogLanguageNotSupported(){
//        AlertHelper.createDialog(getApplicationContext(),
//                getString(R.string.cannot_run_speech_recognition),
//                getString(R.string.speech_recog_language_no_support),
//                true,
//                getString(R.string.ok),
//                null, null, null,
//                null,
//                null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyProgressTheme);
        alertDialogBuilder.setTitle(getString(R.string.cannot_run_speech_recognition));
        alertDialogBuilder.setMessage(getResources().getString(R.string.speech_recog_language_no_support));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogPermissionDenied(){
//        AlertHelper.createDialog(getApplicationContext(),
//                getString(R.string.cannot_run_speech_recognition),
//                getString(R.string.speech_recog_permission_denied),
//                true,
//                getString(R.string.ok),
//                null, null, null,
//                null,
//                null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyProgressTheme);
        alertDialogBuilder.setTitle(getString(R.string.cannot_run_speech_recognition));
        alertDialogBuilder.setMessage(getResources().getString(R.string.speech_recog_language_no_support));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void checkMic(final CompoundButton compoundButton){
        // use google component for recognition service
        ComponentName componentName = ComponentName.unflattenFromString(
                "com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService");
        if (null == componentName) {
            Log.e("nullComponen tes1", "no selected speech recognition service");
            AlertHelper.createDialog(getApplicationContext(),
                    getString(R.string.no_speech_recog_detected),
                    getString(R.string.install_google_app_message),
                    true,
                    getString(R.string.ok),
                    null, null, null,
                    null,
                    null);
            compoundButton.setChecked(false);
            return;
        }

        // if we have google component, continue below
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_DENIED) {
            checkMicAfterPermission(compoundButton);
            Log.d("audioDeny tes1", "DENIED");
            // END IF ! mHasCheckSupportedLanguage
        } // end permission already granted
        else {// if permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    ActivityRequestCode.REQUEST_RECORD_AUDIO);
            // go to onRequestPermissionsResult
        }
    }


    private void adding2List(){
        mListView = (ListView)findViewById(R.id.listview);
        mBaseSettingsItemList.add(new CategorySettingsItem(R.id.acccat, false,
                getString(R.string.account_title)));
        mBaseSettingsItemList.add(new IconSettingsItem(R.id.prof, true, -1,
                getString(R.string.edit_profile_title), null));
        mBaseSettingsItemList.add(new IconSettingsItem(R.id.pass, true, -1,
                getString(R.string.change_password_title), null));

        mBaseSettingsItemList.add(new DividerSettingsItem());

        mBaseSettingsItemList.add(new CategorySettingsItem(R.id.soundcat, false,
                getString(R.string.sound_title)));
        mBaseSettingsItemList.add(new SwitchSettingsItem(R.id.microphone,
                getString(R.string.microphone), null,
                getSharedPref().getBoolean(getString(R.string.flag_use_mic), false),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) { // useMic
                            Log.i("useMic tes1", "TRUE");
                            mTempCompoundButton = compoundButton;
                            checkMic(compoundButton);
                        } else {
                            getSharedPref().edit().putBoolean(getString(R.string.flag_use_mic), false).apply();
                        }
                    }
                }));
        mBaseSettingsItemList.add(new DividerSettingsItem());

        mBaseSettingsItemList.add(new IconSettingsItem(R.id.faq, true, -1,
                getString(R.string.faq), null));
//        mBaseSettingsItemList.add(new IconSettingsItem(R.id.feedback, true, -1,
//                getString(R.string.feedback), null));
        mBaseSettingsItemList.add(new IconSettingsItem(R.id.logout, true, -1,
                getString(R.string.logout_title), null));

        mMainSettingsListAdapter = new MainSettingsBaseAdapter(this,
                mBaseSettingsItemList);

        mListView.setAdapter(mMainSettingsListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick((int)id);
            }
        });
    }

    private void checkMicAfterPermission (final CompoundButton compoundButton){
        // check if there is learn language
        // http://stackoverflow.com/questions/10538791/
        // how-to-set-the-language-in-speech-recognition-on-android

        // START CHECK SUPPORTED LANGUAGE
        BroadcastReceiver langReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() != Activity.RESULT_OK) {
                    showDialogLanguageNotSupported();
                    compoundButton.setChecked(false);
                    return;
                }

                // the list of supported languages.
                Bundle results = getResultExtras(true);
                if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
                    List<String> supportedLanguages = results
                            .getStringArrayList(
                                    RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
                    if (null == supportedLanguages) {
                        // change preference use mic to false.
                        showDialogLanguageNotSupported();
                        compoundButton.setChecked(false);
                    }
                    else {
                        mIsLanguageSupported = false;
//                        String learnLanguage = DatabaseHelper.currentCourse.getLearnLanguage();
//                        for (int i = 0, sizei= supportedLanguages.size(); i<sizei; i++){
//                            String supportedLanguage = supportedLanguages.get(i);
//                            if (supportedLanguage.startsWith(learnLanguage)) {
//                                mIsLanguageSupported = true;
//                                break;
//                            }
//                        }
                        if (mIsLanguageSupported) {
                            getSharedPref().edit().putBoolean(getString(R.string.flag_use_mic), true).apply();
                        } else {
                            compoundButton.setChecked(false);
                            showDialogLanguageNotSupported();
                        }
                    }
                }
                else { // no contain key EXTRA_SUPPORTED_LANGUAGES
                    // assume language is supported
                    getSharedPref().edit().putBoolean(getString(R.string.flag_use_mic), true).apply();
                }
            }
        };
        // END CHECK SUPPORTED LANGUAGE

        Intent getLanguageIntent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        getApplicationContext().sendOrderedBroadcast(getLanguageIntent, null, langReceiver,
                null, Activity.RESULT_OK, null, null);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void onListItemClick (int itemId){
        switch (itemId) {
            case R.id.accver:
                break;
            case R.id.pass:
                Intent pass = new Intent(this, SettingChangePassword.class);
                startActivity(pass);
                break;
            case R.id.prof:
                Intent edit = new Intent(this, EditProfil.class);
                edit.putExtra(getString(R.string.flag_edit_profile), 1);
                startActivity(edit);
                finish();
                break;
            case R.id.faq:
                Uri uriUrl = Uri.parse(URL_FAQ);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                break;
            case R.id.feedback:
                Intent feedback = new Intent(this, SettingFeedback.class);
                startActivity(feedback);
//                finish();
                break;
            case R.id.logout:
                String messageDialog = getString(R.string.logout_app_message_ask_xxx,
                        getSharedPref().getString(getString(R.string.useremail),"")) ;
                final TextView tvTitle = new TextView(getApplicationContext());
                tvTitle.setText(getString(R.string.logout_app_ask));
                tvTitle.setTextSize(18);
                tvTitle.setTypeface(null, Typeface.BOLD);
                //noinspection deprecation
                tvTitle.setTextColor(getResources().getColor(R.color.bahaso_black));
                tvTitle.setPadding(50, 25, 25, 5);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyProgressTheme);
                alertDialogBuilder.setCustomTitle(tvTitle);
                alertDialogBuilder.setMessage(messageDialog);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.text_yes),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        getSharedPref().edit().clear().apply();
                        Intent in = new Intent(SettingsActivity.this, SplashScreen.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //do nothing
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case ActivityRequestCode.REQUEST_RECORD_AUDIO:
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){
                    showDialogPermissionDenied();
                } else {
                    checkMicAfterPermission(mTempCompoundButton);
                }
                break;
        }


    }

}
