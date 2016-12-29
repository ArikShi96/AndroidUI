package com.example.root.experimentassistant.SecondLevel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Json on 2016/12/26.
 */
public class ExperDetailActivity extends AppCompatActivity{
    private static final String detail_url="/student/experdetail";
    private Bundle bundle;
    private int exper_id;
    private ImageButton back_button;
    private TextView expername;
    private TextView begin_time;
    private SimpleDraweeView exper_pic;
    private Button description;
    private Button attach;
    private Button begin;
    private TextView content;

    private TextView title;

    private String desc;
    private String attach_url;

    private Dialog loading_dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exper_detail);

        bundle=getIntent().getExtras();
        exper_id=bundle.getInt("exper_id");

        back_button = (ImageButton)      findViewById(R.id.exper_back);
        expername   = (TextView)         findViewById(R.id.exper_name);
        begin_time  = (TextView)         findViewById(R.id.exper_begin_time);
        exper_pic   = (SimpleDraweeView) findViewById(R.id.exper_pic);
        description = (Button)           findViewById(R.id.exper_description);
        attach      = (Button)           findViewById(R.id.exper_attach);
        begin       = (Button)           findViewById(R.id.exper_begin);
        content     = (TextView)         findViewById(R.id.exper_content);
        title       = (TextView)         findViewById(R.id.exper_detail_title);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticConfig.closeDialog(loading_dialog);
                finish();
            }
        });
        RequestParams params=new RequestParams();
        params.put("exper_id",exper_id);
        loading_dialog = StaticConfig.createLoadingDialog(ExperDetailActivity.this,"加载中...");
        ExperimentHttpClient.getInstance().get(detail_url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                StaticConfig.closeDialog(loading_dialog);
                if(statusCode==200){
                    try {
                            init(response);
                    }catch (Exception e){
                        e.printStackTrace();
                        Intent err=StaticConfig.errorPage(ExperDetailActivity.this,title.getText().toString(),"数据解析失败");
                        startActivity(err);
                        finish();
                    }
                }
                else {
                    Intent err=StaticConfig.errorPage(ExperDetailActivity.this,title.getText().toString(),"加载失败，返回码："+statusCode);
                    startActivity(err);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                StaticConfig.closeDialog(loading_dialog);
                Intent err=StaticConfig.errorPage(ExperDetailActivity.this,title.getText().toString(),"加载失败，网络走丢了："+statusCode);
                startActivity(err);
                finish();
            }
        });
    }

    public void init(JSONObject response) throws org.json.JSONException{
        expername.setText(response.getString("name"));
        begin_time.setText(response.getString("begin_time"));
        exper_pic.setImageURI(response.getString("pic_url"));
        desc = response.getString("describe");
        attach_url = response.getString("attach_url");
        content.setText(desc);

        //set begin button
        if(response.getBoolean("enable")){
            begin.setClickable(true);
            begin.setBackgroundColor(Color.BLUE);
            begin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open new activity
                }
            });
        }
        else{
            begin.setClickable(false);
            begin.setBackgroundColor(Color.LTGRAY);
        }

        //set description button and attach button
        description.setBackgroundColor(Color.WHITE);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setBackgroundColor(Color.WHITE);
                attach.setBackgroundColor(Color.GRAY);
                content.setText(desc);
            }
        });
        attach.setBackgroundColor(Color.GRAY);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setBackgroundColor(Color.GRAY);
                attach.setBackgroundColor(Color.WHITE);
                content.setText(attach_url);
            }
        });
    }
}