package com.example.root.experimentassistant.SecondLevel;

import com.example.root.experimentassistant.Model.RequestCallBack;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private ImageButton cancle;

    private Button loginBtn;

    private TextView id;

    private TextView pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn=(Button)findViewById(R.id.LoginBtn);
        id=(TextView)findViewById(R.id.LoginId);
        pass=(TextView)findViewById(R.id.LoginPass);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        cancle=(ImageButton)findViewById(R.id.loginCancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.getInstance().Login(id.getText().toString(),pass.getText().toString(),new LoginCallBack());
            }
        });
    }

    class LoginCallBack implements RequestCallBack{
        public void onRequestSuccess(Object sender){
            SharedPreferences preferences= getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userId",User.getInstance().getId());
            editor.putString("userPhoneNum",User.getInstance().getPhoneNum());
            editor.putString("userName",User.getInstance().getName());
            editor.commit();
            setResult(RESULT_OK);
            finish();
        }

        public void onRequestFailure(Object sender){
            id.setText("");
            pass.setText("");
            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
        }
    }
}
