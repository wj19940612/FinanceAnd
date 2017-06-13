package com.sbai.finance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.sbai.finance.R;
import com.sbai.finance.service.PushIntentService;
import com.sbai.finance.service.PushService;
import com.sbai.finance.utils.AppInfo;

public class SplashActivity extends BaseActivity {

    private TextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init getui push
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        mVersionName = (TextView) findViewById(R.id.versionName);
        mVersionName.setText(AppInfo.getVersionName(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    gotoMain();
                }
            }
        }).start();
    }

    private void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {

    }
}
