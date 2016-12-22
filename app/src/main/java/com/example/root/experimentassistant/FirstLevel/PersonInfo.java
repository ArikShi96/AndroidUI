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
        note=(TextView) view.findViewById(R.id.LogNote);
        btnLogin=(Button)view.findViewById(R.id.LoginButton);
        btnRegister=(Button)view.findViewById(R.id.RegisterButton);
        menuList=(ListView)view.findViewById(R.id.personMenu);
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
        map.put("text","我的课程");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("text","历史记录");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("text","我的成绩");
        list.add(map);

        map=new HashMap<String, Object>();
        map.put("text","退出登录");
        list.add(map);

        menuList.setAdapter(new SimpleAdapter(this.getContext(),list,R.layout.menuitem,new String[]{"text"},new int[] {R.id.menuItemText}));

        // 绑定菜单项点击事件
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
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
            idText.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);
            note.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);

            idText.setText(" 学号："+User.getInstance().getId());
            nameText.setText("姓名："+User.getInstance().getName());
        }
    }

    private void onLogoff(){
        idText.setVisibility(View.GONE);
        nameText.setVisibility(View.GONE);
        note.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
    }
}
