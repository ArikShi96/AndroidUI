package com.example.root.experimentassistant.FirstLevel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;

import java.util.ArrayList;
import java.util.List;

public class Experiment extends AppCompatActivity {
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;
    private List<LoginOffCallback> observers=new ArrayList<>();

    TextView test1;
    TextView test2;
    TextView test3;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        User.getInstance().Init(getSharedPreferences("user", Context.MODE_PRIVATE));
        Log.d("main","create");

        test1=(TextView)findViewById(R.id.test1);
        test2=(TextView)findViewById(R.id.test2);
        test3=(TextView)findViewById(R.id.test3);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Experiments");

        test1.setOnClickListener(testClickListener);
        test2.setOnClickListener(testClickListener);
        test3.setOnClickListener(testClickListener);

        test2.setSelected(true);
        initFragement2();
    }

    private void hideAllFragement(FragmentTransaction transaction){
        if(fragment1!=null) transaction.hide(fragment1);
        if(fragment2!=null) transaction.hide(fragment2);
        if(fragment3!=null) transaction.hide(fragment3);
    }

    private void initFragement1(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        if(fragment1==null){
            fragment1=new CourseList();
            transaction.add(R.id.content,fragment1);
        }
        hideAllFragement(transaction);

        transaction.show(fragment1);
        transaction.commit();
    }

    private void initFragement2(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        if(fragment2==null){
            fragment2=new ExperimentList();
            observers.add((LoginOffCallback)fragment2);
            transaction.add(R.id.content,fragment2);
        }
        hideAllFragement(transaction);

        transaction.show(fragment2);
        transaction.commit();
    }

    private void initFragement3(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        if(fragment3==null){
            fragment3=new PersonInfo();
            observers.add((LoginOffCallback) fragment3);
            transaction.add(R.id.content,fragment3);
        }
        hideAllFragement(transaction);

        transaction.show(fragment3);
        transaction.commit();
    }

     View.OnClickListener testClickListener=new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             switch (v.getId()) {
                 case R.id.test1:
                     if (test1.isSelected() == false) {
                         test1.setSelected(true);
                         test2.setSelected(false);
                         test3.setSelected(false);
                         initFragement1();
                         toolbar.setTitle("Courses");
                     }
                     break;
                 case R.id.test2:
                     if (test2.isSelected() == false) {
                         test2.setSelected(true);
                         test1.setSelected(false);
                         test3.setSelected(false);
                         initFragement2();
                         toolbar.setTitle("Experiments");
                     }
                     break;
                 case R.id.test3:
                     if (test3.isSelected() == false) {
                         test3.setSelected(true);
                         test1.setSelected(false);
                         test2.setSelected(false);
                         initFragement3();
                         toolbar.setTitle("Personal Info");
                     }
                     break;
                 default:
                     break;
             }
         }
     };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //登录
            case 100:
                if(resultCode==RESULT_OK){
                    if(User.getInstance().isLogin()){
                        for(LoginOffCallback callback:observers){
                            callback.onLogin();
                        }
                    }
                }
                break;
            //注册
            case 101:
                break;
            default:
        }
    }
}
