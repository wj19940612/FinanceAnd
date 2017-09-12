package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.setting.LocationActivity;
import com.sbai.finance.fragment.dialog.ChooseSexDialogFragment;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserDetailInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.autofit.AutofitTextView;
import com.sbai.finance.websocket.WsClient;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.CookieManger;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class ModifyUserInfoActivity extends BaseActivity implements ChooseSexDialogFragment.OnUserSexListener {

    private static final int REQ_CODE_USER_NAME = 165;

    //地址界面
    private static final int REQ_CODE_LOCATION = 803;

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
    @BindView(R.id.credit)
    IconTextRow mCredit;
    @BindView(R.id.logout)
    AppCompatTextView mLogout;

    private String[] mAgeList;

    private int mSelectAgeListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mAgeList = new String[101];
        for (int i = 0; i < 101; i++) {
            mAgeList[i] = ((i + Calendar.getInstance().get(Calendar.YEAR) - 100) + "年");
        }
        requestDetailUserInfo();
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
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .transform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mUserHeadImage);
        } else {
            GlideApp.with(this).load(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
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
                        Log.d(TAG, "onRespSuccessData:  " + data.toString());
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

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.sex, R.id.age, R.id.location, R.id.credit, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                String url[] = new String[]{LocalUser.getUser().getUserInfo().getUserPortrait()};
                UploadUserImageDialogFragment uploadUserImageDialogFragment = UploadUserImageDialogFragment.newInstance(true, url);
                uploadUserImageDialogFragment.show(getSupportFragmentManager());
                break;
            case R.id.nickName:
                umengEventCount(UmengCountEventIdUtils.ME_NICK_NAME);
                Launcher.with(getActivity(), ModifyUserNameActivity.class).executeForResult(REQ_CODE_USER_NAME);
                break;
            case R.id.sex:
                umengEventCount(UmengCountEventIdUtils.ME_SEX);
                new ChooseSexDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.age:
                umengEventCount(UmengCountEventIdUtils.ME_AGE);
                showAgePicker();
                break;
            case R.id.location:
                umengEventCount(UmengCountEventIdUtils.ME_LOCATION);
                Launcher.with(getActivity(), LocationActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, true)
                        .executeForResult(REQ_CODE_LOCATION);
                break;
            case R.id.credit:
                umengEventCount(UmengCountEventIdUtils.ME_CREDIT);
                Launcher.with(getActivity(), CreditActivity.class).execute();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void showAgePicker() {
        OptionPicker picker = new OptionPicker(this, mAgeList);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.picker_press));
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
                default:
                    break;
            }
        }
    }

    private void logout() {
        Client.logout()
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            LocalUser.getUser().logout();
                            CookieManger.getInstance().clearRawCookies();
                            WsClient.get().close();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
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
