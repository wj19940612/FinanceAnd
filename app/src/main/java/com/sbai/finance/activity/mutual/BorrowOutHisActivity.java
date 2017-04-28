package com.sbai.finance.activity.mutual;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowOutHisActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine_his);
        ButterKnife.bind(this);
    }
}
