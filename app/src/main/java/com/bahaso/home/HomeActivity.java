package com.bahaso.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bahaso.R;
import com.bahaso.alltypecasetesting.AllTypeCaseTesting;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.globalvar.GoogleAnalyticsHelper;
import com.bahaso.lesson.LessonCountryCourseActivity;


public class HomeActivity extends Fragment implements View.OnClickListener{

    private SharedPreferences sharedpref;
    private static final String SCREEN_NAME = "Home";
    private GoogleAnalyticsHelper GoogleHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);
        View vLesson = v.findViewById(R.id.cv_lesson);
        View vCommunity = v.findViewById(R.id.cv_community);
        View vAllTypeCase = v.findViewById(R.id.cv_type_case);

        vLesson.setOnClickListener(this);
        vCommunity.setOnClickListener(this);
        vAllTypeCase.setOnClickListener(this);
        InitGoogleAnalytics();

        sendScreenImageName();

        return v;
    }

    private void sendScreenImageName() {
        GoogleHelper.SendScreenNameGoogleAnalytics(SCREEN_NAME, getContext());
    }

    private void InitGoogleAnalytics() {
        GoogleHelper = new GoogleAnalyticsHelper();
        GoogleHelper.initialize(getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {
        GoogleHelper.SendEventGoogleAnalytics(getContext(),iCategoryId,iActionId,iLabelId );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_lesson:
                SendEventGoogleAnalytics("LESSON", "BTN LESSON", "BTN LESSON CLICKED");
                Intent lesson = new Intent(getContext(),LessonCountryCourseActivity.class);
                lesson.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(lesson);
                break;

            case R.id.cv_community:
//                if (getSharedPref().getInt("flag_com_splash", 0) == 0) {
//                    Intent komunitas = new Intent(getActivity(), ComSplash.class);
//                    komunitas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(komunitas);
//                } else {
//                    Intent komunitas = new Intent(getActivity(), CommunityActivity.class);
//                    komunitas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(komunitas);
//                }
                break;

            case R.id.cv_type_case:
//            SendEventGoogleAnalytics("LESSON", "BTN LESSON", "BTN LESSON CLICKED");
                Intent allTypeCaseTesting = new Intent(getContext(),AllTypeCaseTesting.class);
                allTypeCaseTesting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(allTypeCaseTesting);
            break;
        }
    }

    private SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }

}
