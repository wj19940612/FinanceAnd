package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.SpannableString;
import android.util.Log;
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
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment {

    private static final int REQ_CODE_USER_INFO = 801;
    private static final int REQ_CODE_NEW_NEWS = 18;

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
    @BindView(R.id.headImageLayout)
    LinearLayoutCompat mHeadImageLayout;
    @BindView(R.id.logoutImage)
    AppCompatImageView mLogoutImage;


    private BroadcastReceiver LoginBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LoginActivity.LOGIN_SUCCESS_ACTION)) {
                updateUserImage();
                updateUserStatus();
            }
        }
    };

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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(LoginBroadcastReceiver, new IntentFilter(LoginActivity.LOGIN_SUCCESS_ACTION));
        updateUserImage();
        updateUserStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(LoginBroadcastReceiver);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&isAdded()){
            requestUserAttentionAndroidFansNumber();
        }
    }

    private void requestNoReadNewsNumber() {
        Client.getNoReadMessageNumber()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArrayList<NotReadMessageNumberModel>>, ArrayList<NotReadMessageNumberModel>>(false) {
                    @Override
                    protected void onRespSuccessData(ArrayList<NotReadMessageNumberModel> data) {
                        int count = 0;
                        for (NotReadMessageNumberModel notReadMessageNumberData : data) {
                            count = count + notReadMessageNumberData.getCount();
                        }
                        if (count != 0) {
                            SpannableString attentionSpannableString = StrUtil.mergeTextWithColor(getString(R.string.new_message),
                                    " " + count + " ", ContextCompat.getColor(getActivity(), R.color.redPrimary)
                                    , getString(R.string.item));
                            mNews.setSubText(attentionSpannableString);
                        } else {
                            mNews.setSubText("");
                        }
                    }
                })
                .fireSync();
    }

    private void updateUserStatus() {
        if (LocalUser.getUser().isLogin()) {
            updateUserNumber(null);
            requestUserAttentionAndroidFansNumber();
            requestNoReadNewsNumber();
            mHeadImageLayout.setVisibility(View.VISIBLE);
            mLogoutImage.setVisibility(View.GONE);
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
        } else {
            mHeadImageLayout.setVisibility(View.GONE);
            mLogoutImage.setVisibility(View.VISIBLE);
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
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into(mUserHeadImage);
        } else {
            Glide.with(this).load(R.drawable.ic_default_avatar_big)
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
                    .into(mUserHeadImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.headImageLayout, R.id.logoutImage, R.id.userHeadImage,
            R.id.attention, R.id.fans, R.id.minePublish, R.id.news,
            R.id.setting, R.id.aboutUs, R.id.detail, R.id.feedBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                break;
            case R.id.logoutImage:
                Launcher.with(getActivity(), LoginActivity.class).execute();
                break;
            case R.id.userHeadImage:
                startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                break;
            case R.id.attention:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), AttentionActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.fans:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), FansActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.minePublish:
                Launcher.with(getActivity(), PublishActivity.class).execute();
                break;
            case R.id.news:
                startActivityForResult(new Intent(getActivity(), NewsActivity.class), REQ_CODE_NEW_NEWS);
                break;
            case R.id.setting:
                Launcher.with(getActivity(), SettingActivity.class).execute();
                break;
            case R.id.aboutUs:
//                Launcher.with(getActivity(), UserDataActivity.class).execute();
                break;
            case R.id.detail:
                break;
            case R.id.feedBack:
                break;
        }
    }

    private void requestUserAttentionAndroidFansNumber() {
        Client.getAttentionFollowUserNumber(0)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AttentionAndFansNumberModel>, AttentionAndFansNumberModel>(false) {
                    @Override
                    protected void onRespSuccessData(AttentionAndFansNumberModel data) {
                        Log.d(TAG, "粉丝数量 " + data.toString());

                        updateUserNumber(data);
                    }
                })
                .fire();
    }

    private void updateUserNumber(AttentionAndFansNumberModel data) {
        if (data == null)
            data = new AttentionAndFansNumberModel();
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.attention), "\n" + data.getAttention(), 1.8f, Color.WHITE);
        mAttention.setText(attentionSpannableString);
        SpannableString fansSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.fans), "\n" + data.getFollower(), 1.8f, Color.WHITE);
        mFans.setText(fansSpannableString);
        SpannableString minePublishSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.mine_publish), "\n" + data.getViewpoint(), 1.8f, Color.WHITE);
        mMinePublish.setText(minePublishSpannableString);
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
                case REQ_CODE_NEW_NEWS:
                    requestNoReadNewsNumber();
                    break;
            }
        }
    }
}
