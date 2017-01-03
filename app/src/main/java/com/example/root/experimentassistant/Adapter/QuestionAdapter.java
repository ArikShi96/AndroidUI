package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
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

            ViewGroup.LayoutParams params = holder.photo_button.getLayoutParams();
            params.width=params.height;
            holder.photo_button.setLayoutParams(params);
            final TextView question_type=holder.question_type;
            final TextView question_answer=holder.question_answer;


            final Question question=questions.get(i);
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


            //输入文字监听
            TextWatcher answer_watcher=new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    question.setAnswer_type(false);
                    question_type.setText("答案类型:文字");
                    question.setAnswer(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            };
            holder.question_answer.addTextChangedListener(answer_watcher);
            view.setTag(holder);
        }else {
            holder=(ComponentHolder) view.getTag();
        }

        Question question=questions.get(i);
        holder.question_content.setText(question.getQuestion());
        holder.question_answer.setText(question.getAnswer());
        if(question!=null){
            if(!question.getAnswer_type()){
                holder.question_type.setText("答案类型:文字");
            }
            else{
                holder.question_type.setText("答案类型:图片");
            }
        }
        return view;
    }
}

class ComponentHolder{

    public TextView question_title;
    public TextView question_type;
    public TextView question_content;
    public EditText question_answer;
    public ImageView photo_button;
}
