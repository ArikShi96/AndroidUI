package com.example.root.experimentassistant.ThirdLevel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.root.experimentassistant.Adapter.AlbumGridViewAdapter;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ImageItem;
import com.example.root.experimentassistant.util.AlbumHelper;
import com.example.root.experimentassistant.util.Bimp;
import com.example.root.experimentassistant.util.ImageBucket;


public class AlbumActivity extends Activity {
    public static final int num=9;
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    //完成按钮
    private Button okButton;
    // 返回按钮
    private ImageView back;

    private Intent intent;
    // 预览按钮
    private Button preview;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    public static Bitmap bitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        init();
        initListener();
        //这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    // 预览按钮的监听
    private class PreviewListener implements OnClickListener {
        public void onClick(View v) {
//            if (Bimp.tempSelectBitmap.size() > 0) {
//                intent.putExtra("position", "1");
//                intent.setClass(AlbumActivity.this, GalleryActivity.class);
//                startActivity(intent);
//            }
        }

    }

    // 完成按钮的监听
    private class AlbumSendListener implements OnClickListener {
        public void onClick(View v) {
//            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//            intent.setClass(mContext, MainActivity.class);
//            startActivity(intent);
//            finish();
        }

    }

    // 返回按钮监听
    private class BackListener implements OnClickListener {
        public void onClick(View v) {
//            intent.setClass(AlbumActivity.this, ImageFile.class);
//            startActivity(intent);
        }
    }

    // 取消按钮的监听
    private class CancelListener implements OnClickListener {
        public void onClick(View v) {
//            Bimp.tempSelectBitmap.clear();
//            intent.setClass(mContext, MainActivity.class);
//            startActivity(intent);
        }
    }



    // 初始化，给一些对象赋值
    private void init() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll( contentList.get(i).imageList );
        }

        back = (ImageView) findViewById(R.id.Cancle);
        back.setOnClickListener(new BackListener());
        preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(new PreviewListener());
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
                Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                + "/"+num+")");
    }

    private void initListener() {

        gridImageAdapter
                .setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,Button chooseBt) {
                        if (Bimp.tempSelectBitmap.size() >= num) {
                            toggleButton.setChecked(false);
                            chooseBt.setVisibility(View.GONE);
                            if (!removeOneData(dataList.get(position))) {
                                Toast.makeText(AlbumActivity.this, "超出可选图片数量",
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        if (isChecked) {
                            chooseBt.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                                    + "/"+num+")");
                        } else {
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                                    + "/"+num+")");
                        }
                        isShowOkBt();
                    }
                });

        okButton.setOnClickListener(new AlbumSendListener());

    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                    + "/"+num+")");
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                    + "/"+num+")");
            preview.setPressed(true);
            okButton.setPressed(true);
            preview.setClickable(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
            preview.setTextColor(Color.WHITE);
        } else {
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                    + "/"+num+")");
            preview.setPressed(false);
            preview.setClickable(false);
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
            preview.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }

    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
