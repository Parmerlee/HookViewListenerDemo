package com.example.jing.hookdemo.proxy;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * author : zhongwr on 2016/12/30
 * listview或gridView长按代理
 */
public class OnItemLongClickListenerProxy implements OnItemLongClickListener {

    private OnItemLongClickListener onItemLongClickListener;
    private OnListenerProxyCallBack.OnItemLongClickProxyListener onItemLongClickProxyListener;

    public OnItemLongClickListenerProxy(OnItemLongClickListener onItemLongClickListener, OnListenerProxyCallBack.OnItemLongClickProxyListener onItemLongClickProxyListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        this.onItemLongClickProxyListener = onItemLongClickProxyListener;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != onItemLongClickProxyListener) {
            onItemLongClickProxyListener.onItemLongClick(parent, view, position, id);
        }
        if (null != onItemLongClickListener) {
            return onItemLongClickListener.onItemLongClick(parent, view, position, id);
        }
        return false;
    }
}
