package com.sbai.finance.activity.mine;

import android.os.Bundle;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

public class AuthCodeActivity extends BaseActivity {

    public static final int PAGE_TYPE_REGISTER = 801;
    public static final int PAGE_TYPE_FORGET_PSD = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_code);
    }
}
