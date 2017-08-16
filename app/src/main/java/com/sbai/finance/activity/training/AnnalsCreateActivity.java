package com.sbai.finance.activity.training;

import android.os.Bundle;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

/**
 * 年报出炉界面
 */
public class AnnalsCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annals_create);
        translucentStatusBar();
    }
}
