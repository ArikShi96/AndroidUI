package com.example.root.experimentassistant.ThirdLevel;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String vcode_url = "/user/getvetifycode";
    private static final String send_url = "/user/forget_password";

    private EditText identify;
    private EditText phone_number;
    private EditText vcode;
    private EditText new_password;
    private EditText confirm_password;
    private TextView err_msg;

    private Button vcode_button;
    private Button send_button;

    private LinearLayout global_layout;

    private Dialog waitting_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        identify = (EditText) findViewById(R.id.fg_pw_identify);
        phone_number = (EditText) findViewById(R.id.fg_pw_phone_number);
        vcode = (EditText) findViewById(R.id.fg_pw_vcode);
        new_password = (EditText) findViewById(R.id.fg_pw_new);
        confirm_password = (EditText) findViewById(R.id.fg_pw_confirm);
        vcode_button = (Button) findViewById(R.id.fg_pw_vcode_btn);
        send_button = (Button) findViewById(R.id.fg_pw_send);
        global_layout = (LinearLayout) findViewById(R.id.fg_pw_globallayout);
        err_msg = (TextView) findViewById(R.id.fg_pw_err_msg);

        init();
    }

    public void init() {
        //listener for send
        global_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = identify.getText().toString();
                String p_n = phone_number.getText().toString();
                String vc = vcode.getText().toString();
                String new_pw = new_password.getText().toString();
                String cfm_pw = confirm_password.getText().toString();

                if (!id.equals("") && !p_n.equals("") && !vc.equals("") && !new_pw.equals("") && !cfm_pw.equals("")) {
                    if (new_pw.equals(cfm_pw)) {
                        err_msg.setText("");
                        send_button.setClickable(true);
                        send_button.setBackgroundResource(R.drawable.rec_btn_select);
                    } else {
                        err_msg.setText("前后密码不一致");
                        send_button.setClickable(false);
                        send_button.setBackgroundResource(R.drawable.rec_btn_gray);
                    }
                }
            }
        });

        //listener for phone_number
        TextWatcher phone_number_watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 11) {
                    vcode_button.setBackgroundResource(R.drawable.rec_btn_select);
                    vcode_button.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        phone_number.addTextChangedListener(phone_number_watcher);

        //verify code button
        vcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.add("phoneNum", phone_number.getText().toString());
                ExperimentHttpClient.getInstance().post(vcode_url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(ForgetPasswordActivity.this, "发送成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(ForgetPasswordActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }
                });
                vcode_button.setClickable(false);
                TimerTask task=new TimerTask() {
                    private int count=60;
                    @Override
                    public void run() {
                        Message mess=new Message();
                        mess.what=count;
                        handler.sendMessage(mess);

                        count--;
                        if(count<-1){
                            cancel();
                        }
                    }
                };
                timer.schedule(task,0,1000);
            }
        });

        //send button
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params=new RequestParams();
                params.add("identify", identify.getText().toString());
                params.add("phone_number", phone_number.getText().toString());
                params.add("new_password",new_password.getText().toString());
                params.add("verify_code",vcode.getText().toString());
                waitting_dialog= StaticConfig.createLoadingDialog(ForgetPasswordActivity.this,"处理中...");
                ExperimentHttpClient.getInstance().post(send_url,params,new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        StaticConfig.closeDialog(waitting_dialog);
                        if(statusCode==200){
                            try{
                                int code=response.getInt("status");
                                if(code==0){
                                    Toast.makeText(ForgetPasswordActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(ForgetPasswordActivity.this, "修改失败", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(ForgetPasswordActivity.this, "返回码异常", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(ForgetPasswordActivity.this, "错误的返回码"+statusCode, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        StaticConfig.closeDialog(waitting_dialog);
                        Toast.makeText(ForgetPasswordActivity.this, "发送失败"+statusCode, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        send_button.setBackgroundResource(R.drawable.rec_btn_gray);
        send_button.setClickable(true);
        vcode_button.setBackgroundResource(R.drawable.rec_btn_gray);
        vcode_button.setClickable(true);
    }

    //定时器代码
    final Timer timer=new Timer();

    Handler handler=new Handler(){
        public void handleMessage(Message mess){
            if(mess.what>=0){
                vcode_button.setText(mess.what+"秒后再试");
            }
            else{
                vcode_button.setText("获取验证码");
                vcode_button.setEnabled(true);
            }
            super.handleMessage(mess);
        }
    };
}
