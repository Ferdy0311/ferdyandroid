package com.bahaso.typecase;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.fragmenthelper.FragmentBahaso;
import com.bahaso.globalvar.helper.picassohelper.PicassoHelper;
import com.bahaso.model.BoxMatchDisappearPicture;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoxMatchDisappearPictureFragment extends FragmentBahaso {

    float posXSelectedRelativeLayout;
    ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener;

    static ImageView imageViewChoiceUserChoice;
    static View vMain;
    static int layoutPosForScrolled;
    static int posXSPawnAnimaton, posYSpawnAnimation ;
    Button imView;
    View viewFragment;
    FlexboxLayout flex;
    static int coorXMain,coorYMain;
    int heightInPx=0;
    static boolean isAnimation=true;
    List<BoxMatchDisappearPicture> listBoxMatchDisappear ;
    ArrayList<String> listImageChoices ;
    ArrayList<RelativeLayout> listRelativeLayoutQuestionBox;
    String instruction;
    String type;
    String caseID;
    PicassoHelper picassoHelper = new PicassoHelper();
    ViewFragmentHolder holder;
     ArrayList<View> choicesList;
    View.OnLayoutChangeListener onLayoutChangeListener;
    static  RelativeLayout relativeLayoutUserChoice;
    static  RelativeLayout relativeLayoutUserChoiceTemp;
    static int totalAnswerOnContainerClicked;
    boolean loadUserAnswer = false;
    ViewTreeObserver.OnScrollChangedListener scrollChangedListener;
    static int activityTopHeight;
    boolean isShuffle= false;
    boolean isScrollForPositioning= false;

    public BoxMatchDisappearPictureFragment() {
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
        for(View viewChoices : choicesList){
            ImageView imageViewFrontChoice = (ImageView) viewChoices.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);

            if(imageViewFrontChoice.getVisibility()==View.VISIBLE)
            {
                isFullAnswered= false;
                break;
            }
        }

    }

    @Override
    public void checkAnswer() {
        int idxQuestionCase =0;
        for(RelativeLayout relativeLayoutQuestionBox: listRelativeLayoutQuestionBox){
            FlexboxLayout flexBoxBoxMatchDisappearPictureAnswerContainer
                    = (FlexboxLayout)relativeLayoutQuestionBox.findViewById(R.id.flexBoxBoxMatchDisappearPictureAnswerContainer);
            for(int idxAnswer =0 ; idxAnswer < flexBoxBoxMatchDisappearPictureAnswerContainer.getChildCount();idxAnswer++){
                View viewAnswer = flexBoxBoxMatchDisappearPictureAnswerContainer.getChildAt(idxAnswer);
                ImageView imageViewAnswer = (ImageView) viewAnswer.findViewById(R.id.imageViewBoxMatchDisappearPictureAnswerItem);
                Log.i("tagAnswer", imageViewAnswer.getTag().toString());
                String answer = imageViewAnswer.getTag().toString();
                if(checkAnswerFromModel(answer , idxQuestionCase))
                    imageViewAnswer.setColorFilter(getResources().getColor(R.color.bahaso_transparent_bahaso_blue_case_box));
                else
                    imageViewAnswer.setColorFilter(getResources().getColor(R.color.bahaso__transparent_red_case_box));
            }
            idxQuestionCase++;
        }
    }

    public boolean checkAnswerFromModel(String answer, int idxQuestion){
        for(String modelAnswer: listBoxMatchDisappear.get(idxQuestion).getArrayListAnswer()){
            Log.i("modelAnswer", modelAnswer + " " + idxQuestion);
            if(answer.equals(modelAnswer))
                return true;
        }
        return  false;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    static class ViewFragmentHolder{
        TextView textViewInstruction;
        FlexboxLayout flexBoxBoxMatchDisappearPictureCaseContainer;
        FlexboxLayout flexBoxBoxMatchDisappearPictureCaseChoicesContainer;
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
        listRelativeLayoutQuestionBox = new ArrayList<>();
        if(getArguments()!=null) {
            Log.i("wwzzz22aazz","asdasd");

            listBoxMatchDisappear = getArguments().getParcelableArrayList("listData");


            listImageChoices = getArguments().getStringArrayList("listImageChoices");
            instruction = getArguments().getString("instruction");
            caseID= getArguments().getString("caseID");

            setType(getArguments().getString("type"));
        }

        if(viewFragment==null) {
            viewFragment = inflater.inflate(R.layout.fragment_box_match_disappear_picture, container, false);
            holder = new ViewFragmentHolder();
            holder.textViewInstruction = (TextView) viewFragment.findViewById(R.id.textViewBoxMatchDisappearPictureInstruction);
            holder.flexBoxBoxMatchDisappearPictureCaseChoicesContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxBoxMatchDisappearPictureCaseChoicesContainer);
            holder.flexBoxBoxMatchDisappearPictureCaseContainer
                    = (FlexboxLayout) viewFragment.findViewById(R.id.flexBoxBoxMatchDisappearPictureCaseContainer);
            holder.layoutChoiceShowHide = (LinearLayout) viewFragment.findViewById(R.id.buttonHideDragChoices);
            holder.scrollViewChoice = (ScrollView) viewFragment.findViewById(R.id.scrollViewBoxMatchDisappearPictureCaseDragChoices);

            holder.scrollViewCase= (ScrollView) viewFragment.findViewById(R.id.scrollViewBoxMatchDisappearPictureCaseContainer);

            holder.scrollViewCase.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isScrollForPositioning = false;
                    for(View viewChoice: choicesList){
                        ImageView front = (ImageView) viewChoice.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
                        front.setEnabled(true);
                    }
                    return false;
                }
            });


            holder.relativeLayoutCaseContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutBoxMatchDisappearPictureCaseContainer);
            holder.relativeLayoutContainerDragChoices = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutBoxMatchDisappearPictureContainerDragChoices);
            holder.relativeLayoutCaseSubContainer = (RelativeLayout) viewFragment.findViewById(R.id.relativeLayoutCaseSubContainer);

            viewFragment.setTag(holder);
        }
        else{
            holder = (ViewFragmentHolder) viewFragment.getTag();
        }
        holder.flexBoxBoxMatchDisappearPictureCaseChoicesContainer.removeAllViews();
        holder.textViewInstruction.setText(instruction);

        generateCaseContent(inflater);



        holder.layoutChoiceShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(heightInPx==0)
                    heightInPx = holder.relativeLayoutCaseSubContainer.getHeight();
                if(holder.scrollViewChoice.getVisibility()==View.VISIBLE){
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE,R.id.relativeLayoutBoxMatchDisappearPictureContainerDragChoices);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW,0);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    showHideChoiceLayout(relativeLayoutCaseParams,relativeLayoutChoicesParams,relativeLayoutSubCaseParams, View.GONE);

                }
                else {
                    RelativeLayout.LayoutParams relativeLayoutCaseParams = (RelativeLayout.LayoutParams) holder.relativeLayoutCaseContainer.getLayoutParams();
                    relativeLayoutCaseParams.addRule(RelativeLayout.ABOVE,0);
                    RelativeLayout.LayoutParams relativeLayoutChoicesParams = (RelativeLayout.LayoutParams) holder.relativeLayoutContainerDragChoices.getLayoutParams();
                    relativeLayoutChoicesParams.addRule(RelativeLayout.BELOW,R.id.relativeLayoutBoxMatchDisappearPictureCaseContainer);
                    RelativeLayout.LayoutParams relativeLayoutSubCaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,heightInPx);
                    showHideChoiceLayout(relativeLayoutCaseParams,relativeLayoutChoicesParams,relativeLayoutSubCaseParams, View.VISIBLE);
                }
            }
        });

        loadUserAnswer();
        return viewFragment;
    }

    public void generateCaseContent(LayoutInflater inflater){
        loadQuestionCase(inflater);
        loadChoices(inflater);
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

    public void loadQuestionCase(LayoutInflater inflater){
        if(holder.flexBoxBoxMatchDisappearPictureCaseContainer.getChildCount()>1)
            holder.flexBoxBoxMatchDisappearPictureCaseContainer.removeViews(1,holder.flexBoxBoxMatchDisappearPictureCaseContainer.getChildCount()-1);
        for(int idxCase=0;idxCase<listBoxMatchDisappear.size();idxCase++){
            final View layoutCase = inflater.inflate(R.layout.layout_case_box_match_disappear_picture_case_answer_container,null);
            RelativeLayout relativeLayoutCaseContainer = (RelativeLayout) layoutCase.findViewById(R.id.relativeLayoutCaseContainer);

            relativeLayoutCaseContainer.setTag(idxCase);

            relativeLayoutCaseContainer.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    Log.i("dragStarted3","started "+ v.getHeight());
                    switch (event.getAction()){
                        case DragEvent.ACTION_DRAG_ENTERED:
                            Log.i("flexDrag","dragFlexEntered");
                            break;
                        case DragEvent.ACTION_DROP:
//                            relativeLayoutUserChoice = (RelativeLayout) v;
                            relativeLayoutUserChoiceTemp = (RelativeLayout) v;
                            Log.i("flexDragDROP",v.getClass().getName());
                            isAnimation = false;
                            FlexboxLayout flexboxLayoutAnswerContainer = (FlexboxLayout) layoutCase.findViewById(R.id.flexBoxBoxMatchDisappearPictureAnswerContainer);
                            totalAnswerOnContainerClicked = flexboxLayoutAnswerContainer.getChildCount();
                            addAnswer((RelativeLayout) v);
                            break;
                    }
                    return true;
                }
            });

            relativeLayoutCaseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("clickRelativeLayout","clicked");


                    relativeLayoutUserChoice = (RelativeLayout) v;
                    relativeLayoutUserChoice.setTag(v.getTag());

                    relativeLayoutUserChoiceTemp = (RelativeLayout) v;
                    relativeLayoutUserChoiceTemp.setTag(v.getTag());

                    totalAnswerOnContainerClicked = getUserChoiceLayoutAnswerCount();
                    resetQuestionUserChoice();

                    Log.i("childCountSelected",totalAnswerOnContainerClicked+"");
                    int paddingLeft = relativeLayoutUserChoice.getPaddingLeft();
                    int paddingTop = relativeLayoutUserChoice.getPaddingTop();
                    int paddingRight = relativeLayoutUserChoice.getPaddingRight();
                    int paddingBottom = relativeLayoutUserChoice.getPaddingBottom();
                    resetQuestionUserChoice();
                    relativeLayoutUserChoice.setBackgroundResource(R.drawable.background_box_match_disappear_case_answer_container_selected);
                    relativeLayoutUserChoice.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                }
            });

            if(idxCase==0)
                relativeLayoutCaseContainer.performClick();

            TextView textViewCaseQuestion = (TextView) layoutCase.findViewById(R.id.textViewBoxMatchDisappearPictureCategory);
            String question = listBoxMatchDisappear.get(idxCase).getQuestion();
            textViewCaseQuestion.setText(question);
            listRelativeLayoutQuestionBox.add(relativeLayoutCaseContainer);
            holder.flexBoxBoxMatchDisappearPictureCaseContainer.addView(layoutCase);

        }
    }

    public int getUserChoiceLayoutAnswerCount()
    {
        FlexboxLayout flexBoxBoxMatchDisappearPictureAnswerContainer
                = (FlexboxLayout)relativeLayoutUserChoice.findViewById(R.id.flexBoxBoxMatchDisappearPictureAnswerContainer);
        int flexBoxBoxMatchDisappearPictureAnswerContainerChildCount =
                flexBoxBoxMatchDisappearPictureAnswerContainer.getChildCount();
        return  flexBoxBoxMatchDisappearPictureAnswerContainerChildCount;

    }


    public void resetQuestionUserChoice(){
        for(RelativeLayout layout: listRelativeLayoutQuestionBox){
            int paddingLeft = layout.getPaddingLeft();
            int paddingTop = layout.getPaddingTop();
            int paddingRight = layout.getPaddingRight();
            int paddingBottom = layout.getPaddingBottom();
            layout.setBackgroundResource(R.drawable.background_box_match_disappear_case_answer_container_unselected);
            layout.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);

        }
    }


    public void loadChoices(LayoutInflater inflater){
        if(choicesList==null) {
            choicesList = new ArrayList<>();
            for (int idxImageChoices = 0; idxImageChoices < listImageChoices.size(); idxImageChoices++) {
                View layoutImageView = inflater.inflate(R.layout.layout_case_box_match_disappear_picture_choice_item, null);
                RelativeLayout relativeLayoutImageViewChoicesContainer
                        = (RelativeLayout) layoutImageView.findViewById(R.id.relativeLayoutChoicesContainer);
                ImageView imageViewChoiceItemFront
                        = (ImageView) layoutImageView.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
                ImageView imageViewChoiceItemBack
                        = (ImageView) layoutImageView.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemBack);

//            relativeLayoutImageViewChoicesContainer.setClipChildren(true);
                layoutImageView.setTag(idxImageChoices);
                imageViewChoiceItemFront.setTag(idxImageChoices);
                picassoHelper.loadImage(getContext(), listImageChoices.get(idxImageChoices), imageViewChoiceItemFront);

                picassoHelper.loadImage(getContext(), listImageChoices.get(idxImageChoices), imageViewChoiceItemBack);


                addDragClickChoicesListener(relativeLayoutImageViewChoicesContainer, imageViewChoiceItemFront);


                grayScaleImageView(imageViewChoiceItemBack);

                choicesList.add(layoutImageView);
            }

            Log.i("shuffle", "shuffle2");
            Collections.shuffle(choicesList);
            isShuffle = true;
        }
        for(View layoutImageView : choicesList){
            holder.flexBoxBoxMatchDisappearPictureCaseChoicesContainer.addView(layoutImageView);
        }
    }

    public void grayScaleImageView(ImageView imageView){
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        imageView.setColorFilter(new ColorMatrixColorFilter(cm));

    }



    public void addDragClickChoicesListener(final RelativeLayout relativeLayoutImageViewChoicesContainer,
                                            final ImageView imageViewChoiceItemFront)
    {
        imageViewChoiceItemFront.setOnTouchListener(new View.OnTouchListener() {
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
                                imageViewChoiceUserChoice = (ImageView) v;
                            }

                            break;
                    }
                    break;
                }

                return false;

            }
        });
        try {
            imageViewChoiceItemFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableAllChoice(false);
//                    if(isScrollForPositioning)
//                        return;
                    Log.i("wew214",isScrollForPositioning+"");

                    imageViewChoiceUserChoice = imageViewChoiceItemFront;
                    Log.i("TESS 22","sad");
                    int minYAddedLayout , maxYAddedlayout;
                    float choosenLayoutYMin,choosenLayoutYMax;
                    float tempPosXSelectedRelativeLayout;
                    Log.i("tessHeight",holder.textViewInstruction.getHeight()+"");
                    relativeLayoutUserChoiceTemp = relativeLayoutUserChoice;

                    totalAnswerOnContainerClicked = getUserChoiceLayoutAnswerCount();


                    int startAfterTextInstruction = holder.textViewInstruction.getHeight();
                    isAnimation = true;
                    if(scrollChangedListener==null)
                    {
//                        final int maxScrollPos = holder.scrollViewCase.getChildAt(0).getHeight()-holder.scrollViewCase.getHeight();
                        scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
//                                isScrollForPositioning = true;
                                final int maxScrollPos = holder.scrollViewCase.getChildAt(0).getHeight()-holder.scrollViewCase.getHeight();

                                Log.i("holder",holder.scrollViewCase.getScrollY()+"");

                                if(!isScrollForPositioning)
                                    return;

                                if(layoutPosForScrolled<=0)
                                {
                                    isScrollForPositioning = false;

                                    addAnswer(relativeLayoutUserChoice);
                                    layoutPosForScrolled = -1;
                                    return;
                                }

                                if(holder.scrollViewCase.getScrollY()==layoutPosForScrolled || holder.scrollViewCase.getScrollY()>=maxScrollPos) {
                                    isScrollForPositioning = false;

                                    addAnswer(relativeLayoutUserChoice);
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
                        choosenLayoutYMin = ((View)relativeLayoutUserChoiceTemp.getParent()).getY();
                        choosenLayoutYMax = choosenLayoutYMin+ ((View)relativeLayoutUserChoiceTemp.getParent()).getHeight();

                        if(posXSelectedRelativeLayout==0)
                            posXSelectedRelativeLayout = ((View)relativeLayoutUserChoiceTemp.getParent()).getX();

                        tempPosXSelectedRelativeLayout= ((View)relativeLayoutUserChoiceTemp.getParent()).getX();
                        Log.i("minYAddedLayout",minYAddedLayout+"");
                        Log.i("choosenLayoutYMin",choosenLayoutYMin+"");

                        Log.i("maxYAddedLayout",maxYAddedlayout+"");
                        Log.i("choosenLayoutYMax",choosenLayoutYMax+"");
//                        Log.i("scViewHeight",+"");
                        if (choosenLayoutYMin<minYAddedLayout || choosenLayoutYMin>maxYAddedlayout || choosenLayoutYMax<minYAddedLayout || choosenLayoutYMax>maxYAddedlayout) {
                            float diff = choosenLayoutYMax-maxYAddedlayout;
                            if(diff>=-15 && diff<=15)
                            {
//                                if()
                                addAnswer(relativeLayoutUserChoice);
                                return;
                            }
//
//                            if(diff<10)
//                                diff=0;



//                            if(posXSelectedRelativeLayout!=tempPosXSelectedRelativeLayout)
//                                posXSelectedRelativeLayout=tempPosXSelectedRelativeLayout;

                            Log.i("changesScroll",(choosenLayoutYMax-maxYAddedlayout)+"");


                                layoutPosForScrolled = startAfterTextInstruction==choosenLayoutYMin?0: (int) (minYAddedLayout+diff+10);

//                            v.setEnabled(false);
                            isScrollForPositioning = true;
                            Log.i("wew213","asdasd");
                                holder.scrollViewCase.smoothScrollTo(0, startAfterTextInstruction==choosenLayoutYMin?0: (int) (minYAddedLayout+diff+10));
                        }
                        else
                        {
                            addAnswer(relativeLayoutUserChoice);
                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void startAnimation(int posX,int posY,final View viewDestination , final View layoutAnimatedImageView)
    {

        int coordinateDestination[] = new int[2];

        layoutAnimatedImageView.setX(posX);
        layoutAnimatedImageView.setY(posY-activityTopHeight);
        layoutAnimatedImageView.setVisibility(View.VISIBLE);


        viewDestination.getLocationInWindow(coordinateDestination);
        Log.i("posXDest", "X = " + coordinateDestination[0]+"");
        Log.i("posYDest", "Y = " +coordinateDestination[1]+"");

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

    public void enableAllChoice(Boolean isChoicesEnabled)
    {
        for(View viewChoice: choicesList){
            ImageView front = (ImageView) viewChoice.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
            front.setEnabled(isChoicesEnabled);
        }
    }


    public View addAnswer(final RelativeLayout addedLayout)
    {
        final FlexboxLayout flexBoxBoxMatchDisappearPictureAnswerContainer
                = (FlexboxLayout)addedLayout.findViewById(R.id.flexBoxBoxMatchDisappearPictureAnswerContainer);
//        if(onHierarchyChangeListener==null) {
//            onHierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
//                @Override
//                public void onChildViewAdded(View view, View view1) {
//                    Log.i("huehue","huehuehu1");
//
//                    for(int ii=0; ii< ((FlexboxLayout)view).getChildCount();ii++)
//                    {
//                        Log.i("tagNICE",((FlexboxLayout)view).getChildAt(ii).getTag()+"");
//                        Log.i("tagCOORX",((FlexboxLayout)view).getChildAt(ii).getX()+"");
//                        Log.i("tagCOORY",((FlexboxLayout)view).getChildAt(ii).getY()+"");
//
//
//                    }
//
//                    FlexboxLayout flexboxLayoutUserSelected = (FlexboxLayout) view;
//                    int totalChildAfterChangeLayout = flexboxLayoutUserSelected.getChildCount();
//                    Log.i("hueaaa",totalChildAfterChangeLayout+"");
//                    enableAllChoice(true);
//                    int coorViewFragment[] = new int[2];
//                        viewFragment.getLocationOnScreen(coorViewFragment);
//                        activityTopHeight = coorViewFragment[1];
//                        View layoutAnimatedImageView = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_disappear_picture_answer_animation,null);
//                        ImageView imageViewAnimated = (ImageView) layoutAnimatedImageView.findViewById(R.id.imageViewAnimated);
//                        RelativeLayout relativeLayoutFragment = (RelativeLayout) viewFragment;
//                        picassoHelper.loadImage(getContext(),
//                                listImageChoices.get( Integer.parseInt(imageViewChoiceUserChoice.getTag().toString())), imageViewAnimated);
//
//                        View viewDestination = flexboxLayoutUserSelected.getChildAt(totalChildAfterChangeLayout - 1);
//                        Log.i("isDragTESS",isAnimation+" tes");
//                        Log.i("tagTEST",viewDestination .getTag()+"");
//                    int coordinateDestinations[] = new int[2];
//                    viewDestination.getLocationOnScreen(coordinateDestinations);
//                    Log.i("COORTESTX",coordinateDestinations[0]+"");
//                    Log.i("COORTESTY",coordinateDestinations[1]+"");
//
//                    int coorUserChoice[] = new int[2];
//                        imageViewChoiceUserChoice.getLocationOnScreen(coorUserChoice);
//                        posXSPawnAnimaton = coorUserChoice[0];
//                        posYSpawnAnimation = coorUserChoice[1];
//                        if(isAnimation) {
//                            relativeLayoutFragment.addView(layoutAnimatedImageView);
//
//                            startAnimation(posXSPawnAnimaton, posYSpawnAnimation, viewDestination, layoutAnimatedImageView);
//                            imageViewChoiceUserChoice.setVisibility(View.GONE);
//                        }
//                        else{
//                            viewDestination.setVisibility(View.VISIBLE);
//                            imageViewChoiceUserChoice.setVisibility(View.GONE);
//                            isAnimation  = true;
//                        }
////                        listBoxMatchDisappear.get(Integer.parseInt(imageViewChoiceUserChoice.getTag().toString())).getArrayListUserAnswer().add()
//                        Log.i("answerAddedIdx",imageViewChoiceUserChoice.getTag()+"");
//                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
//                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
//                        String answerIdx = imageViewChoiceUserChoice.getTag().toString();
//                        if(!loadUserAnswer) {
//                            listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().add(answerIdx);
//                        }
//                        else
//                            loadUserAnswer = false;
//                        for(int idx=0;idx<listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
//                            Log.i("userAnswerList",listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().get(idx));
//                        }
//                    checkFullAnswered();
//                    totalAnswerOnContainerClicked = totalChildAfterChangeLayout;
//                }
//
//                @Override
//                public void onChildViewRemoved(View view, View view1) {
//                    FlexboxLayout flexboxLayoutUserSelected = (FlexboxLayout) view;
//                    int totalChildAfterChangeLayout = flexboxLayoutUserSelected.getChildCount();
//                    Log.i("answerDeletedIdx",imageViewChoiceUserChoice.getTag()+"");
//                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
//                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
//                        String answerIdx = imageViewChoiceUserChoice.getTag().toString();
//                        listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().remove(answerIdx);
//                        for(int idx=0;idx<listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
//                            Log.i("userAnswerList",listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().get(idx));
//                        }
//                    checkFullAnswered();
//                    totalAnswerOnContainerClicked = totalChildAfterChangeLayout;
//                }
//            };
////
//        }
        if(onLayoutChangeListener==null) {
            onLayoutChangeListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    Log.i("huehue2","huehuehu2");

                    FlexboxLayout flexboxLayoutUserSelected = (FlexboxLayout) v;
                    int totalChildAfterChangeLayout = flexboxLayoutUserSelected.getChildCount();
                    enableAllChoice(true);

                    Log.i("totalChildAfterChange",totalChildAfterChangeLayout +" " +  totalAnswerOnContainerClicked);

                    if(totalChildAfterChangeLayout> totalAnswerOnContainerClicked){
                        int coorViewFragment[] = new int[2];
                        viewFragment.getLocationOnScreen(coorViewFragment);
                        activityTopHeight = coorViewFragment[1];
                        View layoutAnimatedImageView = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_disappear_picture_answer_animation,null);
                        ImageView imageViewAnimated = (ImageView) layoutAnimatedImageView.findViewById(R.id.imageViewAnimated);
                        RelativeLayout relativeLayoutFragment = (RelativeLayout) viewFragment;
                        picassoHelper.loadImage(getContext(),
                                listImageChoices.get( Integer.parseInt(imageViewChoiceUserChoice.getTag().toString())), imageViewAnimated);

                        View viewDestination = flexboxLayoutUserSelected.getChildAt(totalChildAfterChangeLayout - 1);

                        int coordinateDestinations[] = new int[2];
                        viewDestination.getLocationOnScreen(coordinateDestinations);
                        Log.i("COORTESTX",coordinateDestinations[0]+"");
                        Log.i("COORTESTY",coordinateDestinations[1]+"");

                        Log.i("isDragTESS",isAnimation+" tes");
                        int coorUserChoice[] = new int[2];
                        imageViewChoiceUserChoice.getLocationOnScreen(coorUserChoice);
                        posXSPawnAnimaton = coorUserChoice[0];
                        posYSpawnAnimation = coorUserChoice[1];
                        if(isAnimation) {
                            relativeLayoutFragment.addView(layoutAnimatedImageView);

                            startAnimation(posXSPawnAnimaton, posYSpawnAnimation, viewDestination, layoutAnimatedImageView);
                            imageViewChoiceUserChoice.setVisibility(View.GONE);
                        }
                        else{
                            viewDestination.setVisibility(View.VISIBLE);
                            imageViewChoiceUserChoice.setVisibility(View.GONE);
                            isAnimation  = true;
                        }
//                        listBoxMatchDisappear.get(Integer.parseInt(imageViewChoiceUserChoice.getTag().toString())).getArrayListUserAnswer().add()
                        Log.i("answerAddedIdx",imageViewChoiceUserChoice.getTag()+"");
                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
                        String answerIdx = imageViewChoiceUserChoice.getTag().toString();
                        if(!loadUserAnswer) {
                            listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().add(answerIdx);
                        }
                        else
                            loadUserAnswer = false;
                        for(int idx=0;idx<listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
                            Log.i("userAnswerList",listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().get(idx));
                        }
                    }
                    else
                    {
                        Log.i("answerDeletedIdx",imageViewChoiceUserChoice.getTag()+"");
                        Log.i("userChoiceLayout",relativeLayoutUserChoiceTemp.getTag()+"");
                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutUserChoiceTemp.getTag().toString());
                        String answerIdx = imageViewChoiceUserChoice.getTag().toString();
                        listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().remove(answerIdx);
                        for(int idx=0;idx<listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().size();idx++){
                            Log.i("userAnswerList",listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().get(idx));
                        }

                    }
                    checkFullAnswered();
                    totalAnswerOnContainerClicked = totalChildAfterChangeLayout;
                }
            };
        }
//        flexBoxBoxMatchDisappearPictureAnswerContainer.setOnHierarchyChangeListener(onHierarchyChangeListener);

        flexBoxBoxMatchDisappearPictureAnswerContainer.addOnLayoutChangeListener(onLayoutChangeListener);

        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_disappear_picture_answer_item, null);
        final ImageView imageViewBoxMatchDisappearPictureAnswerItem
                = (ImageView) layoutAnswerItem.findViewById(R.id.imageViewBoxMatchDisappearPictureAnswerItem);

        imageViewBoxMatchDisappearPictureAnswerItem.setImageDrawable(imageViewChoiceUserChoice.getDrawable());
        layoutAnswerItem.setTag(imageViewChoiceUserChoice.getTag());
        imageViewBoxMatchDisappearPictureAnswerItem.setTag(imageViewChoiceUserChoice.getTag());

        imageViewBoxMatchDisappearPictureAnswerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                totalAnswerOnContainerClicked = ((FlexboxLayout)v.getParent().getParent()).getChildCount();
                String imageViewIdx = v.getTag().toString();
                for (int idxChoices=0;idxChoices<choicesList.size();idxChoices++)
                {
                    String sourceLayoutImageViewIdx =choicesList.get(idxChoices).getTag().toString();
                    if(sourceLayoutImageViewIdx.equals(imageViewIdx)){
                        View viewChoicesList = choicesList.get(idxChoices);
                        imageViewChoiceUserChoice
                                = (ImageView) viewChoicesList.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
                        int coorAnswer[] = new int[2];
                        v.getLocationOnScreen(coorAnswer);
                        posXSPawnAnimaton = coorAnswer[0];
                        posYSpawnAnimation = coorAnswer[1];
                        relativeLayoutUserChoiceTemp = addedLayout;
                        imageViewChoiceUserChoice.setVisibility(View.VISIBLE);

                        flexBoxBoxMatchDisappearPictureAnswerContainer.removeView((View) v.getParent());
                        totalAnswerOnContainerClicked = flexBoxBoxMatchDisappearPictureAnswerContainer.getChildCount();

                        break;
                    }
                }
            }
        });


        flexBoxBoxMatchDisappearPictureAnswerContainer.addView(layoutAnswerItem);

//        Log.i("userAnswerList233","asd");

        layoutAnswerItem.setVisibility(View.INVISIBLE);

        return flexBoxBoxMatchDisappearPictureAnswerContainer.getChildAt(flexBoxBoxMatchDisappearPictureAnswerContainer.getChildCount()-1);
    }

    public void loadUserAnswer()
    {
        for(int idxCase=0;idxCase<listBoxMatchDisappear.size();idxCase++)
        {
            for(int idxUserAnswer=0;idxUserAnswer<listBoxMatchDisappear.get(idxCase).getArrayListUserAnswer().size();idxUserAnswer++)
            {
                String userAnswer = listBoxMatchDisappear.get(idxCase).getArrayListUserAnswer().get(idxUserAnswer);
                for(View choice: choicesList)
                {
                    if(choice.getTag().toString().equals(userAnswer))
                    {

                        imageViewChoiceUserChoice = (ImageView) choice.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
//                        Log.i("userAnswerList222","afterrs");
                        final RelativeLayout relativeLayoutLoadAnswer = listRelativeLayoutQuestionBox.get(idxCase);

                        final FlexboxLayout flexBoxBoxMatchDisappearPictureAnswerContainer
                                = (FlexboxLayout)relativeLayoutLoadAnswer.findViewById(R.id.flexBoxBoxMatchDisappearPictureAnswerContainer);
                        View layoutAnswerItem = getLayoutInflater(null).inflate(R.layout.layout_case_box_match_disappear_picture_answer_item, null);
                        final ImageView imageViewBoxMatchDisappearPictureAnswerItem
                                = (ImageView) layoutAnswerItem.findViewById(R.id.imageViewBoxMatchDisappearPictureAnswerItem);

                        imageViewBoxMatchDisappearPictureAnswerItem.setImageDrawable(imageViewChoiceUserChoice.getDrawable());

                        imageViewBoxMatchDisappearPictureAnswerItem.setTag(imageViewChoiceUserChoice.getTag());

                        imageViewBoxMatchDisappearPictureAnswerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                totalAnswerOnContainerClicked = ((FlexboxLayout)v.getParent().getParent()).getChildCount();
                                String imageViewIdx = v.getTag().toString();
                                for (int idxChoices=0;idxChoices<choicesList.size();idxChoices++)
                                {
                                    String sourceLayoutImageViewIdx =choicesList.get(idxChoices).getTag().toString();
                                    if(sourceLayoutImageViewIdx.equals(imageViewIdx)){
                                        View viewChoicesList = choicesList.get(idxChoices);
                                        imageViewChoiceUserChoice
                                                = (ImageView) viewChoicesList.findViewById(R.id.imageViewBoxMatchDisappearPictureChoiceItemFront);
                                        int coorAnswer[] = new int[2];
                                        v.getLocationOnScreen(coorAnswer);
                                        posXSPawnAnimaton = coorAnswer[0];
                                        posYSpawnAnimation = coorAnswer[1];
//                                        relativeLayoutUserChoiceTemp = relativeLayoutUserChoiceTemp;
                                        flexBoxBoxMatchDisappearPictureAnswerContainer.removeView((View) v.getParent());
                                        int idxAddedRelativeLayout = Integer.parseInt(relativeLayoutLoadAnswer.getTag().toString());
                                        String answerIdx = imageViewChoiceUserChoice.getTag().toString();
                                        imageViewChoiceUserChoice.setVisibility(View.VISIBLE);

                                        listBoxMatchDisappear.get(idxAddedRelativeLayout).getArrayListUserAnswer().remove(answerIdx);
                                        checkFullAnswered();

                                        break;
                                    }
                                }
                            }
                        });

                        flexBoxBoxMatchDisappearPictureAnswerContainer.addView(layoutAnswerItem);
                        layoutAnswerItem.setVisibility(View.VISIBLE);
                        imageViewChoiceUserChoice.setVisibility(View.GONE);

                    }
                }
            }
        }
    }




}
