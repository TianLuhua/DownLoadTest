package com.example.administrator.downloadtest;

import android.app.Application;

import com.example.administrator.downloadtest.db.ATDbManager;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.URLConnectionNetworkExecutor;
import com.yolanda.nohttp.cache.DiskCacheStore;
import com.yolanda.nohttp.cookie.DBCookieStore;

/**
 * Created by Administrator on 2016/11/27 0027.
 */

public class MyApplication extends Application {

    private static ATDbManager aTDbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initNoHttp();
        initLJDB();
    }

    private void initLJDB() {
        if (aTDbManager == null) {
            aTDbManager = new ATDbManager(MyApplication.this);
        }
    }

    public static ATDbManager getATDbManager() {
        return aTDbManager;
    }


    private void initNoHttp() {
        NoHttp.Config config = new NoHttp.Config();
        config.setConnectTimeout(5 * 1000);
        config.setReadTimeout(5 * 1000);
        config.setCacheStore(new DiskCacheStore(this));
        config.setCookieStore(new DBCookieStore(this).setEnable(true));
        config.setNetworkExecutor(new URLConnectionNetworkExecutor());
        NoHttp.initialize(this, config);
    }
}
