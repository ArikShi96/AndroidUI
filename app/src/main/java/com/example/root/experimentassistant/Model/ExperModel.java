package com.example.root.experimentassistant.Model;

import android.os.Handler;
import android.os.Message;

import com.example.root.experimentassistant.ViewModel.Question;

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
    int question_id;
    List<Step> steps=new ArrayList<>();
    List<Question> question_list=new ArrayList<>();
    int current_time;
    int total_time;

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

    ExperModel(int id, JSONObject response) throws JSONException{
        this.id=id;
        question_id=0;
        current_time=0;
        total_time=0;

        JSONArray steps=response.getJSONArray("step_list");
        for(int i=0;i<steps.length();i++){
            JSONObject item=steps.getJSONObject(i);
            Step step=new Step();
            step.setId(item.getInt("id"));
            step.setName(item.getString("name"));
            step.setContent(item.getString("content"));
            step.setNote(item.getString("note"));

            String suggest_time=item.getString("suggest_time");
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
            JSONArray questions=item.getJSONArray("question_list");
            for(int j=0;j<questions.length();j++){
                Question question=new Question();
                question.setId(question_id++);
                question.setQuestion(questions.getString(j));
                step.getQuestion_list().add(question);
                question_list.add(question);
            }
            JSONArray images=item.getJSONArray("image_list");
            for(int j=0;j<images.length();j++){
                step.getImage_list().add(images.getString(i));
            }
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
}
