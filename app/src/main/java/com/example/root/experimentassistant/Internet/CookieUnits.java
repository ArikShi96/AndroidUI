package com.example.root.experimentassistant.Internet;


import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.cookie.Cookie;

/**
 * Created by root on 2016/12/15.
 */

//单例类 用于全局统一cookie
public class CookieUnits {
    private static PersistentCookieStore mCookieStore=null;

    public static void setCookieStore(PersistentCookieStore cookieStore){
        mCookieStore=cookieStore;
    }

    private static Context appContext;

    public static void setAppContext(Context context){
        appContext=context;
    }

    public static PersistentCookieStore getCookieStore(){
        mCookieStore=new PersistentCookieStore(appContext);
        return mCookieStore;
    }
}
