package com.example.root.experimentassistant.SecondLevel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.Adapter.CoursesAdapter;
import com.example.root.experimentassistant.Model.RequestCallBack;
import com.example.root.experimentassistant.Model.StudentInfo;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ViewCourse;

import java.util.List;

public class MyCourseActivity extends AppCompatActivity {
    private ListView mList;

    private TextView mTitle;

    private ImageView mCancle;

    private Toolbar mBar;

    private StudentInfo studentInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_info_layout);
        mCancle=(ImageView)findViewById(R.id.infoCancle);
        mTitle=(TextView)findViewById(R.id.infoTitle);
        mList=(ListView)findViewById(R.id.infoList);
        mBar=(Toolbar)findViewById(R.id.infoToolBar);

        //设置工具栏
        mBar.setTitle("");
        setSupportActionBar(mBar);

        //设置标题
        mTitle.setText("我的课程");

        //设置列表内容
        studentInfo=new StudentInfo(this);
        studentInfo.getMyCourse(new RequestCallBack() {
            @Override
            public void onRequestSuccess(Object sender) {
                List<ViewCourse> courses=(List<ViewCourse>)sender;
                CoursesAdapter adapter=new CoursesAdapter(MyCourseActivity.this,courses);
                mList.setAdapter(adapter);
            }

            @Override
            public void onRequestFailure(Object sender) {
                int status=((Integer)sender).intValue();

                Log.d("CourseAct","fail"+status);
            }
        });

        //按钮事件
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
