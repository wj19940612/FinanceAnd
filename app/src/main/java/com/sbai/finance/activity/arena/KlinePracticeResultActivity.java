package com.sbai.finance.activity.arena;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KlinePracticeResultActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.upDown)
    TextView mUpDown;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.otherName)
    TextView mOtherName;
    @BindView(R.id.resultTip)
    TextView mResultTip;
    @BindView(R.id.nameLayout)
    LinearLayout mNameLayout;
    @BindView(R.id.reLookBtn)
    TextView mReLookBtn;
    @BindView(R.id.moreOneBtn)
    TextView mMoreOneBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_practice_result);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

    }

    private void updateData(){

    }
}
