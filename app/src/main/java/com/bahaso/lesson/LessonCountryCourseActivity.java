package com.bahaso.lesson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.bahaso.R;
import com.bahaso.adapter.LessonCountryAdapter;
import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.LessonCountry;
import com.bahaso.util.LessonCountryHelper;

import java.util.List;

public class LessonCountryCourseActivity extends AppCompatActivity {
    private static SharedPreferences sharedpref;
    private ProgressDialog pDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_country_course);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ListView listViewLesson = (ListView) findViewById(R.id.listViewLesson);

        String token = getSharedPref().getString(getString(R.string.tokenlogin),"");
        Log.i("tokenInfo", token+"");
//        String token = "q33ie6R0xpMQBvbiOrDcp40G1bxgOG7blPDhIjHM";


        Button btnPlacement = (Button)findViewById(R.id.btnPlacement);
        List<LessonCountry> lessonCountryList = null;

        btnPlacement.setEnabled(false);
        LessonCountryAdapter lessonCountryAdapter = new LessonCountryAdapter(getApplicationContext(),0,lessonCountryList,token,btnPlacement);


        listViewLesson.setAdapter(lessonCountryAdapter);

        lessonCountryList = LessonCountryHelper.getLessonCountryList(getApplicationContext(),token,lessonCountryAdapter);



        btnPlacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placementId = view.getTag().toString();
                if(placementId.isEmpty())
                    placementId = "";
                Log.i("clickPlacement","click placement "+placementId);
                Intent intentPlacementCase = new Intent(getApplicationContext(),ActivityCasePlacement.class);
                intentPlacementCase.putExtra("placementId", placementId);
                startActivity(intentPlacementCase);

            }

        });

    }

    private static SharedPreferences getSharedPref(){
        if(sharedpref==null){
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }
}
