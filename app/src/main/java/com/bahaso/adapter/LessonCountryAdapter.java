package com.bahaso.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.util.LessonCountryHelper;

import java.util.List;

/**
 * Created by shiperus on 3/6/2017.
 */

public class LessonCountryAdapter extends ArrayAdapter{

    private Context context;
    LayoutInflater inflater;
    private String token;
    int selectedIndex = -1;
    Button btnPlacement ;
    boolean flagIsSameIndex = true;
    boolean isTypeChoosen = false;
    int choosenTypeIndex = -1;

    public LessonCountryAdapter(Context context, int resource, List objects, String token, Button btnPlacement) {
        super(context, resource, objects);
        this.token = token;
        this.context = context;
        this.btnPlacement = btnPlacement;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return LessonCountryHelper.getLessonCountryList(context,token,this).size();

    }

    @Override
    public Object getItem(int i) {
        return LessonCountryHelper.getLessonCountryList(context,token,this).get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {

        Log.i("LCAdapter","Jalan");

        View cellView = view;

        cellView = inflater.inflate(R.layout.layout_lesson_item, null);

        TextView txtLessonCountryName  = (TextView) cellView.findViewById(R.id.txtLessonItem);
//
        txtLessonCountryName.setText(LessonCountryHelper.getLessonCountryList(context,token,this).get(pos).getLessonCountry());

        LinearLayout lyClickable = (LinearLayout) cellView.findViewById(R.id.linearLayoutClick);
        LinearLayout lyCourseCountry = (LinearLayout) cellView.findViewById(R.id.layoutCourseCountry);


        RadioButton rbCourseCountrySelect = (RadioButton) cellView.findViewById(R.id.radioButtonCourseCountrySelect);

        rbCourseCountrySelect.setChecked(pos==selectedIndex);
        if(pos==selectedIndex) {
            lyCourseCountry.setVisibility(View.VISIBLE);
            btnPlacement.setTag(LessonCountryHelper.getLessonCountryList(context,token,this)
                    .get(selectedIndex).getPlacementTestId());
            btnPlacement.setEnabled(true);

            GradientDrawable backgroundPlacementButton = (GradientDrawable) btnPlacement.getBackground();

//            if(pos%2==0) {
//                if(isTypeChoosen)   {
//                    backgroundPlacementButton.setColor(context.getResources().getColor(R.color.bahaso_blue));
//                    btnPlacement.setTextColor(context.getResources().getColor(R.color.bahaso_black));
//                }
//                else
//                {
//                    backgroundPlacementButton.setColor(context.getResources().getColor(R.color.white));
//                    btnPlacement.setTextColor(context.getResources().getColor(R.color.bahaso_black_gray));
//                }
//
//            }
//            else {
                backgroundPlacementButton.setColor(context.getResources().getColor(R.color.bahaso_blue));
                btnPlacement.setTextColor(context.getResources().getColor(R.color.white));
//            }
        }else
            lyCourseCountry.setVisibility(View.GONE);

        if(selectedIndex==-1) {
            GradientDrawable backgroundPlacementButton = (GradientDrawable) btnPlacement.getBackground();
            backgroundPlacementButton.setColor(context.getResources().getColor(R.color.white));
            btnPlacement.setTextColor(context.getResources().getColor(R.color.bahaso_black_gray));
        }
        rbCourseCountrySelect.setTag(pos);

        lyClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedIndex==pos)
                    flagIsSameIndex = true;
                else {
                    choosenTypeIndex=-1;
                    flagIsSameIndex = false;
                    isTypeChoosen = false;
                }
                Log.i("FlagSameIndex",flagIsSameIndex+"");

                selectedIndex = pos;

                notifyDataSetChanged();
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout linearLayout = (LinearLayout) cellView.findViewById(R.id.layoutCourseCountry);
        Log.i("TESS",flagIsSameIndex+"");
//        if(pos%2==0) {
//
//
//            if(flagIsSameIndex)
//            {
//                Log.i("Same index","Same index");
//                View v ;
//                for(int idxType = 0;idxType<2;idxType++)
//                {
//                    Log.i("idx",choosenTypeIndex+"");
//                    v = layoutInflater.inflate (R.layout.layout_course_country_typ_button, null);
//
//                    if(idxType==choosenTypeIndex)
//                    {
//
//                        v.setBackgroundColor(Color.BLUE);
//                    }
//
//                    linearLayout.addView(v);
//
//                }
//
//            }
//            else {
//
//                if(isTypeChoosen)
//                {
//                    View v ;
//                    for(int idxType = 0;idxType<2;idxType++)
//                    {
//                        Log.i("idx",choosenTypeIndex+"");
//                        v = layoutInflater.inflate (R.layout.layout_course_country_typ_button, null);
//
//                        if(idxType==choosenTypeIndex)
//                        {
//
//                            v.setBackgroundColor(Color.BLUE);
//                        }
//
//                        linearLayout.addView(v);
//
//                    }
//                }
//                else {
//                    View v = layoutInflater.inflate(R.layout.layout_course_country_typ_button, null);
//                    linearLayout.addView(v);
//                    v = layoutInflater.inflate(R.layout.layout_course_country_typ_button, null);
//                    linearLayout.addView(v);
//                }
//            }
//            for(int i=0;i<linearLayout.getChildCount();i++){
//                linearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        for(int a=0;a<linearLayout.getChildCount();a++) {
//                            linearLayout.getChildAt(a).setBackgroundColor(Color.WHITE);
//                        }
//                        Log.i("Type Course","Type");
//                        int indexTypeBtn = Integer.parseInt(view.getTag().toString());
//                        linearLayout.getChildAt(indexTypeBtn).setBackgroundColor(Color.BLUE);
//                        isTypeChoosen = true;
//                        choosenTypeIndex = indexTypeBtn;
//                        notifyDataSetChanged();
//                    }
//                });
//                linearLayout.getChildAt(i).setTag(i+"");
//
//            }
//        }
//        else
//        {
//            linearLayout.removeAllViews();
//        }
        return cellView;

    }
}


























