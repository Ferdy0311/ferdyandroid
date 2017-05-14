package com.bahaso.typecase;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.adapter.LostWordTypeAdapter;
import com.bahaso.fragmenthelper.FragmentBahaso;
import com.bahaso.lesson.ActivityCasePlacement;
import com.bahaso.model.LittleBoxSelection;
import com.bahaso.model.LostWordType;
import com.google.android.flexbox.FlexboxLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostWordTypeFragment extends FragmentBahaso {

    String type;
    String instruction;
    List<LostWordType> listLostWordType;
    boolean isChecked = false;
    ScrollView scrollView;
    View viewFragment;
    View viewRowCase;
    ViewFragmentHolder holder;
    ArrayList<ArrayList<View>> listEditTextContainer;
    ArrayList<View> listEditText;
    int layoutChildCount;
    boolean isCaseCorrect = true;
    String caseID;

    public LostWordTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean getIsCaseCorrect() {
        return isCaseCorrect;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public boolean getIsFullAnswered() {
        return super.getIsFullAnswered();
    }

    @Override
    public void checkFullAnswered() {
        isFullAnswered = true;
        for (LostWordType lostWordTypeRow : listLostWordType) {

            for (String userAnswer : lostWordTypeRow.getArrayListUserAnswer()) {
                if (userAnswer.equals("")) {
                    isFullAnswered = false;
                    return;
                }
            }

        }
    }


    private static class ViewFragmentHolder {
        LinearLayout layoutLostWordContainer;
        TextView textViewInstruction;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            listLostWordType = getArguments().getParcelableArrayList("listData");
            instruction = getArguments().getString("instruction");
            caseID = getArguments().getString("caseID");

            Log.i("type", getArguments().getString("type"));
            setType(getArguments().getString("type"));
        }

        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_lost_word_type, container, false);
            holder = new ViewFragmentHolder();
            holder.layoutLostWordContainer = (LinearLayout) viewFragment.findViewById(R.id.layoutLostWordTypeContainer);
            holder.textViewInstruction = (TextView) viewFragment.findViewById(R.id.textViewLostWordTypeInstruction);
            viewFragment.setTag(holder);
        } else {
            holder = (ViewFragmentHolder) viewFragment.getTag();
        }

        layoutChildCount = holder.layoutLostWordContainer.getChildCount();
        if (layoutChildCount > 1)
            holder.layoutLostWordContainer.removeViews(1, layoutChildCount - 1);
        holder.textViewInstruction.setText(instruction);

        generateCaseContent(inflater);


        Log.i("listEditTextContainer", listEditTextContainer.size() + "");

        if (isChecked)
            checkAnswer();

        return viewFragment;
    }

    public void generateCaseContent(LayoutInflater inflater) {
        listEditTextContainer = new ArrayList<>();
        for (int idx = 0; idx < listLostWordType.size(); idx++) {

            holder.layoutLostWordContainer.addView(generateRowCase(idx, inflater));
            Log.i("listEditText", listEditText.size() + "");

            listEditTextContainer.add(listEditText);

        }
    }

    public View generateRowCase(int row, LayoutInflater inflater) {
        String question = "";
        int editTextPosition = 0;
//        if(holderContent==null) {
        viewRowCase = inflater.inflate(R.layout.layout_case_lost_word_type_item, null);
        FlexboxLayout layoutCaseLostWordTypeItem = (FlexboxLayout) viewRowCase.findViewById(R.id.layoutCaseLostWordTypeItem);
//        }
        layoutCaseLostWordTypeItem.removeAllViews();

        question = listLostWordType.get(row).getQuestion();
//        question = "asdasd [BLANK/]";
        question = question.replace("[BLANK/]", " # ");
        Log.i("idxPerQuestion", question + "");
        String[] splittedQuestion = question.split(" ");

        listEditText = new ArrayList<>();
        for (int idxQuestionSplitted = 0; idxQuestionSplitted < splittedQuestion.length; idxQuestionSplitted++) {

            if (splittedQuestion[idxQuestionSplitted].trim().equals("#")) {
                addEditText(row, editTextPosition, inflater, layoutCaseLostWordTypeItem);
                editTextPosition++;
            } else {
                addTextView(viewRowCase, " " + splittedQuestion[idxQuestionSplitted].trim() + " ", layoutCaseLostWordTypeItem);
            }
        }


        return viewRowCase;
    }

    public void addTextView(View cellView, String text, FlexboxLayout layoutCaseLostWordTypeItem) {
        TextView txtViewTextQuestion = new TextView(cellView.getContext());
        txtViewTextQuestion.setText(text + "");
        txtViewTextQuestion.setGravity(Gravity.CENTER_VERTICAL);
        txtViewTextQuestion.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
        layoutCaseLostWordTypeItem.addView(txtViewTextQuestion);
    }

    public void addEditText(final int pos, final int editTextPosition, LayoutInflater inflater, FlexboxLayout layoutCaseLostWordTypeItem) {
        View editTextLayout = inflater.inflate(R.layout.layout_case_lost_word_type_item_edit_text, null);
        EditText editTextLostWordType = (EditText) editTextLayout.findViewById(R.id.editTextLostWordType);

        editTextLostWordType.setText(listLostWordType.get(pos).getArrayListUserAnswer().get(editTextPosition));

        editTextLostWordType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listLostWordType.get(pos).getArrayListUserAnswer().set(editTextPosition, s + "");
                checkFullAnswered();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listEditText.add(editTextLayout);
        layoutCaseLostWordTypeItem.addView(editTextLayout);
    }

    public void checkAnswer() {

        for (int idxCase = 0; idxCase < listLostWordType.size(); idxCase++) {
            for (int idxCaseAnswer = 0; idxCaseAnswer < listLostWordType.get(idxCase).getArrayListAnswer().size(); idxCaseAnswer++) {
                String caseAnswer = listLostWordType.get(idxCase).getArrayListAnswer().get(idxCaseAnswer).replace("|","#");
//                Log.i("caseAnswer",caseAnswer+"");
//                Log.i("caseAnswerSplit",caseAnswer.split("|").length+"");

                String caseAnswerSplitted[] = caseAnswer.split("#");

                Log.i("caseAnswerSplitted",caseAnswerSplitted.length+"");

                View layoutEditTextUserAnswer = listEditTextContainer.get(idxCase).get(idxCaseAnswer);
                EditText editTextUserAnswer = (EditText) layoutEditTextUserAnswer.findViewById(R.id.editTextLostWordType);
//                editTextUserAnswer.setActivated(false);
                editTextUserAnswer.setCursorVisible(false);
                editTextUserAnswer.setFocusable(false);
//                editTextUserAnswer.setClickable(false);
//                editTextUserAnswer.setEnabled(false);
                Drawable dr = editTextUserAnswer.getBackground();
                Log.i("edTAns", editTextUserAnswer.getText().toString());
                if (checkAnswerAfterSplitted(caseAnswerSplitted,editTextUserAnswer.getText().toString())) {
                    Log.i("chkTesTrue", "in");
                    dr.setColorFilter(getContext().getResources().getColor(R.color.bahaso_blue), PorterDuff.Mode.SRC_ATOP);
                    editTextUserAnswer.setBackground(dr);
                } else {
                    Log.i("chkTesFalse", "in");
                    isCaseCorrect = false;
                    editTextUserAnswer.setHighlightColor(Color.RED);
                    dr.setColorFilter(getContext().getResources().getColor(R.color.bahaso_red_false_answer_edit_text), PorterDuff.Mode.SRC_ATOP);
                    editTextUserAnswer.setBackground(dr);
                }


            }
        }

    }

    private boolean checkAnswerAfterSplitted(String [] caseAnswerSplitted , String userAnswer)
    {
//        for()
        for(String answerAfterSplitted : caseAnswerSplitted){
            Log.i("answerAfterSplitted",answerAfterSplitted);
            if(answerAfterSplitted.equals(userAnswer))
                return true;
        }

        return false;
    }


}
