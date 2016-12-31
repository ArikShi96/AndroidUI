package com.example.root.experimentassistant.Internet;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

/**
 * Created by root on 2016/12/19.
 */
public class myHttpClient extends AsyncHttpClient {
    @Override
    public void setEnableRedirects(final boolean enableRedirects) {
        ((DefaultHttpClient) getHttpClient()).setRedirectHandler(new DefaultRedirectHandler() {
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 301 || statusCode == 302) {
                    return enableRedirects;
                }
                return false;
            }
        });
    }
}
