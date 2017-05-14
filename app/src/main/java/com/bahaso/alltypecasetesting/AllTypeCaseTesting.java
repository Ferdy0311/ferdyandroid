package com.bahaso.alltypecasetesting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bahaso.R;
import com.bahaso.alltypecasetesting.type.IntroAndGeneralCaseActivity;
import com.bahaso.alltypecasetesting.type.WritingCaseActivity;

public class AllTypeCaseTesting extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_type_case_testing);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final RadioButton radioButtonGeneraleCase = (RadioButton) findViewById(R.id.radioButtonIntroAndGeneralCase);
        final RadioButton radioButtonWritingCase = (RadioButton) findViewById(R.id.radioButtonWritingCase);

        final Button nextButton = (Button) findViewById(R.id.buttonLoadTestingCase);
        radioButtonGeneraleCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nextButton.setBackgroundResource(R.drawable.background_btn_case_check);
                    nextButton.setTextColor(getResources().getColor(R.color.bahaso_white_gray));
                    nextButton.setEnabled(true);
                }
            }
        });
        radioButtonWritingCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nextButton.setBackgroundResource(R.drawable.background_btn_case_check);
                    nextButton.setTextColor(getResources().getColor(R.color.bahaso_white_gray));
                    nextButton.setEnabled(true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup) findViewById(R.id.groupCaseTypeRadioButton);
                if(rg.getCheckedRadioButtonId()==R.id.radioButtonIntroAndGeneralCase)
                {
                    Intent allTypeCaseTesting = new Intent(getApplicationContext(),IntroAndGeneralCaseActivity.class);
                    allTypeCaseTesting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(allTypeCaseTesting);
                }else if(rg.getCheckedRadioButtonId()==R.id.radioButtonWritingCase){
                    Intent allTypeCaseTesting = new Intent(getApplicationContext(),WritingCaseActivity.class);
                    allTypeCaseTesting.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(allTypeCaseTesting);
                }
            }
        });


    }
}
