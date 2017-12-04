package com.sbai.finance.activity.mine.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.google.gson.JsonObject;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.AboutUsActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.game.WsClient;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.feedback)
    IconTextRow mFeedback;
    @BindView(R.id.logout)
    AppCompatTextView mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initFeedBack();
        initView();
    }

    private void initFeedBack() {
        if (LocalUser.getUser().isLogin())
            requestNoReadFeedbackNumber();
    }

    private void initView() {
        if (LocalUser.getUser().isLogin()) {
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.shieldSetting, R.id.newMessageNotification, R.id.appInfo, R.id.securityCenter, R.id.feedback, R.id.aboutUs, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shieldSetting:
                break;
            case R.id.newMessageNotification:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_NEW_AWAIT);
                    Launcher.with(getActivity(), SetNotificationSwitchActivity.class).execute();
                } else {
                    openLoginPage();
                }

                break;
            case R.id.securityCenter:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_SAFETY_CENTER);
                    Client.getUserHasPassWord()
                            .setTag(TAG).setIndeterminate(this)
                            .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                                @Override
                                protected void onRespSuccessData(Boolean data) {
                                    Launcher.with(getActivity(), SecurityCenterActivity.class)
                                            .putExtra(ExtraKeys.HAS_SECURITY_PSD, data.booleanValue())
                                            .execute();
                                }
                            })
                            .fire();
                } else {
                    openLoginPage();
                }


                break;
            case R.id.feedback:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_FEEDBACK);
                    Launcher.with(getActivity(), FeedbackActivity.class).execute();
                } else {
                    openLoginPage();
                }
                break;
            case R.id.aboutUs:
                umengEventCount(UmengCountEventId.ME_ABOUT_US);
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
                break;
            case R.id.appInfo:
                if (BuildConfig.DEBUG) {
                    ToastUtil.show(AppInfo.getVersionName(getActivity()) + ": "
                            + AppInfo.getMetaData(getActivity(), AppInfo.Meta.UMENG_CHANNEL));
                }
                break;
            case R.id.logout:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_EXIT_LOGIN);
                    logout();
                } else {
                    openLoginPage();
                }

                break;
        }
    }

    private void requestNoReadFeedbackNumber() {
        Client.getNoReadFeedbackNumber().setTag(TAG)
                .setCallback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (resp.isSuccess()) {
                            int count = Integer.parseInt(resp.getData());
                            updateNoReadFeedbackCount(count);
                        }
                    }
                }).fireFree();
    }

    private void updateNoReadFeedbackCount(int count) {
        if (count != 0) {
            mFeedback.setSubTextVisible(View.VISIBLE);
        } else {
            mFeedback.setSubTextVisible(View.GONE);
        }
    }

    private void logout() {
        SmartDialog.with(getActivity(), R.string.is_logout_lemi)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestLogout();
                    }
                })
                .show();

    }

    private void requestLogout() {
        Client.logout()
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            LocalUser.getUser().logout();
                            CookieManger.getInstance().clearRawCookies();
                            WsClient.get().close();
                            sendLogoutSuccessBroadcast();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
    }

    private void sendLogoutSuccessBroadcast() {
        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(new Intent(ACTION_LOGOUT_SUCCESS));
    }

    private void openLoginPage() {
        Launcher.with(getActivity(), LoginActivity.class).execute();
    }
}
