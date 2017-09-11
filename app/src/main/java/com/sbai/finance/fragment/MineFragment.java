package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.activity.mine.AboutUsActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.ModifyUserInfoActivity;
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.activity.mine.setting.SettingActivity;
import com.sbai.finance.activity.mine.wallet.WalletActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.IconTextRow;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.activity.mine.LoginActivity.ACTION_LOGIN_SUCCESS;

public class MineFragment extends BaseFragment {

    private static final int REQ_CODE_USER_INFO = 801;
    private static final int REQ_CODE_MESSAGE = 18;
    private static final int REQ_CODE_LOGIN = 10700;

    Unbinder unbinder;

    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.userInfoArea)
    LinearLayout mUserInfoArea;
    @BindView(R.id.wallet)
    IconTextRow mWallet;
    @BindView(R.id.cornucopia)
    IconTextRow mCornucopia;
    @BindView(R.id.message)
    IconTextRow mMessage;
    @BindView(R.id.feedback)
    IconTextRow mFeedback;
    @BindView(R.id.setting)
    IconTextRow mSetting;
    @BindView(R.id.aboutUs)
    IconTextRow mAboutUs;
    @BindView(R.id.financeEvaluation)
    IconTextRow mFinanceEvaluation;

    private BroadcastReceiver LoginBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_LOGIN_SUCCESS)) {
                updateUserImage();
                updateUserStatus();
            }
        }
    };

    private String[] mEvaluationLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(LoginBroadcastReceiver, new IntentFilter(LoginActivity.ACTION_LOGIN_SUCCESS));
        mEvaluationLevel = getResources().getStringArray(R.array.evaluationLevel);
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserImage();
        updateUserStatus();
    }

    private void requestNoReadNewsNumber() {
        Client.getNoReadMessageNumber().setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<NotReadMessageNumberModel>>, ArrayList<NotReadMessageNumberModel>>() {
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
                            mMessage.setSubTextVisible(View.VISIBLE);
                        } else {
                            mMessage.setSubTextVisible(View.GONE);
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
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

    private void updateUserStatus() {
        if (LocalUser.getUser().isLogin()) {
            requestNoReadNewsNumber();
            requestNoReadFeedbackNumber();
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
            int maxLevel = LocalUser.getUser().getUserInfo().getMaxLevel();
            if (maxLevel > 5) {
                maxLevel = 5;
            }
            mFinanceEvaluation.setSubText(mEvaluationLevel[maxLevel]);
        } else {
            mUserName.setText(R.string.login);
            mFinanceEvaluation.setSubText("");
            mMessage.setSubTextVisible(View.GONE);
            mFeedback.setSubTextVisible(View.GONE);
        }
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .transform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into(mUserHeadImage);
        } else {
            GlideApp.with(this).load(R.drawable.ic_default_avatar_big)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mUserHeadImage);
        }
    }

    @OnClick({R.id.userInfoArea,
            R.id.cornucopia, R.id.wallet,
            R.id.message, R.id.feedback, R.id.financeEvaluation,
            R.id.setting, R.id.aboutUs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userInfoArea:
                umengEventCount(UmengCountEventIdUtils.ME_AVATAR);
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
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
            case R.id.wallet:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), WalletActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;

            case R.id.message:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventIdUtils.ME_NEWS);
                    startActivityForResult(new Intent(getActivity(), NewsActivity.class), REQ_CODE_MESSAGE);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.feedback:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventIdUtils.ME_FEEDBACK);
                    Launcher.with(getActivity(), FeedbackActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.financeEvaluation:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventIdUtils.ME_FINANCE_TEST);
                    openLevelStartPage();
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQ_CODE_LOGIN);
                }
                break;
            case R.id.setting:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventIdUtils.ME_SETTING);
                    Launcher.with(getActivity(), SettingActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.aboutUs:
                umengEventCount(UmengCountEventIdUtils.ME_ABOUT_US);
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
                break;
        }
    }

    private void openLevelStartPage() {
        Launcher.with(getActivity(), EvaluationStartActivity.class).execute();
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
                case REQ_CODE_MESSAGE:
                    requestNoReadNewsNumber();
                    break;
                case REQ_CODE_LOGIN:
                    openLevelStartPage();
                    break;
            }
        }
    }
}
