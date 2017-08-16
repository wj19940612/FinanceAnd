package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.train.TrainProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 年报出炉界面
 */
public class AnnalsCreateActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressBar)
    TrainProgressBar mProgressBar;
    @BindView(R.id.annalsData)
    RecyclerView mAnnalsData;
    @BindView(R.id.annalsAddRecycleView)
    RecyclerView mAnnalsAddRecycleView;
    @BindView(R.id.confirmAnnals)
    ImageView mConfirmAnnals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annals_create);
        ButterKnife.bind(this);
        translucentStatusBar();
    }
}
