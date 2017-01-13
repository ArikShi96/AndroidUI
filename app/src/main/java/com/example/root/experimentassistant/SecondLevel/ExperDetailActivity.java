package com.example.root.experimentassistant.SecondLevel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.ExperModel;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.MyView.ImageShowActivity;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.example.root.experimentassistant.ThirdLevel.StepActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
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
    private ImageView exper_pic;
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
        exper_id=(int)bundle.getLong("exper_id");

        back_button = (ImageButton)      findViewById(R.id.exper_back);
        expername   = (TextView)         findViewById(R.id.exper_name);
        begin_time  = (TextView)         findViewById(R.id.exper_begin_time);
        exper_pic   = (ImageView)        findViewById(R.id.exper_pic);
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
        begin_time.setText(response.getString("time"));
        desc = response.getString("descbibe");
        attach_url = response.getString("attach_url");
        content.setText(desc);

        //image
        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(StaticConfig.BASE_URL+response.getString("pic_url"),exper_pic,StaticConfig.options);
        exper_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent show_image=new Intent(ExperDetailActivity.this, ImageShowActivity.class);
                show_image.putExtra("bitmap",((BitmapDrawable)exper_pic.getDrawable()).getBitmap());
                startActivity(show_image);
            }
        });

        //set begin button
        if(/*response.getBoolean("enable")*/true){
            begin.setClickable(true);
            begin.setBackgroundResource(R.drawable.rec_btn_select);
            begin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestParams params=new RequestParams();
                    params.put("exper_id", exper_id);
                    loading_dialog=StaticConfig.createLoadingDialog(ExperDetailActivity.this, "加载中");
                    ExperimentHttpClient.getInstance().post("/student/enterexper",params,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            StaticConfig.closeDialog(loading_dialog);
                            if(statusCode==200){
                                try{
                                    ExperModel model=new ExperModel(exper_id,response);
                                    User.getInstance().setExperiment(model);
                                    Intent step=new Intent(ExperDetailActivity.this, StepActivity.class);
                                    step.putExtra("step",0);
                                    startActivity(step);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Intent err=StaticConfig.errorPage(ExperDetailActivity.this,title.getText().toString(),"数据解析失败");
                                    startActivity(err);
                                }
                            }else{
                                Toast.makeText(ExperDetailActivity.this,"错误返回码:"+statusCode,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject response) {
                            StaticConfig.closeDialog(loading_dialog);
                            Intent err=StaticConfig.errorPage(ExperDetailActivity.this,title.getText().toString(),"加载失败，网络走丢了："+statusCode);
                            startActivity(err);
                        }
                    });
                }
            });
        }
        else{
            begin.setClickable(false);
            begin.setBackgroundResource(R.drawable.rec_btn_gray);
        }

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri attach_url=Uri.parse(StaticConfig.BASE_URL+content.getText().toString());
                intent.setData(attach_url);
                startActivity(intent);
            }
        });

        //set description button and attach button
        description.setBackgroundColor(Color.GRAY);
        content.setClickable(false);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setBackgroundColor(Color.GRAY);
                attach.setBackgroundColor(Color.WHITE);
                content.setText(desc);
                content.setClickable(false);
                content.setTextColor(Color.parseColor("#666666"));
            }
        });
        attach.setBackgroundColor(Color.WHITE);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setBackgroundColor(Color.WHITE);
                attach.setBackgroundColor(Color.GRAY);
                content.setText(attach_url);
                content.setClickable(true);
                content.setTextColor(Color.parseColor("#0099cc"));
            }
        });
    }
}