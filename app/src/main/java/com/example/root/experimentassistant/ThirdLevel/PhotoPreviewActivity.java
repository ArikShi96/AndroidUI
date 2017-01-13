package com.example.root.experimentassistant.ThirdLevel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.ViewGroup.LayoutParams;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.util.Bimp;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class PhotoPreviewActivity extends AppCompatActivity {
    private ImageView back;

    private Button delBtn;

    private Button okBtn;

    private ViewPager viewPager;

    private static int num=9;

    private int position;

    ArrayList<View> viewArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("preview","start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        back=(ImageView)findViewById(R.id.Cancle);
        delBtn=(Button)findViewById(R.id.photoDelete);
        okBtn=(Button)findViewById(R.id.ok_button);
        viewPager=(ViewPager)findViewById(R.id.photoPage);
        position=getIntent().getExtras().getInt("position");

        init();
    }

    private void init(){
        initPager();

        //后退按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
                finish();
            }
        });

        //完成按钮
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
                finish();
            }
        });

        //删除按钮
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bimp.tempSelectBitmap.size()==1){
                    Bimp.tempSelectBitmap.clear();
                    Intent intent = new Intent("data.broadcast.action");
                    sendBroadcast(intent);
                    finish();
                }
                else{
                    Bimp.tempSelectBitmap.remove(position);
                    if(position==Bimp.tempSelectBitmap.size()) position--;
                    initPager();
                }
            }
        });

        //切页监听
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageSelected(int arg0) {
                position = arg0;
                Log.d("page",position+"");
                Log.d("bitmap",Bimp.tempSelectBitmap.size()+"");
                Log.d("adapter",viewPager.getAdapter().getCount()+"");
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void initPager() {
        okBtn.setText("完成" + "(" + Bimp.tempSelectBitmap.size()
                + "/" + num + ")");

        viewArrayList = new ArrayList<View>();
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            View view= LayoutInflater.from(this).inflate(R.layout.photo_pager,null);
            Log.d("isNull",view==null?"NULL":"NotNUll");
            ImageView image=(ImageView)view.findViewById(R.id.photoPreview);
            image.setImageBitmap(Bimp.tempSelectBitmap.get(i).getBitmap());
            viewArrayList.add(view);
        }
        viewPager.setAdapter(new MyPagerAdapter(viewArrayList));
        viewPager.setCurrentItem(position);
    }

    private class MyPagerAdapter extends PagerAdapter{
        private ArrayList<View> viewsList;

        public MyPagerAdapter(ArrayList<View> list){
            viewsList=list;
        }

        public void update(ArrayList<View> list){
            viewsList=list;
            notifyDataSetChanged();
        }

        public int getCount(){
            return viewsList.size();
        }

        public boolean isViewFromObject(android.view.View arg0, java.lang.Object arg1){
            return arg0==arg1;
        }

        public java.lang.Object instantiateItem(android.view.View container, int position){
            try {
                ((ViewPager)container).addView(viewsList.get(position%viewsList.size()));

            } catch (Exception e) {
            }
            return viewsList.get(position%viewsList.size());
        }

        public void destroyItem(android.view.ViewGroup container, int position, java.lang.Object object){
            ((ViewPager)container).removeView(viewsList.get(position%viewsList.size()));
        }
    }
}
