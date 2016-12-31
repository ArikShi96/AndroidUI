package com.example.root.experimentassistant.ThirdLevel;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.root.experimentassistant.Adapter.QuestionAdapter;
import com.example.root.experimentassistant.Model.ExperModel;
import com.example.root.experimentassistant.Model.Step;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.Question;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class StepActivity extends AppCompatActivity {
    int step;
    Step step_item;
    private TextView title;
    private ImageButton back;
    private TextView content_title;
    private TextView content;
    private ImageView image;
    private TextView note_title;
    private TextView note;
    private TextView question_title;
    private ListView questions;
    private TextView count_down;
    private Button next_button;

    private TextView tmp_answer_type;

    //倒计时
    Handler handler_start=new Handler();
    Handler handler_stop=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            int current_time=User.getInstance().getExperiment().getCurrent_time();
            if(step_item.getPrefix_time()<current_time){
                setCountDown(0);
                Message message=new Message();
                message.what=1;
                handler_stop.sendMessage(message);
            }
            else{
                setCountDown(min(step_item.getPrefix_time()-current_time,step_item.getSuggest_time()));
            }
            handler_start.postDelayed(runnable,1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        title          = (TextView)    findViewById(R.id.step_title);
        back           = (ImageButton) findViewById(R.id.step_back);
        content_title  = (TextView)    findViewById(R.id.step_content_title);
        content        = (TextView)    findViewById(R.id.step_content);
        image          = (ImageView)   findViewById(R.id.step_images);
        note_title     = (TextView)    findViewById(R.id.step_note_title);
        note           = (TextView)    findViewById(R.id.step_note);
        question_title = (TextView)    findViewById(R.id.step_question_title);
        questions      = (ListView)    findViewById(R.id.step_question);

        step=getIntent().getIntExtra("step",0);
        step_item=User.getInstance().getExperiment().getSteps().get(step);

        title.setText(step_item.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        content_title.getPaint().setFakeBoldText(true);
        note_title.getPaint().setFakeBoldText(true);
        question_title.getPaint().setFakeBoldText(true);

        content.setText(step_item.getContent());
        //image
        note.setText(step_item.getNote());

        //question
        QuestionAdapter adapter=new QuestionAdapter(step_item.getQuestion_list(),this);
        questions.setAdapter(adapter);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(StepActivity.this,StepActivity.class);
                intent.putExtra("step",step+1);
            }
        });

        //count down
        handler_start.post(runnable);
    }




    /**
     * handle the result from take photo activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take photo complete
        if(requestCode==0) {
            if (resultCode == RESULT_OK) {
                try {
                    Bundle result = data.getExtras();
                    ExperModel exper = User.getInstance().getExperiment();
                    Question question = exper.getQuestions().get(result.getInt("id"));
                    question.setAnswer_type(true);
                    question.setAnswer(result.getString("path"));
                    if(tmp_answer_type!=null){
                        tmp_answer_type.setText("问题类型:图片");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode==RESULT_CANCELED){
                tmp_answer_type=null;
            }
        }
    }

    public void setTmpAnswer(TextView answer_type){
        tmp_answer_type=answer_type;
    }


    /**
     * count down
     * @param second
     */
    public void setCountDown(int second){
        int m=second/60;
        int s=second%60;
        count_down.setText(getCountDownShow(m)+":"+getCountDownShow(s));
    }

    private String getCountDownShow(int val){
        if(val<10){
            return "0"+val;
        }else return ""+val;
    }

    public int min(int x, int y){
        return x<y?x:y;
    }
}
