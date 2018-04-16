package com.example.root.experimentassistant.Model;

import com.example.root.experimentassistant.ViewModel.*;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.example.root.experimentassistant.Internet.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 2016/12/18.
 */
public class User {
    /*--------------常量定义----------------*/
    public static String STORE_NAME_KEY = "userId";
    public static String STORE_PHONE_KEY = "userPhoneNum";
    public static String STORE_TOKEN_KEY = "userName";
    public static String STORE_ID_KEY = "token";

    /*--------------属性定义----------------*/
    private String id;

    private String name;

    private String phoneNum;

    private int currentWeek;

    private String token;

    private ExperModel model;

    public String getId(){return id;}

    public String getName(){return name;}

    public String getPhoneNum(){return phoneNum;}

    public String getToken(){return token;}

    public ExperModel getExperiment(){
        return model;
    }
    public void setExperiment(ExperModel model){
        this.model=model;
    }

    public void setCurrentWeek(SharedPreferences preferences, int week) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        Log.d("setCurrentWeek",""+week);
        if (week <= 0) week = 1;
        if (week > 20) week = 20;
        currentWeek = week;
        //将当前日期 周数存储
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String nowDate = sdf.format(date);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastUsingDate", nowDate);
        editor.putInt("weekCnt", currentWeek);
        editor.commit();
    }

    public int getCurrentWeek(SharedPreferences preferences) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        String beforeDate = preferences.getString("lastUsingDate", "");
        int weekCnt = preferences.getInt("weekCnt", 0);

        Log.d("getCurWeek","bDate"+beforeDate);
        Log.d("getCurWeek","weekCnt "+weekCnt);

        SharedPreferences.Editor editor = preferences.edit();
        //为初始化或小于0错误
        if (weekCnt <= 0) {
            setCurrentWeek(preferences, 1);
            return 1;
        }

        //返回值 现周数

        int rnt = weekCnt;

        try {
            //计算前一次使用日期与现日期中间的星期数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            Date before = sdf.parse(beforeDate);

            Log.d("nowDate",sdf.format(now));
            //前一次使用日期晚于现日期则保留星期数记录现日期
            if (now.before(before)) {
                setCurrentWeek(preferences, weekCnt);
                return weekCnt;
            }
            //现周数=上次使用周数+经过周数
            Log.d("weekBet",weekCntBetween(before, now)+"");
            rnt += weekCntBetween(before, now);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(rnt>25) rnt=25;
        //更新日期数
        setCurrentWeek(preferences, rnt);
        return rnt;
    }

    //获取日期在一年中周数
    private int getWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);

        int rnt = calendar.get(Calendar.WEEK_OF_YEAR);
        if (calendar.get(Calendar.MONTH) > 11 && rnt == 1) {
            rnt += 52;
        }

        return rnt;
    }

    //获取日期年份
    private int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    //获取两个日期之间的星期
    private int weekCntBetween(Date before, Date now) {
        if (now.before(before)) {
            Date tmp = before;
            before = now;
            now = tmp;
        }

        Log.d("WeekOfYear","now "+getWeekOfYear(now));
        Log.d("WeekOfYear","before "+getWeekOfYear(before));
        Log.d("getYear","now "+getYear(now));
        Log.d("getYear","before "+getYear(before));

        if(getWeekOfYear(now)<getWeekOfYear(before)) return 0;

        return getWeekOfYear(now) - getWeekOfYear(before) +
                (getYear(now) - getYear(before)) * 52;
    }


    /*--------------单例类----------------*/
    static private User user = null;

    static public User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void Init(SharedPreferences preferences) {
        id = preferences.getString(STORE_ID_KEY, "");
        phoneNum = preferences.getString(STORE_PHONE_KEY, "");
        name = preferences.getString(STORE_NAME_KEY, "");
        token = preferences.getString(STORE_TOKEN_KEY, "");
    }

    /*--------------对外方法----------------*/
    public boolean isLogin() {
        if (id != null && !id.isEmpty()) {
            Log.d("isLog", "ing");
            return true;
        } else return false;
    }

    //登录
//    Activity callActivity;
    public boolean Login(String Id, String Pass, final RequestCallBack callBack) {
        if (isLogin()) return true;

        //设置参数
        RequestParams params = new RequestParams();

        params.put("identify", Id);
        params.put("password", Pass);

        Log.d("Login", "id" + Id + " pass" + Pass);
        ExperimentHttpClient.getInstance().post("api/sessions", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        id = data.getString("identify");
                        name = data.getString("name");
                        token = response.getString("token");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (callBack != null) callBack.onRequestSuccess(null);
                }
            }

            @Override
            public void onFailure(int v1, Header[] v2, Throwable v3, JSONObject errorResponse){
                String message = errorResponse.optString("message");
                if (message.isEmpty()) {
                    message = "未知原因，登录失败";
                }
                if(callBack!=null) callBack.onRequestFailure(message);
            }
        });

        return isLogin();
    }

    //发送验证码
    public void getVertifyCode(String phoneNum) {
        RequestParams params = new RequestParams();
        params.put("phoneNumber", phoneNum);

        Log.d("UserVer", "phone " + phoneNum);

        ExperimentHttpClient.getInstance().get("api/users/code", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("getVer", "success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("getVer", "Failure " + statusCode);
            }
        });
    }

    //注册
    private int flag;

    public int Register(String Id, String Name, String PhoneNum, String VertifyCode, String Pass, final RequestCallBack callBack) {
        RequestParams params = new RequestParams();

        params.put("identify", Id);
        params.put("name", Name);
        params.put("password", Pass);
        params.put("code", VertifyCode);
        params.put("phoneNumber", PhoneNum);
        params.put("role", "student");

        ExperimentHttpClient.getInstance().post("api/users", params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String message = errorResponse.optString("message");
                if (message.isEmpty()) {
                    message = "未知原因，注册失败";
                }
                if(callBack!=null) callBack.onRequestFailure(message);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(callBack!=null) callBack.onRequestSuccess(null);
            }
        });
        return flag;
    }

    //登出
    public void Logoff(SharedPreferences preferences) {
        if (isLogin() == false) return;

        id = "";
        name = "";
        phoneNum = "";
        token = "";

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(STORE_ID_KEY, User.getInstance().getId());
        editor.putString(STORE_PHONE_KEY, User.getInstance().getPhoneNum());
        editor.putString(STORE_NAME_KEY, User.getInstance().getName());
        editor.putString(STORE_TOKEN_KEY, User.getInstance().getToken());
        editor.putInt("weekCnt",0);
        editor.apply();

        ExperimentHttpClient.getInstance().post("api/sessions/signout", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    //获取本周实验
    private List<ViewExper> expers;

    public List<ViewExper> getCntExper(int week, final RequestCallBack callBack) {
        if (isLogin() == false) return null;

        RequestParams params = new RequestParams();
        params.put("student_id", id);
        params.put("week", "第"+week+"周");

        expers = new ArrayList<ViewExper>();
        ExperimentHttpClient.getInstance().post("student/expers", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode != 200) {
                    Log.d("User get expers", "status " + statusCode);
                    return;
                }
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        ViewExper exper = new ViewExper();

                        exper.setId(object.getString("id"));
                        exper.setCourseName(object.getString("courseName"));
                        exper.setExperName(object.getString("experName"));
                        exper.setTime(object.getString("time"));
                        expers.add(exper);
                    }
                } catch (Exception e) {
                    Log.d("User get expers", "no such key");
                    e.printStackTrace();
                }
                if (callBack != null) callBack.onRequestSuccess(expers);
            }

            @Override
            public void onFailure(int v1, Header[] v2, Throwable v3, JSONObject v4) {
                Log.d("getExper", "failure" + v1);
                if (callBack != null) callBack.onRequestFailure(null);
            }
        });

        return expers;
    }
}
