package com.example.root.experimentassistant.Model;

/**
 * Created by root on 2016/12/27.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.ViewModel.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class StudentInfo {
    private Context mContext;

    public StudentInfo(Context context){
        mContext=context;
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

    //检测登录状态和网络状态
    private boolean isNoConOrNoLogin(final RequestCallBack callBack){
        if(User.getInstance().isLogin()==false){
            if(callBack!=null){
                callBack.onRequestFailure(STATUS_NOLOGIN);
            }
            return true;
        }
//        if(isInternetCon()==false){
//            if(callBack!=null){
//                callBack.onRequestFailure(STATUS_NOCONN);
//            }
//            return true;
//        }
        return false;
    }
    /*-----------------状态码-----------------------*/
    static final public int STATUS_NOLOGIN=-1;
    static final public int STATUS_NOCONN=-2;

    /*------------------对外接口-------------------*/
    //获取正在进行课程 List<ViewCourse>
    public void getMyCourse(final RequestCallBack callBack){
        if(isNoConOrNoLogin(callBack)) return;

        RequestParams params=new RequestParams();
        params.put("student_id",User.getInstance().getId());

        ExperimentHttpClient.getInstance().post("student/mycourse", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode==200){
                    List<ViewCourse> courses=new ArrayList<ViewCourse>();

                    try{
                        for(int i=0;i<response.length();i++){
                            JSONObject object=response.getJSONObject(i);
                            ViewCourse course=new ViewCourse();

                            course.setTime(object.getString("time"));
                            course.setTeacher(object.getString("teacher"));
                            course.setCourseName(object.getString("courseName"));
                            course.setId(object.getString("id"));

                            courses.add(course);
                        }

                        if(callBack!=null){
                            callBack.onRequestSuccess(courses);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.d("studentInfo","myCourseE");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("getMyCourseFail",""+statusCode);
                if(callBack!=null){
                    callBack.onRequestFailure(statusCode);
                }
            }
        });
    }

    //获取实验分数 List<ViewExperScore>
    public void getMyExperScore(final RequestCallBack callBack){
        if(isNoConOrNoLogin(callBack)) return;

        RequestParams params=new RequestParams();
        params.put("student_id",User.getInstance().getId());

        ExperimentHttpClient.getInstance().post("student/expanswer", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode==200){
                    List<ViewExperScore> scores=new ArrayList<ViewExperScore>();

                    try{
                        for(int i=0;i<response.length();i++){
                            JSONObject object=response.getJSONObject(i);
                            ViewExperScore score=new ViewExperScore();

                            score.setCourseName(object.getString("course_name"));
                            score.setExperName(object.getString("experiment_name"));
                            score.setScore(object.getString("score"));

                            scores.add(score);
                        }

                        if(callBack!=null){
                            callBack.onRequestSuccess(scores);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.d("studentInfo","myScoreE");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("getMyScoreFail",""+statusCode);
                if(callBack!=null){
                    callBack.onRequestFailure(statusCode);
                }
            }
        });
    }

    //获取历史课程 List<ViewCourse>
    public void getMyCourseHistory(final RequestCallBack callBack){
        if(isNoConOrNoLogin(callBack)) return;

        RequestParams params=new RequestParams();
        params.put("student_id",User.getInstance().getId());

        ExperimentHttpClient.getInstance().post("student/historycourse", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode==200){
                    List<ViewCourse> courses=new ArrayList<ViewCourse>();

                    try{
                        for(int i=0;i<response.length();i++){
                            JSONObject object=response.getJSONObject(i);
                            ViewCourse course=new ViewCourse();

                            course.setTime(object.getString("time"));
                            course.setTeacher(object.getString("teacher"));
                            course.setCourseName(object.getString("courseName"));
                            course.setId(object.getString("id"));

                            courses.add(course);
                        }

                        if(callBack!=null){
                            callBack.onRequestSuccess(courses);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.d("studentInfo","myHistoryCourseE");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("getOldCourseFail",""+statusCode);
                if(callBack!=null){
                    callBack.onRequestFailure(statusCode);
                }
            }
        });
    }
}
