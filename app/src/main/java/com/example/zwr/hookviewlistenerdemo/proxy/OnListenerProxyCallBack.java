package com.example.zwr.hookviewlistenerdemo.proxy;

import android.view.View;
import android.widget.AdapterView;

/**
 * author : zhongwr on 2016/12/31
 * 代理的监听器的回调
 */
public class OnListenerProxyCallBack {

    public interface OnClickProxyListener {
        void onClickProxy(View view);
    }

    public interface OnLongClickProxyListener {
        void onLongClickProxy(View view);
    }

    public interface OnFocusChangeListener {
        void onFocusChangeProxy(View view);
    }

    public interface OnScrollChangeListener {
        void onScrollChangeProxy(View view);
    }

    public interface OnKeyListener {
        void onKeyProxy(View view);
    }

    public interface OnTouchListener {
        void onTouchProxy(View view);
    }

    public interface OnHoverListener {
        void onHoverProxy(View view);
    }

    public interface OnGenericMotionListener {
        void onGenericMotionProxy(View view);
    }

    public interface OnDragListener {
        void onDragProxy(View view);
    }

    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChangeProxy(View view);
    }

    public interface OnItemClickProxyListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    public interface OnItemLongClickProxyListener {
        void onItemLongClick(AdapterView<?> parent, View view, int position, long id);
    }

    public interface OnItemSelectedProxyListener {
        void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        void onNothingSelected(AdapterView<?> parent);
    }
}
