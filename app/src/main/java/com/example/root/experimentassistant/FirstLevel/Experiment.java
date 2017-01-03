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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.SecondLevel.ExperDetailActivity;
import com.example.root.experimentassistant.SecondLevel.ModifyPasswordActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.example.root.experimentassistant.Adapter.MySpinnerAdapter;
import com.example.root.experimentassistant.Internet.CookieUnits;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Experiment extends AppCompatActivity {
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;

    LinearLayout courseBtn;
    LinearLayout experBtn;
    LinearLayout personBtn;

    ImageView courseIcon;
    ImageView experIcon;
    ImageView personIcon;

    TextView courseText;
    TextView experText;
    TextView personText;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        User.getInstance().Init(getSharedPreferences("user", Context.MODE_PRIVATE));
        Log.d("main","create");

        courseBtn=(LinearLayout)findViewById(R.id.courseBtn);
        experBtn=(LinearLayout)findViewById(R.id.experBtn);
        personBtn=(LinearLayout)findViewById(R.id.personBtn);

        courseIcon=(ImageView)findViewById(R.id.courseIcon);
        experIcon=(ImageView)findViewById(R.id.experIcon);
        personIcon=(ImageView)findViewById(R.id.personIcon);

        courseText=(TextView)findViewById(R.id.courseText);
        experText=(TextView)findViewById(R.id.experText);
        personText=(TextView)findViewById(R.id.personText);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Experiments");

        courseBtn.setOnClickListener(testClickListener);
        experBtn.setOnClickListener(testClickListener);
        personBtn.setOnClickListener(testClickListener);

        experText.setSelected(true);
        experIcon.setSelected(true);
        initFragement2();

        CookieUnits.setAppContext(getApplicationContext());
        init();
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
            transaction.add(R.id.content,fragment3);
        }
        hideAllFragement(transaction);

        transaction.show(fragment3);
        transaction.commit();
    }

    private void setCourseSelect(boolean select){
        courseIcon.setSelected(select);
        courseText.setSelected(select);
    }

    private void setExperSelect(boolean select){
        experIcon.setSelected(select);
        experText.setSelected(select);
    }

    private void setPersonSelect(boolean select){
        personIcon.setSelected(select);
        personText.setSelected(select);
    }

     View.OnClickListener testClickListener=new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             switch (v.getId()) {
                 case R.id.courseBtn:
                     if (courseIcon.isSelected() == false) {
                         setCourseSelect(true);
                         setExperSelect(false);
                         setPersonSelect(false);
                         initFragement1();
                         toolbar.setTitle("Courses");
                     }
                     break;
                 case R.id.experBtn:
                     if (experIcon.isSelected() == false) {
                         setCourseSelect(false);
                         setExperSelect(true);
                         setPersonSelect(false);
                         initFragement2();
                         toolbar.setTitle("Experiments");
                     }
                     break;
                 case R.id.personBtn:
                     if (personIcon.isSelected() == false) {
                         setCourseSelect(false);
                         setExperSelect(false);
                         setPersonSelect(true);
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
        switch (requestCode) {
            //登录
            case 100:
                if (resultCode == RESULT_OK) {
                }
                break;
            //注册
            case 101:
                break;
            default:
        }
    }


    /**
     * this is the main activity, so we do the project initialization here
     */

    public void init(){
        Fresco.initialize(Experiment.this);
    }
}
