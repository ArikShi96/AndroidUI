package com.example.root.experimentassistant.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.root.experimentassistant.FirstLevel.Experiment;
import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.ViewModel.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 2016/12/20.
 */
public class Courses {
    private Context mContext;

    //缓存默认显示数据
    private List<ViewCourse> defaultCourses;

    //缓存结果
    private List<ViewCourse> searchResult;

    //初始化函数，有网取数据，无网读文件缓存
    public Courses(Context context) {
        Log.d("Course","create");
        defaultCourses = new ArrayList<ViewCourse>();
        mContext=context;

        if(isInternetCon()){    //有网
            Log.d("connect","on");
            getDefault(new RequestCallBack() {
                @Override
                public void onRequestSuccess(Object sender) {
                    Log.d("course getDef","onSuccess");
                    saveRecentCourses();
                }

                @Override
                public void onRequestFailure(Object sender) {

                }
            });
        }
        else{   //无网
            Log.d("connect","off");
            readRecentCourses();
        }
    }

    //检查网络状态
    private boolean isInternetCon(){
        if(mContext!=null){
            ConnectivityManager connect=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=connect.getActiveNetworkInfo();
            if(info!=null){
                info.isAvailable();
            }
        }
        return false;
    }
    /*-----------------文件缓存--------------------*/
    private final String fileName="cachedata_"+"courses"+".dat";

    private void saveRecentCourses(){
        Log.d("cache","save");
        File file = new File(mContext.getFilesDir(),fileName);

        try{
            if(!file.exists()) file.createNewFile();
            Log.d("cache","file "+file.getAbsolutePath());
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
            oos.writeObject(defaultCourses);
            oos.flush();
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("cache","save excpt");
        }
    }

    private void readRecentCourses(){
        File file=new File(mContext.getFilesDir(),fileName);
        Log.d("cache","file: "+file.getAbsolutePath());
        if(!file.exists()) {
            Log.d("cache","no file");
            return;
        }

        try{
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
            defaultCourses.addAll((ArrayList<ViewCourse>)ois.readObject());
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d("cache","except");
        }
    }

    /*-----------------对外操作接口--------------------*/

    //获取初始20条数据
    public void getDefault(final RequestCallBack callBack) {
        ExperimentHttpClient.getInstance().get("api/courses/search", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                defaultCourses.clear();
                try {
                    JSONArray courses = response.getJSONArray("data");
                    if (null != courses) {
                        for (int i =0; i < courses.length(); i++) {
                            JSONObject course = courses.getJSONObject(i);
                            ViewCourse viewCourse = new ViewCourse();

                            viewCourse.setId(course.getString("id"));
                            viewCourse.setCourseName(course.getString("name"));
                            viewCourse.setTeacher(course.getString("teacher"));
                            viewCourse.setTime(course.getString("time"));
                            defaultCourses.add(viewCourse);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                saveRecentCourses();
                if(callBack!=null){
                    callBack.onRequestSuccess(defaultCourses);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable,
                                  JSONObject errorResponse){
                Log.d("Courses default fail","status" + statusCode);
                if(defaultCourses.size()==0){
                    Log.d("Course","read cacheData");
                    readRecentCourses();
                }
                if(callBack!=null) callBack.onRequestSuccess(defaultCourses);
            }
        });
    }

    //再获取10条数据
    public List<ViewCourse> getTenMore() {
        return null;
    }

    //获取建议列表
    public void getSuggest(String word,final RequestCallBack callBack) {
        RequestParams params=new RequestParams();
        params.put("q",word);

        ExperimentHttpClient.getInstance().get("api/courses/suggest",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                JSONArray suggests = response.optJSONArray("data");
                if (null == suggests) {
                    return;
                }
                List<String> suggestList=new ArrayList<String>();
                try{
                    for(int i=0; i < suggests.length(); i++){
                        suggestList.add(suggests.getString(i));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d("course suggest","error request params");
                }
                if(callBack!=null){
                    callBack.onRequestSuccess(suggestList);
                }
            }
            @Override
            public void onFailure(int v1, Header[] v2, String v3, Throwable v4){
                Log.d("getSuggest",""+v1);
            }
        });
    }

    //获取搜索结果
    public void search(String word,final RequestCallBack callBack) {
        searchResult = new ArrayList<ViewCourse>();
        RequestParams params=new RequestParams();
        params.put("q", word);

        ExperimentHttpClient.getInstance().get("api/courses/search",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray courses = response.optJSONArray("data");
                    if (null == courses) {
                        if(callBack!=null){
                            callBack.onRequestSuccess(searchResult);
                        }
                        return;
                    }

                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject object = courses.getJSONObject(i);
                        ViewCourse course = new ViewCourse();

                        course.setId(object.getString("id"));
                        course.setCourseName(object.getString("name"));
                        course.setTeacher(object.getString("teacher"));
                        course.setTime(object.getString("time"));
                        searchResult.add(course);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(callBack!=null){
                    callBack.onRequestSuccess(searchResult);
                }
            }

        });

    }

}
