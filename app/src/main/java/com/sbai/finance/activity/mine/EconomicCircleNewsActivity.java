package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.widget.ListView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EconomicCircleNewsActivity extends BaseActivity {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_refresh_listview);
        ButterKnife.bind(this);
    }
}
