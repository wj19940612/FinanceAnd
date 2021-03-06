package com.sbai.finance.activity.mine.userinfo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.activity.mine.IntroduceSetActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.WeChatActivity;
import com.sbai.finance.activity.mine.setting.LocationActivity;
import com.sbai.finance.fragment.dialog.ChooseSexDialogFragment;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserDetailInfo;
import com.sbai.finance.model.mine.UserIdentityCardInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.autofit.AutofitTextView;
import com.sbai.finance.game.WsClient;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.CookieManger;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

import static com.sbai.finance.model.mine.UserInfo.CREDIT_IS_APPROVE_ING;

public class ModifyUserInfoActivity extends WeChatActivity implements ChooseSexDialogFragment.OnUserSexListener {

    private static final int REQ_CODE_USER_NAME = 165;

    //地址界面
    private static final int REQ_CODE_LOCATION = 805;

    private static final int REQ_CODE_CREDIT_APPROVE = 298;

    private static final int REQ_CODE_PROFILE_INTRODUCTION = 2243;

    @BindView(R.id.userHeadImage)
    AppCompatImageView mUserHeadImage;
    @BindView(R.id.headImageLayout)
    RelativeLayout mHeadImageLayout;
    @BindView(R.id.nickName)
    IconTextRow mNickName;
    @BindView(R.id.sex)
    IconTextRow mSex;
    @BindView(R.id.age)
    IconTextRow mAge;
    @BindView(R.id.location)
    AutofitTextView mLocation;
    @BindView(R.id.weChat)
    IconTextRow mWeChat;
    @BindView(R.id.authenticationImage)
    AppCompatImageView mAuthenticationImage;
    @BindView(R.id.authentication)
    LinearLayout mAuthentication;
    @BindView(R.id.financeEvaluation)
    IconTextRow mFinanceEvaluation;
    @BindView(R.id.personalSummary)
    IconTextRow mPersonalSummary;


    private String[] mAgeList;

    private int mSelectAgeListIndex;

    private String[] mEvaluationLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mEvaluationLevel = getResources().getStringArray(R.array.evaluationLevel);
        mAgeList = new String[101];
        for (int i = 0; i < 101; i++) {
            mAgeList[i] = ((i + Calendar.getInstance().get(Calendar.YEAR) - 100) + "年");
        }
        requestDetailUserInfo();
        requestUserCreditApproveStatus();
        updateUserImage();

        updateUserInfo();
    }

    private void updateUserInfo() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        mNickName.setSubText(userInfo.getUserName());
        if (userInfo.getUserSex() == UserInfo.SEX_GIRL) {
            mSex.setSubText(getString(R.string.girl));
        } else if (userInfo.getUserSex() == UserInfo.SEX_BOY) {
            mSex.setSubText(getString(R.string.boy));
        }
        if (userInfo.getAge() != null) {
            mAge.setSubText(userInfo.getAge().toString());
        }
        mLocation.setText(userInfo.getLand());
        if (TextUtils.isEmpty(userInfo.getWxOpenId())) {
            mWeChat.setSubText(getString(R.string.no_bind));
            mWeChat.setRightIconVisible(true);
            if (Preference.get().isShowBindWeChat(userInfo.getUserPhone())) {
                showBindWeChatDialog();
                Preference.get().setNotShowBindWeChat(userInfo.getUserPhone());
            }

        } else {
            mWeChat.setSubText(userInfo.getWxName());
            mWeChat.setRightIconVisible(false);
        }

        int maxLevel = LocalUser.getUser().getUserInfo().getMaxLevel();
        if (maxLevel > 5) {
            maxLevel = 5;
        }
        mFinanceEvaluation.setSubText(mEvaluationLevel[maxLevel]);
        if (LocalUser.getUser().isMiss()) {
            mPersonalSummary.setVisibility(View.VISIBLE);
            mPersonalSummary.setSubText(userInfo.getBriefingText());
        } else {
            mPersonalSummary.setVisibility(View.GONE);
        }
    }

    private void showBindWeChatDialog() {
        SmartDialog.single(getActivity(), getString(R.string.bind_wechat_info))
                .setCancelableOnTouchOutside(false)
                .setNegative(R.string.cancel)
                .setPositive(R.string.bind, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        bindWeChat();
                    }
                }).show();
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mUserHeadImage);
        } else {
            GlideApp.with(this).load(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mUserHeadImage);
        }
    }

    private void requestDetailUserInfo() {
        Client.requestDetailUserInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<UserDetailInfo>, UserDetailInfo>() {
                    @Override
                    protected void onRespSuccessData(UserDetailInfo data) {
                        if (!TextUtils.isEmpty(data.getUserPortrait()) &&
                                !data.getUserPortrait().equalsIgnoreCase(LocalUser.getUser().getUserInfo().getUserPortrait())) {
                            setResult(RESULT_OK);
                        }
                        LocalUser.getUser().getUserInfo().updateLocalUserInfo(data);
                        updateUserInfo();
                        updateUserImage();
                    }
                })
                .fire();
    }

    private void requestUserCreditApproveStatus() {
        Client.getUserCreditApproveStatus()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<UserIdentityCardInfo>, UserIdentityCardInfo>() {
                    @Override
                    protected void onRespSuccessData(UserIdentityCardInfo data) {
                        if (data.getStatus() != null) {
                            updateUserCreditStatus(data.getStatus());
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                })
                .fireFree();
    }

    private void updateUserCreditStatus(Integer status) {
        switch (status) {
            case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                mAuthenticationImage.setImageResource(R.drawable.ic_real_name);
                break;
            case CREDIT_IS_APPROVE_ING:
                mAuthenticationImage.setImageResource(R.drawable.ic_in_review);
                break;
            default:
                mAuthenticationImage.setImageResource(R.drawable.ic_no_real_name);
                break;
        }
    }

    private void submitUserInfo() {
        Integer age = null;
        if (!TextUtils.isEmpty(mAge.getSubText().trim())) {
            age = Integer.parseInt(mAge.getSubText().trim());
        }

        String land = null;
        if (!TextUtils.isEmpty(mLocation.getText())) {
            land = mLocation.getText().toString().trim();
        }

        Integer sex = null;
        if (LocalUser.getUser().getUserInfo().getUserSex() != 0) {
            sex = LocalUser.getUser().getUserInfo().getUserSex() == UserInfo.SEX_BOY ? 2 : 1;
        }
        if (age != null || !TextUtils.isEmpty(land) || sex != null) {
            Client.updateUserInfo(age, land, sex)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            setResult(RESULT_OK);
                        }
                    })
                    .fireFree();
        }
    }

    @Override
    public void onBackPressed() {
        if (LocalUser.getUser().isLogin()) {
            submitUserInfo();
        }
        super.onBackPressed();
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.sex, R.id.age, R.id.location, R.id.authentication, R.id.weChat, R.id.financeEvaluation, R.id.personalSummary})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                umengEventCount(UmengCountEventId.ME_AVATAR);
                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(
                        UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMAGE_SCALE_OR_MOVE,
                        LocalUser.getUser().getUserInfo().getUserPortrait());
                uploadUserImageDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.nickName:
                if (LocalUser.getUser().isMiss()) {
                    //说明是小姐姐，不允许修改昵称
                    ToastUtil.show(R.string.miss_cant_edit_nick);
                    return;
                }
                umengEventCount(UmengCountEventId.ME_NICK_NAME);
                Launcher.with(getActivity(), ModifyUserNameActivity.class).executeForResult(REQ_CODE_USER_NAME);
                break;
            case R.id.sex:
                umengEventCount(UmengCountEventId.ME_SEX);
                new ChooseSexDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.age:
                umengEventCount(UmengCountEventId.ME_AGE);
                showAgePicker();
                break;
            case R.id.location:
                umengEventCount(UmengCountEventId.ME_LOCATION);
                Launcher.with(getActivity(), LocationActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, true)
                        .executeForResult(REQ_CODE_LOCATION);
                break;
            case R.id.authentication:
                umengEventCount(UmengCountEventId.ME_CERTIFICATION);
                Launcher.with(getActivity(), CreditApproveActivity.class).executeForResult(REQ_CODE_CREDIT_APPROVE);
                break;
            case R.id.weChat:
                umengEventCount(UmengCountEventId.ME_BIND_WECHAT);
                final UserInfo userInfo = LocalUser.getUser().getUserInfo();
                if (TextUtils.isEmpty(userInfo.getWxOpenId())) {
                    bindWeChat();
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
            case R.id.personalSummary:
                String localBriefingText = "";
                if(LocalUser.getUser().getUserInfo()!=null){
                    localBriefingText = LocalUser.getUser().getUserInfo().getBriefingText();
                }
                Launcher.with(this, IntroduceSetActivity.class).putExtra(ExtraKeys.PROFILE_INTRODUCE,localBriefingText).executeForResult(REQ_CODE_PROFILE_INTRODUCTION);
                break;
        }
    }

    private void openLevelStartPage() {
        Launcher.with(getActivity(), EvaluationStartActivity.class).execute();
        Preference.get().setIsFirstOpenWalletPage(LocalUser.getUser().getPhone());
    }

    private void bindWeChat() {
        requestWeChatInfo();
    }

    @Override
    protected void bindSuccess() {
        Client.requestBindWeChat(getWeChatOpenid(), getWeChatName(), getWeChatIconUrl(), getWeChatGender()).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(getString(R.string.bind_success));
                            requestDetailUserInfo();
                        }
                    }
                }).fireFree();
    }

    @Override
    protected void bindFailure() {
        ToastUtil.show(R.string.cancel_bind);
    }

    private void showAgePicker() {
        OptionPicker picker = new OptionPicker(this, mAgeList);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(2);
        if (LocalUser.getUser().getUserInfo().getAge() == null) {
            mSelectAgeListIndex = 73;
        } else {
            if (LocalUser.getUser().getUserInfo().getAge() <= 100) {
                mSelectAgeListIndex = 100 - LocalUser.getUser().getUserInfo().getAge();
            } else {
                mSelectAgeListIndex = 100;
            }
        }
        picker.setSelectedItem(mAgeList[mSelectAgeListIndex]);
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mAge.setSubText(String.valueOf(100 - index));
                    LocalUser.getUser().getUserInfo().setAge(100 - index);
                    mSelectAgeListIndex = index;
                }
            }
        });
        picker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_USER_NAME:
                    mNickName.setSubText(LocalUser.getUser().getUserInfo().getUserName());
                    setResult(RESULT_OK);
                    break;
                case UploadUserImageDialogFragment.REQ_CLIP_HEAD_IMAGE_PAGE:
                    updateUserImage();
                    setResult(RESULT_OK);
                    break;
                case REQ_CODE_LOCATION:
                    mLocation.setText(LocalUser.getUser().getUserInfo().getLand());
                    break;
                case REQ_CODE_LOGIN:
                    openLevelStartPage();
                    break;
                case REQ_CODE_CREDIT_APPROVE:
                    updateUserCreditStatus(CREDIT_IS_APPROVE_ING);
                    break;
                case REQ_CODE_PROFILE_INTRODUCTION:
                    requestDetailUserInfo();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onUserSex(int userSex) {
        if (userSex != 0) {
            if (userSex == UserInfo.SEX_GIRL) {
                mSex.setSubText(getString(R.string.girl));
            } else if (userSex == UserInfo.SEX_BOY) {
                mSex.setSubText(getString(R.string.boy));
            }
        }
    }

}
