package com.bahaso.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bahaso.database.DB_UserCourses;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.model.UserCurrentCourse;
import com.bahaso.util.ActivityRequestCode;
import com.bahaso.util.BitmapHelper;
import com.bahaso.util.ConnectionDetector;
import com.bahaso.widget.ExpandableTextView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

    private String URL_PROFIL, URL_PROFIL_AVATAR, URL_COURSE_EXP, usercountrycode;
    private SharedPreferences sharedpref;
    private TextView tvName, tvUsername, tvPremiumStatus, tvBirthdate
            , tvEmail, tvJob, tvCountry, tvPoint, tvCurrentCourse;
    private RoundedImageView roundedImageView;
    private AppCompatImageView avatar_img, edit_img, camera_img, change_course_img;
    private GlobalVar global;
    private Bitmap mEditedBitmap;
    private boolean isCameraAllowed = false;
    private static final String SCREEN_NAME = "Profile";
    private GoogleAnalyticsHelper GoogleHelper;
    private ImageView premium_img, img_country;
    private LinearLayout countryLayout, certificateLayout;
    private ExpandableTextView txt_about;
    boolean isConnected = false, isInternetAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        InitGoogleAnalytics();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        isConnected = connection.isConnectingToInternet();
        isInternetAvailable = connection.isInternetAvailable();

        tvName = (TextView)findViewById(R.id.tv_name);
        tvUsername = (TextView)findViewById(R.id.tv_username);
        tvPremiumStatus = (TextView)findViewById(R.id.tv_premium_status);
        tvPoint = (TextView)findViewById(R.id.tv_points);
        tvBirthdate = (TextView)findViewById(R.id.tv_birthdate);
        tvEmail = (TextView)findViewById(R.id.tv_email);
        tvJob = (TextView)findViewById(R.id.tv_job);
        tvCountry = (TextView)findViewById(R.id.tv_country);
        txt_about = (ExpandableTextView)findViewById(R.id.tv_about);
        tvCurrentCourse = (TextView)findViewById(R.id.tv_current_course);
        roundedImageView = (RoundedImageView)findViewById(R.id.iv_profile_image);
        avatar_img = (AppCompatImageView)findViewById(R.id.iv_current_course);
        edit_img = (AppCompatImageView)findViewById(R.id.iv_edit_profile);
        camera_img = (AppCompatImageView)findViewById(R.id.iv_camera);
        premium_img = (ImageView)findViewById(R.id.iv_img_premium);
        countryLayout = (LinearLayout)findViewById(R.id.country_layout);
        img_country = (ImageView)findViewById(R.id.country_img);
        certificateLayout = (LinearLayout)findViewById(R.id.vg_certificate);
        change_course_img = (AppCompatImageView)findViewById(R.id.iv_change_course);

        global = (GlobalVar)getApplicationContext();
        URL_PROFIL = global.getBaseURLpath() + "profile/";
        URL_PROFIL_AVATAR = global.getBaseURLpath() + "profile/avatar/";
        URL_COURSE_EXP = global.getBaseURLpath() + "course/premium/expired_date/";

        camera_img.setOnClickListener(this);
        roundedImageView.setOnClickListener(this);
        edit_img.setOnClickListener(this);
        certificateLayout.setOnClickListener(this);

        if (!isConnected) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.error_network_timeout),
                    Toast.LENGTH_LONG).show();
            getDataSharePref();
        } else {
            if (!isInternetAvailable){
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.error_internet_access),
                        Toast.LENGTH_LONG).show();
            } else {
                request_getprofil(getSharedPref().getString(getString(R.string.tokenlogin), ""));
                request_getExpireCourse(getSharedPref().getString(getString(R.string.tokenlogin), ""));
                sendScreenImageName();
            }
        }

        Fabric.with(this, new Crashlytics());

    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getApplicationContext());
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(getApplicationContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(getApplicationContext(),iCategoryId,iActionId,iLabelId );
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

    private void checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ActivityRequestCode.PERMIT_WRITE_EXTERNAL_STORAGE);
        } else {
            Log.i("STORAGE AlLOW tes1", "ALLOWED");
            checkPermissionCamera();
        }
    }

    private void checkPermissionCamera() {
        if (global.getAPIversion() == Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        ActivityRequestCode.PERMIT_CAMERA);
            } else {
                Log.i("CAM AlLOW tes1", "ALLOWED");
                isCameraAllowed = true;
                startChooserIntent();
            }
        }
    }

    private void startChooserIntent(){
        Log.i("startChooser tes1", "bla bla bla");
        Intent pickIntent;

        pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.select_image));
        // camera is assumed always allowed
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File image = GlobalVar.getAvatarFile(getApplicationContext());
        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                getString(R.string.file_provider_authority), image);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (isCameraAllowed) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent, cameraIntent});
        } else {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        }
        startActivityForResult(chooserIntent, ActivityRequestCode.REQUEST_CAMERA_OR_GALLERY);
    }

    private void getDataSharePref() {
        if(getSharedPref().getInt(getString(R.string.userpoint), 0) == 0){
            tvPoint.setText(getString(R.string.txt_empty_point));
        }
        if (!getSharedPref().getString(getString(R.string.userbirthday),"").isEmpty()){
            tvBirthdate.setText(getSharedPref().getString(getString(R.string.userbirthday), ""));
        }
        if (!getSharedPref().getString(getString(R.string.userjob),"").isEmpty()){
            tvJob.setText(getSharedPref().getString(getString(R.string.userjob), ""));
        }
        if (!getSharedPref().getString(getString(R.string.userabout), "").isEmpty()){
            txt_about.setText(getSharedPref().getString(getString(R.string.userabout),""));
        }
        if (!getSharedPref().getString(getString(R.string.usercountryid), "").isEmpty()){
            img_country.setImageResource(getResources().getIdentifier("flag_" + getSharedPref().getString(getString(R.string.usercountryid), "").
                    toLowerCase(), "drawable", getApplicationContext().getPackageName()));
            tvCountry.setText(getSharedPref().getString(getString(R.string.usercountryid), ""));
        }
        if (!getSharedPref().getString(getString(R.string.useremail), "").isEmpty()){
            tvEmail.setText(getSharedPref().getString(getString(R.string.useremail), ""));
        }
        if (!getSharedPref().getString("", "").isEmpty()){
            tvName.setText(getSharedPref().getString("", ""));
        }
        if (!getSharedPref().getString(getString(R.string.username),"").isEmpty()){
            tvUsername.setText(getSharedPref().getString(getString(R.string.username),""));
        }
        roundedImageView.setImageResource(R.drawable.no_avatar);
    }

    private void convertImg2Grayscale(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable)premium_img.getDrawable();
        Bitmap imgORI = bitmapDrawable.getBitmap();
        Bitmap imgGray = toGrayscale(imgORI);
        premium_img.setImageBitmap(imgGray);
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);

        return bmpGrayscale;
    }

    public void request_getprofil(final String token){
        final DB_UserCourses userCourseDB = new DB_UserCourses(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, URL_PROFIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if(status.equals("true")){
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                //********* User Birthday *********//
                                String dob = jsonData.getString("birthday");
                                if(dob.isEmpty()) {
                                    tvBirthdate.setTypeface(null, Typeface.ITALIC);
                                    tvBirthdate.setText(getString(R.string.profile_not_filled));
                                } else {
                                    tvBirthdate.setText(dob);
                                }

                                //*********** User Name ***********//
                                String username = jsonData.getString("username");
                                tvUsername.setText(username);
                                getSharedPref().edit().putString(getString(R.string.username), jsonData.getString("username")).apply();
                                //********* User Course *********//
                                JSONArray jsonCourse = jsonData.getJSONArray("courses");
                                int jsonCourseLength = jsonCourse.length();
                                //noinspection ConstantConditions
                                if(jsonCourse != null && jsonCourseLength > 0){
                                    if(jsonCourseLength == 1) {
                                        Log.i("courseLength tes1", "hanya 1");
                                        for (int i = 0; i < jsonCourseLength; i++) {
                                            change_course_img.setVisibility(View.GONE);
                                            JSONObject jsonInsideCourse = jsonCourse.getJSONObject(i);
                                            String nameCourse = jsonInsideCourse.getString("name");
                                            tvCurrentCourse.setText(nameCourse);
                                            String linkImageCourse = jsonInsideCourse.getString("icon_src");
                                            String totalScore = jsonInsideCourse.getString("total_score");
                                            String totalPoint = jsonInsideCourse.getString("total_point");
                                            int userscore = jsonInsideCourse.getInt("score");
                                            int userpoint = jsonInsideCourse.getInt("point");
                                            float userprogress = (float) jsonInsideCourse.getDouble("progress");

                                            Picasso.with(getApplicationContext()).load(linkImageCourse).into(avatar_img);
                                            getSharedPref().edit().putString(getString(R.string.coursename), nameCourse).apply();
                                            getSharedPref().edit().putString(getString(R.string.totalscore), totalScore).apply();
                                            getSharedPref().edit().putString(getString(R.string.totalpoint), totalPoint).apply();
                                            getSharedPref().edit().putInt(getString(R.string.userscore), userscore).apply();
                                            getSharedPref().edit().putInt(getString(R.string.userpoint), userpoint).apply();
                                            getSharedPref().edit().putFloat(getString(R.string.userprogress), userprogress).apply();
                                        }
                                    } else if (jsonCourseLength > 1) {
                                        Log.i("courseLength tes1", "lebih dari 1");
                                        for (int i = 0; i < jsonCourseLength; i++) {
                                            UserCurrentCourse mdlUserCourse = new UserCurrentCourse();
                                            JSONObject jsonInsideCourse = jsonCourse.getJSONObject(i);
                                            String nameCourse = jsonInsideCourse.getString("name");
                                            tvCurrentCourse.setText(nameCourse);
                                            String linkImageCourse = jsonInsideCourse.getString("icon_src");
                                            Picasso.with(getApplicationContext()).load(linkImageCourse).into(avatar_img);

                                            mdlUserCourse.setCourseID(jsonInsideCourse.getInt("id"));
                                            mdlUserCourse.setCourseName(jsonInsideCourse.getString("name"));
                                            mdlUserCourse.setCourseImg(jsonInsideCourse.getString("icon_src"));
                                            mdlUserCourse.setCourseScore(jsonInsideCourse.getInt("score"));
                                            mdlUserCourse.setCoursePoint(jsonInsideCourse.getInt("point"));
                                            mdlUserCourse.setTotalScore(jsonInsideCourse.getString("total_score"));
                                            mdlUserCourse.setTotalPoint(jsonInsideCourse.getString("total_point"));
                                            mdlUserCourse.setCourseProgress(jsonInsideCourse.getDouble("progress"));
                                            userCourseDB.addCourse2DB(mdlUserCourse);
                                        }
                                    }

                                } else {
                                    Log.i("courseLength tes1", "belum pilih course");
                                    tvCurrentCourse.setText(getString(R.string.course_not_choosen));
                                    tvPoint.setText(getString(R.string.txt_empty_point));
                                    change_course_img.setVisibility(View.GONE);
                                }

                                String email = jsonData.getString("email");
                                tvEmail.setText(email);
                                getSharedPref().edit().putString(getString(R.string.useremail), jsonData.getString("email")).apply();

                                int coin = jsonData.getInt("coin");
                                String firstname = jsonData.getString("firstname");
                                String lastname = jsonData.getString("lastname");
                                String name = firstname + " " + lastname;
                                tvName.setText(name);
                                getSharedPref().edit().putString("", name).apply();

                                String linkAvatar = jsonData.getString("avatar");
                                Picasso.with(getApplicationContext()).load(linkAvatar).into(roundedImageView);

                                //********************* User Job *********************//
                                if (jsonData.has("job") && !jsonData.getString("job").isEmpty()) {
                                    getSharedPref().edit().putString(getString(R.string.userjob), jsonData.getString("job")).apply();
                                } else {
                                    getSharedPref().edit().putString(getString(R.string.userjob), "").apply();
                                }

                                if (getSharedPref().getString(getString(R.string.userjob), "").isEmpty()){
                                    tvJob.setTypeface(null, Typeface.ITALIC);
                                    tvJob.setText(getString(R.string.profile_not_filled));
                                } else {
                                    tvJob.setText(getSharedPref().getString(getString(R.string.userjob),""));
                                }

                                //*********************** User Country *********************//
                                if (jsonData.has("country_id") && !jsonData.getString("country_id").isEmpty()) {
                                    Log.i("profile country id", "it has and not empty");
                                    getSharedPref().edit().putString(getString(R.string.usercountryid), jsonData.getString("country_id")).apply();
                                } else {
                                    Log.i("profile country id", "it hasn't");
                                    getSharedPref().edit().putString(getString(R.string.usercountryid), "").apply();
                                }

                                if(getSharedPref().getString(getString(R.string.usercountryid), "").isEmpty()){
                                    Log.i("countryID tes1", "empty");
                                    countryLayout.setVisibility(View.GONE);
                                } else {
                                    Log.i("countryID tes1", "not empty");
                                    img_country.setImageResource(getResources().getIdentifier("flag_" + jsonData.getString("country_id").
                                            toLowerCase(), "drawable", getApplicationContext().getPackageName()));
                                    tvCountry.setText(getSharedPref().getString(getString(R.string.usercountryid),""));
                                }

                                //********************** isPremium User *******************//
                                if(getSharedPref().getString(getString(R.string.isPremium), "false").equals("false")){
                                    convertImg2Grayscale();
                                    tvPremiumStatus.setText(getString(R.string.non_premium_status));
                                } else {
                                    String date = getSharedPref().getString(getString(R.string.expire_course_upgrade), "");
                                    //noinspection deprecation
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                                            getApplicationContext().getResources().getConfiguration().locale);
                                    try {
                                        Date dt = df.parse(date);
                                        //noinspection deprecation
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy",
                                                getApplicationContext().getResources().getConfiguration().locale);
                                        String formattedDate = dateFormat.format(dt);
                                        tvPremiumStatus.setText(getString(R.string.premium_until_xx,formattedDate));
                                    } catch (Exception e){}
                                }

                                //******************** User About *********************//
                                if (jsonData.has("aboutme") && !jsonData.getString("aboutme").isEmpty()) {
                                    getSharedPref().edit().putString(getString(R.string.userabout), jsonData.getString("aboutme")).apply();
                                } else {
                                    getSharedPref().edit().putString(getString(R.string.userabout), "").apply();
                                }

                                if(getSharedPref().getString(getString(R.string.userabout), "").isEmpty()){
                                    Log.i("userAbout tes1", "about empty");
                                } else {
                                    txt_about.setText(getSharedPref().getString(getString(R.string.userabout),""));
                                }

                                //************************ User Gender ************************//
                                if (jsonData.has("gender") && !jsonData.getString("gender").isEmpty()){
                                    getSharedPref().edit().putString(getString(R.string.usergender), jsonData.getString("gender")).apply();
                                } else {
                                    getSharedPref().edit().putString(getString(R.string.usergender), "").apply();
                                }

                                //*********************** User Phone ***********************//
                                if (jsonData.has("cellphonenumber") && !jsonData.getString("cellphonenumber").isEmpty()){
                                    getSharedPref().edit().putString(getString(R.string.userphonenumber), jsonData.getString("cellphonenumber")).apply();
                                } else {
                                    getSharedPref().edit().putString(getString(R.string.userphonenumber), "").apply();
                                }

                                //*********************** User Birthday *******************//
                                if (!jsonData.getString("birthday").isEmpty()) {
                                    getSharedPref().edit().putString(getString(R.string.userbirthday), jsonData.getString("birthday")).apply();
                                } else {
                                    getSharedPref().edit().putString(getString(R.string.userbirthday), "").apply();
                                }

                                //*********************** User Point ********************//
                                if(getSharedPref().getInt(getString(R.string.userpoint), 0) == 0){
                                    Log.i("point tes1", "0 pts");
                                    tvPoint.setText(getString(R.string.txt_empty_point));
                                } else {
                                    Log.i("points tes1", String.valueOf(getSharedPref().getInt(getString(R.string.userpoint), 0)));
                                    tvPoint.setText(String.valueOf(getSharedPref().getInt(getString(R.string.userpoint), 0)));
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public void request_getExpireCourse (final String token){
        StringRequest request = new StringRequest(Request.Method.GET, URL_COURSE_EXP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if(status.equals("true")) {
                                JSONObject jsonData = jsonResponse.getJSONObject("data");
                                getSharedPref().edit().putString(getString(R.string.isPremium), jsonData.getString("is_premium")).apply();
                                getSharedPref().edit().putString(getString(R.string.expire_course_upgrade), jsonData.getString("expired")).apply();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_camera:
            case R.id.iv_profile_image:
                SendEventGoogleAnalytics("CHANGE AVATAR", "BTN CHANGE AVATAR", "BTN CHANGE AVATAR CLICKED");
                if(global.getAPIversion() >= Build.VERSION_CODES.M) {
                    checkPermissionStorage();
                } else {
                    startChooserIntent();
                }
                break;

            case R.id.iv_edit_profile:
                SendEventGoogleAnalytics("EDIT PROFILE", "BTN EDIT PROFILE", "BTN EDIT PROFILE CLICKED");
                Intent in = new Intent(getApplicationContext(), EditProfil.class);
                startActivity(in);
                finish();
                break;

            case R.id.vg_current_course:
                Log.i("changeCourse tes1", "clicked");
                break;

            case R.id.vg_certificate:
                Log.i("certificate tes1", "clicked");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ActivityRequestCode.REQUEST_CAMERA_OR_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("isOK tes1", "OK");
                    if (data == null) { // from Camera, stored in File
                        File imageFile = GlobalVar.getAvatarFile(getApplicationContext());
                        if (imageFile.exists()) {
                            try {
//                                showBar(BahasoURL.ACTION_UPLOADAVATAR, true);
                                // test add uri
                                Uri uri = Uri.fromFile(imageFile);
                                // below is not needed, change to uri
                                //String filePath = imageFile.getPath();
                                //Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                mEditedBitmap = BitmapHelper.editBitmapToThumbnail(getApplicationContext(), uri);
                                File editedImageFile = GlobalVar.bitmapToAvatarFile(getApplicationContext(),mEditedBitmap);
//                                RetrieveHelper.retrieveByPriorityNew(BahasoURL.ACTION_UPLOADAVATAR,
//                                        this,null, new BundleParams.ActionUploadAvatar().withParams(
//                                                mUser.getAccessToken(),editedImageFile
//                                        ));
                                onUpdateAvatar(editedImageFile);
                            }
                            catch (Exception e) {
                                Crashlytics.logException(e);
//                                showBar(BahasoURL.ACTION_UPLOADAVATAR, false);
                            }
                        }
//                        else {
                            //showBar(BahasoURL.ACTION_UPLOADAVATAR, false);
//                        }
                        return;
                    } else {
                        Uri uri = data.getData();
                        Log.i("pickGallery tes1", String.valueOf(uri));
                        if (uri == null) {
                            try {
//                            showBar(BahasoURL.ACTION_UPLOADAVATAR, true);
                                // thumbnail only
                                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                                mEditedBitmap = BitmapHelper.editBitmapToThumbnail(getApplicationContext(), thumbnail);

                                File bitmapFile = GlobalVar.bitmapToAvatarFile(getApplicationContext(), mEditedBitmap);

//                            RetrieveHelper.retrieveByPriorityNew(BahasoURL.ACTION_UPLOADAVATAR,
//                                    this,null, new BundleParams.ActionUploadAvatar().withParams(
//                                            mUser.getAccessToken(),bitmapFile
//                                    ));
                                onUpdateAvatar(bitmapFile);
                            } catch (Exception e) {
                                Crashlytics.logException(e);
//                            showBar(BahasoURL.ACTION_UPLOADAVATAR, false);
                            }
                        } else { //uri not null
                            Log.i("uriNotNULL", "NOT NULL");
                            try {
//                            showBar(BahasoURL.ACTION_UPLOADAVATAR, true);
                                mEditedBitmap = BitmapHelper.editBitmapToThumbnail(getApplicationContext(), uri);
                                File bitmapFile = GlobalVar.bitmapToAvatarFile(getApplicationContext(), mEditedBitmap);
                                Log.i("bitmapFile tes1", String.valueOf(bitmapFile));
//                            RetrieveHelper.retrieveByPriorityNew(BahasoURL.ACTION_UPLOADAVATAR,
//                                    this,null, new BundleParams.ActionUploadAvatar().withParams(
//                                            mUser.getAccessToken(),bitmapFile
//                                    ));
                                onUpdateAvatar(bitmapFile);
                            } catch (Exception e) {
                                Crashlytics.logException(e);
//                            showBar(BahasoURL.ACTION_UPLOADAVATAR, false);
                            }
                        }
                    }
                }
                else {
                    // mUser cancel the intent
                    // no op
                    Log.i("usercancel tes1", "CANCELED");
                }
                break;
        }
    }

    private Object onUpdateAvatar(File img) {
        ResponseBody body = null;

            final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

            RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)/*.addFormDataPart("userid", "8457851245")*/
                    .addFormDataPart("avatar_img", "avatar_img",
                            RequestBody.create(MEDIA_TYPE_JPEG, img)).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .header("Authorization", "Bearer " + getSharedPref().getString(getString(R.string.tokenlogin),""))
                    .url(URL_PROFIL_AVATAR)
                    .post(req)
                    .build();

        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();
            body = response.body();
            if (response.isSuccessful()) {
                Intent in = new Intent(this, ProfilActivity.class);
                startActivity(in);
                finish();
                return body.string();
            } else {
                Log.i("responseFail tes1", "FAILED");
            }
            Log.d("response tes1", "uploadImage:"+response.body().string());

        }  catch (Exception e) {
            Log.e("Other Error: ", e.getLocalizedMessage());
        } finally {
            if (null != body) {
                body.close();
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(BuildConfig.DEBUG) {
                        Log.i("STOREShow1 tes1 2", "TRUE");
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enable_storage_permission),
                            Toast.LENGTH_LONG).show();
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.i("STOREShow1 tes1 2", "FALSE");
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            checkPermissionCamera();
                        }
                    }, 250);
                }
                break;

            case 2:
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    if(BuildConfig.DEBUG) {
                        Log.i("CAMShow1 tes1 2", "TRUE");
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enable_camera_permission),
                            Toast.LENGTH_LONG).show();
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.i("CAMShow1 tes1 2", "FALSE");
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            startChooserIntent();
                        }
                    }, 250);
                }
                break;
        }
    }


}
