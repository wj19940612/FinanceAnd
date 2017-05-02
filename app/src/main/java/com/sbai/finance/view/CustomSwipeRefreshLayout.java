package com.sbai.finance.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * Created by linrongfang on 2017/5/2.
 */

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    private int mTounchslop;

    private View mListViewFooter;

    private ListView mListView;

    private int mYdown;

    private int mYlast;

    private boolean isLoading = false;

    private OnLoadMoreListener mOnLoadMoreListener;

    private TextView mTvLoadMore;

    private int mVisibleItemCount;

    private int mTotalItemCount;

    public CustomSwipeRefreshLayout(Context context) {
        this(context,null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTounchslop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        mTvLoadMore = (TextView) mListViewFooter.findViewById(R.id.loadMore);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //获取listview
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
                    mListView = (ListView)child;
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
                mYdown = (int)ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mYlast = (int)ev.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (canLoad()) {
                    loadData();
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setAdapte(ListView listView, ListAdapter adapter) {
        if (listView != null) {
            listView.addFooterView(mListViewFooter);
            listView.setAdapter(adapter);
            listView.removeFooterView(mListViewFooter);
        }
    }

    private boolean canLoad() {
        return !isLoading && isPullup() && isBottom();
    }

    private boolean enableBottomLoad() {
        return !isLoading && isBottom();
    }

    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mVisibleItemCount < mTotalItemCount && mListView.getLastVisiblePosition() == mListView.getAdapter().getCount()-1;
        }
        return false;
    }

    private boolean isPullup() {
        return mYdown-mYlast >= mTounchslop;
    }

    private void loadData() {
        if (mOnLoadMoreListener != null) {
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

    public void setLoadingContent(String string) {
        mTvLoadMore.setText(string);
    }
    public void setLoadingContent(int resId) {
        mTvLoadMore.setText(resId);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mVisibleItemCount = visibleItemCount;
        mTotalItemCount = totalItemCount;
        if (visibleItemCount < totalItemCount && enableBottomLoad()) {
            loadData();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
