package com.example.root.experimentassistant.Internet;

import android.util.Log;

import com.example.root.experimentassistant.StaticSetting.StaticConfig;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 2017/1/11.
 */
public class MultiFileUpload {
    static public String uploadFiles(String rPath, List<String> filesList,Map<String,String> params){
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "--------boundary";

        try{
            URL url=new URL(StaticConfig.BASE_URL+rPath);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            //配置连接参数
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("charset","UTF-8");
            con.setRequestProperty("Content-Type","\"multipart/form-data;boundary=\" + boundary");

            //向Http报表写文件
            DataOutputStream dos=new DataOutputStream(con.getOutputStream());

            for(int i=0;i<filesList.size();i++){
                String filePath=filesList.get(i);
                int endFileIndex=filePath.lastIndexOf("/");
            }
        }
        catch (Exception e){
            Log.d("MultiPart","exception");
            e.printStackTrace();
        }


        return null;
    }
}
