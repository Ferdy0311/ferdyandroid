package com.bahaso.gold;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.util.AlertHelper;

//import com.bahaso.helper.AlertHelper;
import id.co.veritrans.sdk.coreflow.BuildConfig;
import id.co.veritrans.sdk.coreflow.core.Constants;

public class PaymentWebActivity extends AppCompatActivity {
    private WebView mWebView;
    private static final String DONE = "done";
    private static final String SUCCESS = "scs";

    Handler mDismissHandler;
    Runnable mDismissRunnable;
    boolean mIsTransactionDone;
    boolean mIs3dsSuccessOrNotEnrolled;

    View mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web);
        String webURL = getIntent().getStringExtra(Constants.WEBURL);

        mLoadingProgressBar = findViewById(R.id.loading_progress_bar);
        mWebView = (WebView) findViewById(R.id.webview);
        initwebview();

        if (null == savedInstanceState) {
            mIsTransactionDone = false;
            mIs3dsSuccessOrNotEnrolled = false;
            mWebView.loadUrl(webURL);
        }
        else {
            mIsTransactionDone = savedInstanceState.getBoolean(DONE);
            mIs3dsSuccessOrNotEnrolled = savedInstanceState.getBoolean(SUCCESS);
            mWebView.restoreState(savedInstanceState);
        }
        initiateToolbar();
    }

    @SuppressLint("AddJavascriptInterface")
    private void initwebview(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setInitialScale(25);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // hide progress dialog
                mLoadingProgressBar.setVisibility(View.INVISIBLE);
                if (url.contains(BuildConfig.CALLBACK_STRING)) {
                    mIsTransactionDone = true;
                    view.loadUrl("javascript:console.log(document.getElementsByClassName('container')[0].innerHTML);");
                    setCancelWebView();

//                    Intent returnIntent = new Intent();
//                    setResult(RESULT_OK, returnIntent);
//                    PaymentWebActivity.this.finish();
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                /* process JSON */
                String callbackString = consoleMessage.message();
                if (callbackString.contains("Success")) {
                    mIs3dsSuccessOrNotEnrolled = true;
                    initTimerToFinish();
                }
                if (callbackString.contains("Not enrolled")) {
                    mIs3dsSuccessOrNotEnrolled = true;
                    setResult(RESULT_OK);
                    PaymentWebActivity.this.finish();
                }
                return true;
            }
        });
    }

    private void initiateToolbar (){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // fix color tint in some device
        Drawable icon = toolbar.getNavigationIcon();
        if (null!=icon){
            icon.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    android.R.color.white)
                    , PorterDuff.Mode.MULTIPLY);
            toolbar.setNavigationIcon(icon);
        }

//        TextView tvToolbarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
//        tvToolbarTitle.setText(getString(R.string.secure_3d));
        toolbar.setTitle(getString(R.string.secure_3d));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // mActionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        // show default back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Toolbar.LayoutParams lp = (Toolbar.LayoutParams) toolbar.getChildAt(0).getLayoutParams();
        lp.gravity = Gravity.CENTER_VERTICAL;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
//                this.finish();
//                overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement
    }

    @Override
    protected void onPause() {
        if (null!=mDismissHandler && null!= mDismissRunnable) {
            mDismissHandler.removeCallbacks(mDismissRunnable);
            mDismissRunnable = null;
            mDismissHandler = null;
        }
        mWebView.setOnTouchListener(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsTransactionDone) {
            initTimerToFinish();
            setCancelWebView();
        }
    }

    private void initTimerToFinish(){
        if (null == mDismissHandler){
            mDismissHandler = new Handler();
        }
        if (null == mDismissRunnable ){
            mDismissRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        setResult(mIs3dsSuccessOrNotEnrolled ? RESULT_OK : RESULT_CANCELED);
                        PaymentWebActivity.this.finish();
                        mDismissHandler = null;
                        mDismissRunnable = null;
                    }
                    catch (Exception e) {

                    }
                }
            };
        }
        // 10 second to destroy activity
        mDismissHandler.postDelayed(mDismissRunnable, 10000);
    }

    private void setCancelWebView(){
        mWebView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        setResult(mIs3dsSuccessOrNotEnrolled ? RESULT_OK : RESULT_CANCELED);
                        PaymentWebActivity.this.finish();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsTransactionDone) {
            setResult(mIs3dsSuccessOrNotEnrolled ? RESULT_OK: RESULT_CANCELED);
            super.onBackPressed();
        }
        else {
            AlertHelper.createDialog(PaymentWebActivity.this,
                    getString(R.string.payment_web_exit_dialog_title),
                    getString(R.string.payment_web_exit_dialog_message),
                    true,
                    getString(R.string.text_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_CANCELED);
                            PaymentWebActivity.super.onBackPressed();
                        }
                    },
                    getString(R.string.text_no),
                    null,
                    null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DONE, mIsTransactionDone);
        outState.putBoolean(SUCCESS, mIs3dsSuccessOrNotEnrolled);
        mWebView.saveState(outState);
    }
}
