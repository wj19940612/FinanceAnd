package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        String version = this.getString(R.string.app_name) + "v" + AppInfo.getVersionName(this);
        mVersionName.setText(version);
        mCompany.setText(getString(R.string.company, Calendar.getInstance().get(Calendar.YEAR)));
    }

    @OnClick({R.id.versionName, R.id.action_product, R.id.user_protocol, R.id.checkUpdate})
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
                // TODO: 2017/6/7 用户更新
                break;
        }
    }

    private void openUserProtocolPage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_USER).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>(false) {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
//                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .putExtra(WebActivity.EX_URL,Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.protocol))
                                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }
}
