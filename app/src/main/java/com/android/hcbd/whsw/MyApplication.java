package com.android.hcbd.whsw;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.hcbd.whsw.crash.Cockroach;
import com.android.hcbd.whsw.entity.LoginInfo;
import com.android.hcbd.whsw.utils.SharedPreferencesUtil;
import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.pgyersdk.crash.PgyCrashManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

/**
 * Created by guocheng on 2017/8/22.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        SDKInitializer.initialize(this);
        Utils.init(this);
        initOkGo();
        initCockroach();
        PgyCrashManager.register(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    private void initCockroach() {
        Cockroach.install(new Cockroach.ExceptionHandler() {
            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("AndroidRuntime","--->CockroachException:"+thread+"<---",throwable);
                            //Toast.makeText(mContext, "Exception Happend\n" + thread + "\n" + throwable.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(6000/*OkGo.DEFAULT_MILLISECONDS*/, TimeUnit.MILLISECONDS);

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //必须设置OkHttpClient
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                  //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        //.addCommonHeaders(headers)                      //全局公共头
        //.addCommonParams(params);                       //全局公共参数
    }

    public String getPowerStr(String str){
        String powerStr = "";
        List<LoginInfo.menuInfo> menuList = MyApplication.getInstance().getLoginInfo().getMenuList();
        for(LoginInfo.menuInfo menuInfo : menuList){
            if(menuInfo.getName().equals(str)){
                powerStr = menuInfo.getCode();
                break;
            }
        }
        return powerStr;
    }

    public LoginInfo getLoginInfo() {
        String s = SharedPreferencesUtil.get(this, "login_info");
        LoginInfo loginInfo = gson.fromJson(s,LoginInfo.class);
        return loginInfo;
    }

    public String getIpInfo(){
        return SharedPreferencesUtil.getByDefault(this, "ip_info","112.124.108.24:8685");
    }

    public String getBsaeUrl(){
        return "http://"+ getIpInfo()+"/whsw";
    }

    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}
