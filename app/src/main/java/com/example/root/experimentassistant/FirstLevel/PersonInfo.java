package com.example.root.experimentassistant.FirstLevel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.Model.*;
import com.example.root.experimentassistant.SecondLevel.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 2016/12/6.
 */
public class PersonInfo extends Fragment{
    private TextView idText;

    private TextView nameText;

    private TextView note;

    private Button btnLogin;

    private Button btnRegister;

    private ListView menuList;

    private LinearLayout loginLayout;

    private RelativeLayout noLoginLayout;

    @Override
    public void onStart(){
        super.onStart();
        if(User.getInstance().isLogin()){
            onLogin();
        }
        else{
            onLogoff();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.personal_info,container,false);

        idText=(TextView) view.findViewById(R.id.StudentId);
        nameText=(TextView) view.findViewById(R.id.StudentName);
        btnLogin=(Button)view.findViewById(R.id.LoginButton);
        btnRegister=(Button)view.findViewById(R.id.RegisterButton);
        menuList=(ListView)view.findViewById(R.id.personMenu);
        loginLayout=(LinearLayout)view.findViewById(R.id.loginLayout) ;
        noLoginLayout=(RelativeLayout)view.findViewById(R.id.noLoginLayout);

        Log.d("PersonInfo","id"+User.getInstance().getId());
        //获取USER信息
        if(User.getInstance().isLogin()){
            onLogin();
        }
        else{
            onLogoff();
        }

        //登录注册按钮功能绑定
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(),LoginActivity.class),100);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(),RegisterActivity.class),101);
            }
        });

        //绑定菜单内容
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pic",R.mipmap.mcourse_blue);
        map.put("text","我的课程");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("pic",R.mipmap.mhistory_yellow);
        map.put("text","历史课程");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("pic",R.mipmap.mscore_green);
        map.put("text","我的成绩");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("pic",R.mipmap.mexit_orange);
        map.put("text","退出登录");
        list.add(map);

        menuList.setAdapter(new SimpleAdapter(this.getContext(),list,R.layout.menuitem,new String[]{"text","pic"},new int[]{R.id.menuItemText,R.id.menuItemIcon}));

        // 绑定菜单项点击事件
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    //我的课程
                    case 0:
                        startActivity(new Intent(getContext(),MyCourseActivity.class));
                        break;
                    //历史课程
                    case 1:
                        startActivity(new Intent(getContext(),HistoryCourseActivity.class));
                        break;
                    //我的成绩
                    case 2:
                        startActivity(new Intent(getContext(),MyScoreActivity.class));
                        break;
                    //登出
                    case 3:
                        onLogoff();
                        User.getInstance().Logoff(getContext().getSharedPreferences("user", Context.MODE_PRIVATE));
                        break;
                    default:
                }
            }
        });

        return view;
    }

    private void onLogin(){
        if(User.getInstance().isLogin()){
            loginLayout.setVisibility(View.VISIBLE);
            noLoginLayout.setVisibility(View.GONE);

            idText.setText(" 学号："+User.getInstance().getId());
            nameText.setText(User.getInstance().getName());
        }
    }

    private void onLogoff(){
        loginLayout.setVisibility(View.GONE);
        noLoginLayout.setVisibility(View.VISIBLE);
    }
}
