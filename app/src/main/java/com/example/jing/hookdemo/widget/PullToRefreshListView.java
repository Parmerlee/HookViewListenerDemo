package com.example.jing.hookdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jing.hookdemo.R;
import com.example.jing.hookdemo.manager.HookViewManager;


public class PullToRefreshListView extends ListView implements OnScrollListener {
    private static final String TAG = "ElasticScrollView";
    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;
    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;

    private int headContentWidth;
    private int headContentHeight;

    private LinearLayout innerLayout;
    private LinearLayout headView;
    //	private ImageView arrowImageView;
    private ProgressBar progressBar;
    //	private TextView tipsTextview;
//	private TextView lastUpdatedTextView;
    private OnRefreshListener refreshListener;
    private OnMoveListener mOnMoveListener;
    private boolean isRefreshable;
    private int state;
    private boolean isBack;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private boolean canReturn;
    private boolean isRecored;
    private int startY;
    private String down_str = "下拉刷新";
    private String release_str = "松开刷新";
    /**
     * 滑动前:第一个可见位置
     */
    private int lastViblePostion = 0;

    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        headView = (LinearLayout) inflater.inflate(
                R.layout.pulltorefreshlistview_head, null);
//		arrowImageView = (ImageView) headView
//				.findViewById(R.id.head_arrowImageView);
        progressBar = (ProgressBar) headView
                .findViewById(R.id.head_progressBar);
//		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
//		lastUpdatedTextView = (TextView) headView
//				.findViewById(R.id.head_lastUpdatedTextView);
        measureView(headView);

        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();
        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        Log.i("size", "width:" + headContentWidth + " height:"
                + headContentHeight);

        addHeaderView(headView);

        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
        canReturn = false;
        setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (getScrollY() == 0 && !isRecored) {
                        isRecored = true;
                        startY = (int) event.getY();
                        Log.i(TAG, "在down时候记录当前位置‘");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            // 什么都不做
                        }
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                            Log.i(TAG, "由下拉刷新状态，到done状态");
                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            Log.i(TAG, "由松开刷新状态，到done状态");
                        }
                    }
                    isRecored = false;
                    isBack = false;

                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    if (!isRecored && getScrollY() == 0) {
                        Log.i(TAG, "在move时候记录下位置");
                        isRecored = true;
                        startY = tempY;
                    } else {
                        if (startY < tempY) {
                            if (mOnMoveListener != null) {
                                mOnMoveListener.onMoveList(true);
                            }
                        } else {
                            if (mOnMoveListener != null) {
                                mOnMoveListener.onMoveList(false);
                            }
                        }
                    }
                    if (state != REFRESHING
                            && isRecored
                            && state != LOADING
                            && PullToRefreshListView.this.getFirstVisiblePosition() == 0) {
                        // 可以松手去刷新了
                        if (state == RELEASE_To_REFRESH) {
                            canReturn = true;

                            if (((tempY - startY) / RATIO < headContentHeight)
                                    && (tempY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
                            }
                            // 一下子推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到done状态");
                            } else {
                                // 不用进行特别的操作，只用更新paddingTop的值就行了
                            }
                        }
                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (state == PULL_To_REFRESH) {
                            canReturn = true;

                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                                Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
                            }
                            // 上推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
                            }
                        }

                        // done状态下
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                        }

                        // 更新headView的size
                        if (state == PULL_To_REFRESH) {
                            headView.setPadding(0, -1 * headContentHeight
                                    + (tempY - startY) / RATIO, 0, 0);

                        }

                        // 更新headView的paddingTop
                        if (state == RELEASE_To_REFRESH) {
                            headView.setPadding(0, (tempY - startY) / RATIO
                                    - headContentHeight, 0, 0);
                        }
                        if (canReturn) {
                            canReturn = false;
                            return true;
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
//			arrowImageView.setVisibility(View.GONE);
//			progressBar.setVisibility(View.GONE);
//			tipsTextview.setVisibility(View.GONE);
//			lastUpdatedTextView.setVisibility(View.GONE);

//			arrowImageView.clearAnimation();
//			arrowImageView.startAnimation(animation);

//			tipsTextview.setText(release_str);

                Log.i(TAG, "当前状态，松开刷新");
                break;
            case PULL_To_REFRESH:
                progressBar.setVisibility(View.VISIBLE);
//			tipsTextview.setVisibility(View.GONE);
//			lastUpdatedTextView.setVisibility(View.GONE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.GONE);
                // 是由RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
//				arrowImageView.clearAnimation();
//				arrowImageView.startAnimation(reverseAnimation);

//				tipsTextview.setText(down_str);
                } else {
//				tipsTextview.setText(down_str);
                }
                Log.i(TAG, "当前状态，下拉刷新");
                break;

            case REFRESHING:

                headView.setPadding(0, 0, 0, 0);

                progressBar.setVisibility(View.VISIBLE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setVisibility(View.GONE);
//			tipsTextview.setText("正在刷新...");
//			lastUpdatedTextView.setVisibility(View.GONE);

                Log.i(TAG, "当前状态,正在刷新...");
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);

                progressBar.setVisibility(View.VISIBLE);
//			arrowImageView.clearAnimation();
//			arrowImageView.setImageResource(R.drawable.head_arrow);
//			tipsTextview.setText(down_str);
//			lastUpdatedTextView.setVisibility(View.GONE);

                Log.i(TAG, "当前状态，done");
                break;
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setOnMoveListener(OnMoveListener moveListener) {
        this.mOnMoveListener = moveListener;
    }

    public interface OnMoveListener {
        public void onMoveList(Boolean isShow);
    }

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void onRefreshComplete() {
        state = DONE;
//		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
        changeHeaderViewByState();
        invalidate();
        scrollTo(0, 0);
    }

    public void setDownStr(String s) {
        down_str = s;
    }

    public void setReleaseStr(String s) {
        release_str = s;
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    public void addChild(View child) {
        innerLayout.addView(child);
    }

    public void addChild(View child, int position) {
        innerLayout.addView(child, position);
    }


    /**
     * 加载更多的底部
     */
    private View mFootView;
    private RelativeLayout rlFootNoMoreParent;
    private LinearLayout llFootLoadingParent;
    /**
     * 加载更多的监听器
     */
    private OnLoadingMoreListener mLoadingMoreListener;
    /**
     * 加载出错时，点击加载更多
     */
    private Button btnReload;
    /**
     * 没有更多
     */
    private TextView tvNoMoreText;
    private Context mContext;
    private OnScrollStateChangedListener mScrollStateChangedListener;
    /**
     * 预留空间，可以加入预定的位置
     */
    private LinearLayout llRemainderSpaceContainer;

    /**
     * 设置上拉加载更多可用
     */
    public void setOnLoadingMoreListener(OnLoadingMoreListener loadingMoreListener) {
        this.mLoadingMoreListener = loadingMoreListener;
        // 添加底部
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.listview_more_footview, null);
        rlFootNoMoreParent = (RelativeLayout) this.mFootView.findViewById(R.id.rl_no_more_parent);
        llFootLoadingParent = (LinearLayout) this.mFootView.findViewById(R.id.ll_loading_parent);
        llRemainderSpaceContainer = (LinearLayout) this.mFootView.findViewById(R.id.ll_remainder_space_container);
        tvNoMoreText = (TextView) this.mFootView.findViewById(R.id.foot_layout_no_more_text);
        btnReload = (Button) this.mFootView.findViewById(R.id.bt_load);
        btnReload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ("加载失败,点击重试".equals(btnReload.getText().toString()) && null != mLoadingMoreListener) {
                    mLoadingMoreListener.onLoadingMore(PullToRefreshListView.this, OnScrollListener.SCROLL_STATE_IDLE);
                }
            }
        });
        // 首次加载应该是隐藏的
        // setFootVisiable(View.VISIBLE, View.GONE, View.GONE);
        addFooterView(this.mFootView);

        // 添加底部之后就可以上拉加载更多 ：这个监听器，可以消除上滑的时候不加载图片
        // setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, this));
//        setOnScrollListener(this);

    }

    /**
     * 设置底部的背景颜色
     * 上午10:59:01
     * void
     */
    public void setFootViewBackground(int colorId) {
        if (null != mFootView) {
            mFootView.setBackgroundColor(mContext.getResources().getColor(colorId));
        }
    }

    /**
     * 添加预留的子footview 下午3:23:54 void
     */
    public void addFootChildView(View view) {
        if (null != llRemainderSpaceContainer && null != view) {
            llRemainderSpaceContainer.addView(view);
        }
    }

    public void showFootChildView() {
        llRemainderSpaceContainer.setVisibility(View.VISIBLE);
    }

    public void hiddenFootChildView() {
        llRemainderSpaceContainer.setVisibility(View.GONE);
    }

    /**
     * @param isNoMore 是否没有更多了
     * @description 加载完成
     * @author zhongwr
     * @update 2015-11-6 上午1:08:15
     */
    public void setOnLoadingMoreCompelete(boolean isNoMore) {
        setOnLoadingMoreCompelete(isNoMore, false);
    }

    public void setOnLoadingMoreCompelete() {
        setFootVisiable(View.GONE, View.GONE, View.GONE);
        this.isLoadingMore = false;
    }

    /**
     * @param isNoMore            是否没有更多了
     * @param isLoadingMoreFailed 加载更多时出错
     * @description 加载完成
     * @author zhongwr
     * @update 2015-11-6 上午1:08:15
     */
    public void setOnLoadingMoreCompelete(boolean isNoMore, boolean isLoadingMoreFailed) {
        if (!isLoadingMoreFailed) {
            if (!"加载更多...".equals(btnReload.getText().toString())) {
                btnReload.setText("加载更多...");
            }
            if (isNoMore) {
                setFootVisiable(View.VISIBLE, View.VISIBLE, View.GONE);
            } else {
                setFootVisiable(View.VISIBLE, View.GONE, View.VISIBLE);
            }
        } else {// 加载失败，提示点击重试
            btnReload.setText("加载失败,点击重试");
            setFootVisiable(View.VISIBLE, View.GONE, View.VISIBLE);
        }
        this.isLoadingMore = false;
    }

    /***
     * 隐藏"加载更多"
     *
     * @author "allen"
     * @update 2016-2-20 上午10:23:44
     */
    public void hiddenReload() {
        if (llFootLoadingParent != null) {
            llFootLoadingParent.setVisibility(View.GONE);
        }
        if (btnReload != null) {
            btnReload.setVisibility(View.GONE);
        }
    }

    /***
     * @description 用于下拉刷新时，隐藏底部
     * @author zhongwr
     * @update 2015-11-25 下午5:24:51
     */
    public void hiddenFootView() {
        if (null != mFootView) {
            isLoadingMore = false;
            mFootView.setVisibility(View.GONE);
        }
    }

    /**
     * @description 显示底部
     * @author zhongwr
     * @params
     * @update 2016年1月4日 下午4:16:06
     */
    public void showFootView() {
        if (null != mFootView) {
            mFootView.setVisibility(View.VISIBLE);
        }
    }

    /***
     * @param footView          这个底部是否可见
     * @param footNoMoreParent  没有更多是否可见
     * @param footLoadingParent 加载更多是否可见
     * @description 设置底部可见：加载更多或没有更多
     * @author zhongwr
     * @update 2015年9月24日 上午11:26:32
     */
    private void setFootVisiable(int footView, int footNoMoreParent, int footLoadingParent) {
        if (null != mFootView) {
            if (footView != mFootView.getVisibility()) {
                mFootView.setVisibility(footView);
            }
            // 整个底部可见,设置子布局是否可见
            if (footNoMoreParent != rlFootNoMoreParent.getVisibility()) {
                this.rlFootNoMoreParent.setVisibility(footNoMoreParent);
            }
            if (footLoadingParent != llFootLoadingParent.getVisibility()) {
                llFootLoadingParent.setVisibility(footLoadingParent);
            }
        }
    }

    /**
     * 是否加载更多
     */
    private boolean isLoadingMore;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListenerExtra != null) {
            mOnScrollListenerExtra.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (null != mFootView) {
            // 不是首页&历史数据没有加载完&最后一天item&上一次已经加载完成
            if (rlFootNoMoreParent.getVisibility() == View.GONE && view.getLastVisiblePosition() == view.getCount() - 1 && scrollState == SCROLL_STATE_IDLE && state == DONE
                    && !isLoadingMore) {
                isLoadingMore = true;
                setFootVisiable(View.VISIBLE, View.GONE, View.VISIBLE);
                if (null != mLoadingMoreListener) {
                    mLoadingMoreListener.onLoadingMore(view, scrollState);
                }
            }
        }
        // 实现滑动状态改变事件
        if (mScrollStateChangedListener != null) {
            mScrollStateChangedListener.onScrollStateChanged(view, scrollState);
        }

        if (mOnScrollListenerExtra != null) {
            mOnScrollListenerExtra.onScrollStateChanged(view, scrollState);
        }
        if (SCROLL_STATE_IDLE == scrollState && getFirstVisiblePosition() != lastViblePostion) {
            lastViblePostion = getFirstVisiblePosition();
            //已经设置过build了，不再需要重新设置，直接hook就好
            HookViewManager.getInstance().hookStart(this, true);
            Log.d(TAG, "SCROLL_STATE_IDLE" + view.getClass().getSimpleName());
        }
    }


    /**
     * @Description 增加ListView滑动状态改变监听 接口
     * @date 2015-12-17
     */
    public interface OnScrollStateChangedListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);
    }

    public void setOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
        this.mScrollStateChangedListener = listener;
    }

    private OnScrollListener mOnScrollListenerExtra;

    public void setOnScrollListenerExtra(OnScrollListener listener) {
        this.mOnScrollListenerExtra = listener;
    }

    /***
     * 加载更多的接口
     *
     * @author zhongwr
     * @update 2015年9月28日 下午6:03:35
     */
    public interface OnLoadingMoreListener {
        public void onLoadingMore(AbsListView view, int scrollState);
    }

}
