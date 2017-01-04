package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ThirdLevel.StepActivity;
import com.example.root.experimentassistant.ThirdLevel.TakePhotoActivity;
import com.example.root.experimentassistant.ViewModel.Question;

import java.util.List;

/**
 * Created by Json on 2016/12/29.
 */
public class QuestionAdapter extends BaseAdapter{
    private List<Question> questions;
    private StepActivity activity;
    private int exper_id;

    private int touch_pos=-1;

    public QuestionAdapter(List<Question> q, StepActivity s, int eid){
        questions=q;
        activity=s;
        exper_id=eid;
    }

    @Override
    public int getCount() {
        if(questions!=null){
            return questions.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(questions!=null){
            return questions.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if(questions!=null){
            return questions.get(i).getId();
        }
        return -1;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ComponentHolder holder;
        if(view==null){
            holder=new ComponentHolder();
            LayoutInflater inflater=LayoutInflater.from(activity);
            view=inflater.inflate(R.layout.question_item,null);

            holder.question_title=(TextView) view.findViewById(R.id.question_title);
            holder.question_type=(TextView) view.findViewById(R.id.question_type);
            holder.question_content=(TextView) view.findViewById(R.id.question_content);
            holder.question_answer=(EditText) view.findViewById(R.id.question_answer);
            holder.photo_button=(ImageView) view.findViewById(R.id.photo_image);

            //加粗
            holder.question_title.getPaint().setFakeBoldText(true);
            holder.question_type.getPaint().setFakeBoldText(true);

            final TextView question_type=holder.question_type;
            final TextView question_answer=holder.question_answer;


            Question question=questions.get(position);
            final Bundle bundle=new Bundle();
            bundle.putInt("id",question.getId());
            bundle.putInt("exper_id",exper_id);


            //点击照片按钮监听
            holder.photo_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent picture=new Intent(activity, TakePhotoActivity.class);
                    picture.putExtras(bundle);
                    activity.startActivityForResult(picture,0);
                    activity.setTmpAnswer(question_type, question_answer);
                }
            });

            //输入监听
            holder.question_answer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    touch_pos=(Integer) view.getTag();
                    if ((view.getId() == R.id.question_answer && canVerticalScroll((EditText)view))) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                    return false;
                }
            });

            holder.text_watcher=new MyTextWatcher();
            holder.text_watcher.setData(question, holder);

            view.setTag(holder);
        }else {
            holder=(ComponentHolder) view.getTag();
        }

        holder.question_answer.setTag(position);

        if(touch_pos==position){
            holder.question_answer.requestFocus();
            holder.question_answer.setSelection(holder.question_answer.getText().length());
        }else{
            holder.question_answer.clearFocus();
        }

        Question question=questions.get(position);
        if(question!=null){
            holder.question_content.setText(question.getQuestion());
            holder.question_answer.setText(question.getAnswer());
            if(!question.getAnswer_type()){
                holder.question_type.setText("答案类型:文字");
            }
            else{
                holder.question_type.setText("答案类型:图片");
            }
        }
        return view;
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText 需要判断的EditText
     * @return true：可以滚动 false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }
}

class ComponentHolder{

    public TextView question_title;
    public TextView question_type;
    public TextView question_content;
    public EditText question_answer;
    public ImageView photo_button;

    public MyTextWatcher text_watcher;
}

class MyTextWatcher implements TextWatcher {
    private Question question;
    private ComponentHolder holder;

    public void setData(Question q, ComponentHolder h){
        question=q;
        holder=h;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        question.setAnswer_type(false);
        question.setAnswer(s.toString());
        holder.question_type.setText("答案类型:文字");
    }
};
