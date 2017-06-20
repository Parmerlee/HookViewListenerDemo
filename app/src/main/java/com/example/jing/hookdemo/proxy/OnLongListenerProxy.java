package com.example.jing.hookdemo.proxy;

import android.view.View;

/**
 * author : zhongwr on 2016/12/30
 * 长按点击代理
 */
public class OnLongListenerProxy implements View.OnLongClickListener {

    private static final String TAG = "OnClickListenerProxy";
    private View.OnLongClickListener onLongClickListener;
    private OnListenerProxyCallBack.OnLongClickProxyListener longClickProxyListener;

    public OnLongListenerProxy(View.OnLongClickListener onLongClickListener, OnListenerProxyCallBack.OnLongClickProxyListener longClickProxyListener) {
        this.onLongClickListener = onLongClickListener;
        this.longClickProxyListener = longClickProxyListener;
    }


    @Override
    public boolean onLongClick(View v) {
        if (null != longClickProxyListener) {
            longClickProxyListener.onLongClickProxy(v);
        }
        if (null != onLongClickListener) {
            return onLongClickListener.onLongClick(v);
        }
        return false;
    }
}
