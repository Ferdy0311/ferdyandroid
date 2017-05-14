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
import com.bahaso.model.SentenceFormation;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentenceFormationFragment extends FragmentBahaso {

    int addedAnswerPos;

    float posXSelectedRelativeLayout;
    ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener;
    boolean isAdd = false;
    static Button buttonChoiceUserChoice;
    static View vMain;
    static int layoutPosForScrolled;
    static int posXSPawnAnimaton, posYSpawnAnimation;
    Button imView;
    View viewFragment;
    FlexboxLayout flex;
    static int coorXMain, coorYMain;
    int heightInPx = 0;
    static boolean isAnimation = true;
    List<SentenceFormation> listSentenceFormation;
    ArrayList<RelativeLayout> listRelativeLayoutQuestionBox;
    String instruction;
    String type;
    String caseID;
    ViewFragmentHolder holder;
    ArrayList<View> choicesList;
    ArrayList<String> listChoices;

    View.OnLayoutChangeListener onLayoutChangeListener;
    static RelativeLayout relativeLayoutUserChoice;
    static RelativeLayout relativeLayoutUserChoiceTemp;
    static int totalAnswerOnContainerClicked;
    boolean loadUserAnswer = false;
    ViewTreeObserver.OnScrollChangedListener scrollChangedListener;
    static int activityTopHeight;
    boolean isShuffle = false;
    boolean isScrollForPositioning = false;


    @Override
    public boolean getIsFullAnswered() {
        return super.getIsFullAnswered();
    }

    @Override
    public void checkFullAnswered()
    {
        isFullAnswered = true;
        for(SentenceFormation rowData : listSentenceFormation){

            if(rowData.getUserAnswer().equals(""))
            {
                isFullAnswered= false;
                break;
            }
        }

    }

    @Override
    public void checkAnswer() {
        for(RelativeLayout relativeLayoutQuestionBox: listRelativeLayoutQuestionBox){
            FlexboxLayout flexBoxSentenceFormationAnswerContainer
                    = (FlexboxLayout)relativeLayoutQuestionBox.findViewById(R.id.flexBoxSentenceFormationAnswerContainer);
            for(int idxAnswer =0 ; idxAnswer < flexBoxSentenceFormationAnswerContainer.getChildCount();idxAnswer++){
                View viewAnswer = flexBoxSentenceFormationAnswerContainer.getChildAt(idxAnswer);
                Button buttonAnswer = (Button) viewAnswer.findViewById(R.id.buttonSentenceFormationAnswerItem);
                Log.i("tagAnswer", buttonAnswer.getTag().toString());
                String answer = buttonAnswer.getTag().toString().split("#")[0];
                String bahasoAnswer = listSentenceFormation.get(idxAnswer).getAnswer();
                if(answer.equals(bahasoAnswer)) {
                    int paddingTop = buttonAnswer.getPaddingTop();
                    int paddingBottom = buttonAnswer.getPaddingBottom();
                    int paddingLeft = buttonAnswer.getPaddingLeft();
                    int paddingRight = buttonAnswer.getPaddingRight();

                    buttonAnswer.setBackgroundResource(R.drawable.background_box_correct_answer);
                    buttonAnswer.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);

                }else {
                    int paddingTop = buttonAnswer.getPaddingTop();
                    int paddingBottom = buttonAnswer.getPaddingBottom();
                    int paddingLeft = buttonAnswer.getPaddingLeft();
                    int paddingRight = buttonAnswer.getPaddingRight();
                    buttonAnswer.setBackgroundResource(R.drawable.background_box_false_answer);
                    buttonAnswer.setTextColor(getContext().getResources().getColor(R.color.bahaso_white_gray));
                    buttonAnswer.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);

                }
            }
        }

    }

    static class ViewFragmentHolder {
        TextView textViewInstruction;
        FlexboxLayout flexBoxSentenceFormationCaseContainer;
        FlexboxLayout flexBoxSentenceFormationCaseChoicesContainer;
        LinearLayout layoutChoiceShowHide;
        ScrollView scrollViewChoice;
        ScrollView scrollViewCase;
        RelativeLayout relativeLayoutCaseContainer;
        RelativeLayout relativeLayoutContainerDragChoices;
        RelativeLayout relativeLayoutCaseSubContainer;
    }


    public SentenceFormationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listRelativeLayoutQuestionBox = new ArrayList<>();
        if (getArguments() != null) {
            Log.i("wwzzz22aazz", "asdasd");

            listSentenceFormation = getArguments().getParcelableArrayList("listData");
            listChoices = getArguments().getStringArrayList("listChoices");


            instruction = getArguments().getString("instruction");
            caseID = getArguments().getString("caseID");

            setType(getArguments().getString("type"));
        }

        if (viewFragment == null) {
            viewFragment = inflater.inflate(R.layout.fragment_sentence_formation, container, false);
            holder = new ViewFragmentHolder();
            holder.textViewInstruction = (TextView) viewFragment.findViewById(R.id.textViewSentenceFormationInstruction);
            holder.flexBoxSentenceFormationCaseChoicesContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxSentenceFormationCaseChoicesContainer);
            holder.flexBoxSentenceFormationCaseContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxSentenceFormationCaseContainer);
            holder.layoutChoiceShowHide = (LinearLayout) viewFragment.findViewById(R.id.buttonHideDragChoices);
            holder.scrollViewChoice = (ScrollView) viewFragment.findViewById(R.id.scrollViewSentenceFormationCaseDragChoices);

            holder.scrollViewCase = (ScrollView) viewFragment.findViewById(R.id.scrollViewSentenceFormationCaseContainer);

            holder.scrollViewCase.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isScrollForPositioning = false;
                    for (View viewChoice : choicesList) {
                        Button buttonChoice = (Button) viewChoice.findViewById(R.id.buttonSentenceFormationChoiceItemFront);
                        buttonChoice.setEnabled(true);
                    }
                    return false;
                }
            });


            holder.relativeLayoutCaseContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutSentenceFormationCaseContainer);
            holder.relativeLayoutContainerDragChoices = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutSentenceFormationContainerDragChoices);
            holder.relativeLayoutCaseSubContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutCaseSubContainer);

            viewFragment.setTag(holder);
        } else {
            holder = (ViewFragmentHolder) viewFragment.getTag();
        }
        holder.flexBoxSentenceFormationCaseChoicesContainer.removeAllViews();
        holder.textViewInstruction.setText(instruction);

        generateCaseContent(inflater);


        holder.layoutChoiceShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heightInPx == 0)
                    heightInPx = holder.relativeLayoutCaseSubContainer.getHeight();
                if (holder.scrollViewChoice.getVisibility() == View.VISIBLE) {
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE, R.id.relativeLayoutSentenceFormationContainerDragChoices);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW, 0);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    showHideChoiceLayout(relativeLayoutCaseParams, relativeLayoutChoicesParams, relativeLayoutSubCaseParams, View.GONE);

                } else {
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE, 0);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW, R.id.relativeLayoutSentenceFormationCaseContainer);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightInPx);
                    showHideChoiceLayout(relativeLayoutCaseParams, relativeLayoutChoicesParams, relativeLayoutSubCaseParams, View.VISIBLE);
                }
            }
        });

        loadUserAnswer();
        return viewFragment;
    }

    public void showHideChoiceLayout(RelativeLayout.LayoutParams relativeLayoutCaseParams,
                                     RelativeLayout.LayoutParams relativeLayoutChoicesParams,
                                     RelativeLayout.LayoutParams relativeLayoutSubCaseParams,
                                     int VISIBILITY

    ) {
        holder.relativeLayoutCaseContainer.setLayoutParams(relativeLayoutCaseParams);
        holder.relativeLayoutContainerDragChoices.setLayoutParams(relativeLayoutChoicesParams);
        holder.relativeLayoutCaseSubContainer.setLayoutParams(relativeLayoutSubCaseParams);
        holder.scrollViewChoice.setVisibility(VISIBILITY);


    }

    public void generateCaseContent(LayoutInflater inflater) {
        loadQuestionCase(inflater);
        loadChoices(inflater);
    }

    public void loadQuestionCase(LayoutInflater inflater) {
        if (holder.flexBoxSentenceFormationCaseContainer.getChildCount() > 1)
            holder.flexBoxSentenceFormationCaseContainer.removeViews(1, holder.flexBoxSentenceFormationCaseContainer.getChildCount() - 1);
//        for (int idxCase = 0; idxCase < listSentenceFormation.size(); idxCase++) {
            final View layoutCase = inflater.inflate(R.layout.layout_case_sentence_formation_case_answer_container, null);
            RelativeLayout relativeLayoutCaseContainer = (RelativeLayout) layoutCase.findViewById(R.id.relativeLayoutCaseContainer);

            relativeLayoutCaseContainer.setTag(0);
            layoutCase.setTag(0);

//            relativeLayoutCaseContainer.setOnDragListener(new View.OnDragListener() {
//                @Override
//                public boolean onDrag(View v, DragEvent event) {
//                    Log.i("dragStarted3","started "+ v.getHeight());
//                    switch (event.getAction()){
//                        case DragEvent.ACTION_DRAG_ENTERED:
//                            Log.i("flexDrag","dragFlexEntered");
//                            break;
//                        case DragEvent.ACTION_DROP:
////                            relativeLayoutUserChoice = (RelativeLayout) v;
//                            relativeLayoutUserChoiceTemp = (RelativeLayout) v;
//                            Log.i("flexDragDROP",v.getClass().getName());
//                            isAnimation = false;
//                            FlexboxLayout flexboxLayoutAnswerContainer = (FlexboxLayout) layoutCase.findViewById(R.id.flexBoxSentenceFormationAnswerContainer);
//                            totalAnswerOnContainerClicked = flexboxLayoutAnswerContainer.getChildCount();
//                            addAnswer((RelativeLayout) v);
//                            break;
//                    }
//                    return true;
//                }
//            });

//            relativeLayoutCaseContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Log.i("clickRelativeLayout","clicked");
//
//
//                    relativeLayoutUserChoice = (RelativeLayout) v;
//                    relativeLayoutUserChoice.setTag(v.getTag());
//
//                    relativeLayoutUserChoiceTemp = (RelativeLayout) v;
//                    relativeLayoutUserChoiceTemp.setTag(v.getTag());
//
//                    totalAnswerOnContainerClicked = getUserChoiceLayoutAnswerCount();
//                    resetQuestionUserChoice();
//
//                    Log.i("childCountSelected",totalAnswerOnContainerClicked+"");
//                    int paddingLeft = relativeLayoutUserChoice.getPaddingLeft();
//                    int paddingTop = relativeLayoutUserChoice.getPaddingTop();
//                    int paddingRight = relativeLayoutUserChoice.getPaddingRight();
//                    int paddingBottom = relativeLayoutUserChoice.getPaddingBottom();
//                    resetQuestionUserChoice();
//                    relativeLayoutUserChoice.setBackgroundResource(R.drawable.background_box_match_disappear_case_answer_container_selected);
//                    relativeLayoutUserChoice.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
//                }
//            });

//            if(idxCase==0)
//                relativeLayoutCaseContainer.performClick();
            relativeLayoutUserChoice = (RelativeLayout) layoutCase;
            listRelativeLayoutQuestionBox.add(relativeLayoutCaseContainer);
            holder.flexBoxSentenceFormationCaseContainer.addView(layoutCase);

//        }
    }

    public void loadChoices(LayoutInflater inflater) {
        if (choicesList == null) {
            choicesList = new ArrayList<>();
            for (int idxChoices = 0; idxChoices < listChoices.size(); idxChoices++) {
                View layoutButton = inflater.inflate(R.layout.layout_case_sentence_formation_choice_item, null);
                RelativeLayout relativeLayoutButtonChoicesContainer
                        = (RelativeLayout) layoutButton.findViewById(R.id.relativeLayoutChoicesContainer);

                Button buttonChoiceItemBack
                        = (Button) layoutButton.findViewById(R.id.buttonSentenceFormationChoiceItemBack);


                Button buttonChoiceItemFront
                        = (Button) layoutButton.findViewById(R.id.buttonSentenceFormationChoiceItemFront);

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
        for (View layoutButton : choicesList) {
            holder.flexBoxSentenceFormationCaseChoicesContainer.addView(layoutButton);
        }
    }

    public void addDragClickChoicesListener(final Button buttonChoiceItemFront) {
        buttonChoiceItemFront.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (int idxPointer = 0; idxPointer < event.getPointerCount(); idxPointer++) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            if (event.getY() > v.getHeight() || event.getY() < 0) {
                                ClipData data = ClipData.newPlainText("", "");
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                                v.startDrag(data, shadowBuilder, v, 0);
                                buttonChoiceUserChoice = (Button) v;
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
                    Log.i("wew214", isScrollForPositioning + "");

                    buttonChoiceUserChoice = buttonChoiceItemFront;
                    Log.i("TESS 22", "sad");
                    int minYAddedLayout, maxYAddedlayout;
                    float choosenLayoutYMin, choosenLayoutYMax;
                    float tempPosXSelectedRelativeLayout;
                    Log.i("tessHeight", holder.textViewInstruction.getHeight() + "");
                    relativeLayoutUserChoiceTemp = relativeLayoutUserChoice;

                    totalAnswerOnContainerClicked = getUserChoiceLayoutAnswerCount();


                    int startAfterTextInstruction = holder.textViewInstruction.getHeight();
                    isAnimation = true;
                    if (scrollChangedListener == null) {
//                        final int maxScrollPos = holder.scrollViewCase.getChildAt(0).getHeight()-holder.scrollViewCase.getHeight();
                        scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
//                                isScrollForPositioning = true;
                                final int maxScrollPos = holder.scrollViewCase.getChildAt(0).getHeight() - holder.scrollViewCase.getHeight();

                                Log.i("holder", holder.scrollViewCase.getScrollY() + "");

                                if (!isScrollForPositioning)
                                    return;

                                if (layoutPosForScrolled <= 0) {
                                    isScrollForPositioning = false;

                                    addAnswer(relativeLayoutUserChoice);
                                    layoutPosForScrolled = -1;
                                    return;
                                }

                                if (holder.scrollViewCase.getScrollY() == layoutPosForScrolled || holder.scrollViewCase.getScrollY() >= maxScrollPos) {
                                    isScrollForPositioning = false;

                                    addAnswer(relativeLayoutUserChoice);
                                    layoutPosForScrolled = -1;
//                                    imageViewChoiceUserChoice=null;
                                }

                            }
                        };

                        holder.scrollViewCase.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);

                    }
                    if (holder.scrollViewCase != null) {
                        minYAddedLayout = holder.scrollViewCase.getScrollY();
                        maxYAddedlayout = minYAddedLayout + holder.relativeLayoutCaseSubContainer.getHeight();
                        choosenLayoutYMin = ((View) relativeLayoutUserChoiceTemp.getParent()).getY();
                        choosenLayoutYMax = choosenLayoutYMin + ((View) relativeLayoutUserChoiceTemp.getParent()).getHeight();

                        if (posXSelectedRelativeLayout == 0)
                            posXSelectedRelativeLayout = ((View) relativeLayoutUserChoiceTemp.getParent()).getX();

                        tempPosXSelectedRelativeLayout = ((View) relativeLayoutUserChoiceTemp.getParent()).getX();
                        Log.i("minYAddedLayout", minYAddedLayout + "");
                        Log.i("choosenLayoutYMin", choosenLayoutYMin + "");

                        Log.i("maxYAddedLayout", maxYAddedlayout + "");
                        Log.i("choosenLayoutYMax", choosenLayoutYMax + "");
//                        Log.i("scViewHeight",+"");
                        if (choosenLayoutYMin < minYAddedLayout || choosenLayoutYMin > maxYAddedlayout || choosenLayoutYMax < minYAddedLayout || choosenLayoutYMax > maxYAddedlayout) {
                            float diff = choosenLayoutYMax - maxYAddedlayout;
                            if (diff >= -15 && diff <= 15) {
//                                if()
                                addAnswer(relativeLayoutUserChoice);
                                return;
                            }
//
//                            if(diff<10)
//                                diff=0;


//                            if(posXSelectedRelativeLayout!=tempPosXSelectedRelativeLayout)
//                                posXSelectedRelativeLayout=tempPosXSelectedRelativeLayout;

                            Log.i("changesScroll", (choosenLayoutYMax - maxYAddedlayout) + "");


                            layoutPosForScrolled = startAfterTextInstruction == choosenLayoutYMin ? 0 : (int) (minYAddedLayout + diff + 10);

//                            v.setEnabled(false);
                            isScrollForPositioning = true;
                            Log.i("wew213", "asdasd");
                            holder.scrollViewCase.smoothScrollTo(0, startAfterTextInstruction == choosenLayoutYMin ? 0 : (int) (minYAddedLayout + diff + 10));
                        } else {
                            addAnswer(relativeLayoutUserChoice);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getUserChoiceLayoutAnswerCount() {
        FlexboxLayout flexBoxSentenceFormationAnswerContainer
                = (FlexboxLayout) relativeLayoutUserChoice.findViewById(R.id.flexBoxSentenceFormationAnswerContainer);
        int flexBoxSentenceFormationAnswerContainerChildCount =
                flexBoxSentenceFormationAnswerContainer.getChildCount();
        return flexBoxSentenceFormationAnswerContainerChildCount;

    }

    public void startAnimation(int posX, int posY, final View viewDestination, final View layoutAnimatedImageView) {

        int coordinateDestination[] = new int[2];

        layoutAnimatedImageView.setX(posX);
        layoutAnimatedImageView.setY(posY - activityTopHeight);
        layoutAnimatedImageView.setVisibility(View.VISIBLE);


        viewDestination.getLocationInWindow(coordinateDestination);
        Log.i("posXDest", "X = " + coordinateDestination[0] + "");
        Log.i("posYDest", "Y = " + coordinateDestination[1] + "");

        AnimatorSet animSetXY = new AnimatorSet();
        ObjectAnimator y = ObjectAnimator.ofFloat(layoutAnimatedImageView,
                "translationY", coordinateDestination[1] - activityTopHeight);
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

                CountDownTimer cd = new CountDownTimer(10, 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ((RelativeLayout) viewFragment).removeView(layoutAnimatedImageView);

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

    public View addAnswer(final RelativeLayout addedLayout) {
        final FlexboxLayout flexBoxSentenceFormationAnswerContainer
                = (FlexboxLayout) addedLayout.findViewById(R.id.flexBoxSentenceFormationAnswerContainer);

        if (onLayoutChangeListener == null) {
            onLayoutChangeListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (!isAdd)
                        return;
                    Log.i("huehue2", "huehuehu2");

                    FlexboxLayout flexboxLayoutUserSelected = (FlexboxLayout) v;
                    int totalChildAfterChangeLayout = flexboxLayoutUserSelected.getChildCount();
                    enableAllChoice(true);

                    Log.i("totalChildAfterChange", totalChildAfterChangeLayout + " " + totalAnswerOnContainerClicked);

                    if (totalChildAfterChangeLayout > totalAnswerOnContainerClicked) {
                        int coorViewFragment[] = new int[2];
                        viewFragment.getLocationOnScreen(coorViewFragment);
                        activityTopHeight = coorViewFragment[1];
                        View layoutAnimatedButton = getLayoutInflater(null).inflate(R.layout.layout_case_sentence_formation_answer_animation, null);
                        Button buttonAnimated = (Button) layoutAnimatedButton.findViewById(R.id.buttonAnimated);
                        buttonAnimated.setText(buttonChoiceUserChoice.getText());

                        RelativeLayout relativeLayoutFragment = (RelativeLayout) viewFragment;

                        View viewDestination = flexboxLayoutUserSelected.getChildAt(totalChildAfterChangeLayout - 1);

                        int coordinateDestinations[] = new int[2];
                        viewDestination.getLocationOnScreen(coordinateDestinations);
                        Log.i("COORTESTX", coordinateDestinations[0] + "");
                        Log.i("COORTESTY", coordinateDestinations[1] + "");

                        Log.i("isDragTESS", isAnimation + " tes");
                        int coorUserChoice[] = new int[2];
                        buttonChoiceUserChoice.getLocationOnScreen(coorUserChoice);
                        posXSPawnAnimaton = coorUserChoice[0];
                        posYSpawnAnimation = coorUserChoice[1];
                        if (isAnimation) {
                            relativeLayoutFragment.addView(layoutAnimatedButton);

                            startAnimation(posXSPawnAnimaton, posYSpawnAnimation, viewDestination, layoutAnimatedButton);
                            buttonChoiceUserChoice.setVisibility(View.GONE);
                        } else {
                            viewDestination.setVisibility(View.VISIBLE);
                            buttonChoiceUserChoice.setVisibility(View.GONE);
                            isAnimation = true;
                        }
//                        listSentenceFormation.get(Integer.parseInt(imageViewChoiceUserChoice.getTag().toString())).getArrayListUserAnswer().add()
                        Log.i("answerAddedIdx", buttonChoiceUserChoice.getTag() + "");
                        Log.i("userChoiceLayout", relativeLayoutUserChoiceTemp.getTag() + "");
                        Log.i("addedAnswerPos", addedAnswerPos + "");

                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
                        String answerIdx = buttonChoiceUserChoice.getTag().toString();
                        if (!loadUserAnswer) {
                            listSentenceFormation.get(addedAnswerPos).setUserAnswer(answerIdx);
                        } else
                            loadUserAnswer = false;
                    } else {
                        Log.i("answerDeletedIdx", buttonChoiceUserChoice.getTag() + "");
                        Log.i("userChoiceLayout", relativeLayoutUserChoiceTemp.getTag() + "");
                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
                        String answerIdx = buttonChoiceUserChoice.getTag().toString();
                        listSentenceFormation.get(addedAnswerPos).setUserAnswer("");

                    }
                    isAdd = false;
                    checkFullAnswered();
                    totalAnswerOnContainerClicked = totalChildAfterChangeLayout;

                }
            };
        }
//        flexBoxSentenceFormationAnswerContainer.setOnHierarchyChangeListener(onHierarchyChangeListener);
        flexBoxSentenceFormationAnswerContainer.addOnLayoutChangeListener(onLayoutChangeListener);

        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_sentence_formation_answer_item, null);
        final Button buttonSentenceFormationAnswerItem
                = (Button) layoutAnswerItem.findViewById(R.id.buttonSentenceFormationAnswerItem);

        totalAnswerOnContainerClicked = flexBoxSentenceFormationAnswerContainer.getChildCount();
        Log.i("nerClickedGGZ",totalAnswerOnContainerClicked+"");
        buttonSentenceFormationAnswerItem.setText(buttonChoiceUserChoice.getText());
        layoutAnswerItem.setTag(buttonChoiceUserChoice.getTag());
        buttonSentenceFormationAnswerItem.setTag(buttonChoiceUserChoice.getTag()+"#"+totalAnswerOnContainerClicked);

        addedAnswerPos=totalAnswerOnContainerClicked;

        buttonSentenceFormationAnswerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                isAdd = true;
                totalAnswerOnContainerClicked = ((FlexboxLayout) v.getParent().getParent()).getChildCount();
                String imageViewIdx = v.getTag().toString().split("#")[0];
                for (int idxChoices = 0; idxChoices < choicesList.size(); idxChoices++) {
                    String sourceLayoutImageViewIdx = choicesList.get(idxChoices).getTag().toString();
                    if (sourceLayoutImageViewIdx.equals(imageViewIdx)) {
                        addedAnswerPos = Integer.parseInt(v.getTag().toString().split("#")[1]);
                        Log.i("addedAnswerPosGGZWEWQ",addedAnswerPos +"");

                        View viewChoicesList = choicesList.get(idxChoices);
                        buttonChoiceUserChoice
                                = (Button) viewChoicesList.findViewById(R.id.buttonSentenceFormationChoiceItemFront);
                        int coorAnswer[] = new int[2];
                        v.getLocationOnScreen(coorAnswer);
                        posXSPawnAnimaton = coorAnswer[0];
                        posYSpawnAnimation = coorAnswer[1];
                        relativeLayoutUserChoiceTemp = addedLayout;
                        buttonChoiceUserChoice.setVisibility(View.VISIBLE);

                        flexBoxSentenceFormationAnswerContainer.removeView((View) v.getParent());
                        totalAnswerOnContainerClicked = flexBoxSentenceFormationAnswerContainer.getChildCount();

                        break;
                    }
                }
            }
        });

        isAdd = true;
        flexBoxSentenceFormationAnswerContainer.addView(layoutAnswerItem);

        Log.i("GGZ", "asd");

        layoutAnswerItem.setVisibility(View.INVISIBLE);

        return flexBoxSentenceFormationAnswerContainer.getChildAt(flexBoxSentenceFormationAnswerContainer.getChildCount() - 1);
    }

    public void enableAllChoice(Boolean isChoicesEnabled)
    {
        for(View viewChoice: choicesList){
            Button buttonChoiceFront = (Button) viewChoice.findViewById(R.id.buttonSentenceFormationChoiceItemFront);
            buttonChoiceFront.setEnabled(isChoicesEnabled);
        }
    }

    public void loadUserAnswer()
    {
        for(int idxCase=0;idxCase<listSentenceFormation.size();idxCase++)
        {

                String userAnswer = listSentenceFormation.get(idxCase).getUserAnswer();
            Log.i("userAnswerGGZ",userAnswer+" ussAns");

            for(View choice: choicesList)
                {
                    if(choice.getTag().toString().equals(userAnswer))
                    {

                        buttonChoiceUserChoice = (Button) choice.findViewById(R.id.buttonSentenceFormationChoiceItemFront);
                        Log.i("GGZZS","afterrs");
                        final RelativeLayout relativeLayoutLoadAnswer = listRelativeLayoutQuestionBox.get(0);

                        final FlexboxLayout flexBoxSentenceFormationAnswerContainer
                                = (FlexboxLayout)relativeLayoutLoadAnswer.findViewById(R.id.flexBoxSentenceFormationAnswerContainer);
                        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_sentence_formation_answer_item, null);
                        final Button buttonSentenceFormationAnswerItem
                                = (Button) layoutAnswerItem.findViewById(R.id.buttonSentenceFormationAnswerItem);
                        totalAnswerOnContainerClicked = flexBoxSentenceFormationAnswerContainer.getChildCount();

                        buttonSentenceFormationAnswerItem.setText(buttonChoiceUserChoice.getText());

                        buttonSentenceFormationAnswerItem.setTag(buttonChoiceUserChoice.getTag()+"#"+totalAnswerOnContainerClicked);

                        buttonSentenceFormationAnswerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                totalAnswerOnContainerClicked = ((FlexboxLayout)v.getParent().getParent()).getChildCount();
                                String imageViewIdx = v.getTag().toString().split("#")[0];
                                for (int idxChoices=0;idxChoices<choicesList.size();idxChoices++)
                                {
                                    String sourceLayoutImageViewIdx =choicesList.get(idxChoices).getTag().toString();
                                    if(sourceLayoutImageViewIdx.equals(imageViewIdx)){
                                        addedAnswerPos = Integer.parseInt(v.getTag().toString().split("#")[1]);

                                        View viewChoicesList = choicesList.get(idxChoices);
                                        buttonChoiceUserChoice
                                                = (Button) viewChoicesList.findViewById(R.id.buttonSentenceFormationChoiceItemFront);
                                        int coorAnswer[] = new int[2];
                                        v.getLocationOnScreen(coorAnswer);
                                        posXSPawnAnimaton = coorAnswer[0];
                                        posYSpawnAnimation = coorAnswer[1];
//                                        relativeLayoutUserChoiceTemp = relativeLayoutUserChoiceTemp;
                                        flexBoxSentenceFormationAnswerContainer.removeView((View) v.getParent());
                                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutLoadAnswer.getTag().toString());
                                        String answerIdx = buttonChoiceUserChoice.getTag().toString();
                                        buttonChoiceUserChoice.setVisibility(View.VISIBLE);

                                        listSentenceFormation.get(addedAnswerPos).setUserAnswer("");

                                        totalAnswerOnContainerClicked = flexBoxSentenceFormationAnswerContainer.getChildCount();

                                        checkFullAnswered();

                                        break;
                                    }
                                }
                            }
                        });

                        flexBoxSentenceFormationAnswerContainer.addView(layoutAnswerItem);
                        layoutAnswerItem.setVisibility(View.VISIBLE);
                        buttonChoiceUserChoice.setVisibility(View.GONE);

                    }
                }

        }
    }
}
