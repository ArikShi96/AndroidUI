package com.example.root.experimentassistant.SecondLevel;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.Model.*;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private ImageButton cancle;

    private EditText regId;

    private EditText regName;

    private EditText regPass1;

    private EditText regPass2;

    private EditText regPhone;

    private EditText vertifyCode;

    private Button btnGetCode;

    private Button btnRegister;

    private TextView note;

    public Context getcontext(){return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ToolBar初始化
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        cancle=(ImageButton)findViewById(R.id.registerCancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化输入框按钮控件
        regId=(EditText)findViewById(R.id.regId);
        regName=(EditText)findViewById(R.id.regName);
        regPass1=(EditText)findViewById(R.id.regPass1);
        regPass2=(EditText)findViewById(R.id.regPass2);
        regPhone=(EditText)findViewById(R.id.regPhone);
        vertifyCode=(EditText)findViewById(R.id.vertifyCode);
        btnGetCode=(Button) findViewById(R.id.regVertifyBtn);
        btnRegister=(Button)findViewById(R.id.regBtn);
        note=(TextView)findViewById(R.id.passDiff) ;

        //绑定按钮事件
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(regPhone.getText().toString().isEmpty()){
                    Toast.makeText(getcontext(),"请先输入电话号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                btnGetCode.setEnabled(false);

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
                //获取验证码网络事件
                User.getInstance().getVertifyCode(regPhone.getText().toString());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册网络事件
                int status=User.getInstance().Register(regId.getText().toString(),regName.getText().toString(),regPhone.getText().toString()
                        ,vertifyCode.getText().toString() ,regPass1.getText().toString(),new RegisterCallBack());
            }
        });

        //密码确认监听
        TextWatcher watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(regPass1.getText().toString().equals(regPass2.getText().toString())){
                    note.setVisibility(View.INVISIBLE);
                }
                else{
                    note.setVisibility(View.VISIBLE);
                }
            }
        };
        regPass1.addTextChangedListener(watcher);

        regPass2.addTextChangedListener(watcher);


        //注册键enable设置
        TextWatcher regWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!regPhone.getText().toString().isEmpty()
                        &&!regId.getText().toString().isEmpty()
                        &&!regName.getText().toString().isEmpty()
                        &&!regPass1.getText().toString().isEmpty()
                        &&regPass1.getText().toString().equals(regPass2.getText().toString())
                        &&!regPhone.getText().toString().isEmpty()
                        &&!vertifyCode.getText().toString().isEmpty())
                {
                    btnRegister.setEnabled(true);
                }
                else{
                    btnRegister.setEnabled(false);
                }
            }
        };

        regId.addTextChangedListener(regWatcher);
        regName.addTextChangedListener(regWatcher);
        regPass1.addTextChangedListener(regWatcher);
        regPass2.addTextChangedListener(regWatcher);
        regPhone.addTextChangedListener(regWatcher);
        vertifyCode.addTextChangedListener(regWatcher);
    }


    //定时器代码
    final Timer timer=new Timer();

    Handler handler=new Handler(){
        public void handleMessage(Message mess){
            if(mess.what>=0){
                btnGetCode.setText(mess.what+"秒后再试");
            }
            else{
                btnGetCode.setText("获取验证码");
                btnGetCode.setEnabled(true);
            }
            super.handleMessage(mess);
        }
    };

    private class RegisterCallBack implements RequestCallBack {
        public void onRequestSuccess(Object sender) {
            int status = ((Integer) sender).intValue();
            switch (status) {
                case 0:
                    //注册成功
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                    break;
                //参数不匹配
                case 1:
                    Toast.makeText(RegisterActivity.this, "网络参数错误", Toast.LENGTH_SHORT);
                    break;
                //手机号重复使用
                case 2:
                    Toast.makeText(RegisterActivity.this, "手机号已被注册", Toast.LENGTH_SHORT);
                    regPhone.setText("");
                    break;
                //验证码错误
                case 3:
                    Toast.makeText(RegisterActivity.this,"验证码错误",Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }

        public void onRequestFailure(Object sender){
            Toast.makeText(RegisterActivity.this,"网络错误 ",Toast.LENGTH_SHORT);
        }
    }
}
