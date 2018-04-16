package com.example.root.experimentassistant.SecondLevel;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Json on 2016/12/26.
 */
public class ModifyPasswordActivity extends AppCompatActivity {
    private static final String mdf_pw_url="api/users/ModifyPassword";
    private String user_id;
    private EditText old_password;
    private EditText new_password;
    private EditText confirm_password;
    private TextView error_message;

    private LinearLayout global_layout;

    private Button send;

    private Dialog waitting_dialog;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);

        user_id=getIntent().getStringExtra("user_id");

        old_password     = (EditText)     findViewById(R.id.mdf_pw_old);
        new_password     = (EditText)     findViewById(R.id.mdf_pw_new);
        confirm_password = (EditText)     findViewById(R.id.mdf_pw_confirm);
        send             = (Button)       findViewById(R.id.mdf_pw_send);
        error_message    = (TextView)     findViewById(R.id.mdf_pw_err_msg);
        global_layout    = (LinearLayout) findViewById(R.id.mdf_pw_globallayout);
        back=(ImageView)findViewById(R.id.mdf_pw_back);
        init();
    }

    public void init(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        global_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_pw=old_password.getText().toString();
                String new_pw=new_password.getText().toString();
                String cfm_pw=confirm_password.getText().toString();

                if(!old_pw.equals("")&&!new_pw.equals("")&&!cfm_pw.equals("")){
                    if(new_pw.equals(cfm_pw)) {
                        error_message.setText("");
                        send.setClickable(true);
                        send.setBackgroundResource(R.drawable.rec_btn_select);
                    }
                    else{
                        error_message.setText("前后密码不一致");
                        send.setClickable(false);
                        send.setBackgroundResource(R.drawable.rec_btn_gray);
                    }
                }
            }
        });

        send.setClickable(false);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params=new RequestParams();
                params.put("identify", User.getInstance().getId());
                params.put("oldPassword", old_password.getText().toString());
                params.put("newPassword", new_password.getText().toString());
                waitting_dialog=StaticConfig.createLoadingDialog(ModifyPasswordActivity.this,"处理中...");
                ExperimentHttpClient.getInstance().post(mdf_pw_url,params,new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        StaticConfig.closeDialog(waitting_dialog);
                        if(statusCode==200){
                            Toast.makeText(ModifyPasswordActivity.this,
                                    "修改成功",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else{
                            Toast.makeText(ModifyPasswordActivity.this,"服务器未知错误:"+statusCode,Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                        StaticConfig.closeDialog(waitting_dialog);
                        String message = errorResponse.optString("message");
                        if (message.isEmpty()) {
                            message = "未知原因,修改失败";
                        }
                        Toast.makeText(ModifyPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
