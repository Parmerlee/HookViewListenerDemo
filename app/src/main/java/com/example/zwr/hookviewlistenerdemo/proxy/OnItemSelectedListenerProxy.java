package com.example.zwr.hookviewlistenerdemo.proxy;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * author : zhongwr on 2016/12/30
 * listview或gridView点击代理
 */
public class OnItemSelectedListenerProxy implements OnItemSelectedListener {
    private OnItemSelectedListener onItemSelectedListener;
    private OnListenerProxyCallBack.OnItemSelectedProxyListener onItemSelectedProxyListener;

    public OnItemSelectedListenerProxy(OnItemSelectedListener onItemSelectedListener, OnListenerProxyCallBack.OnItemSelectedProxyListener onItemSelectedProxyListener) {
        this.onItemSelectedListener = onItemSelectedListener;
        this.onItemSelectedProxyListener = onItemSelectedProxyListener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (null != onItemSelectedListener) {
            onItemSelectedListener.onItemSelected(parent, view, position, id);
        }
        if (null != onItemSelectedProxyListener) {
            onItemSelectedProxyListener.onItemSelected(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (null != onItemSelectedListener) {
            onItemSelectedListener.onNothingSelected(parent);
        }
        if (null != onItemSelectedProxyListener) {
            onItemSelectedProxyListener.onNothingSelected(parent);
        }
    }
}
