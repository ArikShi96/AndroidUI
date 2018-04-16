package com.example.root.experimentassistant.SecondLevel;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.Model.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import java.util.Timer;
import java.util.TimerTask;
import android.os.Message;
import android.widget.Toast;
import android.widget.ImageView;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private ImageView cancle;

    private EditText regId;

    private EditText regName;

    private EditText regPass1;

    private EditText regPass2;

    private EditText regPhone;

    private EditText vertifyCode;

    private Button btnGetCode;

    private Button btnRegister;

    private ImageView nameDel;

    private ImageView idDel;

    private ImageView pass1Del;

    private ImageView pass2Del;

    private ImageView phoneDel;

    private ImageView codeDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ToolBar初始化
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        cancle=(ImageView)findViewById(R.id.registerCancle);
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
        idDel=(ImageView)findViewById(R.id.reg_id_delete);
        nameDel=(ImageView)findViewById(R.id.reg_name_delete);
        pass1Del=(ImageView)findViewById(R.id.reg_pass1_delete);
        pass2Del=(ImageView)findViewById(R.id.reg_pass2_delete);
        phoneDel=(ImageView)findViewById(R.id.reg_phone_delete);
        codeDel=(ImageView)findViewById(R.id.reg_code_delete);

        final Drawable errorPic=getResources().getDrawable(R.mipmap.exclamation);
        errorPic.setBounds(0,0,40,40);

        //图标标准化
        Drawable leftDrawable=regId.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,50,50);
        regId.setCompoundDrawables(leftDrawable,null,null,null);

        leftDrawable=regName.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,50,50);
        regName.setCompoundDrawables(leftDrawable,null,null,null);

        leftDrawable=regPass1.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,50,50);
        regPass1.setCompoundDrawables(leftDrawable,null,null,null);

        leftDrawable=regPass2.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,50,50);
        regPass2.setCompoundDrawables(leftDrawable,null,null,null);

        leftDrawable=regPhone.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,60,60);
        regPhone.setCompoundDrawables(leftDrawable,null,null,null);

        leftDrawable=vertifyCode.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,60,60);
        vertifyCode.setCompoundDrawables(leftDrawable,null,null,null);

        //键入监听绑定
        regId.addTextChangedListener(idWatcher);
        regName.addTextChangedListener(nameWatcher);
        regPass1.addTextChangedListener(pass1Watcher);
        regPass2.addTextChangedListener(pass2Watcher);
        regPhone.addTextChangedListener(phoneWatcher);
        vertifyCode.addTextChangedListener(codeWatcher);

        //清空按钮点击监听
        idDel.setOnClickListener(deleteClickListener);
        nameDel.setOnClickListener(deleteClickListener);
        pass1Del.setOnClickListener(deleteClickListener);
        pass2Del.setOnClickListener(deleteClickListener);
        phoneDel.setOnClickListener(deleteClickListener);
        codeDel.setOnClickListener(deleteClickListener);

        //绑定按钮事件
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(regPhone.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"请先输入电话号码",Toast.LENGTH_SHORT).show();
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
                if(regName.getText().toString().isEmpty()){
                    regName.setError("名字不为空",errorPic);
                    regName.requestFocus();
                }
                else if(regId.getText().toString().isEmpty()){
                    regId.setError("学号不为空",errorPic);
                    regId.requestFocus();
                }
                else if(regPass1.getText().toString().isEmpty()){
                    regPass1.setError("密码不为空",errorPic);
                    regPass1.requestFocus();
                }
                else if(!regPass2.getText().toString().equals(regPass1.getText().toString())){
                    pass2Del.setVisibility(View.GONE);
                    regPass2.setError("密码输入不一致",errorPic);
                    regPass2.requestFocus();
                }
                else if(regPhone.getText().toString().isEmpty()){
                    regPhone.setError("电话号码不为空",errorPic);
                    regPhone.requestFocus();
                }
                else if(vertifyCode.getText().toString().isEmpty()){
                    vertifyCode.setError("验证码不为空",errorPic);
                    vertifyCode.requestFocus();
                }
                else {
                    //注册网络事件
                    int status = User.getInstance().Register(regId.getText().toString(), regName.getText().toString(), regPhone.getText().toString()
                            , vertifyCode.getText().toString(), regPass1.getText().toString(), new RegisterCallBack());
                }
            }
        });

    }

    //delete显示
    //Name
    TextWatcher nameWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                nameDel.setVisibility(View.GONE);
            }
            else{
                nameDel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Id
    TextWatcher idWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                idDel.setVisibility(View.GONE);
            }
            else{
                idDel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Pass1
    TextWatcher pass1Watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                pass1Del.setVisibility(View.GONE);
            }
            else{
                pass1Del.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Pass2
    TextWatcher pass2Watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                pass2Del.setVisibility(View.GONE);
            }
            else{
                pass2Del.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Phone
    TextWatcher phoneWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                phoneDel.setVisibility(View.GONE);
            }
            else{
                phoneDel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //Code
    TextWatcher codeWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                codeDel.setVisibility(View.GONE);
            }
            else{
                codeDel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    //文本清空键回调函数
    View.OnClickListener deleteClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.reg_name_delete:
                    regName.setText("");
                    break;
                case R.id.reg_id_delete:
                    regId.setText("");
                    break;
                case R.id.reg_pass1_delete:
                    regPass1.setText("");
                    break;
                case R.id.reg_pass2_delete:
                    regPass2.setText("");
                    break;
                case R.id.reg_phone_delete:
                    regPhone.setText("");
                    break;
                case R.id.reg_code_delete:
                    vertifyCode.setText("");
                    break;
                default:
            }
        }
    };


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
            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
            RegisterActivity.this.finish();
        }

        public void onRequestFailure(Object sender){
            String message = (String)sender;
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
