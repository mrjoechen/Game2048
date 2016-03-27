package com.example.chenqiao.game2048;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import net.youmi.android.AdManager;

import net.youmi.android.spot.SpotManager;


/**
 * Created by CHENQIAO on 2016/3/23.
 */
public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isNetworkAvailable(SplashActivity.this)) {
            Toast.makeText(getApplicationContext(), "当前无可用网络", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }else {
            //在当前的splash页面停留一会，然后自动跳入到home
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                            finish();
                        }
                    });
                }
            }.start();
        }

//初始化广告
        AdManager.getInstance(this).init("4d5b512731e943de", "00eaf0f1bba18bdd", true);

        //在当前页面加入插屏广告
        SpotManager.getInstance(this).loadSpotAds();
        SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
        SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_SIMPLE);
        SpotManager.getInstance(this).showSpotAds(this);
    }


    //当用户点击手机的back键会调用该函数
    public void onBackPressed() {
        // super.onBackPressed();

        // 如果有需要，可以点击后退关闭插播广告。
        if (!SpotManager.getInstance(this).disMiss()) {
            // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
            super.onBackPressed();
            finish();
        }
        Log.i("splash", "onBackPressed");
    }

    @Override
    protected void onDestroy() {
        SpotManager.getInstance(  this).onDestroy();
        super.onDestroy();
    }


    //判断当前网络状态
    public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    /*
                    System.out: 0===状态===DISCONNECTED
                    System.out: 0===类型===MOBILE
                    System.out: 1===状态===CONNECTED
                    System.out: 1===类型===WIFI
                     */
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
