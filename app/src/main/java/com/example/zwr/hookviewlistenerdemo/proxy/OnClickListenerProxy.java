package com.example.zwr.hookviewlistenerdemo.proxy;

import android.view.View;

/**
 * author : zhongwr on 2016/12/30
 * 点击代理
 */
public class OnClickListenerProxy implements View.OnClickListener {

    private static final String TAG = "OnClickListenerProxy";
    private View.OnClickListener onClickListener;
    private OnListenerProxyCallBack.OnClickProxyListener onClickProxyListener;

    public OnClickListenerProxy(View.OnClickListener onClickListener, OnListenerProxyCallBack.OnClickProxyListener onClickProxyListener) {
        this.onClickListener = onClickListener;
        this.onClickProxyListener = onClickProxyListener;
    }

    @Override
    public void onClick(View v) {
        if (null != onClickProxyListener) {//点击代理回调
            onClickProxyListener.onClickProxy(v);
        }
        if (null != onClickListener) {
            onClickListener.onClick(v);
        }
    }
}
