package com.bahaso.alltypecasetesting.type;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bahaso.R;
import com.bahaso.adapter.ViewPagerAdapterCase;
import com.bahaso.fragmenthelper.FragmentBahaso;

import com.bahaso.globalvar.GlobalVar;
import com.bahaso.model.BoxMatchDisappear;
import com.bahaso.model.BoxMatchDisappearPicture;
import com.bahaso.model.BoxMatchList;
import com.bahaso.model.BoxMatchParagraph;
import com.bahaso.model.BoxMatchPicture;
import com.bahaso.model.LittleBoxSelection;
import com.bahaso.model.LostWordType;
import com.bahaso.model.SentenceFormation;
import com.bahaso.typecase.BoxMatchDisappearFragment;
import com.bahaso.typecase.BoxMatchDisappearPictureFragment;
import com.bahaso.typecase.BoxMatchListFragment;
import com.bahaso.typecase.BoxMatchParagraphFragment;
import com.bahaso.typecase.BoxMatchPictureFragment;
import com.bahaso.typecase.EmptyFragmentForTesting;
import com.bahaso.typecase.LittleBoxSelectionFragment;
import com.bahaso.typecase.LostWordTypeFragment;
import com.bahaso.typecase.SentenceFormationFragment;
import com.bahaso.util.AlertDialogYesNoSimple;
import com.bahaso.util.BoxMatchDisappearHelper;
import com.bahaso.util.BoxMatchDisappearPictureHelper;
import com.bahaso.util.BoxMatchListHelper;
import com.bahaso.util.BoxMatchParagraphHelper;
import com.bahaso.util.BoxMatchPictureHelper;
import com.bahaso.util.LittleBoxSelectionHelper;
import com.bahaso.util.LostWordTypeHelper;
import com.bahaso.util.SentenceFormationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroAndGeneralCaseActivity extends AppCompatActivity {

    private ArrayList<String> listTypeCaseForHeader;
    private TextView txtIntroGeneralCaseType;
    private LinearLayout layoutCaseControl;
    private View viewCaseNumberButton;
    private LayoutInflater layoutInflater;
    private static int idxCaseBtn;
    private CountDownTimer countDownTimer;
    private TextView txtCountdownTimer;
    private ViewPager viewPagerCase;
    private List<FragmentBahaso> listCaseFragment;
    private static String timerFormatted;
    private static DateFormat dateFormat;
    ArrayList<Integer> arrayListPageList = new ArrayList<>();
    private ImageButton buttonExit;
    int beforePage = 0;
    static ViewPager.OnPageChangeListener pageChangeListener;
    public static int currentPage = 0;
    String placementTestId = "";
    int durationPlacement = 0;
    String token;
    private static SharedPreferences sharedpref;
    private String instruction = "";
    static ImageButton btnkCaseCheck;
    HorizontalScrollView scrollView;
    int btnWidth = 0;
    int btnHeight = 0;
    int caseNumBtnLayoutWidth = 0;
    private ProgressDialog pDialog;
    LinearLayout layoutCasePlacement;
    LinearLayout linearLayoutWrongCorrectAnswer;

    private void showPDialog() {
        pDialog = new ProgressDialog(this, R.style.MyProgressTheme);
        pDialog.setMessage("Loading... Please Wait");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public static void disableCheckButton() {
        btnkCaseCheck.setEnabled(false);
    }

    public static void enableCheckButton() {
        btnkCaseCheck.setEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listTypeCaseForHeader = new ArrayList<>();
        super.onCreate(savedInstanceState);
        listCaseFragment = new ArrayList<>();
        setContentView(R.layout.activity_intro_and_general_case);
        layoutCasePlacement = (LinearLayout) findViewById(R.id.activityCasePlacementContainer);
        linearLayoutWrongCorrectAnswer = (LinearLayout) findViewById(R.id.linearLayoutWrongCorrectAnswer);

        layoutCasePlacement.setVisibility(View.INVISIBLE);


        token = getSharedPref().getString(getString(R.string.tokenlogin), "");
//        placementTestId = getIntent().getExtras().getString("placementId");
        Log.i("tokens", token + "");
        txtIntroGeneralCaseType = (TextView) findViewById(R.id.txtIntroGeneralCaseType);
        btnkCaseCheck = (ImageButton) findViewById(R.id.btnCaseCheck);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadCaseFragment();

        btnkCaseCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard();
//                To check answer and show correct or wrong case message

                FragmentBahaso fragmentCurrent = listCaseFragment.get(currentPage);
                if (fragmentCurrent.mainType.equals("case")) {

                    if (!fragmentCurrent.getIsFullAnswered()) {
                        Toast.makeText(IntroAndGeneralCaseActivity.this, "Please Fill All Answer", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragmentCurrent.setChecked(true);
                    fragmentCurrent.checkAnswer();
                    if (fragmentCurrent.getIsChecked()) {
                        linearLayoutWrongCorrectAnswer.setVisibility(View.VISIBLE);
                        linearLayoutWrongCorrectAnswer.removeAllViews();
                        if (fragmentCurrent.getIsCaseCorrect()) {
                            showCorrectCaseMessage();
                        } else {
                            showWrongCaseMessage();
                        }
                    }
                } else
                    viewPagerCase.setCurrentItem(currentPage + 1);
            }
        });


    }

    public void showWrongCaseMessage() {
        View caseWrongMessage = layoutInflater.inflate(R.layout.layout_case_wrong_message_chance, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayoutWrongCorrectAnswer.addView(caseWrongMessage, params);
    }

    public void showCorrectCaseMessage() {
        View caseCorrectMessage = layoutInflater.inflate(R.layout.layout_case_correct_message, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayoutWrongCorrectAnswer.addView(caseCorrectMessage, params);
    }

    @Override
    public void onBackPressed() {

//        showDialog();
        this.finish();
    }

    public void showDialog() {
        //                Toast.makeText(ActivityCasePlacement.this, "Toast", Toast.LENGTH_SHORT).show();
        AlertDialogYesNoSimple yesNoSimpleAlertDialog = new AlertDialogYesNoSimple(IntroAndGeneralCaseActivity.this, "Keluar", "Apakah Anda Ingin Keluar?", "Batal", "Keluar");
//                AlertDialog yesNoSimpleAlertDialog = new AlertDialog(getApplicationContext());
        yesNoSimpleAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        yesNoSimpleAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        yesNoSimpleAlertDialog.show();
    }

    public void refreshCaseButton() {
        LinearLayout layoutCaseNumber;
        Button btnCaseNumber;
        Log.i("caseButtonLength", layoutCaseControl.getChildCount() + "");

        for (int idxLayoutCaseControl = 0; idxLayoutCaseControl < layoutCaseControl.getChildCount(); idxLayoutCaseControl++) {

            View viewCaseButton = layoutCaseControl.getChildAt(idxLayoutCaseControl);
            layoutCaseNumber = (LinearLayout) viewCaseButton.findViewById(R.id.layoutCaseNumber);
            if (btnWidth <= 0 && btnHeight <= 0) {
                btnWidth = layoutCaseNumber.getWidth();
                btnHeight = layoutCaseNumber.getHeight();

            }
            btnCaseNumber = (Button) viewCaseButton.findViewById(R.id.btnCaseNumber);
            if (idxLayoutCaseControl == currentPage) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnWidth + 5, btnHeight + 5);
                layoutCaseNumber.setLayoutParams(params);
                btnCaseNumber.setBackground(getResources().getDrawable(R.drawable.circle_blue));
            } else {
                Log.i("ggghh", "asdasdasd");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnWidth - 5, btnHeight - 5);
                layoutCaseNumber.setLayoutParams(params);
                if (listCaseFragment.get(idxLayoutCaseControl).getIsFullAnswered())
                    btnCaseNumber.setBackground(getResources().getDrawable(R.drawable.circle_blue));
                else
                    btnCaseNumber.setBackground(getResources().getDrawable(R.drawable.circle_lightblue));
            }
        }
    }

    public void loadComponent() {
        buttonExit = (ImageButton) findViewById(R.id.buttonExitCase);

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        layoutCaseControl = (LinearLayout) findViewById(R.id.layoutCaseNumberBtnContainer);
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadCaseData();

        viewPagerCase = (ViewPager) findViewById(R.id.pagerCase);
        txtCountdownTimer = (TextView) findViewById(R.id.txtActivityCaseTimer);
        ViewPagerAdapterCase adapterCase = new ViewPagerAdapterCase(getSupportFragmentManager(), listCaseFragment.size(), listCaseFragment);

        viewPagerCase.setAdapter(adapterCase);

        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                linearLayoutWrongCorrectAnswer.setVisibility(View.GONE);
                linearLayoutWrongCorrectAnswer.removeAllViews();
                if (listCaseFragment.size() <= 0)
                    return;
                arrayListPageList.add(position);

                txtIntroGeneralCaseType.setText(listTypeCaseForHeader.get(position));

                currentPage = position;
                if (arrayListPageList.size() > 1)
                    beforePage = arrayListPageList.get(arrayListPageList.size() - 1 - 1);
                if (currentPage > beforePage) {
                    Log.i("direction", "next");

                } else if (currentPage < beforePage) {
                    Log.i("direction2", "prev");

                }
                scrollView = (HorizontalScrollView) findViewById(R.id.caseNumberButtonScrollView);

                Log.i("scView", scrollView.getWidth() + "");
                LinearLayout layoutCaseNumber;

                int widthButton = 0;

                for (int idxLayoutCaseControl = 0; idxLayoutCaseControl < layoutCaseControl.getChildCount(); idxLayoutCaseControl++) {
                    View viewCaseButton = layoutCaseControl.getChildAt(idxLayoutCaseControl);
                    layoutCaseNumber = (LinearLayout) viewCaseButton.findViewById(R.id.layoutCaseNumber);
                    Log.i("scView2", viewCaseButton.getWidth() + "");
                    if (caseNumBtnLayoutWidth == 0)
                        caseNumBtnLayoutWidth = viewCaseButton.getWidth();
                    widthButton = viewCaseButton.getWidth();
                }

                Log.i("scrollX", scrollView.getScrollX() + "");
                int totalCaseNumBtn = 0;
                try {
                    totalCaseNumBtn = scrollView.getWidth() / caseNumBtnLayoutWidth;
                } catch (Exception e) {
                    return;
                }
                refreshCaseButton();

//                if(position<totalCaseNumBtn) {
//                    if (totalCaseNumBtn % 2 == 0) {
//
//                    } else {
//
//                    }
//                }

                Log.i("totalCaseNumBtn ", totalCaseNumBtn + "");

                scrollView.smoothScrollTo((position - (totalCaseNumBtn / 2)) * caseNumBtnLayoutWidth, 0);
                hideSoftKeyboard();


//                Log.i("curPage",position+"");
//                Log.i("befPage",beforePage+"");
//                Log.i("befPage2",listCaseFragment.get(position)+"");
//                FragmentBahaso fVidBef = listCaseFragment.get(beforePage);
//                fVidBef.pauseVideo();
//
//                FragmentBahaso fVidCurr = listCaseFragment.get(position);
//                fVidCurr.playVideo();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPagerCase.addOnPageChangeListener(pageChangeListener);

        viewPagerCase.post(new Runnable() {
            @Override
            public void run() {
                pageChangeListener.onPageSelected(0);
            }
        });
//        createTimer(durationPlacement*1000);
//        countDownTimer.start();
    }

    public final void hideSoftKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            new CountDownTimer(200, 100) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    imm.hideSoftInputFromWindow(viewPagerCase.getWindowToken(), 0);
                }

            }.start();
        } else {

        }
    }

    private void createLittleBoxSelectionCase(JSONObject caseObject, String caseID) throws JSONException {
        LittleBoxSelectionHelper littleBoxSelectionHelper = new LittleBoxSelectionHelper();
        LittleBoxSelection littleBoxSelection;
        ArrayList<ArrayList<String>> arListChoices = new ArrayList<>();
        ArrayList<String> arChoices = new ArrayList<>();
        ArrayList<String> arAnswer = new ArrayList<>();

        JSONObject littleBoxSelectionCaseObject = caseObject;
        JSONArray arrayQuestions = littleBoxSelectionCaseObject.getJSONArray("questions");
        JSONArray arrayChoices = littleBoxSelectionCaseObject.getJSONArray("choices");
        JSONArray arrayAnswer = littleBoxSelectionCaseObject.getJSONArray("bahaso_answers");

        instruction = littleBoxSelectionCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayQuestions.length(); idxArrayCase++) {
            String question = arrayQuestions.getString(idxArrayCase);
            JSONArray answerArray = arrayAnswer.getJSONArray(idxArrayCase);
            arListChoices = new ArrayList<>();
            arAnswer = new ArrayList<>();
            for (int idxAnsArray = 0; idxAnsArray < answerArray.length(); idxAnsArray++) {
                String answer = arrayAnswer.getJSONArray(idxArrayCase).getString(idxAnsArray);
                arAnswer.add(answer);
            }

            for (int idxChoiceRow = 0; idxChoiceRow < arrayChoices.getJSONArray(idxArrayCase).length(); idxChoiceRow++) {
                arChoices = new ArrayList<>();
                for (int idxChoiceCol = 0; idxChoiceCol < arrayChoices.getJSONArray(idxArrayCase).getJSONArray(idxChoiceRow).length(); idxChoiceCol++) {
                    String choices = arrayChoices.getJSONArray(idxArrayCase).getJSONArray(idxChoiceRow).getString(idxChoiceCol);
                    arChoices.add(choices);
                }
                arListChoices.add(arChoices);
            }
            littleBoxSelection = new LittleBoxSelection(question, arListChoices, arAnswer);
            littleBoxSelectionHelper.add(littleBoxSelection);
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "Little Box Multiple Choice");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) littleBoxSelectionHelper.getListData());
        FragmentBahaso myFrag = new LittleBoxSelectionFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    private void createLostWordTypeCase(JSONObject caseObject, String caseID) throws JSONException {
        LostWordTypeHelper lostWordTypeHelper = new LostWordTypeHelper();
        LostWordType lostWordType;
        ArrayList<String> arrayListAnswer = new ArrayList<>();

        JSONObject lostWordTypeCaseObject = caseObject;

        JSONArray arrayQuestions = lostWordTypeCaseObject.getJSONArray("questions");
        JSONArray arrayAnswer = lostWordTypeCaseObject.getJSONArray("bahaso_answers");

        instruction = lostWordTypeCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayQuestions.length(); idxArrayCase++) {
            String question = arrayQuestions.getString(idxArrayCase);
            JSONArray answer = arrayAnswer.getJSONArray(idxArrayCase);
            arrayListAnswer = new ArrayList<>();
            for (int idxAnswer = 0; idxAnswer < answer.length(); idxAnswer++) {
                arrayListAnswer.add(answer.getString(idxAnswer));
            }
            lostWordType = new LostWordType(question, arrayListAnswer);
            lostWordTypeHelper.add(lostWordType);
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) lostWordTypeHelper.getListData());
        FragmentBahaso myFrag = new LostWordTypeFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    private void createBoxMatchDisappearPictureCase(JSONObject caseObject, String caseID) throws JSONException {

        BoxMatchDisappearPictureHelper boxMatchDisappearPictureHelper = new BoxMatchDisappearPictureHelper();
        BoxMatchDisappearPicture boxMatchDisappearPicture;
        ArrayList<String> arrayListAnswer = new ArrayList<>();

        JSONObject boxMatchDisappearCaseObject = caseObject;

        JSONArray arrayQuestions = boxMatchDisappearCaseObject.getJSONArray("questions");
        JSONArray arrayAnswer = boxMatchDisappearCaseObject.getJSONArray("bahaso_answers");
        JSONArray arrayImageChoices = boxMatchDisappearCaseObject.getJSONArray("image_srcs");

        instruction = boxMatchDisappearCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayQuestions.length(); idxArrayCase++) {
            String question = arrayQuestions.getString(idxArrayCase);
            JSONArray answer = arrayAnswer.getJSONArray(idxArrayCase);
            arrayListAnswer = new ArrayList<>();
            for (int idxAnswer = 0; idxAnswer < answer.length(); idxAnswer++) {
                arrayListAnswer.add(answer.getString(idxAnswer));
            }
            boxMatchDisappearPicture = new BoxMatchDisappearPicture(question, arrayListAnswer);
            boxMatchDisappearPictureHelper.add(boxMatchDisappearPicture);
        }

        for (int idxArrayCaseImage = 0; idxArrayCaseImage < arrayImageChoices.length(); idxArrayCaseImage++) {
            String imageSrc = arrayImageChoices.getString(idxArrayCaseImage);
            boxMatchDisappearPictureHelper.addImageString(imageSrc);

        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) boxMatchDisappearPictureHelper.getListData());
        bundle.putStringArrayList("listImageChoices", (ArrayList<String>) boxMatchDisappearPictureHelper.getListImageChoices());

        FragmentBahaso myFrag = new BoxMatchDisappearPictureFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    private void createBoxMatchParagraphCase(JSONObject caseObject, String caseID) throws JSONException {
        BoxMatchParagraphHelper boxMatchParagraphHelper = new BoxMatchParagraphHelper();
        BoxMatchParagraph boxMatchParagraph;

        JSONObject boxMatchParagraphCaseObject = caseObject;

        String question = boxMatchParagraphCaseObject.getString("question");
        JSONArray arrayAnswer = boxMatchParagraphCaseObject.getJSONArray("bahaso_answers");
        JSONArray arrayChoices = boxMatchParagraphCaseObject.getJSONArray("choices");

        instruction = boxMatchParagraphCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayAnswer.length(); idxArrayCase++) {
            String answer = arrayAnswer.getString(idxArrayCase);
            boxMatchParagraph = new BoxMatchParagraph(answer);
            boxMatchParagraphHelper.add(boxMatchParagraph);
        }

        for (int idxArrayCaseChoice = 0; idxArrayCaseChoice < arrayChoices.length(); idxArrayCaseChoice++) {
            String choice = arrayChoices.getString(idxArrayCaseChoice);
            boxMatchParagraphHelper.addChoices(choice);
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);
        bundle.putString("question", question);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) boxMatchParagraphHelper.getListData());
        bundle.putStringArrayList("listChoices", (ArrayList<String>) boxMatchParagraphHelper.getListChoices());

        FragmentBahaso myFrag = new BoxMatchParagraphFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    public void createBoxMatchList(JSONObject caseObject, String caseID) throws JSONException {
        BoxMatchListHelper boxMatchListHelper = new BoxMatchListHelper();
        BoxMatchList boxMatchList;
        ArrayList<String> arrayListAnswer = new ArrayList<>();

        JSONObject boxMatchListCaseObject = caseObject;

        JSONArray arrayQuestions = boxMatchListCaseObject.getJSONArray("questions");
        JSONArray arrayAnswer = boxMatchListCaseObject.getJSONArray("bahaso_answers");
        JSONArray arrayChoices = boxMatchListCaseObject.getJSONArray("choices");

        instruction = boxMatchListCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayQuestions.length(); idxArrayCase++) {
            String question = arrayQuestions.getString(idxArrayCase);
            JSONArray answer = arrayAnswer.getJSONArray(idxArrayCase);
            arrayListAnswer = new ArrayList<>();
            for (int idxAnswer = 0; idxAnswer < answer.length(); idxAnswer++) {
                arrayListAnswer.add(answer.getString(idxAnswer));
            }
            boxMatchList = new BoxMatchList(question, arrayListAnswer);
            boxMatchListHelper.add(boxMatchList);
        }

        for (int idxArrayCaseChoice = 0; idxArrayCaseChoice < arrayChoices.length(); idxArrayCaseChoice++) {
            String choice = arrayChoices.getString(idxArrayCaseChoice);
            boxMatchListHelper.addChoices(choice);
        }

        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) boxMatchListHelper.getListData());
        bundle.putStringArrayList("listChoices", (ArrayList<String>) boxMatchListHelper.getListChoices());

        FragmentBahaso myFrag = new BoxMatchListFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    private void createBoxMatchPicture(JSONObject caseObject, String caseID) throws JSONException {
        BoxMatchPictureHelper boxMatchPictureHelper = new BoxMatchPictureHelper();
        BoxMatchPicture boxMatchPicture;
        ArrayList<String> arrayPictureAnswer = new ArrayList<>();

        JSONObject boxMatchPictureCaseObject = caseObject;

        JSONArray arrayImageSrc = boxMatchPictureCaseObject.getJSONArray("image_srcs");
        JSONArray arrayAnswer = boxMatchPictureCaseObject.getJSONArray("bahaso_answers");
        JSONArray arrayChoices = boxMatchPictureCaseObject.getJSONArray("choices");

        instruction = boxMatchPictureCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayImageSrc.length(); idxArrayCase++) {
            String imageSrc = arrayImageSrc.getString(idxArrayCase);
            String answer = arrayAnswer.getString(idxArrayCase);

            boxMatchPicture = new BoxMatchPicture(imageSrc, answer);
            boxMatchPictureHelper.add(boxMatchPicture);
        }

        for (int idxArrayCaseChoice = 0; idxArrayCaseChoice < arrayChoices.length(); idxArrayCaseChoice++) {
            String choice = arrayChoices.getString(idxArrayCaseChoice);
            boxMatchPictureHelper.addChoices(choice);
        }

        Bundle bundle = new Bundle();
        bundle.putString("type", "Box Match Picture");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) boxMatchPictureHelper.getListData());
        bundle.putStringArrayList("listChoices", (ArrayList<String>) boxMatchPictureHelper.getListChoices());

        FragmentBahaso myFrag = new BoxMatchPictureFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);

    }

    private void createBoxMatchDisappear(JSONObject caseObject, String caseID) throws JSONException {

        BoxMatchDisappearHelper boxMatchDisappearHelper = new BoxMatchDisappearHelper();
        BoxMatchDisappear boxMatchDisappear;
        ArrayList<String> arrayListAnswer = new ArrayList<>();

        JSONObject boxMatchDisappearCaseObject = caseObject;

        JSONArray arrayQuestions = boxMatchDisappearCaseObject.getJSONArray("questions");
        JSONArray arrayAnswer = boxMatchDisappearCaseObject.getJSONArray("bahaso_answers");
        JSONArray arrayChoices = boxMatchDisappearCaseObject.getJSONArray("choices");

        instruction = boxMatchDisappearCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayQuestions.length(); idxArrayCase++) {
            String question = arrayQuestions.getString(idxArrayCase);
            JSONArray answer = arrayAnswer.getJSONArray(idxArrayCase);
            arrayListAnswer = new ArrayList<>();
            for (int idxAnswer = 0; idxAnswer < answer.length(); idxAnswer++) {
                arrayListAnswer.add(answer.getString(idxAnswer));
            }
            boxMatchDisappear = new BoxMatchDisappear(question, arrayListAnswer);
            boxMatchDisappearHelper.add(boxMatchDisappear);
        }

        for (int idxArrayCaseChoice = 0; idxArrayCaseChoice < arrayChoices.length(); idxArrayCaseChoice++) {
            String choice = arrayChoices.getString(idxArrayCaseChoice);
            boxMatchDisappearHelper.addChoices(choice);
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) boxMatchDisappearHelper.getListData());
        bundle.putStringArrayList("listChoices", (ArrayList<String>) boxMatchDisappearHelper.getListChoices());

        FragmentBahaso myFrag = new BoxMatchDisappearFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }

    private void createSentenceFormation(JSONObject caseObject, String caseID) throws JSONException {

        SentenceFormationHelper sentenceFormationHelper = new SentenceFormationHelper();
        SentenceFormation sentenceFormation;
        ArrayList<String> arrayListAnswer = new ArrayList<>();

        JSONObject sentenceFormationCaseObject = caseObject;

        JSONArray arrayAnswer = sentenceFormationCaseObject.getJSONArray("bahaso_answers");

        instruction = sentenceFormationCaseObject.getString("instruction");

        for (int idxArrayCase = 0; idxArrayCase < arrayAnswer.length(); idxArrayCase++) {
            String answer = idxArrayCase + "";
            String choice = arrayAnswer.getString(idxArrayCase);
            sentenceFormation = new SentenceFormation(answer);
            sentenceFormationHelper.add(sentenceFormation);
            sentenceFormationHelper.addChoices(choice);

        }

        Bundle bundle = new Bundle();
        bundle.putString("type", "Lost Word Type");
        bundle.putString("instruction", instruction);
        bundle.putString("caseID", caseID);

        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) sentenceFormationHelper.getListData());
        bundle.putStringArrayList("listChoices", (ArrayList<String>) sentenceFormationHelper.getListChoices());

        FragmentBahaso myFrag = new SentenceFormationFragment();
        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }


    private void createEmptyForTesting() {
        FragmentBahaso myFrag = new EmptyFragmentForTesting();
        myFrag.mainType = "intro";
//        myFrag.setArguments(bundle);
        listCaseFragment.add(myFrag);
    }


    private void loadCaseFragment() {
        getCaseData(); //For get case data from server
    }


    public void getCaseData() {
        showPDialog();
        final GlobalVar global = (GlobalVar) getApplicationContext();
        final String URL_PLACEMENT_CASE = global.getBaseURLpath() + "all_type_case/";
        try {


            StringRequest request = new StringRequest(Request.Method.GET, URL_PLACEMENT_CASE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse = new JSONObject(response);

                                Log.i("jsonResponse", jsonResponse + "");

                                Log.i("jsonResponseLength", jsonResponse.toString().length() + "");

                                String status = jsonResponse.getString("status");
                                String message = jsonResponse.getString("message");

                                //
                                Log.i("JSONMsg", message + " asdd");

//                                if(status.equals("true")) {
                                JSONObject jsonArr = jsonResponse.getJSONObject("data");
//                                    durationPlacement = jsonArr.getInt("duration");
                                JSONObject packetArray = jsonArr.getJSONObject("packets");
//                                    String placementTestIdJSON = jsonArr.getString("id");

                                listCaseFragment = new ArrayList<FragmentBahaso>();
                                Log.i("JSONMsg22", packetArray.length() + " asdd");

                                JSONArray casesArray =
                                        packetArray.getJSONArray("cases");
                                JSONArray introsArray =
                                        packetArray.getJSONArray("intros");
                                Log.i("casesArrayLength", casesArray.length() + "");


                                for (int idxIntroArr = 0; idxIntroArr < introsArray.length(); idxIntroArr++) {
                                    JSONObject caseObject = introsArray.getJSONObject(idxIntroArr);

                                    String caseType = caseObject.getString("type") + " Intro Page";
//                                    String caseType = "intros";


                                    String caseID = caseObject.getString("id");

                                    Log.i("caseType", caseType);

                                    if (caseType.equals("Little Box Multiple Choice")) {
                                        createLittleBoxSelectionCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Lost Word Type")) {
                                        createLostWordTypeCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match Disappear Picture")) {
                                        createBoxMatchDisappearPictureCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match Paragraph")) {
                                        createBoxMatchParagraphCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match List")) {
                                        createBoxMatchList(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else {
                                        createEmptyForTesting();
                                        listTypeCaseForHeader.add(caseType);
                                    }
                                }
                                for (int idxCaseArr = 0; idxCaseArr < casesArray.length(); idxCaseArr++) {
                                    JSONObject caseObject = casesArray.getJSONObject(idxCaseArr);


                                    String caseType = caseObject.getString("typeText");


                                    String caseID = caseObject.getString("id");

                                    Log.i("caseType", caseType);

                                    if (caseType.equals("Little Box Multiple Choice")) {
                                        createLittleBoxSelectionCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Lost Word Type")) {
                                        createLostWordTypeCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match Disappear Picture")) {
                                        createBoxMatchDisappearPictureCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match Paragraph")) {
                                        createBoxMatchParagraphCase(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match List")) {
                                        createBoxMatchList(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);

                                    } else if (caseType.equals("Box Match Picture")) {
                                        createBoxMatchPicture(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);
                                    } else if (caseType.equals("Box Match Disappear")) {
                                        createBoxMatchDisappear(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);
                                    } else if (caseType.equals("Sentence formation")) {
                                        createSentenceFormation(caseObject, caseID);
                                        listTypeCaseForHeader.add(caseType);
                                    } else {
                                        createEmptyForTesting();
                                        listTypeCaseForHeader.add(caseType);
                                    }
                                }


                                hidePDialog();
                                layoutCasePlacement.setVisibility(View.VISIBLE);

                                loadComponent();

//                                }
//                                else{
//                                    hidePDialog();
//                                    finish();
//                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                hidePDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(getApplicationContext(),
                                        getApplicationContext().getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(getApplicationContext(),
                                        getApplicationContext().getString(R.string.error_auth_failure),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(getApplicationContext(),
                                        getApplicationContext().getString(R.string.error_server),
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                                Toast.makeText(getApplicationContext(),
                                        getApplicationContext().getString(R.string.error_internet_access),
                                        Toast.LENGTH_LONG).show();
                            }
//                        hidePDialog();

                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(getApplicationContext()).add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void createTimer(int timeDuration) {
//        Log.i("tesFuncTimer","Enter");
//        int maxTime = timeDuration;
//        int countdownInterval = 1000;
//        dateFormat = new SimpleDateFormat("mm:ss");
////        timerFormatted = dateFormat.format(maxTime);
//        txtCountdownTimer.setText(timerFormatted);
//        countDownTimer = new CountDownTimer(maxTime+2000,countdownInterval) {
//            @Override
//            public void onTick(long l) {
//                timerFormatted = dateFormat.format(l-1000);
//                Log.i("tesFuncTimer",l+"");
//                txtCountdownTimer.setText(timerFormatted);
//                if(l-1000<1000) {
//                    Toast.makeText(IntroAndGeneralCaseActivity.this, "Times Up", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                timerFormatted = dateFormat.format(0);
//                txtCountdownTimer.setText(timerFormatted);
//                Log.i("TimerFinished","Timer Finished");
//            }
//        };
//
//
//
//    }

    private void loadCaseData() {

        Button btnCaseNumber;

        for (int i = 0; i < listCaseFragment.size(); i++) {
            idxCaseBtn = i;
            viewCaseNumberButton = layoutInflater.inflate(R.layout.layout_case_number_btn, null);

            btnCaseNumber = (Button) viewCaseNumberButton.findViewById(R.id.btnCaseNumber);
            btnCaseNumber.setText((i + 1) + "");
            btnCaseNumber.setTag(i);
            btnCaseNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPagerCase.setCurrentItem(Integer.parseInt(view.getTag().toString()));
                    Log.i("caseBtn", view.getTag() + "");

                }
            });

            layoutCaseControl.addView(viewCaseNumberButton);
        }
    }

    private static SharedPreferences getSharedPref() {
        if (sharedpref == null) {
            sharedpref = GlobalVar.getInstance().getSharedPreferences();
        }
        return sharedpref;
    }
}
