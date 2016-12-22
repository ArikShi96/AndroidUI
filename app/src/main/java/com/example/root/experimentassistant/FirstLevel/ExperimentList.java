package com.example.root.experimentassistant.FirstLevel;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.root.experimentassistant.Adapter.ExpersAdapter;
import com.example.root.experimentassistant.Model.RequestCallBack;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ViewExper;

import java.util.ArrayList;
import java.util.List;

public class ExperimentList extends Fragment{
    ListView experimentList;

    private MaterialRefreshLayout myMaterialRefreshLayout;

    private int weekCnt;

    private Spinner weekSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.experiment_list,container,false);
        Log.d("Exper","create");
        experimentList=(ListView) view.findViewById(R.id.experiment_list);
        myMaterialRefreshLayout=(MaterialRefreshLayout)view.findViewById(R.id.experiment_refresh);
        weekSpinner=(Spinner)view.findViewById(R.id.weekSpinner);

        //刷新回调函数
        myMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if(User.getInstance().isLogin()) bindExperiment();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myMaterialRefreshLayout.finishRefreshLoadMore();
                    }
                },3000);
            }
        });

        //若登录初始化列表
        if(User.getInstance().isLogin()) {
            weekCnt = User.getInstance().getCurrentWeek(getContext().getSharedPreferences("user", Context.MODE_PRIVATE));
            weekSpinner.setSelection(weekCnt - 1);
            bindExperiment();
        }

        //列表item点击回调
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekCnt=position+1;
                bindExperiment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.d("Exper","create finish");
        return view;
    }


    private void bindExperiment() {
        if(User.getInstance().isLogin()==false) return;
        Log.d("bindExper","week"+weekCnt);
        List<ViewExper> expers = User.getInstance().getCntExper(weekCnt,new getExpersCallback());

        ExpersAdapter adapter = new ExpersAdapter(this.getContext(), expers);
        experimentList.setAdapter(adapter);
    }

    public void onLogin(){
        if(User.getInstance().isLogin()) {
            weekSpinner.setSelection(User.getInstance().getCurrentWeek(getContext().getSharedPreferences("user", Context.MODE_PRIVATE)) - 1);
            bindExperiment();
        }
    }

    public void onLogoff(){
        experimentList.removeAllViews();
    }

    private class getExpersCallback implements RequestCallBack{
        public void onRequestSuccess(Object sender){
            myMaterialRefreshLayout.finishRefresh();
        }

        public void onRequestFailure(Object sender){

        }
    }
}
