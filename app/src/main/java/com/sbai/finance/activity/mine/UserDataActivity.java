package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.userHeadImage)
    AppCompatImageView mUserHeadImage;
    @BindView(R.id.userName)
    AppCompatTextView mUserName;
    @BindView(R.id.location)
    AppCompatTextView mLocation;
    @BindView(R.id.attentionAndFansNumber)
    AppCompatTextView mAttentionAndFansNumber;
    @BindView(R.id.hisPublish)
    IconTextRow mHisPublish;
    @BindView(R.id.realNameApprove)
    LinearLayout mRealNameApprove;
    @BindView(R.id.attention)
    AppCompatTextView mAttention;
    @BindView(R.id.divider)
    View mDivider;
    @BindView(R.id.shield)
    AppCompatTextView mShield;
    @BindView(R.id.report)
    AppCompatTextView mReport;

    // TODO: 2017/4/19 后期删除
    private static final String userName = "万年王八";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);
        Glide.with(getActivity()).load("")
                .placeholder(R.drawable.default_headportrait160x160)
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .into(mUserHeadImage);
        mUserName.setText(userName);
        mUserName.setCompoundDrawablesWithIntrinsicBounds(android.R.mipmap.sym_def_app_icon, 0, 0, 0);
        mLocation.setText(" 浙江  温州 ");
        mAttentionAndFansNumber.setText(getString(R.string.attention_fans_number, 100, 1000));
    }

    @OnClick({R.id.attention, R.id.shield, R.id.report})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attention:

                break;
            case R.id.shield:

                SmartDialog.with(getActivity(),
                        getString(R.string.shield_dialog_content, userName)
                        , getString(R.string.shield_dialog_title, userName))
                        .setPositive(android.R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                ToastUtil.curt("移除 " + userName);

                            }
                        })
                        .setTitleMaxLines(2)
                        .setNegative(android.R.string.cancel)
                        .show();

                break;
            case R.id.report:
                break;
        }
    }
}
