package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.leveltest.LevelTestStartActivity;
import com.sbai.finance.activity.mine.AttentionActivity;
import com.sbai.finance.activity.mine.FansActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.ModifyUserInfoActivity;
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.mine.PublishActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.activity.mine.setting.SettingActivity;
import com.sbai.finance.activity.mine.wallet.WalletActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.activity.mine.LoginActivity.ACTION_LOGIN_SUCCESS;

public class MineFragment extends BaseFragment {

    private static final int REQ_CODE_USER_INFO = 801;
    private static final int REQ_CODE_NEW_NEWS = 18;
    private static final int REQ_CODE_FANS_PAGE = 322;
    private static final int REQ_CODE_ATTENTION_PAGE = 4555;

    Unbinder unbinder;
    @BindView(R.id.userHeadImage)
    AppCompatImageView mUserHeadImage;
    @BindView(R.id.userName)
    AppCompatTextView mUserName;
    @BindView(R.id.headImageLayout)
    LinearLayout mHeadImageLayout;
    @BindView(R.id.attention)
    TextView mAttention;
    @BindView(R.id.fans)
    TextView mFans;
    @BindView(R.id.minePublish)
    TextView mMinePublish;
    @BindView(R.id.wallet)
    IconTextRow mWallet;
    @BindView(R.id.news)
    IconTextRow mNews;
    @BindView(R.id.feedBack)
    IconTextRow mFeedBack;
    @BindView(R.id.setting)
    IconTextRow mSetting;
    @BindView(R.id.aboutUs)
    IconTextRow mAboutUs;
    @BindView(R.id.cornucopia)
    IconTextRow mCornucopia;

    private BroadcastReceiver LoginBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_LOGIN_SUCCESS)) {
                Log.d("TAG", "onReceive: " + "ACTION_LOGIN_SUCCESS");
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
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(LoginBroadcastReceiver, new IntentFilter(LoginActivity.ACTION_LOGIN_SUCCESS));
        initViews();
    }

    private void initViews() {
        int margin = (int) Display.dp2Px(4f, getResources());
        mNews.setRightTextMargin(margin);
        mFeedBack.setRightTextMargin(margin);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(LoginBroadcastReceiver);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded() && LocalUser.getUser().isLogin()) {
            requestNoReadNewsNumber();
            requestNoReadFeedbackNumber();
            requestUserAttentionAndroidFansNumber();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserImage();
        updateUserStatus();
    }

    private void requestNoReadNewsNumber() {
        Client.getNoReadMessageNumber()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<NotReadMessageNumberModel>>, ArrayList<NotReadMessageNumberModel>>(false) {
                    @Override
                    protected void onRespSuccessData(ArrayList<NotReadMessageNumberModel> data) {
                        int count = 0;
                        for (NotReadMessageNumberModel notReadMessageNumberData : data) {
                            if (notReadMessageNumberData.isSystemNews()) {
                                count = notReadMessageNumberData.getCount();
                                break;
                            }
                        }
                        if (count != 0) {
                            mNews.setRightTextDrawable(R.drawable.ic_new_message);
                        } else {
                            mNews.setRightTextDrawable(0);
                        }
                    }
                })
                .fireFree();
    }

    private void requestNoReadFeedbackNumber() {
        Client.getNoReadFeedbackNumber()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (resp.isSuccess()) {
                            int count = Integer.parseInt(resp.getData());
                            updateNoReadFeedbackCount(count);
                        }
                    }
                })
                .fireFree();
    }

    private void updateNoReadFeedbackCount(int count) {
        if (count != 0) {
            mFeedBack.setRightTextDrawable(R.drawable.ic_new_message);
        } else {
            mFeedBack.setRightTextDrawable(0);
        }
    }

    private void updateUserStatus() {
        if (LocalUser.getUser().isLogin()) {
            updateUserNumber(null);
            requestUserAttentionAndroidFansNumber();
            requestNoReadNewsNumber();
            requestNoReadFeedbackNumber();
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
        } else {
            mUserName.setText(R.string.login);
            int color = ContextCompat.getColor(getActivity(), R.color.unluckyText);
            SpannableString attentionSpannableString = StrUtil.mergeTextWithColor("-", "\n" + getString(R.string.attention), color);
            mAttention.setText(attentionSpannableString);
            SpannableString fansSpannableString = StrUtil.mergeTextWithColor("-", "\n" + getString(R.string.fans), color);
            mFans.setText(fansSpannableString);
            SpannableString minePublishSpannableString = StrUtil.mergeTextWithColor("-", "\n" + getString(R.string.my_publish), color);
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

    @OnClick({R.id.headImageLayout, R.id.userHeadImage,
            R.id.attention, R.id.fans, R.id.minePublish,
            R.id.cornucopia, R.id.wallet,
            R.id.news, R.id.setting, R.id.aboutUs, R.id.feedBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.userHeadImage:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.attention:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), AttentionActivity.class), REQ_CODE_ATTENTION_PAGE);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.fans:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), FansActivity.class), REQ_CODE_FANS_PAGE);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.minePublish:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), PublishActivity.class).putExtra(Launcher.EX_PAYLOAD_1, LocalUser.getUser().getUserInfo().getUserSex()).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.news:
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), NewsActivity.class), REQ_CODE_NEW_NEWS);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.setting:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), SettingActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.aboutUs:
//                Launcher.with(getActivity(), AboutUsActivity.class)
//                        .execute();
                // TODO: 2017/7/31  先增加水平测试入口
                Launcher.with(getActivity(), LevelTestStartActivity.class).execute();

                break;
            case R.id.wallet:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), WalletActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.feedBack:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), FeedbackActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.cornucopia:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), CornucopiaActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void requestUserAttentionAndroidFansNumber() {
        Client.getAttentionFollowUserNumber(null)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<AttentionAndFansNumberModel>, AttentionAndFansNumberModel>(false) {
                    @Override
                    protected void onRespSuccessData(AttentionAndFansNumberModel data) {
                        updateUserNumber(data);
                    }
                })
                .fire();
    }

    private void updateUserNumber(AttentionAndFansNumberModel data) {
        if (data == null)
            data = new AttentionAndFansNumberModel();
        int color = ContextCompat.getColor(getActivity(), R.color.unluckyText);
        SpannableString attentionSpannableString = StrUtil.mergeTextWithColor(data.getAttention() + "", "\n" + getString(R.string.attention), color);
        mAttention.setText(attentionSpannableString);
        SpannableString fansSpannableString = StrUtil.mergeTextWithColor(data.getFollower() + "", "\n" + getString(R.string.fans), color);
        mFans.setText(fansSpannableString);
        SpannableString minePublishSpannableString = StrUtil.mergeTextWithColor(data.getViewpoint() + "", "\n" + getString(R.string.my_publish), color);
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
                case REQ_CODE_FANS_PAGE:
                    requestUserAttentionAndroidFansNumber();
                    break;
                case REQ_CODE_ATTENTION_PAGE:
                    requestUserAttentionAndroidFansNumber();
                    break;
            }
        }
    }
}
