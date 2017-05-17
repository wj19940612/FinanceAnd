package com.sbai.finance.activity.mine;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.sbai.finance.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetNotificationSwitchActivity extends AppCompatActivity {

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @BindView(R.id.acceptNewsSwitch)
    AppCompatImageView mAcceptNewsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification_switch);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isNotificationEnabled()) {
            mAcceptNewsSwitch.setSelected(true);
        } else {
            mAcceptNewsSwitch.setSelected(false);
        }
    }

    @OnClick(R.id.acceptNewsSwitch)
    public void onViewClicked() {
        openSystemSettingPage();
    }

    private void openSystemSettingPage() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }


    /**
     * 获取通知栏权限是否开启
     */
    @SuppressLint("NewApi")
    public boolean isNotificationEnabled() {

        AppOpsManager mAppOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = this.getApplicationInfo();
        String pkg = this.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
      /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
