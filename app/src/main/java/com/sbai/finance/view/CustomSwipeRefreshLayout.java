package com.sbai.finance.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * Created by linrongfang on 2017/5/2.
 */

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    private int mTouchSlop;

    private View mListViewFooter;

    private ListView mListView;

    //暂不支持RecyclerView
    private RecyclerView mRecyclerView;

    private int mYdown;

    private int mYlast;

    private boolean isLoading = false;

    //滑动到底部的时候手动设置不允许loadMore
    private boolean loadMoreEnable = true;

    private boolean isFlingOrTouch = true;

    private OnLoadMoreListener mOnLoadMoreListener;

    private OnScrollStateListener mOnScrollStateListener;

    private TextView mLoadMoreTv;

    private int mVisibleItemCount;

    private int mTotalItemCount;

    public CustomSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        mLoadMoreTv = (TextView) mListViewFooter.findViewById(R.id.loadMore);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //只存在两种情况  不是ListView就是RecyclerView
        if (mListView == null) {
            getListView();
        }
    }

    private void getListView() {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child instanceof ListView) {
                    mListView = (ListView) child;
                    mListView.setOnScrollListener(this);
                }
                if (child instanceof ViewGroup){
                    findListView((ViewGroup) child);
                }
            }
        }
    }

    private void findListView(ViewGroup childView)  {
            int childCount= childView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = childView.getChildAt(i);
                    if (child instanceof ListView) {
                        mListView = (ListView) child;
                        mListView.setOnScrollListener(this);
                    }
                }
            }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYdown = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mYlast = (int) ev.getY();
                break;

            case MotionEvent.ACTION_UP:
//                if (canLoad()) {
//                    loadData();
//                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setAdapter(ListView listView, ListAdapter adapter) {
        if (listView != null) {
            listView.addFooterView(mListViewFooter);
            listView.setAdapter(adapter);
            listView.removeFooterView(mListViewFooter);
        }
    }

    private boolean canLoad() {

        return loadMoreEnable && !isLoading && isPullUp() && isBottom();
    }

    private boolean enableBottomLoad() {
        return !isLoading && isBottom();
    }

    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mVisibleItemCount < mTotalItemCount && mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1;
        }
        return false;
    }

    private boolean isPullUp() {
        return mYdown - mYlast >= mTouchSlop;
    }

    private void loadData() {
        if (mOnLoadMoreListener != null && loadMoreEnable) {
            setLoading(true);
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (loading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYdown = 0;
            mYlast = 0;
        }

    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoadingContent(String string) {
        mLoadMoreTv.setText(string);
    }

    public void setLoadingContent(int resId) {
        mLoadMoreTv.setText(resId);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollStateListener != null) {
            mOnScrollStateListener.scrollStateChange(scrollState);
        }
        isFlingOrTouch = (scrollState != SCROLL_STATE_IDLE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

        mVisibleItemCount = visibleItemCount;
        mTotalItemCount = totalItemCount;
        if (visibleItemCount < totalItemCount && canLoad()) {
            loadData();
        }
    }

    public boolean canLoadData() {
        return !isFlingOrTouch;
    }

    public void setLoadMoreEnable(boolean enable) {
        loadMoreEnable = enable;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    //滚动监听 一般在滚动结束后才发起请求
    public void setOnScrollStateListener(OnScrollStateListener listener) {
        mOnScrollStateListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnScrollStateListener {
        int scrollStateChange(int scrollState);
    }
}
