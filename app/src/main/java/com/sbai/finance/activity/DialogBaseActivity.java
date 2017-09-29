package com.sbai.finance.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DialogBaseActivity extends AppCompatActivity {

    protected String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (dm.widthPixels * getWidthRatio());
    }

    protected float getWidthRatio() {
        return 0.75f;
    }

    protected FragmentActivity getActivity() {
        return this;
    }
}

