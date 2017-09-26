package com.sbai.finance.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.activity.mine.AboutUsActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.MissMyQuestionAndAnswerActivity;
import com.sbai.finance.activity.mine.MyCollectionActivity;
import com.sbai.finance.activity.mine.NewsActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.activity.mine.setting.SettingActivity;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.activity.mine.userinfo.ModifyUserInfoActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;
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
    //打开钱包页面时需要设置安全密码的请求吗
    public static final int REQ_CODE_OPEN_WALLET_SET_SAFETY_PASSWORD = 7004;

    Unbinder unbinder;

    @BindView(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.userInfoArea)
    LinearLayout mUserInfoArea;
    @BindView(R.id.wallet)
    IconTextRow mWallet;
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
    @BindView(R.id.mineQuestionsAndAnswers)
    IconTextRow mMineQuestionsAndAnswers;
    @BindView(R.id.mineCollection)
    IconTextRow mMineCollection;

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

        Log.d(TAG, "onActivityCreated: " + "STRING".length());
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
                            if (notReadMessageNumberData.isSystemNews() || notReadMessageNumberData.isMissNews()) {
                                count = count + notReadMessageNumberData.getCount();
                            }
                        }
                        if (count != 0) {
                            mMessage.setSubTextVisible(View.VISIBLE);
                            if (count <= 99) {
                                mMessage.setSubTextSize(12);
                                mMessage.setSubText(String.valueOf(count));
                            } else {
                                mMessage.setSubTextSize(9);
                                mMessage.setSubText("99+");
                            }
                        } else {
                            mMessage.setSubTextVisible(View.GONE);
                        }
                        setNoReadNewsCount(count);
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

    private void requestMyIngotNumber() {
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        mWallet.setSubText(getString(R.string.my_ingot_, data.getYuanbao()));
                    }
                })
                .fireFree();
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
            requestMyIngotNumber();
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
            int maxLevel = LocalUser.getUser().getUserInfo().getMaxLevel();
            if (maxLevel > 5) {
                maxLevel = 5;
            }
            mFinanceEvaluation.setSubText(mEvaluationLevel[maxLevel]);
        } else {
            mUserName.setText(R.string.login);
            mFinanceEvaluation.setSubText("");
            mWallet.setSubText("");
            mMessage.setSubTextVisible(View.GONE);
            mFeedback.setSubTextVisible(View.GONE);
            setNoReadNewsCount(0);
        }
    }

    private void setNoReadNewsCount(int count) {
        if (getActivity() instanceof OnNoReadNewsListener) {
            ((OnNoReadNewsListener) getActivity()).onNoReadNewsNumber(count);
        }
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into(mUserHeadImage);
        } else {
            GlideApp.with(this).load(R.drawable.ic_default_avatar_big)
                    .circleCrop()
                    .into(mUserHeadImage);
        }
    }

    @OnClick({R.id.userInfoArea, R.id.wallet, R.id.mineQuestionsAndAnswers, R.id.mineCollection,
            R.id.message, R.id.feedback, R.id.financeEvaluation, R.id.setting, R.id.aboutUs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userInfoArea:
                umengEventCount(UmengCountEventId.ME_AVATAR);
                if (LocalUser.getUser().isLogin()) {
                    startActivityForResult(new Intent(getActivity(), ModifyUserInfoActivity.class), REQ_CODE_USER_INFO);
                } else {
                    openLoginPage();
                }
                break;

            case R.id.wallet:
                if (LocalUser.getUser().isLogin()) {
                    boolean firstOpenWalletPage = Preference.get().isFirstOpenWalletPage(LocalUser.getUser().getPhone());
                    if (firstOpenWalletPage) {
                        requestUserHasSafetyPassword();
                    } else {
                        openWalletPage();
                    }
                } else {
                    openLoginPage();
                }
                break;

            case R.id.mineQuestionsAndAnswers:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), MissMyQuestionAndAnswerActivity.class).execute();
                } else {
                    openLoginPage();
                }
                break;
            case R.id.mineCollection:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), MyCollectionActivity.class).execute();
                } else {
                    openLoginPage();
                }
                break;
            case R.id.message:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_NEWS);
                    startActivityForResult(new Intent(getActivity(), NewsActivity.class), REQ_CODE_MESSAGE);
                    setNoReadNewsCount(0);
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
            case R.id.financeEvaluation:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_FINANCE_TEST);
                    openLevelStartPage();
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQ_CODE_LOGIN);
                }
                break;
            case R.id.setting:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_SETTING);
                    Launcher.with(getActivity(), SettingActivity.class).execute();
                } else {
                    openLoginPage();
                }
                break;
            case R.id.aboutUs:
                umengEventCount(UmengCountEventId.ME_ABOUT_US);
                Launcher.with(getActivity(), AboutUsActivity.class).execute();
                break;
        }
    }

    private void openLoginPage() {
        Launcher.with(getActivity(), LoginActivity.class).execute();
    }

    private void requestUserHasSafetyPassword() {
        Client.getUserHasPassWord()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        if (!data) {
                            showAddSafetyPassDialog();
                        } else {
                            openWalletPage();
                        }
                    }
                })
                .fire();
    }

    private void openWalletPage() {
        Launcher.with(getActivity(), WalletActivity.class).execute();
        Preference.get().setIsFirstOpenWalletPage(LocalUser.getUser().getPhone());
    }

    private void showAddSafetyPassDialog() {
        SmartDialog.with(getActivity(), getString(R.string.is_not_set_safety_pass))
                .setPositive(R.string.go_to_set, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), UpdateSecurityPassActivity.class);
                        intent.putExtra(Launcher.EX_PAYLOAD, false);
                        startActivityForResult(intent, REQ_CODE_OPEN_WALLET_SET_SAFETY_PASSWORD);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        openWalletPage();
                    }
                })
                .show();
    }

    private void openLevelStartPage() {
        Launcher.with(getActivity(), EvaluationStartActivity.class).execute();
        Preference.get().setIsFirstOpenWalletPage(LocalUser.getUser().getPhone());
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
                case REQ_CODE_OPEN_WALLET_SET_SAFETY_PASSWORD:
                    openWalletPage();
                    break;
            }
        }
    }
}
