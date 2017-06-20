package com.example.jing.hookdemo;

import android.app.Application;

import static com.example.jing.hookdemo.MainActivity.attachContext;

/**
 * Created by Administrator on 2017/6/20.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            attachContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
