package com.ofu.baseapp.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.ofu.baseapp.BuildConfig;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.yokeyword.fragmentation.Fragmentation;
import okhttp3.OkHttpClient;

/**
 * Created by ofu_zs on 2017/8/19.
 */

public class BaseApp extends MultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        initFragmentation();
        initOkGo();
        initStetho();
        initLeakCanary();
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    private void initFragmentation() {
        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install();
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void initOkGo() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //非必要情况，不建议使用，第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        //使用内存保持cookie，app退出后，cookie消失
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        OkGo.getInstance()
                .init(this)
                //建议设置OkHttpClient，不设置将使用默认的//必须调用初始化
                .setOkHttpClient(builder.build())
                //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                //全局统一缓存时间，默认永不过期，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(1);

    }

    private void initLeakCanary(){
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
