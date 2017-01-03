package com.example.root.experimentassistant.ThirdLevel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.root.experimentassistant.Adapter.QuestionAdapter;
import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.ExperModel;
import com.example.root.experimentassistant.Model.Step;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.example.root.experimentassistant.ViewModel.Question;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class StepActivity extends AppCompatActivity {
    int step;
    int exper_id;
    Step step_item;
    private TextView title;
    private ImageButton back;
    private TextView content_title;
    private TextView content;
    private SimpleDraweeView image;
    private TextView note_title;
    private TextView note;
    private TextView question_title;
    private ListView questions;
    private TextView count_down;
    private Button next_button;

    private TextView tmp_answer_type;

    private Dialog loading_dialog;

    //倒计时
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
        image          = (SimpleDraweeView)   findViewById(R.id.step_images);
        note_title     = (TextView)    findViewById(R.id.step_note_title);
        note           = (TextView)    findViewById(R.id.step_note);
        question_title = (TextView)    findViewById(R.id.step_question_title);
        questions      = (ListView)    findViewById(R.id.step_question);
        next_button    = (Button)      findViewById(R.id.step_next_button);
        count_down     = (TextView)    findViewById(R.id.step_count_down);

        step=getIntent().getIntExtra("step",0);
        exper_id=User.getInstance().getExperiment().getId();
        step_item=User.getInstance().getExperiment().getSteps().get(step);

        title.setText(step_item.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        content_title.getPaint().setFakeBoldText(true);
        note_title.getPaint().setFakeBoldText(true);
        question_title.getPaint().setFakeBoldText(true);

        content.setText(step_item.getContent());
        //image
        image.setImageURI(StaticConfig.TEST_IMAGE_URL);

        note.setText(step_item.getNote());

        //question
        QuestionAdapter adapter=new QuestionAdapter(step_item.getQuestion_list(),this,exper_id);
        questions.setAdapter(adapter);

        //next button
        if(step==User.getInstance().getExperiment().getStep_count()-1){
            next_button.setText("提     交");
        }
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(step==User.getInstance().getExperiment().getStep_count()-1){
                   submit();
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(StepActivity.this, StepActivity.class);
                    intent.putExtra("step", step + 1);
                    startActivityForResult(intent, 1);
                }
            }
        });

        //count down
        if(step==0){
            User.getInstance().getExperiment().beginCountDown();
        }
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
        switch (requestCode) {
            case 0:       //take photo complete
                if (resultCode == RESULT_OK) {
                    try {
                        Bundle result = data.getExtras();
                        if(result.getString("path").isEmpty()) return;
                        ExperModel exper = User.getInstance().getExperiment();
                        Question question = exper.getQuestions().get(result.getInt("id"));
                        question.setAnswer_type(true);
                        question.setAnswer(result.getString("path"));
                        if (tmp_answer_type != null) {
                            tmp_answer_type.setText("问题类型:图片");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    tmp_answer_type = null;
                }
                break;

            case 1:
                if(resultCode==RESULT_OK) {
                    if(step!=0) {
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        finish();
                    }
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
        String show=getCountDownShow(m)+":"+getCountDownShow(s);
        count_down.setText(show);
    }

    private String getCountDownShow(int val){
        if(val<10){
            return "0"+val;
        }else return ""+val;
    }

    public int min(int x, int y){
        return x<y?x:y;
    }

    public void submit(){
        //submit answer
        RequestParams params=new RequestParams();
        params.put("student_id",User.getInstance().getId());
        params.put("exper_id",exper_id);
        List<Question> questions=User.getInstance().getExperiment().getQuestions();
        try {
            for (Question question : questions) {
                if (!question.getAnswer_type()) {
                    params.put("answer_list", "*#" + question.getId() + "*#" + question.getAnswer());
                } else {
                    File file = new File(question.getAnswer());
                    params.put("image_list", file);
                }
            }
        }catch (Exception e){
            Toast.makeText(StepActivity.this,"图片读取失败",Toast.LENGTH_LONG).show();
            return;
        }
        loading_dialog=StaticConfig.createLoadingDialog(StepActivity.this,"发送中...");
        ExperimentHttpClient.getInstance().post("student/submit",params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                StaticConfig.closeDialog(loading_dialog);
                if(statusCode==200){
                    try {
                        int code=response.getInt("status");
                        if(code==0){
                            Intent success= StaticConfig.successPage(StepActivity.this,title.getText().toString(),"提交成功");
                            startActivity(success);

                            setResult(RESULT_OK);
                            finish();
                        }
                        else{
                            Intent err=StaticConfig.errorPage(StepActivity.this,title.getText().toString(),"未知返回码:"+code);
                            startActivity(err);
                        }
                    }catch (Exception e){
                        Intent err=StaticConfig.errorPage(StepActivity.this,title.getText().toString(),"返回值解析失败");
                        startActivity(err);
                    }
                }else {
                    Intent err=StaticConfig.errorPage(StepActivity.this,title.getText().toString(),"未知返回码:"+statusCode);
                    startActivity(err);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Intent err=StaticConfig.errorPage(StepActivity.this,title.getText().toString(),"发送失败");
                startActivity(err);
            }
        });
    }
}
