package com.sbai.finance.activity.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.fragment.dialog.system.UpdateVersionDialogFragment;
import com.sbai.finance.model.AppVersion;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.model.system.ServiceConnectWay;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.IconTextRow;
import com.sbai.httplib.CookieManger;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.versionName)
    AppCompatTextView mVersionName;
    @BindView(R.id.action_product)
    IconTextRow mActionProduct;
    @BindView(R.id.user_protocol)
    IconTextRow mUserProtocol;
    @BindView(R.id.checkUpdate)
    IconTextRow mCheckUpdate;
    @BindView(R.id.company)
    AppCompatTextView mCompany;
    @BindView(R.id.copyConnectWay)
    TextView mCopyConnectWay;
    @BindView(R.id.weChat)
    TextView mWeChat;
    @BindView(R.id.qq)
    TextView mQq;
    @BindView(R.id.serviceConnect)
    LinearLayout mServiceConnect;
    private ServiceConnectWay mServiceConnectWay;
    private ClipboardManager mClipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String version = this.getString(R.string.app_name) + "v" + AppInfo.getVersionName(this);
        mVersionName.setText(version);
        mCompany.setText(getString(R.string.company, Calendar.getInstance().get(Calendar.YEAR)));

        mServiceConnectWay = Preference.get().getServiceConnectWay();
        if (mServiceConnectWay != null) {
            mServiceConnect.setVisibility(View.VISIBLE);
            mQq.setText(getString(R.string.qq, mServiceConnectWay.getQq()));
            mWeChat.setText(getString(R.string.we_chat, mServiceConnectWay.getWeixin()));
        }
    }

    @OnClick({R.id.versionName, R.id.action_product, R.id.user_protocol,
            R.id.checkUpdate, R.id.weChat, R.id.qq})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.versionName:
                break;
            case R.id.action_product:
                Launcher.with(getActivity(), WebActivity.class).putExtra(WebActivity.EX_URL, Client.ABOUT_US_PAGE_URL).execute();
                break;
            case R.id.user_protocol:
                openUserProtocolPage();
                break;
            case R.id.checkUpdate:
                checkVersion();
                break;
            case R.id.weChat:
                copyConnectWay(mServiceConnectWay.getWeixin());
                break;
            case R.id.qq:
                openQQ();
                break;
        }
    }

    private void openQQ() {
        if (mServiceConnectWay != null) {
            String serviceQQUrl = Client.getServiceQQ(mServiceConnectWay.getQq());
            Intent intentQQ = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl));
            if (intentQQ.resolveActivity(getPackageManager()) != null) {
                startActivity(intentQQ);
            } else {
                ToastUtil.show(R.string.install_qq_first);
            }
        }
    }

    private void copyConnectWay(String connectWay) {
        ClipData clipData = ClipData.newPlainText("", connectWay);
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }

    private void checkVersion() {
        Client.updateVersion()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AppVersion>, AppVersion>() {
                    @Override
                    protected void onRespSuccessData(final AppVersion data) {
                        if (data.isForceUpdate()) {
                            UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate()).show(getSupportFragmentManager());
                        } else if (data.isNeedUpdate()) {
                            UpdateVersionDialogFragment.newInstance(data, data.isForceUpdate()).show(getSupportFragmentManager());
                        } else {
                            ToastUtil.show(R.string.is_already_latest_version);
                        }
                    }
                })
                .fireFree();
    }

    private void openUserProtocolPage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_USER).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
                                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }
}
