package com.example.root.experimentassistant.MyView;

import android.widget.ArrayAdapter;

/**
 * Created by root on 2016/12/9.
 */
public interface MySearchViewListener {
    public void search(String searchText);

    public void getMatching(String matchText);
}
