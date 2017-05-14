package com.bahaso.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bahaso.R;
import com.bahaso.model.LittleBoxSelection;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.disableDependentsState;
import static android.R.attr.id;
import static android.R.attr.width;
import static android.support.design.R.attr.height;
import static android.support.design.R.attr.logo;

/**
 * Created by bahaso on 23/03/2017.
 */

public class LittleBoxSelectionAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    private Context context;
    String instruction;
    List<LittleBoxSelection> listLittleBoxSelection ;

    ArrayList<Button> arButton;
    String question="";
    boolean isCheckAnswer = false;
    Context ctx;
    ViewHolder holder;
    int rowChoicesPosition ;
    static int idxItemStatic ;
    public LittleBoxSelectionAdapter(@NonNull Context context,
                                     @LayoutRes int resource,
                                     @NonNull List objects,String instruction) {
        super(context, resource, objects);
        ctx =context;
        arButton = new ArrayList<>();
        this.listLittleBoxSelection = objects;
        this.instruction = instruction;
        this.context = context;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setIsCheckAnswer(boolean isCheckAnswer){
        this.isCheckAnswer = isCheckAnswer;
    }

    @Override
    public int getCount() {
        return listLittleBoxSelection.size();

    }

    @Override
    public Object getItem(int i) {
        return listLittleBoxSelection.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static  class ViewHolder{
        TextView txtViewInstruction;
        RelativeLayout layoutCaseLittleBoxSelectionItemTitle;
        FlexboxLayout layoutCaseLittleBoxSelectionItem;
        Button btnLittleBoxSelection;
        TextView txtViewTextQuestion;
        View viewBtnItem;

    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if(view==null) {

//            view = inflater.inflate(R.layout.layout_case_little_box_selection_item, null);
//            holder = new ViewHolder();
//            holder.txtViewInstruction = (TextView) view.findViewById(R.id.textViewLittleBoxSelectionInstruction);
//            holder.layoutCaseLittleBoxSelectionItemTitle = (RelativeLayout) view.findViewById
//                    (R.id.layoutCaseLittleBoxSelectionItemTitle);
//            holder.layoutCaseLittleBoxSelectionItem = (FlexboxLayout) view.findViewById(R.id.layoutCaseLittleBoxSelectionItem);
//            view.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Log.i("pos", pos+"");
        holder.layoutCaseLittleBoxSelectionItem.removeAllViews();


        question = listLittleBoxSelection.get(pos).getQuestion();
//        question = "Haasd gasgas asdsa sad [BLANK/]";

//        int idxPerQuestion = 0;
//        while(idxPerQuestion<question.length())
//        {
//            Log.i()
//        }
        //        question = "asdasd [BLANK/]";
        question= question.replace("[BLANK/]"," # ");
        Log.i("idxPerQuestion", question +"");
        String [] splittedQuestion = question.split(" ");
        rowChoicesPosition = 0;

        for(int idxQuestionSplitted =0 ; idxQuestionSplitted<splittedQuestion.length;idxQuestionSplitted++){
            if(splittedQuestion[idxQuestionSplitted].trim().equals("#"))
            {
                loadButton(pos,rowChoicesPosition,view);
                rowChoicesPosition++;
            }
            else{
                loadTextView(view," "+splittedQuestion[idxQuestionSplitted].trim()+" ");
            }
        }

        if(pos==0){
            holder.layoutCaseLittleBoxSelectionItemTitle.setVisibility(View.VISIBLE);

            holder.txtViewInstruction.setText(instruction);
        }
        else
            holder.layoutCaseLittleBoxSelectionItemTitle.setVisibility(View.GONE);

//        ImageView imageViewChoosePicListen = (ImageView) cellView.findViewById(R.id.imageViewChoosePicListen);
//        TextView txtChoosePicListenContent = (TextView) cellView.findViewById(R.id.txtChoosePicListenContent);
//        ImageView choosePicListenSoundButton = (ImageView) cellView.findViewById(R.id.choosePicListenSoundButton);

//        choosePicListenSoundButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Sound", Toast.LENGTH_SHORT).show();
//            }
//        });

//        String choosePicListenContent = listChoosePic.get(pos).getChoosePicListenContentTxt();
//        txtChoosePicListenContent.setText(choosePicListenContent);

//        Log.i("LCAdapter","Jalan");
        if(isCheckAnswer && pos==listLittleBoxSelection.size()-1)
            isCheckAnswer = false;
        return view;

    }
    public void loadTextView(View cellView,String text)
    {
        holder.txtViewTextQuestion = new TextView(cellView.getContext());
        holder.txtViewTextQuestion.setText(text+"");
        holder.txtViewTextQuestion.setGravity(Gravity.CENTER_VERTICAL);
        holder.txtViewTextQuestion.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
        holder.layoutCaseLittleBoxSelectionItem.addView(holder.txtViewTextQuestion);
    }
//    public void checkButtonAnswer(int pos)
//    {
//        if (listLittleBoxSelection.get(pos).getUserAnswer()
//                == Integer.parseInt(listLittleBoxSelection.get(pos).getAnswer()))
//            btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected);
//        else
//            btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected_false);
//    }

    public void loadButton(final int pos , final int rowChoicesPosition, View cellView){

        for(int idxItem=0;idxItem<listLittleBoxSelection.get(pos).getArrayListChoicesList().get(rowChoicesPosition).size();idxItem++){
//            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(10,0,10,20);
            holder.viewBtnItem = inflater.inflate(R.layout.layout_case_little_box_selection_item_button,null);
            holder.btnLittleBoxSelection = (Button) holder.viewBtnItem.findViewById(R.id.btnSelectionLittleBox);

            String choiceContent = listLittleBoxSelection.get(pos).getArrayListChoicesList().get(rowChoicesPosition).get(idxItem);
            String userAnswer = listLittleBoxSelection.get(pos).getUserAnswer().get(rowChoicesPosition);
            String answer = listLittleBoxSelection.get(pos).getAnswer().get(rowChoicesPosition);

            holder.btnLittleBoxSelection.setText(choiceContent);
            holder.btnLittleBoxSelection.setTag(idxItem);
//            idxItemStatic = idxItem;
            if(userAnswer.equals("")){
                holder.btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
                holder.btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);

            }
            else
            {
                if(userAnswer.equals(idxItem+"")) {

                    if(isCheckAnswer) {
                        if (userAnswer.equals(answer))
                            holder.btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected);
                        else
                            holder.btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected_false);
                    }
                    else
                    {
                        holder.btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_selected);
                    }
                    holder.btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.white));
                }
                else
                {
                    holder.btnLittleBoxSelection.setTextColor(cellView.getResources().getColor(R.color.bahaso_black_gray));
                    holder.btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);
                }
            }

            holder.btnLittleBoxSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tagIdx = (int) view.getTag();
                    listLittleBoxSelection.get(pos).getUserAnswer().set(rowChoicesPosition,tagIdx+"");
                    notifyDataSetChanged();
                }
            });
//            btnLittleBoxSelection.setBackgroundResource(R.drawable.background_box_selection_unselected);


            holder.layoutCaseLittleBoxSelectionItem.addView(holder.viewBtnItem);
        }


    }
}
