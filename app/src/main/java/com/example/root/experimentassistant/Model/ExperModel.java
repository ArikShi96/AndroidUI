package com.example.root.experimentassistant.Model;

import android.os.Handler;
import android.os.Message;

import com.example.root.experimentassistant.ViewModel.Question;
import com.example.root.experimentassistant.ViewModel.photoQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Json on 2016/12/30.
 */
public class ExperModel {
    int id;
    int question_count;
    List<Step> steps=new ArrayList<>();
    List<Question> question_list=new ArrayList<>();
    int current_time;
    int total_time;

    int step_count;

    Handler handler_start=new Handler();
    Handler handler_stop=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    handler_start.removeCallbacks(runnable);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            current_time++;
            if(current_time>total_time){
                Message message=new Message();
                message.what=1;
                handler_stop.sendMessage(message);
            }
            handler_start.postDelayed(runnable,1000);
        }
    };

    public ExperModel(int id, JSONArray response) throws JSONException{
        this.id=id;
        question_count=0;
        current_time=0;
        total_time=0;

        JSONArray steps=response;
        step_count=steps.length();
        if (step_count==0) throw new JSONException("null array list");
        for(int i=0;i<steps.length();i++){
            JSONObject item=steps.getJSONObject(i);
            Step step=new Step();
            step.setId(item.getInt("id"));
            step.setName(item.getString("name"));
            step.setContent(item.getString("content"));
            step.setNote(item.getString("note"));

            String suggest_time=item.getString("suggestTime");
            String[] time_tmp=suggest_time.split(":");
            int step_time;
            if(time_tmp.length==2){
                step_time=Integer.valueOf(time_tmp[0])*60+Integer.valueOf(time_tmp[1]);
                total_time+=step_time;
                step.setSuggest_time(step_time);
                step.setPrefix_time(total_time);
            }else {
                throw new JSONException("cannot parse the Suggest time in step");
            }
            JSONArray questions=item.getJSONArray("questions");
            for(int j=0;j<questions.length();j++){
                Question question=new Question();
                question.setId(question_count++);
                question.setQuestion(questions.getString(j));
                step.getQuestion_list().add(question);
                question_list.add(question);
            }
            JSONArray images=item.getJSONArray("images");
            for(int j=0;j<images.length();j++){
                step.getImage_list().add(images.getString(j));
            }

            this.steps.add(step);
        }
    }

    public List<Step> getSteps(){
        return steps;
    }

    public List<Question> getQuestions(){
        return question_list;
    }

    public int getCurrent_time(){
        return current_time;
    }

    public void beginCountDown(){
        handler_start.post(runnable);
    }

    public int getStep_count(){
        return step_count;
    }

    public int getId(){
        return id;
    }
}
