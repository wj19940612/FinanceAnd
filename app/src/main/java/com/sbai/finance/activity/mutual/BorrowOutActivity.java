package com.sbai.finance.activity.mutual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BorrowOutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_out_mine);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.borrowOutHis)
    public void onClick(View view){
        Launcher.with(this,BorrowOutHisActivity.class).execute();
    }
}
