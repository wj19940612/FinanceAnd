package com.sbai.finance.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.AttentionActivity;
import com.sbai.finance.activity.mine.FansActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.ModifyUserInfoActivity;
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.mine.PublishActivity;
import com.sbai.finance.activity.mine.SettingActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment {

    private static final int REQ_CODE_USER_INFO = 801;

    Unbinder unbinder;

    @BindView(R.id.userHeadImage)
    AppCompatImageView mUserHeadImage;
    @BindView(R.id.userName)
    AppCompatTextView mUserName;
    @BindView(R.id.attention)
    TextView mAttention;
    @BindView(R.id.fans)
    TextView mFans;
    @BindView(R.id.minePublish)
    TextView mMinePublish;
    @BindView(R.id.setting)
    IconTextRow mSetting;
    @BindView(R.id.aboutUs)
    IconTextRow mAboutUs;
    @BindView(R.id.news)
    IconTextRow mNews;
    @BindView(R.id.detail)
    IconTextRow mDetail;
    @BindView(R.id.feedBack)
    IconTextRow mFeedBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateUserImage();
        updateUserStatus();
    }

    private void updateUserStatus() {
        if (LocalUser.getUser().isLogin()) {
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
            SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.attention), "\n1222", 1.8f, Color.WHITE);
            mAttention.setText(attentionSpannableString);
            SpannableString fansSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.fans), "\n12220", 1.8f, Color.WHITE);
            mFans.setText(fansSpannableString);
            SpannableString minePublishSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.mine_publish), "\n12220", 1.8f, Color.WHITE);
            mMinePublish.setText(minePublishSpannableString);
        } else {
            mUserName.setText(R.string.please_login);
            SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.attention), "\n-", 1.3f, Color.WHITE);
            mAttention.setText(attentionSpannableString);
            SpannableString fansSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.fans), "\n-", 1.3f, Color.WHITE);
            mFans.setText(fansSpannableString);
            SpannableString minePublishSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.mine_publish), "\n-", 1.3f, Color.WHITE);
            mMinePublish.setText(minePublishSpannableString);
        }
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            Glide.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.default_headportrait160x160)
                    .into(mUserHeadImage);
        } else {
            Glide.with(this).load(R.drawable.default_headportrait160x160)
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
                    .into(mUserHeadImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.userHeadImage, R.id.attention, R.id.fans, R.id.minePublish, R.id.news,
            R.id.setting, R.id.aboutUs, R.id.detail, R.id.feedBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userHeadImage:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), Launcher.REQ_CODE_LOGIN);
                }
                break;
            case R.id.attention:
                Launcher.with(getActivity(), AttentionActivity.class).execute();
                break;
            case R.id.fans:
                Launcher.with(getActivity(), FansActivity.class).execute();
                break;
            case R.id.minePublish:
                Launcher.with(getActivity(), PublishActivity.class).execute();
                break;
            case R.id.news:
                Launcher.with(getActivity(), NewsActivity.class).execute();
                break;
            case R.id.setting:
                Launcher.with(getActivity(), SettingActivity.class).execute();
                break;
            case R.id.aboutUs:
                Launcher.with(getActivity(), UserDataActivity.class).execute();
                break;
            case R.id.detail:
                break;
            case R.id.feedBack:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseActivity.RESULT_OK) {
            switch (requestCode) {
                case Launcher.REQ_CODE_LOGIN:
                    updateUserStatus();
                    updateUserImage();
                    break;
                case REQ_CODE_USER_INFO:
                    updateUserStatus();
                    updateUserImage();
                    break;
            }
        }
    }
}
