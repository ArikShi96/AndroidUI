package com.example.root.experimentassistant.StaticSetting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.experimentassistant.MyView.LoadingFailedActivity;
import com.example.root.experimentassistant.MyView.SuccessActivity;
import com.example.root.experimentassistant.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.File;

import static com.example.root.experimentassistant.R.layout.dialog_loading;

/**
 * Created by Json on 2016/12/26.
 */
public class StaticConfig {
    public static final String BASE_URL="http://111.231.83.220:8088/";
    public static final String IMAGE_STORAGE_URL= Environment.getExternalStorageDirectory().toString()+ File.separator+"experiment/image/";
    public static final DisplayImageOptions options=new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.progress_ing)
            .showImageForEmptyUri(R.mipmap.icon_progress_fail)
            .showImageOnFail(R.mipmap.icon_progress_fail)
            .delayBeforeLoading(0)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        loadingDialog.show();

        return loadingDialog;
    }

    /**
     * 关闭dialog
     *
     * http://blog.csdn.net/qq_21376985
     *
     * @param mDialogUtils
     */
    public static void closeDialog(Dialog mDialogUtils) {
        if (mDialogUtils != null && mDialogUtils.isShowing()) {
            mDialogUtils.dismiss();
        }
    }

    public static Intent errorPage(Context context, String title, String err_msg){
        Intent err=new Intent();
        err.setClass(context, LoadingFailedActivity.class);
        Bundle bundle=new Bundle();
        bundle.putCharSequence("title", title);
        bundle.putCharSequence("err_msg", err_msg);
        err.putExtras(bundle);
        return err;
    }

    public static Intent successPage(Context context, String title, String success_msg){
        Intent success=new Intent();
        success.setClass(context, SuccessActivity.class);
        Bundle bundle=new Bundle();
        bundle.putCharSequence("title", title);
        bundle.putCharSequence("success_msg", success_msg);
        success.putExtras(bundle);
        return success;
    }

}