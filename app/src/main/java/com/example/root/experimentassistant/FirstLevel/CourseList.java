package com.example.root.experimentassistant.FirstLevel;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.root.experimentassistant.MyView.*;
import com.example.root.experimentassistant.R;

/**
 * Created by root on 2016/12/6.
 */
public class CourseList extends Fragment implements MySearchViewListener{
    private SearchView searchView;

    private ListView courseList;

    private MaterialRefreshLayout myMaterialRefreshLayout;

    String[] courses=new String[]{"asddsa1","asddsa2","asddsa3","asddsa4","asddsa5","asddsa6","asddsa7",
            "asddsa","asddsa","asddsa","asddsa","asddsa","asddsa","asddsa","asddsa","asddsa"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.course_list,container,false);

        searchView=(SearchView) view.findViewById(R.id.search_view);
        courseList=(ListView)view.findViewById(R.id.course_list);
        myMaterialRefreshLayout=(MaterialRefreshLayout)view.findViewById(R.id.course_refresh);

        searchView.bindListener(this);
        courseList.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,courses));

        myMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myMaterialRefreshLayout.finishRefresh();
                    }
                },3000);
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

        return view;
    }

    @Override
    public void search(String searchText){
        Toast.makeText(getContext(),searchText,Toast.LENGTH_LONG).show();
    }

    @Override
    public ArrayAdapter<String> getMatching(String matchText){
        String[] tmp=new String[]{"asd","ads","azx","axc"};
        return new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,tmp);
    }
}
