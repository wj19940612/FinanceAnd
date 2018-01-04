package com.sbai.finance.fragment.dialog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.anchor.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;

/**
 * 打赏输入安全密码页面
 */

public class RewardInputSafetyPassDialogFragment extends DialogFragment {
    public static final String TAG = "RewardInputSafetyPassDialogFragment";

    private static final String KEY_MONEY = "MONEY";
    private static final String KEY_HINT = "hint";
    private static final String KEY_REWARD = "reward";
    private static final String KEY_ID = "ID";
    private static final String KEY_TYPE = "type";


    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.money)
    TextView mMoney;
    @BindView(R.id.safety_password_number)
    SafetyPasswordEditText mSafetyPasswordNumber;
    @BindView(R.id.hint)
    TextView mHint;
    private Unbinder mBind;
    private String mRechargeMoney;
    private long mRewardMoney;
    private String mTitleHint;
    private int mId;
    private int mType;
    private boolean mIsSuccess;

    private RewardResultCallback mRewardResultCallback;

    public RewardInputSafetyPassDialogFragment setOnSelectMoneyCallback(RewardResultCallback rewardResultCallback) {
        mRewardResultCallback = rewardResultCallback;
        return this;
    }

    public interface RewardResultCallback {
        void success();

        void failure();
    }

    public static RewardInputSafetyPassDialogFragment newInstance(String money) {
        Bundle args = new Bundle();
        RewardInputSafetyPassDialogFragment fragment = new RewardInputSafetyPassDialogFragment();
        args.putString(KEY_MONEY, money);
        fragment.setArguments(args);
        return fragment;
    }

    public static RewardInputSafetyPassDialogFragment newInstance(int id, String money, String hint, long rewardMoney, int type) {
        Bundle args = new Bundle();
        RewardInputSafetyPassDialogFragment fragment = new RewardInputSafetyPassDialogFragment();
        args.putString(KEY_MONEY, money);
        args.putString(KEY_HINT, hint);
        args.putInt(KEY_ID, id);
        args.putLong(KEY_REWARD, rewardMoney);
        args.putInt(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 6) {
                requestRewardMiss(s.toString());
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        }

        //api19 手机可能会出现分割线不出现问题
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            mSafetyPasswordNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mSafetyPasswordNumber.addTextChangedListener(mValidationWatcher);
        mMoney.setText(mRechargeMoney);
        if (!TextUtils.isEmpty(mTitleHint)) {
            mHint.setText(mTitleHint);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_safety_pass_dialog_fragment, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog);
        if (getArguments() != null) {
            mRechargeMoney = getArguments().getString(KEY_MONEY);
            mRewardMoney = getArguments().getLong(KEY_REWARD);
            mTitleHint = getArguments().getString(KEY_HINT);
            mType = getArguments().getInt(KEY_TYPE);
            mId = getArguments().getInt(KEY_ID);
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mIsSuccess) {
            if (mRewardResultCallback != null) {
                mRewardResultCallback.success();
            }
        } else {
            if (mRewardResultCallback != null) {
                mRewardResultCallback.failure();
            }
        }
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @OnClick(R.id.title)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }

    private void requestRewardMiss(String password) {
        if (mType == RewardInfo.TYPE_MISS) {
            Client.rewardMiss(mId, Double.valueOf(mRewardMoney), AccountFundDetail.TYPE_INGOT, password)
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            ToastUtil.show(getString(R.string.success_reward));
                            mIsSuccess = true;
                            sendRewardSuccessBroadcast(getActivity());
                            dismissAllowingStateLoss();
                        }

                        @Override
                        protected void onRespFailure(Resp failedResp) {
                            if (failedResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                                dismissAllowingStateLoss();
                            } else {
                                if (failedResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {
                                    mSafetyPasswordNumber.clearSafetyNumber();
                                }
                                ToastUtil.show(failedResp.getMsg());
                            }
                        }
                    }).fire();

        }
        if (mType == RewardInfo.TYPE_QUESTION) {
            Client.rewardQuestion(mId, Double.valueOf(mRewardMoney), AccountFundDetail.TYPE_INGOT, password)
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            ToastUtil.show(getString(R.string.success_reward));
                            mIsSuccess = true;
                            sendRewardSuccessBroadcast(getActivity());
                            dismissAllowingStateLoss();
                        }

                        @Override
                        protected void onRespFailure(Resp failedResp) {
                            if (failedResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                                dismissAllowingStateLoss();
                            } else {
                                if (failedResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {
                                    mSafetyPasswordNumber.clearSafetyNumber();
                                }
                                ToastUtil.show(failedResp.getMsg());
                            }
                        }
                    }).fire();

        }
    }

    private void sendRewardSuccessBroadcast(FragmentActivity activity) {
        Intent intent = new Intent();
        intent.setAction(ACTION_REWARD_SUCCESS);
        intent.putExtra(Launcher.EX_PAYLOAD, mType);
	    intent.putExtra(Launcher.EX_PAYLOAD_1, mId);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
