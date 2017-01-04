package com.example.root.experimentassistant.MyView;
import com.example.root.experimentassistant.*;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by root on 2016/12/9.
 */
public class SearchView extends LinearLayout{
    private Spinner searchBy;

    private EditText searchText;

    private ImageView searchCancle;

    private Button  searchButton;

    private ListView searchList;

    private Context myContext;

    private MySearchViewListener myListener;

    public void bindListener(MySearchViewListener listener){
        myListener=listener;
    }

    private boolean suggestClick=false;

    private void processSearch(){
        if(myListener!=null) myListener.search(searchText.getText().toString());
        searchList.setVisibility(GONE);

        InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0) ;
    }

    private void initViews(){
//        searchBy=(Spinner)findViewById(R.id.search_by);
        searchText=(EditText)findViewById(R.id.search_text);
        searchList=(ListView)findViewById(R.id.search_list);
        searchButton=(Button)findViewById(R.id.search_button);
        searchCancle=(ImageView)findViewById(R.id.search_cancle);

        Drawable leftDrawable=searchText.getCompoundDrawables()[0];
        leftDrawable.setBounds(0,0,40,40);
        searchText.setCompoundDrawables(leftDrawable,null,null,null);

        //提示框点击回调函数
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                suggestClick=true;
                String text=searchList.getAdapter().getItem(position).toString();
                searchText.setText(text);
                searchText.setSelection(text.length());
                searchList.setVisibility(View.GONE);
            }
        });

        //取消图片点击回调函数
        searchCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.setVisibility(View.GONE);
                searchText.setText("");
            }
        });

        //搜索按钮点击回调函数
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                processSearch();
            }
        });

        //搜索框内容改变回调函数
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(suggestClick){
                    suggestClick=false;
                    return;
                }

                if(s.length()<2){
                    searchList.setVisibility(GONE);
                    if(s.length()==0){
                        searchCancle.setVisibility(GONE);
                    }
                    else{
                        searchCancle.setVisibility(VISIBLE);
                    }
                }
                else{
                    //获取匹配搜索项
                    if(myListener!=null&&s.length()>=2){
                        myListener.getMatching(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    searchList.setVisibility(GONE);
                    //搜索处理
                    processSearch();
                }
                return true;
            }
        });
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        initViews();
    }

    public void setSuggestList(BaseAdapter suggest){
        if(suggest==null) return;
        if(suggest.getCount()==0) searchList.setVisibility(GONE);
        else searchList.setVisibility(VISIBLE);
        searchList.setAdapter(suggest);
    }
}
