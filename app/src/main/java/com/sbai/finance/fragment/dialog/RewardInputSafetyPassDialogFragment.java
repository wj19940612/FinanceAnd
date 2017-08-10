package com.sbai.finance.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    private boolean mIsSuccess;
    private boolean mIsRecharge;
    private int mType;

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
        setStyle(STYLE_NO_TITLE, R.style.BindBankHintDialog);
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
        if (!mIsSuccess && !mIsRecharge) {
            RewardMissDialogFragment.newInstance()
                    .show(getActivity().getSupportFragmentManager());
        }
        if (mIsRecharge) {
            showRechargeDialog(getActivity());
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
        mIsSuccess = false;
        if (mType == RewardInfo.TYPE_MISS) {
            Client.rewardMiss(mId, Double.valueOf(mRewardMoney), ExchangeDetailModel.TYPE_INGOT, password)
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onReceiveResponse(Resp<Object> objectResp) {
                            if (objectResp.isSuccess()) {
                                ToastUtil.show(getString(R.string.success_reward));
                                mIsSuccess = true;
                                dismissAllowingStateLoss();
                            } else {
                                if (objectResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                                    mIsRecharge = true;
                                    dismissAllowingStateLoss();
                                } else {
                                    if (objectResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {
                                        mSafetyPasswordNumber.clearSafetyNumber();
                                    }
                                    ToastUtil.show(objectResp.getMsg());
                                }
                            }
                        }

                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                        }
                    }).fire();

        }
        if (mType == RewardInfo.TYPE_QUESTION) {
            Client.rewardQuestion(mId, Double.valueOf(mRewardMoney), ExchangeDetailModel.TYPE_INGOT, password)
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onReceiveResponse(Resp<Object> objectResp) {
                            if (objectResp.isSuccess()) {
                                ToastUtil.show(getString(R.string.success_reward));
                                mIsSuccess = true;
                                dismissAllowingStateLoss();
                            } else {
                                if (objectResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                                    mIsRecharge = true;
                                    dismissAllowingStateLoss();
                                } else {
                                    if (objectResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {
                                        mSafetyPasswordNumber.clearSafetyNumber();
                                    }
                                    ToastUtil.show(objectResp.getMsg());
                                }
                            }
                        }

                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                        }
                    }).fire();

        }
    }

    private void showRechargeDialog(final FragmentActivity activity) {
        SmartDialog.single(getActivity(), getString(R.string.ignot_not_enough))
                .setPositive(R.string.go_exchange, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dismiss();
                        Launcher.with(activity, CornucopiaActivity.class).execute();
                    }
                }).setNegative(R.string.cancel)
                .show();
    }
}
