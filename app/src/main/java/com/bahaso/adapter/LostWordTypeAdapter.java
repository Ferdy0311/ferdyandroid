package com.bahaso.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.model.LostWordType;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import static android.R.attr.id;
import static android.R.attr.width;
import static android.support.design.R.attr.height;
import static com.bahaso.R.id.layoutCaseLostWordTypeItem;

/**
 * Created by bahaso on 30/03/2017.
 */

public class LostWordTypeAdapter {

    LayoutInflater inflater;
    private Context context;
    String instruction;
    List<LostWordType> listLostWordType;

    String question="";
    boolean isCheckAnswer = false;
    Context ctx;
    int editTextPosition = 0;
    ViewHolder holder;
    FlexboxLayout layoutCaseLostWordTypeItem;
    public void setIsCheckAnswer(boolean isCheckAnswer){
        this.isCheckAnswer = isCheckAnswer;
    }

    public boolean getIsCheckAnswer()
    {
        return isCheckAnswer;
    }

    public LostWordTypeAdapter(@NonNull Context context, LinearLayout layoutLostWordContainer,
                               @NonNull List objects,String instruction) {
        ctx =context;
        this.listLostWordType = objects;
        this.instruction = instruction;
        this.context = context;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int idx =0; idx<this.listLostWordType.size();idx++)
        {
            addToLayoutContainer(layoutLostWordContainer,getView(idx,null,null));
        }
    }


    public void addToLayoutContainer(LinearLayout layoutLostWordContainer,View view)
    {
        layoutLostWordContainer.addView(view);
    }


    static class ViewHolder{
        TextView txtViewInstruction;
        RelativeLayout layoutCaseLostWordTypeItemTitle;
        FlexboxLayout layoutCaseLostWordTypeItem;
        EditText editTextLostWordType;
        TextView txtViewTextQuestion;
        View viewBtnItem ;
    }

    public View getView(final int pos, View view, ViewGroup viewGroup) {

        if(view==null) {
//            view = inflater.inflate(R.layout.layout_case_lost_word_type_item, null);
//            holder = new ViewHolder();
//            holder.txtViewInstruction = (TextView) view.findViewById(R.id.textViewLostWordTypeInstruction);
//            holder.layoutCaseLostWordTypeItemTitle = (RelativeLayout) view.findViewById
//                    (R.id.layoutCaseLostWordTypeItemTitle);
//            holder.layoutCaseLostWordTypeItem = (FlexboxLayout) view.findViewById(R.id.layoutCaseLostWordTypeItem);
//            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        holder.layoutCaseLostWordTypeItem.removeAllViews();
        Log.i("pos", pos+"");
        question = listLostWordType.get(pos).getQuestion();
//        question = "asdasd [BLANK/]";
        question= question.replace("[BLANK/]"," # ");
        Log.i("idxPerQuestion", question +"");
        String [] splittedQuestion = question.split(" ");
        editTextPosition = 0;

        for(int idxQuestionSplitted =0 ; idxQuestionSplitted<splittedQuestion.length;idxQuestionSplitted++){
            if(splittedQuestion[idxQuestionSplitted].trim().equals("#"))
            {
                loadEditText(pos,editTextPosition,view);
                editTextPosition++;
            }
            else{
                loadTextView(view," "+splittedQuestion[idxQuestionSplitted].trim()+" ");
            }
        }
        if(pos==0){
            holder.layoutCaseLostWordTypeItemTitle.setVisibility(View.VISIBLE);
            holder.txtViewInstruction.setText(instruction);
        }
        else
            holder.layoutCaseLostWordTypeItemTitle.setVisibility(View.GONE);


        return view;

    }
    public void loadTextView(View cellView,String text)
    {
        holder.txtViewTextQuestion = new TextView(cellView.getContext());
        holder.txtViewTextQuestion.setText(text+"");
        holder.txtViewTextQuestion.setGravity(Gravity.CENTER_VERTICAL);
        holder.txtViewTextQuestion.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
        holder.layoutCaseLostWordTypeItem.addView(holder.txtViewTextQuestion);
    }
    public void loadEditText(final int pos, final int editTextPosition, View cellView)
    {
        View editLayout = inflater.inflate(R.layout.layout_case_lost_word_type_item_edit_text,null);
//        Button btn = new Button(context);
        EditText edt = (EditText) editLayout.findViewById(R.id.editTextLostWordType);
//        holder.editTextLostWordType = (EditText) holder.viewBtnItem.findViewById(R.id.editTextLostWordType);

        edt.setText(listLostWordType.get(pos).getArrayListUserAnswer().get(editTextPosition));
//        Log.i("typeTest", holder.editTextLostWordType.getText().toString());
//        if(listLostWordType.get(pos).getArrayListEditTextState().get(editTextPosition))
//        {
//            Log.i("aswqeqwe","gdsg");
//
//        }
//
//        holder.editTextLostWordType.setFocusable(true);
//

//        holder.editTextLostWordType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if(hasFocus) {
//                    listLostWordType.get(pos).getArrayListEditTextState().set(editTextPosition, true);
//                }
//            }
//        });

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listLostWordType.get(pos).getArrayListUserAnswer().set(editTextPosition,s+"");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        if(isCheckAnswer)
//        {
//            Log.i("chkTes",listLostWordType.get(pos).getArrayListUserAnswer().get(editTextPosition));
//            Drawable dr  = holder.editTextLostWordType.getBackground();
//            if(listLostWordType.get(pos).getArrayListUserAnswer().get(editTextPosition).equalsIgnoreCase(listLostWordType.get(pos).getArrayListAnswer().get(editTextPosition))) {
//                Log.i("chkTesTrue","in");
//                dr.setColorFilter(context.getResources().getColor(R.color.bahaso_light_blue),PorterDuff.Mode.SRC_ATOP);
//                holder.editTextLostWordType.setBackground(dr);
//            }
//            else
//            {
//                Log.i("chkTesFalse","in");
//                holder.editTextLostWordType.setHighlightColor(Color.RED);
//                dr.setColorFilter(context.getResources().getColor(R.color.bahaso_darker_red),PorterDuff.Mode.SRC_ATOP);
//                holder.editTextLostWordType.setBackground(dr);
//            }
//        }

//        if(listLostWordType.get(pos).get)
//
//            if(listLittleBoxSelection.get(pos).getUserAnswer()!=-1) {
//                if(listLittleBoxSelection.get(pos).getUserAnswer()==i) {
//                    if(listLittleBoxSelection.get(pos).getCheckActive()) {
//                        checkButtonAnswer(pos);
//                    }
//                    else
//                        btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected);
//                    btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.white));
//
//                }
//                else {
//                    btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);
//                    btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
//
//                }
//            }
//            else {
//                btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);
//                btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
//
//            }
////            btnTest.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
////            btnLittleBoxSelection.setPadding(20,0,20,0);
////            btnTest.setText("Button asdasd" + i);
//            btnLittleBoxSelection.setText(listLittleBoxSelection.get(pos).getArrayListChoices().get(i));
//
//            btnLittleBoxSelection.setTag(i);
////            btnLittleBoxSelection.setLayoutParams(layoutParams);
//
//            btnLittleBoxSelection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    listLittleBoxSelection.get(pos).setUserAnswer(Integer.parseInt(v.getTag().toString()));
//                    listLittleBoxSelection.get(pos).setCheckActive(false);
//                    Log.i("idxLittle", pos+"");
//                    notifyDataSetChanged();
//                }
//            });
//            arButton.add(btnLittleBoxSelection);
        holder.layoutCaseLostWordTypeItem.addView(editLayout);
    }
}
