package com.example.zwr.hookviewlistenerdemo.proxy;

import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnClickProxyListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnFocusChangeListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnGenericMotionListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnHoverListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnKeyListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnLongClickProxyListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnScrollChangeListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnSystemUiVisibilityChangeListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnTouchListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnItemClickProxyListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnItemLongClickProxyListener;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack.OnItemSelectedProxyListener;

/**
 * author : zhongwr on 2016/12/31
 * 代理的监听器的回调
 */
public class ProxyListenerConfigBuilder {
    private OnClickProxyListener onClickProxyListener;
    private OnLongClickProxyListener onLongClickProxyListener;
    private OnFocusChangeListener onFocusChangeListener;
    private OnScrollChangeListener onScrollChangeListener;
    private OnKeyListener onKeyListener;
    private OnTouchListener onTouchListener;
    private OnHoverListener onHoverListener;
    private OnGenericMotionListener onGenericMotionListener;
    private OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener;

    private OnItemClickProxyListener onItemClickProxyListener;
    private OnItemLongClickProxyListener OnItemLongClickProxyListener;
    private OnItemSelectedProxyListener OnItemSelectedProxyListener;

    public ProxyListenerConfigBuilder buildOnClickProxyListener(OnClickProxyListener onClickProxyListener) {
        this.onClickProxyListener = onClickProxyListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnLongClickProxyListener(OnLongClickProxyListener onLongClickProxyListener) {
        this.onLongClickProxyListener = onLongClickProxyListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnKeyListener(OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnHoverListener(OnHoverListener onHoverListener) {
        this.onHoverListener = onHoverListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnGenericMotionListener(OnGenericMotionListener onGenericMotionListener) {
        this.onGenericMotionListener = onGenericMotionListener;
        return this;
    }

    public ProxyListenerConfigBuilder buildOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener) {
        this.onSystemUiVisibilityChangeListener = onSystemUiVisibilityChangeListener;
        return this;
    }

    /***
     * listview 的点击代理监听器回调
     *
     * @param onItemClickProxyListener
     * @return
     */
    public ProxyListenerConfigBuilder buildOnItemClickProxyListener(OnItemClickProxyListener onItemClickProxyListener) {
        this.onItemClickProxyListener = onItemClickProxyListener;
        return this;
    }

    /***
     * listview 的长按代理监听器回调
     *
     * @param onItemLongClickProxyListener
     * @return
     */
    public ProxyListenerConfigBuilder buildOnItemLongClickProxyListener(OnItemLongClickProxyListener onItemLongClickProxyListener) {
        OnItemLongClickProxyListener = onItemLongClickProxyListener;
        return this;
    }

    /***
     * listview 的onItemSelected代理监听器
     *
     * @param onItemSelectedProxyListener
     * @return
     */
    public ProxyListenerConfigBuilder buildOnItemSelectedProxyListener(OnItemSelectedProxyListener onItemSelectedProxyListener) {
        OnItemSelectedProxyListener = onItemSelectedProxyListener;
        return this;
    }

    public OnClickProxyListener getOnClickProxyListener() {
        return onClickProxyListener;
    }

    public OnLongClickProxyListener getOnLongClickProxyListener() {
        return onLongClickProxyListener;
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        return onFocusChangeListener;
    }

    public OnScrollChangeListener getOnScrollChangeListener() {
        return onScrollChangeListener;
    }

    public OnKeyListener getOnKeyListener() {
        return onKeyListener;
    }

    public OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    public OnHoverListener getOnHoverListener() {
        return onHoverListener;
    }

    public OnGenericMotionListener getOnGenericMotionListener() {
        return onGenericMotionListener;
    }

    public OnSystemUiVisibilityChangeListener getOnSystemUiVisibilityChangeListener() {
        return onSystemUiVisibilityChangeListener;
    }

    public OnItemClickProxyListener getOnItemClickProxyListener() {
        return onItemClickProxyListener;
    }

    public OnListenerProxyCallBack.OnItemLongClickProxyListener getOnItemLongClickProxyListener() {
        return OnItemLongClickProxyListener;
    }

    public OnItemSelectedProxyListener getOnItemSelectedProxyListener() {
        return OnItemSelectedProxyListener;
    }
}
