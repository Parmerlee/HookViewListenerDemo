package com.example.zwr.hookviewlistenerdemo;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwr.hookviewlistenerdemo.adapter.ContentAdapter;
import com.example.zwr.hookviewlistenerdemo.manager.HookViewManager;
import com.example.zwr.hookviewlistenerdemo.proxy.OnListenerProxyCallBack;
import com.example.zwr.hookviewlistenerdemo.proxy.ProxyListenerConfigBuilder;
import com.example.zwr.hookviewlistenerdemo.widget.PullToRefreshListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView tv_click_hello;
    private TextView tv_click_me;
    private TextView tv_long_click_me;
    private Context context;
    private PullToRefreshListView lvTestClick;
    private LinearLayout ll_view_parent;

    private ContentAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        contentAdapter = new ContentAdapter(this, getContentTestList());
        lvTestClick.setAdapter(contentAdapter);
    }

    public List<String> getContentTestList() {
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (0 == i % 2) {
                strList.add("2016最后一篇文章");
            } else {
                strList.add("2017第一篇文章");
            }
        }
        return strList;
    }

    private void initListener() {
        tv_click_hello.setOnClickListener(this);
        tv_click_me.setOnClickListener(this);
        ll_view_parent.setOnClickListener(this);
        lvTestClick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "lvTestClick = onItemClick");
            }
        });
        lvTestClick.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "lvTestClick = onItemLongClick");
                return false;
            }
        });
        lvTestClick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "lvTestClick = onItemSelected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "lvTestClick = onNothingSelected");
            }
        });
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "onLayoutChange:" + v.getClass().getSimpleName());
                if (hasHooked) {//等待activity执行完毕，刷新界面是重新检测是否有需要hook的；已经初始化过了，不用每次都初始化
                    Log.d(TAG, "hasHooked onLayoutChange:" + v.getClass().getSimpleName());
                    HookViewManager.getInstance().hookStart(MainActivity.this);
                }
            }
        });
        getWindow().getDecorView().//view加载完成时回调
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "OnGlobalLayoutListener() :");
            }
        });
    }

    private void initView() {
        context = this;
        tv_click_hello = (TextView) findViewById(R.id.tv_click_hello);
        tv_click_me = (TextView) findViewById(R.id.tv_click_me);
        tv_long_click_me = (TextView) findViewById(R.id.tv_long_click_me);
        ll_view_parent = (LinearLayout) findViewById(R.id.ll_view_parent);
        lvTestClick = (PullToRefreshListView) findViewById(R.id.lv_click_listview);
    }


    /**
     * 首次执行会hook监听器
     */
    private boolean hasHooked = false;


    private void buildListernerConfigBuilder() {
        ProxyListenerConfigBuilder configBuilder = HookViewManager.getInstance().newProxyListenerConfigBuilder();
        configBuilder.buildOnClickProxyListener(new OnListenerProxyCallBack.OnClickProxyListener() {
            @Override
            public void onClickProxy(View view) {
                Log.d(TAG, "onClickProxy == view:" + view.getClass().getSimpleName());
            }
        }).buildOnLongClickProxyListener(new OnListenerProxyCallBack.OnLongClickProxyListener() {
            @Override
            public void onLongClickProxy(View view) {
                Log.d(TAG, "onLongClickProxy ==  view:" + view.getClass().getSimpleName());
            }
        }).buildOnItemClickProxyListener(new OnListenerProxyCallBack.OnItemClickProxyListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OnItemClickProxyListener ==  view:" + view.getClass().getSimpleName());
            }
        }).buildOnItemLongClickProxyListener(new OnListenerProxyCallBack.OnItemLongClickProxyListener() {
            @Override
            public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OnItemLongClickProxyListener ==  view:" + view.getClass().getSimpleName());
            }
        }).buildOnItemSelectedProxyListener(new OnListenerProxyCallBack.OnItemSelectedProxyListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "OnItemSelectedProxyListener  onItemSelected ==  view:" + view.getClass().getSimpleName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "OnItemSelectedProxyListener  onNothingSelected ==  view:" + parent.getClass().getSimpleName());
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChange:");
        if (hasHooked) {//防止退出的时候还hook
            return;
        }
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {//等待view都执行完毕之后再hook,否则onLayoutChange执行多次就会hook多次
                Log.d(TAG, "Runnable()1 :");
                buildListernerConfigBuilder();
                HookViewManager.getInstance().hookStart(MainActivity.this);
                try {
                    attachContext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hasHooked = true;
                Log.d(TAG, "Runnable()2 :");
            }
        });
    }

    /**
     * 当数据加载完成后，才更新数据和设置监听器，这时需要重新hook,所以在getDecorView中添加onLayoutChange方法监听所有view发生变化
     */
    private void testViewUpdate() {
        tv_click_hello.setText("hello be changed for yes ");
        tv_long_click_me.setOnClickListener(MainActivity.this);
        tv_long_click_me.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tv_click_hello) {
            Toast.makeText(context, "click hello world", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(MainActivity.this, TestActivity.class));
            MainActivity.this.startActivity(new Intent(MainActivity.this, TestActivity.class));
        } else if (v == tv_click_me) {
            Toast.makeText(context, "click me !", Toast.LENGTH_SHORT).show();
        } else if (v == tv_long_click_me) {
            Toast.makeText(context, "click long me", Toast.LENGTH_SHORT).show();

        }
    }

    public static void attachContext() throws Exception {
        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 拿到原始的 mInstrumentation字段
        Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);

        // 创建代理对象
        Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);

        // 偷梁换柱
        mInstrumentationField.set(currentActivityThread, evilInstrumentation);
    }
}
