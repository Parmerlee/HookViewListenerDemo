package com.example.jing.hookdemo.proxy;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * author : zhongwr on 2016/12/30
 * listview或gridView点击代理
 */
public class OnItemClickListenerProxy implements OnItemClickListener {
    private OnItemClickListener onItemClickListener;
    private OnListenerProxyCallBack.OnItemClickProxyListener onItemClickProxyListener;

    public OnItemClickListenerProxy(OnItemClickListener onItemClickListener, OnListenerProxyCallBack.OnItemClickProxyListener onItemClickProxyListener) {
        this.onItemClickListener = onItemClickListener;
        this.onItemClickProxyListener = onItemClickProxyListener;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != onItemClickListener) {
            onItemClickListener.onItemClick(parent, view, position, id);
        }
        if (null != onItemClickProxyListener) {
            onItemClickProxyListener.onItemClick(parent, view, position, id);
        }
    }
}
