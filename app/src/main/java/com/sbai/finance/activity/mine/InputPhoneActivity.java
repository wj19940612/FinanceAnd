package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册 & 找回密码的输入手机号页面
 */
public class InputPhoneActivity extends BaseActivity {

    public static final int PAGE_TYPE_REGISTER = 801;
    public static final int PAGE_TYPE_FORGET_PSD = 108;

    /**
     * 子操作 请求码
     */
    private static final int REQ_CODE_SUB_OPERATION = 999;

    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    ImageView mPhoneNumberClear;

    @BindView(R.id.next)
    TextView mNext;

    @BindView(R.id.protocolArea)
    LinearLayout mProtocolArea;

    private int mPageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone);
        ButterKnife.bind(this);

        initData(getIntent());

        if (mPageType == PAGE_TYPE_REGISTER) {
            mProtocolArea.setVisibility(View.VISIBLE);
        } else {
            mProtocolArea.setVisibility(View.GONE);
        }

        mPhoneNumber.requestFocus();
        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);

        mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && hasFocus) {
                    mPhoneNumberClear.setVisibility(View.VISIBLE);
                }
            }
        });

        mPhoneNumber.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardUtils.openKeyBoard(mPhoneNumber);
            }
        }, 200);
    }

    private void initData(Intent intent) {
        mPageType = intent.getIntExtra(ExtraKeys.PAGE_TYPE, 0);
    }

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

            formatPhoneNumber();

            mPhoneNumberClear.setVisibility(checkClearBtnVisible() ? View.VISIBLE : View.INVISIBLE);

            boolean enable = checkNextButtonEnable();
            if (enable != mNext.isEnabled()) {
                mNext.setEnabled(enable);
            }
        }
    };

    private boolean checkNextButtonEnable() {
        String phone = getPhoneNumber();
        return !(TextUtils.isEmpty(phone) || phone.length() < 11);
    }

    private boolean checkClearBtnVisible() {
        String phone = mPhoneNumber.getText().toString();
        return !TextUtils.isEmpty(phone);
    }

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNumber.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNumber.setText(newPhone);
            mPhoneNumber.setSelection(newPhone.length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNumber.removeTextChangedListener(mPhoneValidationWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SUB_OPERATION && resultCode == RESULT_OK) {
            if (mPageType == PAGE_TYPE_REGISTER) {
                // 注册成功 设置回调ok 关闭页面
                setResult(RESULT_OK);
                finish();
            } else if (mPageType == PAGE_TYPE_FORGET_PSD) {
                // 更新密码成功 关闭页面
                finish();
            }
        }
    }

    private String getPhoneNumber() {
        return mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
    }

    @OnClick({R.id.next, R.id.rootView, R.id.financeUserProtocol, R.id.phoneNumberClear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                checkPhoneAndNext();
                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(mRootView);
                break;
            case R.id.financeUserProtocol:
                openUserProtocolPage();
                break;
            case R.id.phoneNumberClear:
                mPhoneNumber.setText("");
                break;
        }
    }

    private void checkPhoneAndNext() {
        int type = (mPageType == PAGE_TYPE_REGISTER ? 0 : 1);
        Client.checkPhone(getPhoneNumber(), type).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        Launcher.with(getActivity(), AuthCodeActivity.class)
                                .putExtra(ExtraKeys.PAGE_TYPE, mPageType)
                                .putExtra(ExtraKeys.PHONE, getPhoneNumber())
                                .executeForResult(REQ_CODE_SUB_OPERATION);
                    }
                }).fire();
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

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fire();
    }

}
