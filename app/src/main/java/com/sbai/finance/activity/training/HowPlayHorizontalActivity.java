package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 怎么玩页面
 */

public class HowPlayHorizontalActivity extends BaseActivity {
    @BindView(R.id.content)
    TextView mContent;
    @BindView(R.id.confirm)
    TextView mConfirm;
    @BindView(R.id.close)
    ImageView mClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_play_horizontal);
        ButterKnife.bind(this);
        initView();
        requestTrainGuide();
    }

    private void initView() {
        // TODO: 2017-08-14 load gif
    }

    private void requestTrainGuide() {


    }

    @OnClick({R.id.close, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                finish();
                break;
            case R.id.confirm:
                finish();
                break;
        }
    }
}
