package com.sbai.finance.activity.mutual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-17.
 */

public class MutualActivity extends BaseActivity {

    @BindView(R.id.borrowIn)
    IconTextRow mBorrowIn;
    @BindView(R.id.borrowOut)
    IconTextRow mBorrowOut;
    @BindView(R.id.borrow)
    IconTextRow mBorrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.borrowIn, R.id.borrowOut, R.id.borrow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.borrowIn:
                break;
            case R.id.borrowOut:
                break;
            case R.id.borrow:
                break;
            default:
                break;
        }
    }
}
