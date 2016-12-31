package com.example.root.experimentassistant.Internet;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by root on 2016/12/11.
 */
public class HttpRequest {
    static HttpRequest mHttpRequest;

    private HttpRequest(){}

    static public HttpRequest getInstance(){
        if(mHttpRequest==null){
            mHttpRequest=new HttpRequest();
        }
        return mHttpRequest;
    }

    //以post的方法访问url，params为参数列表
    public String doPost(String url,List<NameValuePair> params){
        String result=null;
        HttpClient httpClient=new DefaultHttpClient();
        HttpPost request=new HttpPost(url);

        try{
            if(params!=null){
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,3000);

            HttpResponse response=httpClient.execute(request);
            if(response.getStatusLine().getStatusCode()==200){
                result= EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
            }
            else{
                Log.i("PostRequest","failed");
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return result;
    }

    public String doGet(String url){
        String result=null;
        HttpClient httpClient=new DefaultHttpClient();
        HttpGet request=new HttpGet(url);
        try{
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,3000);

            HttpResponse response=httpClient.execute(request);
            if(response.getStatusLine().getStatusCode()==200){
                result= EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
            }
            else{
                Log.i("PostRequest","failed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
