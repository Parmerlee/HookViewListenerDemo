package com.example.zwr.hookviewlistenerdemo.manager;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zwr.hookviewlistenerdemo.R;
import com.example.zwr.hookviewlistenerdemo.proxy.OnClickListenerProxy;
import com.example.zwr.hookviewlistenerdemo.proxy.OnItemClickListenerProxy;
import com.example.zwr.hookviewlistenerdemo.proxy.OnItemLongClickListenerProxy;
import com.example.zwr.hookviewlistenerdemo.proxy.OnItemSelectedListenerProxy;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack;
import com.example.zwr.hookviewlistenerdemo.proxy.OnLongListenerProxy;
import com.example.zwr.hookviewlistenerdemo.proxy.ProxyListenerConfigBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author : zhongwr on 2016/12/30
 */
public class HookViewManager {

    private static final String TAG = "HookViewManager";
    private static HookViewManager instance;
    private ProxyListenerConfigBuilder proxyListenerConfigBuilder = new ProxyListenerConfigBuilder();

    private HookViewManager() {
    }

    public static HookViewManager getInstance() {
        if (instance == null) {
            instance = new HookViewManager();
        }
        return instance;
    }

    /**
     * 获取代理监听器的build
     *
     * @return
     */
    public ProxyListenerConfigBuilder newProxyListenerConfigBuilder() {
        proxyListenerConfigBuilder = new ProxyListenerConfigBuilder();
        return proxyListenerConfigBuilder;
    }


    public ProxyListenerConfigBuilder getProxyListenerConfigBuilder() {
        return proxyListenerConfigBuilder;
    }

    public void hookStart(Activity activity) {
        if (null != activity) {
            View view = activity.getWindow().getDecorView();
            if (null != view) {
                if (view instanceof ViewGroup) {
                    hookStart((ViewGroup) view);
                } else {
                    hookOnClickListener(view, false);
                    hookOnLongClickListener(view, false);
                }
            }
        }
    }

    public void hookStart(ViewGroup viewGroup) {
        hookStart(viewGroup, false);
    }

    /**
     * hook掉viewGroup
     *
     * @param viewGroup
     * @param isScrollAbsListview lsitview或gridView是否滚动：true：滚动则重新hook，false：表示view不是listview或者gridview或者没滚动
     */
    public void hookStart(ViewGroup viewGroup, boolean isScrollAbsListview) {
        if (viewGroup == null) {
            return;
        }
//        int count = viewGroup.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View view = viewGroup.getChildAt(i);
//            if (view instanceof ViewGroup) {//递归查询所有子view
//                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
//                hookStart((ViewGroup) view, isScrollAbsListview);
//            } else {
//                hookOnClickListener(view, isScrollAbsListview);
//                hookOnLongClickListener(view, isScrollAbsListview);
//            }
//        }
        hookListViewListener(viewGroup);
        hookOnClickListener(viewGroup, isScrollAbsListview);
        hookOnLongClickListener(viewGroup, isScrollAbsListview);
    }


    /**
     * hook到Listview的listener
     *
     * @param viewGroup
     */
    private void hookListViewListener(ViewGroup viewGroup) {//已经设置过的不会重新设置
        if (viewGroup instanceof ListView) {
            ListView listView = (ListView) viewGroup;
            AdapterView.OnItemClickListener itemClickListener = listView.getOnItemClickListener();
            if (null != itemClickListener && !(itemClickListener instanceof OnItemClickListenerProxy)) {
                if (null == listView.getTag(R.id.tag_onItemClick)) {//还没hook过
                    listView.setOnItemClickListener(new OnItemClickListenerProxy(itemClickListener,
                            proxyListenerConfigBuilder.getOnItemClickProxyListener()));
                    setHookedTag(listView, R.id.tag_onItemClick);
                }
            }
            AdapterView.OnItemLongClickListener itemLongClickListener = listView.getOnItemLongClickListener();
            if (null != itemLongClickListener && !(itemLongClickListener instanceof OnItemLongClickListenerProxy)) {
                if (null == listView.getTag(R.id.tag_onItemLong)) {//还没hook过
                    listView.setOnItemLongClickListener(new OnItemLongClickListenerProxy(itemLongClickListener, proxyListenerConfigBuilder.getOnItemLongClickProxyListener()));
                    setHookedTag(listView, R.id.tag_onItemLong);
                }
            }
            AdapterView.OnItemSelectedListener itemSelectedListener = listView.getOnItemSelectedListener();
            if (null != itemSelectedListener && !(itemSelectedListener instanceof OnItemSelectedListenerProxy)) {
                if (null == listView.getTag(R.id.tag_onitemSelected)) {//还没hook过
                    listView.setOnItemSelectedListener(new OnItemSelectedListenerProxy(itemSelectedListener, proxyListenerConfigBuilder.getOnItemSelectedProxyListener()));
                    setHookedTag(listView, R.id.tag_onitemSelected);
                }
            }
        }
    }

    private void hookView(View view) {

    }

    /**
     * 点击监听器
     *
     * @param view
     * @param isScrollAbsListview lsitview或gridView是否滚动：true：滚动则重新hook，false：表示view不是listview或者gridview或者没滚动
     */
    private void hookOnClickListener(View view, boolean isScrollAbsListview) {

        if (!view.isClickable()) {//默认是不可点击的，只有设置监听器才会设置为true,证明没有设置点击事件或者初始化时没有设置点击事件
            Log.d(TAG, "isClickable name = " + view.getClass().getSimpleName());
            return;
        }
        if (view.getTag() != null) {
            return;
        }
        Log.d(TAG, "null != view.getTag(R.id.tag_onclick) = " + (null != view.getTag(R.id.tag_onclick)));
        if (!isScrollAbsListview && null != view.getTag(R.id.tag_onclick)) {//已经hook过，并且不是滚动的listview,不用再hook了
            return;
        }
        try {
            //hook view的信息载体实例listenerInfo：事件监听器都是这个实例保存的
            Class viewClass = Class.forName("android.view.View");
            Method method = viewClass.getDeclaredMethod("getListenerInfo");
            method.setAccessible(true);
            Object listenerInfoInstance = method.invoke(view);

            //hook信息载体实例listenerInfo的属性
            Class listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
            /*Field[] fields = listenerInfoClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Log.d(TAG, "field Name = " + field.getName());
            }*/

            Field onClickListerField = listenerInfoClass.getDeclaredField("mOnClickListener");
            onClickListerField.setAccessible(true);
            View.OnClickListener onClickListerObj = (View.OnClickListener) onClickListerField.get(listenerInfoInstance);//获取已设置过的监听器
            Log.d(TAG, "onClickListerObj = " + onClickListerObj);
            if (isScrollAbsListview && onClickListerObj instanceof OnClickListenerProxy) {//针对adapterView的滚动item复用会导致重复hook代理监听器
                return;
            }
            //hook事件，设置自定义的载体事件监听器
            onClickListerField.set(listenerInfoInstance, new OnClickListenerProxy(onClickListerObj, proxyListenerConfigBuilder.getOnClickProxyListener()));
            setHookedTag(view, R.id.tag_onclick);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /***
     * 长按监听器
     *
     * @param view
     */
    private void hookOnLongClickListener(View view, boolean isScrollAbsListview) {
        if (!view.isLongClickable()) {//默认是不可点击的，只有设置监听器才会设置为true,证明没有设置点击事件或者初始化时没有设置点击事件
            Log.d(TAG, "isLongClickable name = " + view.getClass().getSimpleName());
            return;
        }
        Log.d(TAG, "null != view.getTag(R.id.tag_onlongclick) = " + (null != view.getTag(R.id.tag_onlongclick)));
        if (!isScrollAbsListview && null != view.getTag(R.id.tag_onlongclick)) {//已经hook过，并且不是滚动的listview,不用再hook了
            return;
        }
        try {
            //hook view的信息载体实例listenerInfo：事件监听器都是这个实例保存的
            Class viewClass = Class.forName("android.view.View");
            Method method = viewClass.getDeclaredMethod("getListenerInfo");
            method.setAccessible(true);
            Object listenerInfoInstance = method.invoke(view);

            //hook信息载体实例listenerInfo的属性
            Class listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
            /*Field[] fields = listenerInfoClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Log.d(TAG, "field Name = " + field.getName());
            }*/
            Field onLongClickListenerField = listenerInfoClass.getDeclaredField("mOnLongClickListener");
            Log.d(TAG, "onLongClickListenerField Name = " + onLongClickListenerField.getName());
            onLongClickListenerField.setAccessible(true);
            View.OnLongClickListener onLongListerObj = (View.OnLongClickListener) onLongClickListenerField.get(listenerInfoInstance);//获取已设置过的监听器
            if (isScrollAbsListview && onLongListerObj instanceof OnLongListenerProxy) {//针对adapterView的滚动item复用会导致重复hook代理监听器
                return;
            }
            //hook事件，设置自定义的载体事件监听器
            onLongClickListenerField.set(listenerInfoInstance, new OnLongListenerProxy(onLongListerObj, proxyListenerConfigBuilder.getOnLongClickProxyListener()));
            setHookedTag(view, R.id.tag_onlongclick);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /***
     * 为已经hook过的view设置一个标志,防止多次hook
     *
     * @param view
     */
    private void setHookedTag(View view, int tagKey) {
        view.setTag(tagKey, true);
    }
}
