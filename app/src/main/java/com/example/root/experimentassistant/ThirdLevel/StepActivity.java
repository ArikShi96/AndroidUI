package com.example.root.experimentassistant.ThirdLevel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.example.root.experimentassistant.Adapter.QuestionAdapter;
import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.ExperModel;
import com.example.root.experimentassistant.Model.Step;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.MyView.ImageShowActivity;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.example.root.experimentassistant.ViewModel.Question;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.example.root.experimentassistant.ViewModel.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class StepActivity extends AppCompatActivity implements QuestionAdapter.OnEditBtnClickListerner{
    int step;
    int exper_id;
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
    private TextView tmp_answer;

    private Dialog loading_dialog;

    public static final int EDITANSWER=1;

    private PopupWindow popupWindow;

    private View parentView;

    private LinearLayout layout;

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
        parentView=getLayoutInflater().inflate(R.layout.activity_step, null);
        setContentView(parentView);

        title          = (TextView)    findViewById(R.id.step_title);
        back           = (ImageButton) findViewById(R.id.step_back);
        content_title  = (TextView)    findViewById(R.id.step_content_title);
        content        = (TextView)    findViewById(R.id.step_content);
        image          = (ImageView)   findViewById(R.id.step_images);
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
        ArrayList<String> images=step_item.getImage_list();
        if(images.size()>0) {
            ImageLoader imageLoader=ImageLoader.getInstance();
            imageLoader.displayImage(StaticConfig.BASE_URL + step_item.getImage_list().get(0),image,StaticConfig.options);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent show_image=new Intent(StepActivity.this, ImageShowActivity.class);
                    show_image.putExtra("bitmap",((BitmapDrawable)image.getDrawable()).getBitmap());
                    startActivity(show_image);
                }
            });
        }

        note.setText(step_item.getNote());

        //question
        QuestionAdapter adapter=new QuestionAdapter(step_item.getQuestion_list(),this,exper_id);
        adapter.setmListerner(this);
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

        //popUp初始化
        popupWindow=new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.activity_take_photo,null);
        layout=(LinearLayout)view.findViewById(R.id.pop_layout);

        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);

        Button editPhoto=(Button)view.findViewById(R.id.btn_take_photo);
        Button editChart=(Button)view.findViewById(R.id.btn_pick_photo);
        Button cancle=(Button)view.findViewById(R.id.btn_cancel);

        editPhoto.setText("编辑图片");
        editChart.setText("编辑折线图");

        //图片作答
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question= User.getInstance().getExperiment().getQuestions().get(questionId);
                photoQuestion photo=new photoQuestion();
                photo.setAnswer_type(Question.PHOTOQUESTION);
                photo.setId(questionId);
                photo.setQuestion(question.getQuestion());
                photo.setAnswered(false);
                User.getInstance().getExperiment().getQuestions().remove(questionId);
                User.getInstance().getExperiment().getQuestions().add(questionId,photo);

                onEditBtnClick(questionId);
                popupWindow.dismiss();
                layout.clearAnimation();
            }
        });
        // 折现图作答
        editChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question= User.getInstance().getExperiment().getQuestions().get(questionId);
                chartQuestion chart=new chartQuestion();
                chart.setAnswer_type(Question.CHARTQUESTION);
                chart.setId(questionId);
                chart.setQuestion(question.getQuestion());
                chart.setAnswered(false);
                User.getInstance().getExperiment().getQuestions().remove(questionId);
                User.getInstance().getExperiment().getQuestions().add(questionId,chart);

                onEditBtnClick(questionId);
                popupWindow.dismiss();
                layout.clearAnimation();
            }
        });
        //取消
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.startAnimation(AnimationUtils.loadAnimation(StepActivity.this,R.anim.trans_out));

                new Handler().postDelayed(new Runnable(){

                    public void run() {
                        popupWindow.dismiss();
                        layout.clearAnimation();
                    }

                }, 300);
            }
        });

    }




    /**
     * handle the result from take photo activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActResult","req "+requestCode+" res "+resultCode);
        switch (requestCode) {
            case EDITANSWER:
                if(resultCode==RESULT_OK) {
                    questionId=data.getExtras().getInt("questionId");
                    step_item = User.getInstance().getExperiment().getSteps().get(step);
                    for (Question question : step_item.getQuestion_list()) {
                        if (question.getId() == questionId) {
                            Log.d("onActResult", "id" + questionId);
                            question.setAnswered(User.getInstance().getExperiment().getQuestions().get(questionId).isAnswered());
                            question.setAnswer_type(User.getInstance().getExperiment().getQuestions().get(questionId).getAnswer_type());
                        }
                    }

                    QuestionAdapter adapter = new QuestionAdapter(step_item.getQuestion_list(), this, exper_id);
                    adapter.setmListerner(this);
                    questions.setAdapter(adapter);
                    break;
                }
        }
    }

    public void setTmpAnswer(TextView answer_type, TextView answer){
        tmp_answer_type=answer_type;
        tmp_answer=answer;
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
        String url = "api/experiments/" + exper_id + "/submit2";

        Map<String, String> heads = new HashMap<>();
        heads.put("token", User.getInstance().getToken());

        loading_dialog=StaticConfig.createLoadingDialog(StepActivity.this,"发送中...");
        ExperimentHttpClient.getInstance().post(url,null, heads, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("submit","success "+statusCode);
                StaticConfig.closeDialog(loading_dialog);
                if(statusCode==200){
                    try {
                        Intent success= StaticConfig.successPage(StepActivity.this,title.getText().toString(),"提交成功");
                        startActivity(success);

                        setResult(RESULT_OK);
                        finish();
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
                StaticConfig.closeDialog(loading_dialog);
                String message = response.optString("message");
                if (message.isEmpty()) {
                    message = "网络故障，请稍候再试";
                }

                Toast.makeText(StepActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int questionId;

    @Override
    public void onEditBtnClick(int questionId){
        this.questionId=questionId;
        Question question=User.getInstance().getExperiment().getQuestions().get(questionId);
        if(question.getAnswer_type()==1) {
            Intent intent = new Intent(this, PhotoAnswer.class);
            intent.putExtra("questionId", questionId);
            startActivityForResult(intent, EDITANSWER);
        }
        else if(question.getAnswer_type()==2){
            Intent intent=new Intent(this,LineChatAnswer.class);
            Bundle bundle=new Bundle();
            bundle.putInt("minY",0);
            bundle.putInt("maxY",100);
            bundle.putInt("offset",5);
            bundle.putInt("questionId",questionId);
            intent.putExtras(bundle);
            startActivityForResult(intent, EDITANSWER);
        }
        else{
            layout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.trans_in));
            popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
    }
}
