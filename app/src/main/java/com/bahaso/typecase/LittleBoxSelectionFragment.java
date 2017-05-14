package com.bahaso.typecase;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.adapter.LittleBoxSelectionAdapter;
import com.bahaso.fragmenthelper.FragmentBahaso;
import com.bahaso.lesson.ActivityCasePlacement;
import com.bahaso.model.LittleBoxSelection;
import com.google.android.flexbox.FlexboxLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LittleBoxSelectionFragment extends FragmentBahaso {


    String type;
    String instruction;

    ArrayList<ArrayList<ArrayList<View>>> listButtonMainContainer;
    ArrayList<ArrayList<View>> listButtonChoicesContainer;
    ArrayList<View> listButtonChoices;

    View viewFragment;
    View viewRowCase;

    int layoutChildCount;

    ViewFragmentHolder holder;
    String caseID;
    List<com.bahaso.model.LittleBoxSelection> listLittleBoxSelection ;
    boolean isChecked = false;
    boolean isCaseCorrect = true;

    public LittleBoxSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean getIsCaseCorrect() {
        return isCaseCorrect;
    }
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean getIsFullAnswered() {
        return super.getIsFullAnswered();
    }

    @Override
    public void checkFullAnswered()
    {
        isFullAnswered = true;
        for(LittleBoxSelection littleBoxRow : listLittleBoxSelection){

            for(String userAnswer : littleBoxRow.getUserAnswer())
            {
                    if (userAnswer.equals("")) {
                        isFullAnswered = false;
                        return;
                    }
            }

        }
    }

    @Override
    public void checkAnswer()
    {

        for(int idxRowCase =0; idxRowCase<listLittleBoxSelection.size();idxRowCase++)
        {
            for(int idxPositionChoices = 0; idxPositionChoices< listLittleBoxSelection.get(idxRowCase).getAnswer().size(); idxPositionChoices++){
                String answer = listLittleBoxSelection.get(idxRowCase).getAnswer().get(idxPositionChoices);
                String userAnswer = listLittleBoxSelection.get(idxRowCase).getUserAnswer().get(idxPositionChoices);
                Log.i("userAnswer",userAnswer);
                if(answer.equals(userAnswer))
                    refreshChoicesCorrectAnswer(idxRowCase,idxPositionChoices,userAnswer);
                else
                    refreshChoicesWrongAnswer(idxRowCase,idxPositionChoices,userAnswer);
            }

        }

    }

    @Override
    public String getType() {

        return type;
    }
    public boolean getIsChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    private static class ViewFragmentHolder{
        LinearLayout layoutLittleBoxSelectionContainer;
        TextView textViewLittleBoxSelectionInstruction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(getArguments()!=null) {
            listLittleBoxSelection = getArguments().getParcelableArrayList("listData");
            instruction = getArguments().getString("instruction");
            caseID= getArguments().getString("caseID");


            setType(getArguments().getString("type"));
        }
        
        if(viewFragment==null) {
            viewFragment = inflater.inflate(R.layout.fragment_little_box_selection, container, false);
            FrameLayout frameLayout = (FrameLayout) viewFragment;
            holder = new ViewFragmentHolder();
            holder.layoutLittleBoxSelectionContainer =(LinearLayout) viewFragment.findViewById(R.id.layoutLittleBoxSelectionContainer);
            holder.textViewLittleBoxSelectionInstruction = (TextView) viewFragment.findViewById(R.id.textViewLittleBoxSelectionInstruction);
            viewFragment.setTag(holder);
        }
        else{
            holder = (ViewFragmentHolder) viewFragment.getTag();
        }

        layoutChildCount = holder.layoutLittleBoxSelectionContainer.getChildCount();
        if(layoutChildCount >1)
            holder.layoutLittleBoxSelectionContainer.removeViews(1,layoutChildCount-1);
        holder.textViewLittleBoxSelectionInstruction.setText(instruction);


        generateCaseContent(inflater);
        if(isChecked)
            checkAnswer();
        return viewFragment;
    }

    public void generateCaseContent(LayoutInflater inflater){
        listButtonMainContainer = new ArrayList<>();
        for(int idx =0; idx<listLittleBoxSelection.size();idx++)
        {

            holder.layoutLittleBoxSelectionContainer.addView(generateRowCase(idx,inflater));
//            Log.i("listEditText",listEditText.size()+"");

            listButtonMainContainer.add(listButtonChoicesContainer);

        }
    }

    public View generateRowCase(int row,LayoutInflater inflater){
        String question="";
        int choicesPosition = 0;
        viewRowCase = inflater.inflate(R.layout.layout_case_little_box_selection_item, null);
        FlexboxLayout layoutCaseLittleBoxSelectionItem = (FlexboxLayout) viewRowCase.findViewById(R.id.layoutCaseLittleBoxSelectionItem);
        layoutCaseLittleBoxSelectionItem.removeAllViews();

        question = listLittleBoxSelection.get(row).getQuestion();
//        question = "asdasd [BLANK/]";
        question= question.replace("[BLANK/]"," # ");
        Log.i("idxPerQuestion", question +"");
        String [] splittedQuestion = question.split(" ");

        listButtonChoicesContainer = new ArrayList<>();
        for(int idxQuestionSplitted =0 ; idxQuestionSplitted<splittedQuestion.length;idxQuestionSplitted++){

            if(splittedQuestion[idxQuestionSplitted].trim().equals("#"))
            {
                addButton(row,choicesPosition,inflater,layoutCaseLittleBoxSelectionItem);
                choicesPosition++;
            }
            else{
                addTextView(viewRowCase," "+splittedQuestion[idxQuestionSplitted].trim()+" ",layoutCaseLittleBoxSelectionItem);
            }
        }



        return  viewRowCase;
    }
    public void addTextView(View cellView,String text,FlexboxLayout layoutToBeAdded)
    {
        TextView txtViewTextQuestion = new TextView(cellView.getContext());
        txtViewTextQuestion.setText(text+"");
        txtViewTextQuestion.setGravity(Gravity.CENTER_VERTICAL);
        txtViewTextQuestion.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
        layoutToBeAdded.addView(txtViewTextQuestion);
    }

    public void addButton(final int pos ,final int choicesPosition , LayoutInflater inflater,FlexboxLayout layoutCaseLostWordTypeItem){

        listButtonChoices = new ArrayList<>();
        for(int idxItem=0;idxItem<listLittleBoxSelection.get(pos).getArrayListChoicesList().get(choicesPosition).size();idxItem++){
            View buttonChoiceLayout = inflater.inflate(R.layout.layout_case_little_box_selection_item_button,null);
            Button btnLittleBoxSelection = (Button) buttonChoiceLayout.findViewById(R.id.btnSelectionLittleBox);

            String choiceContent = listLittleBoxSelection.get(pos).getArrayListChoicesList().get(choicesPosition).get(idxItem);
            String userAnswer = listLittleBoxSelection.get(pos).getUserAnswer().get(choicesPosition);
            String answer = listLittleBoxSelection.get(pos).getAnswer().get(choicesPosition);

            btnLittleBoxSelection.setText(choiceContent);
            btnLittleBoxSelection.setTag(idxItem);
//            idxItemStatic = idxItem;
            if(userAnswer.equals("")){
                btnLittleBoxSelection.setTextColor(getContext().getResources().getColor(R.color.bahaso_black_gray));
                btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);
            }
            else
            {
                if(userAnswer.equals(idxItem+"")) {
                    btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected);
                    btnLittleBoxSelection.setTextColor(getContext().getResources().getColor(R.color.white));
                }
                else
                {
                    btnLittleBoxSelection.setTextColor(getContext().getResources().getColor(R.color.bahaso_black_gray));
                    btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);
                }
            }

            btnLittleBoxSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tagIdx = (int) view.getTag();
                    Button clickedButton = (Button) view;
                    Log.i("btnTagClicked",tagIdx+"");
                    resetChoicesButton(pos,choicesPosition);
                    listLittleBoxSelection.get(pos).getUserAnswer().set(choicesPosition,tagIdx+"");
                    clickedButton.setBackgroundResource(R.drawable.background_box_selection_selected);
                    clickedButton.setTextColor(getContext().getResources().getColor(R.color.white));
                    checkFullAnswered();
                }
            });
            listButtonChoices.add(buttonChoiceLayout);
            layoutCaseLostWordTypeItem.addView(buttonChoiceLayout);
        }
        listButtonChoicesContainer.add(listButtonChoices);


    }

    public void resetChoicesButton(int pos, int choicesPosition){
        for(int idxChoiceButton = 0 ; idxChoiceButton<listButtonMainContainer.get(pos).get(choicesPosition).size();idxChoiceButton++)
        {
            View btnChoiceslayout = listButtonMainContainer.get(pos).get(choicesPosition).get(idxChoiceButton);
            Button btnChoices = (Button) btnChoiceslayout.findViewById(R.id.btnSelectionLittleBox);
            btnChoices.setTextColor(getContext().getResources().getColor(R.color.bahaso_black_gray));
            btnChoices.setBackgroundResource(R.drawable.background_box_selection_unselected);

        }
    }

    public void refreshChoicesCorrectAnswer(int pos,int choicesPosition,String userAnswer)
    {
        for(int idxChoiceButton = 0 ; idxChoiceButton<listButtonMainContainer.get(pos).get(choicesPosition).size();idxChoiceButton++)
        {
            View btnChoiceslayout = listButtonMainContainer.get(pos).get(choicesPosition).get(idxChoiceButton);
            Button btnChoices = (Button) btnChoiceslayout.findViewById(R.id.btnSelectionLittleBox);

            if(btnChoices.getTag().toString().equals(userAnswer)) {
                btnChoices.setBackgroundResource(R.drawable.background_box_selection_selected);
                btnChoices.setTextColor(getContext().getResources().getColor(R.color.bahaso_white_gray));

            }else {
                btnChoices.setBackgroundResource(R.drawable.background_box_selection_unselected);
                btnChoices.setTextColor(getContext().getResources().getColor(R.color.bahaso_black));

            }

        }
    }
    public void refreshChoicesWrongAnswer(int pos,int choicesPosition,String userAnswer)
    {
        isCaseCorrect = false;
        for(int idxChoiceButton = 0 ; idxChoiceButton<listButtonMainContainer.get(pos).get(choicesPosition).size();idxChoiceButton++)
        {
            View btnChoiceslayout = listButtonMainContainer.get(pos).get(choicesPosition).get(idxChoiceButton);
            Button btnChoices = (Button) btnChoiceslayout.findViewById(R.id.btnSelectionLittleBox);

            Log.i("tagButton" ,btnChoices.getTag()+"");
            Log.i("tagButtonUserAnswer" ,userAnswer+"");

            if(btnChoices.getTag().toString().equals(userAnswer)) {
                btnChoices.setBackgroundResource(R.drawable.background_box_selection_selected_false);
                btnChoices.setTextColor(getContext().getResources().getColor(R.color.bahaso_white_gray));

            }else {
                btnChoices.setBackgroundResource(R.drawable.background_box_selection_unselected);
                btnChoices.setTextColor(getContext().getResources().getColor(R.color.bahaso_black));

            }


        }
    }



}
