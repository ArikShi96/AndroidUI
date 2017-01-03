package com.example.root.experimentassistant.SecondLevel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CourseDetailActivity extends AppCompatActivity {
    private static final String url="/student/course_detail";
    private static final String join_url="student/joincourse";
    private static final String cancel_url="student/cancelcourse";

    private int course_id;

    private boolean enable;

    private TextView title;
    private ImageButton back_button;
    private SimpleDraweeView course_pic;
    private TextView course_name;
    private TextView teacher_name;
    private TextView course_time;
    private Button join_button;
    private TextView description;

    private Dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        enable=true;

        title=(TextView) findViewById(R.id.cs_dtl_title);
        back_button=(ImageButton) findViewById(R.id.cs_dtl_back);
        course_pic=(SimpleDraweeView) findViewById(R.id.cs_dtl_pic);
        course_name=(TextView) findViewById(R.id.cs_dtl_name);
        course_time=(TextView) findViewById(R.id.cs_dtl_time);
        teacher_name=(TextView) findViewById(R.id.cs_dtl_teacher);
        join_button=(Button) findViewById(R.id.cs_dtl_join);
        description=(TextView) findViewById(R.id.cs_dtl_desc);

        course_id= (int)getIntent().getLongExtra("course_id",0);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticConfig.closeDialog(loading_dialog);
                finish();
            }
        });

        //get the data from server
        RequestParams params=new RequestParams();
        params.put("student_id",User.getInstance().getId());
        params.put("course_id",course_id);
        loading_dialog = StaticConfig.createLoadingDialog(CourseDetailActivity.this,"加载中...");
        ExperimentHttpClient.getInstance().get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                StaticConfig.closeDialog(loading_dialog);
                if(statusCode==200){
                    try {
                        init(response);
                    }catch (Exception e){
                        e.printStackTrace();
                        Intent err=StaticConfig.errorPage(CourseDetailActivity.this,title.getText().toString(),"数据解析失败");
                        startActivity(err);
                        finish();
                    }
                }
                else {
                    Intent err=StaticConfig.errorPage(CourseDetailActivity.this,title.getText().toString(),"加载失败，返回码："+statusCode);
                    startActivity(err);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                StaticConfig.closeDialog(loading_dialog);
                Intent err=StaticConfig.errorPage(CourseDetailActivity.this,title.getText().toString(),"加载失败，网络走丢了："+statusCode);
                startActivity(err);
                finish();
            }
        });
    }

    public void init(JSONObject response) throws JSONException{
        course_name.setText(response.getString("course_name"));
        course_time.setText(response.getString("course_time"));
        teacher_name.setText(response.getString("teacher_name"));
        if(!response.getBoolean("enable")){
            enable=false;
            join_button.setText("退出");
            join_button.setBackgroundColor(Color.RED);
        }
        else{
            join_button.setText("参加");
            join_button.setBackgroundColor(Color.parseColor("#0099ff"));
        }
        course_pic.setImageURI(response.getString("course_pic"));
        description.setText(response.getString("description"));

        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enable){
                    //try to join
                    RequestParams params=new RequestParams();
                    params.put("student_id", User.getInstance().getId());
                    params.put("course_id",course_id);
                    loading_dialog=StaticConfig.createLoadingDialog(CourseDetailActivity.this,"处理中...");
                    ExperimentHttpClient.getInstance().post(join_url,params,new JsonHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            StaticConfig.closeDialog(loading_dialog);
                            if(statusCode==200){
                                try{
                                    //success
                                    if(response.getInt("status")==0){
                                        Toast.makeText(CourseDetailActivity.this,"参加成功",Toast.LENGTH_LONG).show();
                                        enable=false;
                                        join_button.setText("退出");
                                        join_button.setBackgroundColor(Color.RED);
                                    }
                                    else{
                                        Toast.makeText(CourseDetailActivity.this,"参加失败",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch(Exception e){
                                    Toast.makeText(CourseDetailActivity.this,"未知返回码:"+response,Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(CourseDetailActivity.this,"服务器未知错误:"+statusCode,Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(int v1, Header[] v2, String v3, Throwable v4){
                            StaticConfig.closeDialog(loading_dialog);
                            Toast.makeText(CourseDetailActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    RequestParams params=new RequestParams();
                    params.put("student_id", User.getInstance().getId());
                    params.put("course_id",course_id);
                    loading_dialog=StaticConfig.createLoadingDialog(CourseDetailActivity.this,"处理中...");
                    ExperimentHttpClient.getInstance().post(cancel_url,params,new JsonHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            StaticConfig.closeDialog(loading_dialog);
                            if(statusCode==200){
                                try{
                                    //cancel success
                                    if(response.getInt("status")==0){
                                        Toast.makeText(CourseDetailActivity.this,"退课成功",Toast.LENGTH_LONG).show();
                                        enable=true;
                                        join_button.setText("参加");
                                        join_button.setBackgroundColor(Color.parseColor("#0099ff"));
                                    }
                                    else{
                                        Toast.makeText(CourseDetailActivity.this,"退课失败",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch(Exception e){
                                    Toast.makeText(CourseDetailActivity.this,"未知返回码:"+response,Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(CourseDetailActivity.this,"服务器未知错误:"+statusCode,Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(int v1, Header[] v2, String v3, Throwable v4){
                            StaticConfig.closeDialog(loading_dialog);
                            Toast.makeText(CourseDetailActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}