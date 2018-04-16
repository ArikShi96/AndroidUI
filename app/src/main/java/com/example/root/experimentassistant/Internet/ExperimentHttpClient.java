package com.example.root.experimentassistant.Internet;

import android.util.Log;
import com.loopj.android.http.*;

import java.net.URI;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ProtocolException;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.RedirectHandler;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by root on 2016/12/15.
 */
public class ExperimentHttpClient  {
    private static final String url="http://111.231.83.220:8088/";

    private static AsyncHttpClient myClient;

    private static ExperimentHttpClient myEHC=null;

    public static ExperimentHttpClient getInstance(){
        if(myEHC==null){
            myEHC=new ExperimentHttpClient();
        }

        return myEHC;
    }

    private ExperimentHttpClient(){
        myClient=new AsyncHttpClient();

        //设置超时时间
        myClient.setConnectTimeout(10);
        myClient.setResponseTimeout(10);
    }

    private String getAbsoluteUrl(String rPath){
        return url+rPath;
    }

    private void bindCookie(){
        myClient.setCookieStore(CookieUnits.getCookieStore());
    }

    public void get(String rPath, RequestParams params, AsyncHttpResponseHandler responseHandler){
        bindCookie();
        myClient.addHeader("content-type", "application/json");
        myClient.get(getAbsoluteUrl(rPath), params, responseHandler);
    }

    public void post(String rPath, RequestParams params, AsyncHttpResponseHandler responseHandler){
        bindCookie();
        if (null != params) {
            params.setUseJsonStreamer(true);
        }
        myClient.post(getAbsoluteUrl(rPath),params,responseHandler);
    }
}
