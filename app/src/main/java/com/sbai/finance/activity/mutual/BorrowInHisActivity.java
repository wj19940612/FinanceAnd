package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.BorrowInHis;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowInHisActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_in_mine_his);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mListView.setEmptyView(mEmpty);
       // mListView.setAdapter();
    }
    static class BorrowInAdapter extends ArrayAdapter<BorrowInHis>{

        public BorrowInAdapter(@NonNull Context context) {
            super(context, 0);
        }
    }
}
