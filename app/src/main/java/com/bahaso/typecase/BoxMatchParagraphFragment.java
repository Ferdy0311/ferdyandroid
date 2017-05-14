package com.bahaso.typecase;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.fragmenthelper.FragmentBahaso;
import com.bahaso.model.BoxMatchParagraph;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoxMatchParagraphFragment extends FragmentBahaso {

    boolean isAdd = false;
    List<BoxMatchParagraph> listBoxMatchParagraph ;
    ArrayList<String> listChoices ;
    String instruction;
    static int posXSPawnAnimaton, posYSpawnAnimation ;
    boolean loadUserAnswer = false;

    String caseID;
    boolean isScrollForPositioning= false;
    ArrayList<View> choicesList;
    ArrayList<View> listAnswerContainer;

    int heightInPx=0;
    String question;
    static boolean isAnimation=true;
    static int totalAnswerOnContainer;
    static int activityTopHeight;
    static int layoutPosForScrolled;


    static RelativeLayout relativeLayoutAddedAnswer;

    View viewFragment;
    ViewFragmentHolder holder;
    static Button buttonChoiceUserChoice;
//    View.OnLayoutChangeListener onLayoutChangeListener;

    ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener;

    ViewTreeObserver.OnScrollChangedListener scrollChangedListener;

    public BoxMatchParagraphFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean getIsFullAnswered() {
        return super.getIsFullAnswered();
    }

    @Override
    public void checkFullAnswered()
    {
        isFullAnswered = true;
        for(BoxMatchParagraph rowData : listBoxMatchParagraph){

            if(rowData.getUserAnswer().equals(""))
            {
                isFullAnswered= false;
                break;
            }
        }

    }

    @Override
    public void checkAnswer() {
        int idxQuestion = 0;
        for(BoxMatchParagraph rowData : listBoxMatchParagraph){
            String userAnswer = rowData.getUserAnswer();
            String bahasoAnswer = rowData.getAnswer();
            if(userAnswer.equals(bahasoAnswer)) {
                FlexboxLayout flexContainerAnswer = (FlexboxLayout) listAnswerContainer.get(idxQuestion).findViewById(R.id.containerAnswer);
                Button btnAnswer = (Button) flexContainerAnswer.getChildAt(0).findViewById(R.id.buttonBoxMatchParagraphAnswerItem);
                int paddingTop = btnAnswer.getPaddingTop();
                int paddingBottom = btnAnswer.getPaddingBottom();
                int paddingLeft = btnAnswer.getPaddingLeft();
                int paddingRight = btnAnswer.getPaddingRight();

                btnAnswer.setBackgroundResource(R.drawable.background_box_correct_answer);
                btnAnswer.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);

            }   else{
                FlexboxLayout flexContainerAnswer = (FlexboxLayout) listAnswerContainer.get(idxQuestion).findViewById(R.id.containerAnswer);
                Button btnAnswer = (Button) flexContainerAnswer.getChildAt(0).findViewById(R.id.buttonBoxMatchParagraphAnswerItem);
                int paddingTop = btnAnswer.getPaddingTop();
                int paddingBottom = btnAnswer.getPaddingBottom();
                int paddingLeft = btnAnswer.getPaddingLeft();
                int paddingRight = btnAnswer.getPaddingRight();

                btnAnswer.setBackgroundResource(R.drawable.background_box_false_answer);
                btnAnswer.setTextColor(getContext().getResources().getColor(R.color.bahaso_white_gray));

                btnAnswer.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);

            }
            idxQuestion++;
        }
    }


    static class ViewFragmentHolder{
        TextView textViewInstruction;
        FlexboxLayout flexBoxBoxMatchParagraphCaseContainer;
        FlexboxLayout flexBoxBoxMatchParagraphCaseChoicesContainer;
        LinearLayout layoutChoiceShowHide;
        ScrollView scrollViewChoice;
        ScrollView scrollViewCase;
        RelativeLayout relativeLayoutCaseContainer;
        RelativeLayout relativeLayoutContainerDragChoices;
        RelativeLayout relativeLayoutCaseSubContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listAnswerContainer = new ArrayList<>();

        if(getArguments()!=null) {
            Log.i("wwzzz","asdasd");
            if(listBoxMatchParagraph==null)
                listBoxMatchParagraph = getArguments().getParcelableArrayList("listData");
            listChoices = getArguments().getStringArrayList("listChoices");
            instruction = getArguments().getString("instruction");
            caseID= getArguments().getString("caseID");
            question= getArguments().getString("question");
            setType(getArguments().getString("type"));
        }

        if(viewFragment==null) {
            viewFragment = inflater.inflate(R.layout.fragment_box_match_paragraph, container, false);
            holder = new ViewFragmentHolder();
            holder.textViewInstruction = (TextView) viewFragment.findViewById(R.id.textViewBoxMatchParagraphInstruction);
            holder.flexBoxBoxMatchParagraphCaseChoicesContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxBoxMatchParagraphCaseChoicesContainer);
            holder.flexBoxBoxMatchParagraphCaseContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxBoxMatchParagraphCaseContainer);
            holder.layoutChoiceShowHide = (LinearLayout) viewFragment.findViewById(R.id.buttonHideDragChoices);
            holder.scrollViewChoice = (ScrollView) viewFragment.findViewById(R.id.scrollViewBoxMatchParagraphCaseDragChoices);

            holder.scrollViewCase= (ScrollView) viewFragment.findViewById(R.id.scrollViewBoxMatchParagraphCaseContainer);

            holder.scrollViewCase.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isScrollForPositioning = false;
                    for(View viewChoice: choicesList){
                        Button buttonChoice = (Button) viewChoice.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);
                        buttonChoice.setEnabled(true);
                    }
                    return false;
                }
            });


            holder.relativeLayoutCaseContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutBoxMatchParagraphCaseContainer);
            holder.relativeLayoutContainerDragChoices = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutBoxMatchParagraphContainerDragChoices);
            holder.relativeLayoutCaseSubContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutCaseSubContainer);

            viewFragment.setTag(holder);
        }
        else{
            holder = (ViewFragmentHolder) viewFragment.getTag();
        }
        holder.flexBoxBoxMatchParagraphCaseChoicesContainer.removeAllViews();
        holder.textViewInstruction.setText(instruction);

        generateCaseContent(inflater);

        holder.layoutChoiceShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heightInPx==0)
                    heightInPx = holder.relativeLayoutCaseSubContainer.getHeight();
                if(holder.scrollViewChoice.getVisibility()==View.VISIBLE){
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE,R.id.relativeLayoutBoxMatchParagraphContainerDragChoices);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW,0);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    showHideChoiceLayout(relativeLayoutCaseParams,relativeLayoutChoicesParams,relativeLayoutSubCaseParams, View.GONE);

                }
                else {
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE,0);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW,R.id.relativeLayoutBoxMatchParagraphCaseContainer);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,heightInPx);
                    showHideChoiceLayout(relativeLayoutCaseParams,relativeLayoutChoicesParams,relativeLayoutSubCaseParams, View.VISIBLE);
                }
            }
        });

        loadUserAnswer();

        return viewFragment;
    }


    public void addDragClickChoicesListener(final Button buttonChoiceItemFront)
    {
        buttonChoiceItemFront.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for(int idxPointer=0; idxPointer<event.getPointerCount();idxPointer++){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_MOVE:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            if(event.getY()>v.getHeight() || event.getY()<0) {
                                ClipData data = ClipData.newPlainText("", "");
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                                v.startDrag(data, shadowBuilder, v, 0);
                                buttonChoiceUserChoice = (Button) v;
//                                imageViewChoiceUserChoice = (ImageView) v;
                            }

                            break;
                    }
                    break;
                }

                return false;

            }
        });
        try {
            buttonChoiceItemFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableAllChoice(false);
//                    if(isScrollForPositioning)
//                        return;
                    Log.i("buttonTAG0",buttonChoiceItemFront.getTag()+"");

                    Log.i("wew214",isScrollForPositioning+"");

                    buttonChoiceUserChoice= buttonChoiceItemFront;
                    Log.i("TESS 22","sad");
                    int minYAddedLayout , maxYAddedlayout;
                    float choosenLayoutYMin,choosenLayoutYMax;
                    Log.i("tessHeight",holder.textViewInstruction.getHeight()+"");

                    relativeLayoutAddedAnswer = getFirstEmptyAnswerContainer();

                    if(relativeLayoutAddedAnswer==null)
                        return;
                    totalAnswerOnContainer = 0;


                    int startAfterTextInstruction = holder.textViewInstruction.getHeight();
                    isAnimation = true;

                    Log.i("relTag22", relativeLayoutAddedAnswer.getTag()+"");

                    if(scrollChangedListener==null)
                    {
                        scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
                                final int maxScrollPos = holder.scrollViewCase.getChildAt(0).getHeight()-holder.scrollViewCase.getHeight();

//                                isScrollForPositioning = true;
                                Log.i("holder",holder.scrollViewCase.getScrollY()+"");
                                Log.i("holderMax",maxScrollPos+"");
                                Log.i("holderScrolled",layoutPosForScrolled+"");



                                if(!isScrollForPositioning)
                                    return;

                                if(holder.scrollViewCase.getScrollY()==layoutPosForScrolled || holder.scrollViewCase.getScrollY()>=maxScrollPos) {
                                    isScrollForPositioning = false;

                                    Log.i("tesEndScroll", "tesEndScroll");

                                    CountDownTimer cd = new CountDownTimer(150,150) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            addAnswer(relativeLayoutAddedAnswer);

                                        }
                                    };
                                    cd.start();
                                    layoutPosForScrolled = -1;
//                                    imageViewChoiceUserChoice=null;
                                }

                            }
                        };

                        holder.scrollViewCase.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);

                    }
                    if(holder.scrollViewCase!=null) {
                        minYAddedLayout = holder.scrollViewCase.getScrollY();
                        maxYAddedlayout = minYAddedLayout+holder.relativeLayoutCaseSubContainer.getHeight();
                        choosenLayoutYMin = relativeLayoutAddedAnswer.getY();
                        choosenLayoutYMax = choosenLayoutYMin+ relativeLayoutAddedAnswer.getHeight();
                        Log.i("minYAddedLayout",minYAddedLayout+"");
                        Log.i("choosenLayoutYMin",choosenLayoutYMin+"");
//                        Log.i("scViewHeight",+"");
                        if (choosenLayoutYMin<minYAddedLayout || choosenLayoutYMin>maxYAddedlayout || choosenLayoutYMax<minYAddedLayout || choosenLayoutYMax>maxYAddedlayout) {
                            layoutPosForScrolled = startAfterTextInstruction==choosenLayoutYMin?0: (int) choosenLayoutYMin;
//                            v.setEnabled(false);
                            isScrollForPositioning = true;
                            Log.i("wew213","asdasd");
                            holder.scrollViewCase.smoothScrollTo(0, startAfterTextInstruction==choosenLayoutYMin?0: (int) choosenLayoutYMin);
                        }
                        else
                        {
                            addAnswer(relativeLayoutAddedAnswer);
                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void enableAllChoice(Boolean isChoicesEnabled)
    {
        for(View viewChoice: choicesList){
            Button buttonChoiceFront = (Button) viewChoice.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);
            buttonChoiceFront.setEnabled(isChoicesEnabled);
        }
    }

    public RelativeLayout getFirstEmptyAnswerContainer(){
        for(View viewChoice: listAnswerContainer){
            FlexboxLayout linearLayoutAnswerContainer = (FlexboxLayout) viewChoice.findViewById(R.id.containerAnswer);
            if(linearLayoutAnswerContainer.getChildCount()==0)
                return (RelativeLayout) viewChoice;
        }

        return null;
    }


    public void showHideChoiceLayout(RelativeLayout.LayoutParams relativeLayoutCaseParams,
                                     RelativeLayout.LayoutParams relativeLayoutChoicesParams,
                                     RelativeLayout.LayoutParams relativeLayoutSubCaseParams,
                                     int VISIBILITY

    ){
        holder.relativeLayoutCaseContainer.setLayoutParams(relativeLayoutCaseParams);
        holder.relativeLayoutContainerDragChoices.setLayoutParams(relativeLayoutChoicesParams);
        holder.relativeLayoutCaseSubContainer.setLayoutParams(relativeLayoutSubCaseParams);
        holder.scrollViewChoice.setVisibility(VISIBILITY);


    }

    public void generateCaseContent(LayoutInflater inflater){
        loadQuestionCase(inflater);
        loadChoices(inflater);
    }

    public void loadQuestionCase(LayoutInflater inflater){
        int buttonPosition = 0;

        if(holder.flexBoxBoxMatchParagraphCaseContainer.getChildCount()>1)
            holder.flexBoxBoxMatchParagraphCaseContainer.removeViews(1,holder.flexBoxBoxMatchParagraphCaseContainer.getChildCount()-1);

        question= question.replace("[BLANK/]"," # ");
        Log.i("idxPerQuestion", question +"");
        String [] splittedQuestion = question.split(" ");


        for(int idxQuestionSplitted =0 ; idxQuestionSplitted<splittedQuestion.length;idxQuestionSplitted++){

            if(splittedQuestion[idxQuestionSplitted].trim().equals("#"))
            {
                addAnswerContainer(buttonPosition,inflater,holder.flexBoxBoxMatchParagraphCaseContainer);
                buttonPosition++;
            }
            else{
                addTextView(viewFragment," "+splittedQuestion[idxQuestionSplitted].trim()+" ",holder.flexBoxBoxMatchParagraphCaseContainer);
            }
        }
    }

    public void startAnimation(int posX,int posY,final View viewDestination , final View layoutAnimatedImageView)
    {
        int coordinateDestination[] = new int[2];

        layoutAnimatedImageView.setX(posX);
        layoutAnimatedImageView.setY(posY-activityTopHeight);
        layoutAnimatedImageView.setVisibility(View.VISIBLE);


        viewDestination.getLocationInWindow(coordinateDestination);

        AnimatorSet animSetXY = new AnimatorSet();
        ObjectAnimator y = ObjectAnimator.ofFloat(layoutAnimatedImageView,
                "translationY",coordinateDestination[1]-activityTopHeight );
        ObjectAnimator x = ObjectAnimator.ofFloat(layoutAnimatedImageView,
                "translationX", coordinateDestination[0]);
        animSetXY.playTogether(x, y);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(300);
        animSetXY.start();

        animSetXY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                CountDownTimer cd = new CountDownTimer(10,10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ((RelativeLayout)viewFragment).removeView(layoutAnimatedImageView);
                        enableAllChoice(true);
                        viewDestination.setVisibility(View.VISIBLE);
                    }
                };
                cd.start();

            }


            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public View addAnswer(final RelativeLayout addedLayout)
    {
        Log.i("aaaazz","zzz");
        final FlexboxLayout linearLayoutBoxMatchParagraphAnswerContainer
                = (FlexboxLayout) addedLayout.findViewById(R.id.containerAnswer);
        Log.i("tagTestRel",addedLayout.getTag()+"");

        if(onHierarchyChangeListener==null) {
            Log.i("huehue","huehuehu");
            onHierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
                @Override
                public void onChildViewAdded(View parent, View child) {
                    FlexboxLayout linearLayoutAddedAnswer = (FlexboxLayout) parent;
                    int totalChildAfterChangeLayout = linearLayoutAddedAnswer.getChildCount();
//                    enableAllChoice(true);
                    Log.i("totalChildAfterChange",totalChildAfterChangeLayout +" " + totalAnswerOnContainer);
                    checkFullAnswered();
                    Log.i("tagTest",((RelativeLayout)linearLayoutAddedAnswer.getParent()).getTag()+"");
                    isAdd = true;
                        int coorViewFragment[] = new int[2];
                        viewFragment.getLocationOnScreen(coorViewFragment);
                        activityTopHeight = coorViewFragment[1];
                        View layoutAnimatedButton = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_paragraph_answer_animation,null);
                        Button buttonAnimated = (Button) layoutAnimatedButton.findViewById(R.id.buttonAnimated);
                        buttonAnimated.setText(buttonChoiceUserChoice.getText());
                        RelativeLayout relativeLayoutFragment = (RelativeLayout) viewFragment;

                        View viewDestination = linearLayoutAddedAnswer.getChildAt(totalChildAfterChangeLayout - 1);
                        Log.i("isDragTESS",isAnimation+" tes");
                        int coorUserChoice[] = new int[2];
                        buttonChoiceUserChoice.getLocationOnScreen(coorUserChoice);
                        posXSPawnAnimaton = coorUserChoice[0];
                        posYSpawnAnimation = coorUserChoice[1];
                        if(isAnimation) {
                            relativeLayoutFragment.addView(layoutAnimatedButton);
                            Log.i("isANIM","animationYES");

                            startAnimation(posXSPawnAnimaton, posYSpawnAnimation, viewDestination, layoutAnimatedButton);
                            buttonChoiceUserChoice.setVisibility(View.GONE);
                        }
                        else{
                            viewDestination.setVisibility(View.VISIBLE);
                            buttonChoiceUserChoice.setVisibility(View.GONE);
                            isAnimation  = true;
                        }
//                        listBoxMatchParagraph.get(Integer.parseInt(buttonChoiceUserChoice.getTag().toString())).
//                        Log.i("answerAddedIdx",buttonChoiceUserChoice.getTag()+"");
//                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutAddedAnswer.getTag().toString());
                        String answerIdx = buttonChoiceUserChoice.getTag().toString();
                        if(!loadUserAnswer) {
                            Log.i("tessass2111", idxAddedRelativeLayout+ " " + answerIdx);

                            listBoxMatchParagraph.get(idxAddedRelativeLayout).setUserAnswer(answerIdx);
                        }
                        else
                            loadUserAnswer = false;
//                        for(int idx=0;idx<listBoxMatchParagraph.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
                            Log.i("userAnswerList",listBoxMatchParagraph.get(idxAddedRelativeLayout).getUserAnswer());
//                        }
                        isAdd = false;
                    checkFullAnswered();
//
                    totalAnswerOnContainer = totalChildAfterChangeLayout;
                }

                @Override
                public void onChildViewRemoved(View view, View view1) {
                    FlexboxLayout linearLayoutAddedAnswer = (FlexboxLayout) view.findViewById(R.id.containerAnswer);
                    int totalChildAfterChangeLayout = linearLayoutAddedAnswer.getChildCount();
                    if(!isAdd) {
                            Log.i("qqq211", "asd");
//                        Log.i("answerDeletedIdx",imageViewChoiceUserChoice.getTag()+"");
//                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
                            int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutAddedAnswer.getTag().toString());
                            String answerIdx = buttonChoiceUserChoice.getTag().toString();
                            listBoxMatchParagraph.get(idxAddedRelativeLayout).setUserAnswer("");
//                        for(int idx=0;idx<listBoxMatchParagraph.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
                            Log.i("userAnswerList", listBoxMatchParagraph.get(idxAddedRelativeLayout).getUserAnswer());
//                        }
                        }
                    checkFullAnswered();
//
                    totalAnswerOnContainer = totalChildAfterChangeLayout;
                }
            };

        }

        linearLayoutBoxMatchParagraphAnswerContainer.setOnHierarchyChangeListener(onHierarchyChangeListener);

        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_paragraph_answer_item, null);
        final Button buttonBoxMatchParagraphAnswerItem
                = (Button) layoutAnswerItem.findViewById(R.id.buttonBoxMatchParagraphAnswerItem);



        buttonBoxMatchParagraphAnswerItem.setText(buttonChoiceUserChoice.getText());
        layoutAnswerItem.setTag(buttonChoiceUserChoice.getTag());
        buttonBoxMatchParagraphAnswerItem.setTag(buttonChoiceUserChoice.getTag());

        buttonBoxMatchParagraphAnswerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                totalAnswerOnContainer = ((FlexboxLayout)v.getParent().getParent()).getChildCount();
                String buttonIdx = v.getTag().toString();
                for (int idxChoices=0;idxChoices<choicesList.size();idxChoices++)
                {
                    String sourceLayoutImageViewIdx =choicesList.get(idxChoices).getTag().toString();
                    if(sourceLayoutImageViewIdx.equals(buttonIdx)){
                        View viewChoicesList = choicesList.get(idxChoices);
                        buttonChoiceUserChoice
                                = (Button) viewChoicesList.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);
                        int coorAnswer[] = new int[2];
                        v.getLocationOnScreen(coorAnswer);
//                        posXSPawnAnimaton = coorAnswer[0];
//                        posYSpawnAnimation = coorAnswer[1];
                        relativeLayoutAddedAnswer = addedLayout;
                        Log.i("buttonTAG",buttonChoiceUserChoice.getTag()+"");
                        buttonChoiceUserChoice.setVisibility(View.VISIBLE);

                        linearLayoutBoxMatchParagraphAnswerContainer.removeView((View) v.getParent());
                        totalAnswerOnContainer = linearLayoutBoxMatchParagraphAnswerContainer.getChildCount();


                        break;
                    }
                }
            }
        });

        layoutAnswerItem.setVisibility(View.GONE);

        linearLayoutBoxMatchParagraphAnswerContainer.addView(layoutAnswerItem);

        return linearLayoutBoxMatchParagraphAnswerContainer.getChildAt(linearLayoutBoxMatchParagraphAnswerContainer.getChildCount()-1);
    }


    public void addAnswerContainer(int containerNumber,LayoutInflater inflater,FlexboxLayout layoutCaseLostWordTypeItem)
    {
        final View answerContainerLayout = inflater.inflate(R.layout.layout_case_box_match_paragraph_answer_container,null);
        RelativeLayout relativeLayoutCaseContainer = (RelativeLayout) answerContainerLayout.findViewById(R.id.relativeLayoutCaseContainer);

        relativeLayoutCaseContainer.setTag(containerNumber);
        answerContainerLayout.setTag(containerNumber);
        relativeLayoutCaseContainer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.i("dragStarted3","started "+ v.getHeight());
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i("flexDrag","dragFlexEntered " + v.getTag());
                        break;
                    case DragEvent.ACTION_DROP:
//                            relativeLayoutUserChoice = (RelativeLayout) v;
                        relativeLayoutAddedAnswer = (RelativeLayout) v;
                        Log.i("flexDragDROP",v.getClass().getName());
                        isAnimation = false;
                        FlexboxLayout flexboxLayoutAnswerContainer = (FlexboxLayout) answerContainerLayout.findViewById(R.id.containerAnswer);
                        if(flexboxLayoutAnswerContainer.getChildCount()>0)
                        {
                            swapAnswer(flexboxLayoutAnswerContainer);

                        }
                        totalAnswerOnContainer= flexboxLayoutAnswerContainer.getChildCount();

                        addAnswer((RelativeLayout) v);

                        break;
                }
                return true;
            }
        });

        layoutCaseLostWordTypeItem.addView(answerContainerLayout);
        listAnswerContainer.add(answerContainerLayout);
    }

    public void swapAnswer(FlexboxLayout flexboxLayoutSwap)
    {

        String oldAnswerTag = flexboxLayoutSwap.getChildAt(0).getTag()+"";
        flexboxLayoutSwap.removeAllViews();
        for(View choices : choicesList){
            if(choices.getTag().toString().equals(oldAnswerTag.toString()))
            {
                choices.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront).setVisibility(View.VISIBLE);
                break;
            }
        }
        Log.i("oldAnswerTag",oldAnswerTag );
    }


    public void addTextView(View cellView,String text,FlexboxLayout layoutToBeAdded)
    {
        TextView txtViewTextQuestion = new TextView(cellView.getContext());
        txtViewTextQuestion.setText(text+"");
        txtViewTextQuestion.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
        layoutToBeAdded.addView(txtViewTextQuestion);
    }

    public void loadChoices(LayoutInflater inflater){
        if(choicesList==null) {
            choicesList = new ArrayList<>();
            for (int idxChoices = 0; idxChoices < listChoices.size(); idxChoices++) {
                View layoutButton = inflater.inflate(R.layout.layout_case_box_match_paragraph_choice_item, null);
                RelativeLayout relativeLayoutButtonChoicesContainer
                        = (RelativeLayout) layoutButton.findViewById(R.id.relativeLayoutChoicesContainer);

                Button buttonChoiceItemBack
                        = (Button) layoutButton.findViewById(R.id.buttonBoxMatchParagraphChoiceItemBack);


                Button buttonChoiceItemFront
                        = (Button) layoutButton.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);

                layoutButton.setTag(idxChoices);
                buttonChoiceItemFront.setTag(idxChoices);

                buttonChoiceItemFront.setText(listChoices.get(idxChoices));
                buttonChoiceItemBack.setText(listChoices.get(idxChoices));


                addDragClickChoicesListener(buttonChoiceItemFront);


                choicesList.add(layoutButton);
            }

            Log.i("shuffle", "shuffle2");
            Collections.shuffle(choicesList);
        }
        for(View layoutButton : choicesList){
            holder.flexBoxBoxMatchParagraphCaseChoicesContainer.addView(layoutButton);
        }
    }

    public void loadUserAnswer()
    {
        for(int idxCase=0;idxCase<listBoxMatchParagraph.size();idxCase++)
        {
                String userAnswer = listBoxMatchParagraph.get(idxCase).getUserAnswer();
                Log.i("userAnswerListAA",userAnswer + " aaa");

            for(View choice: choicesList)
                {
                    Log.i("userAnswerList222",userAnswer);
                    Log.i("userAnswerList2223",choice.getTag().toString());

                    if(choice.getTag().toString().equals(userAnswer))
                    {

                        buttonChoiceUserChoice = (Button) choice.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);
                        Log.i("userAnswerList222","afterrs");
                        final RelativeLayout relativeLayoutLoadAnswer = (RelativeLayout) listAnswerContainer.get(idxCase);

                        final FlexboxLayout flexBoxBoxMatchParagraphAnswerContainer
                                = (FlexboxLayout)relativeLayoutLoadAnswer.findViewById(R.id.containerAnswer);
                        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_paragraph_answer_item, null);
                        final Button buttonBoxMatchParagraphAnswerItem
                                = (Button) layoutAnswerItem.findViewById(R.id.buttonBoxMatchParagraphAnswerItem);

                        buttonBoxMatchParagraphAnswerItem.setText(buttonChoiceUserChoice.getText());

                        buttonBoxMatchParagraphAnswerItem.setTag(buttonChoiceUserChoice.getTag());

                        buttonBoxMatchParagraphAnswerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                totalAnswerOnContainer = ((FlexboxLayout)v.getParent().getParent()).getChildCount();
                                String imageViewIdx = v.getTag().toString();
                                for (int idxChoices=0;idxChoices<choicesList.size();idxChoices++)
                                {
                                    String sourceLayoutImageViewIdx =choicesList.get(idxChoices).getTag().toString();
                                    if(sourceLayoutImageViewIdx.equals(imageViewIdx)){
                                        View viewChoicesList = choicesList.get(idxChoices);
                                        buttonChoiceUserChoice
                                                = (Button) viewChoicesList.findViewById(R.id.buttonBoxMatchParagraphChoiceItemFront);
                                        int coorAnswer[] = new int[2];
                                        v.getLocationOnScreen(coorAnswer);
                                        posXSPawnAnimaton = coorAnswer[0];
                                        posYSpawnAnimation = coorAnswer[1];
//                                        relativeLayoutUserChoiceTemp = relativeLayoutUserChoiceTemp;
                                        flexBoxBoxMatchParagraphAnswerContainer.removeView((View) v.getParent());
                                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutLoadAnswer.getTag().toString());
                                        String answerIdx = buttonChoiceUserChoice.getTag().toString();
                                        buttonChoiceUserChoice.setVisibility(View.VISIBLE);

                                        listBoxMatchParagraph.get(idxAddedRelativeLayout).setUserAnswer("");
//                                        checkFullAnswered();
                                        totalAnswerOnContainer = flexBoxBoxMatchParagraphAnswerContainer.getChildCount();
                                        checkFullAnswered();
                                        break;
                                    }
                                }
                            }
                        });

                        flexBoxBoxMatchParagraphAnswerContainer.addView(layoutAnswerItem);
                        layoutAnswerItem.setVisibility(View.VISIBLE);
                        buttonChoiceUserChoice.setVisibility(View.GONE);

                    }
                }

        }
    }

}
