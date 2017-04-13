package com.sbai.finance;

import android.os.Bundle;

import com.sbai.finance.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translucentStatusBar();

        
    }
}