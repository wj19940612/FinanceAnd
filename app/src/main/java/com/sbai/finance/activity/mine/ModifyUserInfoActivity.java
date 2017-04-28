package com.sbai.finance.activity.mine;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
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
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.IconTextRow;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

public class ModifyUserInfoActivity extends BaseActivity {

    private static final int REQ_CODE_USER_NAME = 165;

    private static final String SEX_BOY = "男";
    private static final String SEX_GIRL = "女";

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
    AppCompatTextView mLocation;
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
            mAgeList[i] = ((i + 1917) + "年");
        }
        requestDetailUserInfo();
        updateUserImage();

        updateUserInfo();
    }

    private void updateUserInfo() {
        UserInfo userInfo = LocalUser.getUser().getUserInfo();
        mNickName.setSubText(userInfo.getUserName());
        mSex.setSubText(userInfo.getChinaSex());
        if (userInfo.getAge() != null) {
            mAge.setSubText(userInfo.getAge().toString());
        }
        mLocation.setText(userInfo.getLand());
    }

    private void updateUserImage() {
        if (LocalUser.getUser().isLogin()) {
            Glide.with(this).load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.ic_default_headportrait160x160)
                    .into(mUserHeadImage);
        } else {
            Glide.with(this).load(R.drawable.ic_default_headportrait160x160)
                    .bitmapTransform(new GlideCircleTransform(getActivity()))
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
                        LocalUser.getUser().getUserInfo().updateLocalUserInfo(data);
                        updateUserInfo();
                        updateUserImage();
                    }
                })
                .fire();
    }

    private void submitUserInfo() {
        int age = 0;
        if (!TextUtils.isEmpty(mAge.getSubText().trim())) {
            age = Integer.parseInt(mAge.getSubText().trim());
        }

        String land = "";
        if (!TextUtils.isEmpty(mLocation.getText())) {
            land = mLocation.getText().toString().trim();
        }

        int sex = 0;
        if (!TextUtils.isEmpty(mSex.getSubText().trim())) {
            sex = mSex.getSubText().equalsIgnoreCase(SEX_BOY) ? 1 : 2;
        }
        Client.updateUserInfo(age, land, sex)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.hasData()) {
                            Log.d(TAG, "onRespSuccess: " + resp.getData().toString());
                        }
                    }
                })
                .fireSync();
    }

    @Override
    public void onBackPressed() {
        submitUserInfo();
        super.onBackPressed();
    }

    @OnClick({R.id.headImageLayout, R.id.nickName, R.id.sex, R.id.age, R.id.location, R.id.credit, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headImageLayout:
                UploadUserImageDialogFragment.newInstance().show(getSupportFragmentManager());
                break;
            case R.id.nickName:
                Launcher.with(getActivity(), ModifyUserNameActivity.class).executeForResult(REQ_CODE_USER_NAME);
                break;
            case R.id.sex:
                showSexPicker();
                break;
            case R.id.age:
//                showBirthdayPicker();
                showAgePicker();
                break;
            case R.id.location:
                showLocationPicker();
                break;
            case R.id.credit:
                Launcher.with(getActivity(), CreditActivity.class).execute();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void showAgePicker() {
        OptionPicker picker = new OptionPicker(this, mAgeList);
        picker.setCancelTextColor(Color.WHITE);
        picker.setSubmitTextColor(Color.WHITE);
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setOffset(2);
        if (LocalUser.getUser().getUserInfo().getAge() == null) {
            mSelectAgeListIndex = 100;
        } else {
            mSelectAgeListIndex = 100 - LocalUser.getUser().getUserInfo().getAge();
        }
        picker.setSelectedItem(mAgeList[mSelectAgeListIndex]);
        picker.setLineConfig(new WheelView.LineConfig(0));//使用最长的线
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
                default:
                    break;
            }
        }
    }

    private void showLocationPicker() {
        AddressInitTask addressInitTask = new AddressInitTask(getActivity());
        String land = LocalUser.getUser().getUserInfo().getLand();
        String province = "";
        String city = "";
        String country = "";
        if (!TextUtils.isEmpty(land)) {
            String[] split = land.split("-");
            if (split.length == 3) {
                province = split[0];
                city = split[1];
                country = split[2];
            }
        }
        addressInitTask.execute(province, city, country);
    }

    private void showSexPicker() {
        OptionPicker picker = new OptionPicker(this, new String[]{SEX_BOY, SEX_GIRL,});
        picker.setCancelTextColor(Color.WHITE);
        picker.setSubmitTextColor(Color.WHITE);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setOffset(1);
        if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getChinaSex())) {
            picker.setSelectedItem(LocalUser.getUser().getUserInfo().getChinaSex());
        }
//        picker.setTopPadding(toDp(10));
//                picker.setTextSize(11);
        picker.setLineConfig(new WheelView.LineConfig(0));//使用最长的线
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mSex.setSubText(item);
                    LocalUser.getUser().getUserInfo().setChinaSex(item);
                }
            }
        });
        picker.show();
    }

    private void showBirthdayPicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

//        final String birthday = LocalUser.getUser().getUserInfo().getBirthday();
        final String birthday = mAge.getSubText();
        if (!TextUtils.isEmpty(birthday)) {
            String[] split = birthday.split("-");
            if (split.length == 3) {
                year = Integer.valueOf(split[0]);
                month = Integer.valueOf(split[1]) - 1;
                day = Integer.valueOf(split[2]);
            }
        }
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "年  " + year + " 月 " + month + "  日  " + dayOfMonth);
            }
        }, year, month, day);

        //设置时间picker的取消颜色
        SpannableStringBuilder cancelSpannableString = new SpannableStringBuilder();
        cancelSpannableString.append(getString(R.string.cancel));
        ForegroundColorSpan backgroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.assistText));
        cancelSpannableString.setSpan(backgroundColorSpan, 0, cancelSpannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, cancelSpannableString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //设置时间picker的确定颜色
        SpannableStringBuilder confirmSpannableString = new SpannableStringBuilder();
        confirmSpannableString.append(getString(R.string.ok));
        ForegroundColorSpan confirmColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary));
        confirmSpannableString.setSpan(confirmColorSpan, 0, confirmSpannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, confirmSpannableString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                int dayOfMonth = datePicker.getDayOfMonth();

                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                int nowMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                if (year <= nowYear && month <= nowMonth && dayOfMonth <= today) {
                    StringBuilder birthdayDate = new StringBuilder();
                    mAge.setSubText(formatBirthdayDate(year, month, dayOfMonth, birthdayDate));
//                    LocalUser.getUser().getUserInfo().setBirthday(formatBirthdayDate(year, month, dayOfMonth, birthdayDate));
                } else {
                    ToastUtil.curt("不能大于当前时间");
                }
            }
        });
        datePickerDialog.show();
    }

    private String formatBirthdayDate(int year, int month, int dayOfMonth, StringBuilder birthdayDate) {
        birthdayDate.append(year);
        birthdayDate.append("-");
        birthdayDate.append(month);
        birthdayDate.append("-");
        birthdayDate.append(dayOfMonth);
        return birthdayDate.toString();
    }

    private void logout() {
        Client.logout()
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            LocalUser.getUser().logout();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
    }


    /**
     * 获取地址数据并显示地址选择器
     *
     * @author
     * @since 2015/12/15
     */
    private class AddressInitTask extends AsyncTask<String, Void, ArrayList<Province>> {
        private static final String TAG = "AddressInitTask";

        private Activity mActivity;
        private String mSelectedProvince = "",
                mSelectedCity = "",
                mSelectedCounty = "";
        private boolean mHideCounty;

        public AddressInitTask(Activity activity) {
            this.mActivity = activity;
        }

        /**
         * 初始化为不显示区县的模式
         */
        public AddressInitTask(Activity activity, boolean hideCounty) {
            this.mActivity = activity;
            this.mHideCounty = hideCounty;
        }

        @Override
        protected ArrayList<Province> doInBackground(String... params) {
            if (params != null) {
                switch (params.length) {
                    case 1:
                        mSelectedProvince = params[0];
                        break;
                    case 2:
                        mSelectedProvince = params[0];
                        mSelectedCity = params[1];
                        break;
                    case 3:
                        mSelectedProvince = params[0];
                        mSelectedCity = params[1];
                        mSelectedCounty = params[2];
                        break;
                    default:
                        break;
                }
            }
            ArrayList<Province> data = new ArrayList<Province>();
            try {
                String json = ConvertUtils.toString(mActivity.getAssets().open("city.json"));

                Type listType = new TypeToken<ArrayList<Province>>() {
                }.getType();

                ArrayList<Province> provinces = new Gson().fromJson(json, listType);
                data.addAll(provinces);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Province> result) {
//        dialog.dismiss();
            if (result.size() > 0) {
                AddressPicker picker = new AddressPicker(mActivity, result);
                picker.setHideCounty(mHideCounty);
                if (mHideCounty) {
                    picker.setColumnWeight(1 / 3.0, 2 / 3.0);//将屏幕分为3份，省级和地级的比例为1:2
                } else {
                    picker.setColumnWeight(2 / 8.0, 3 / 8.0, 3 / 8.0);//省级、地级和县级的比例为2:3:3
                }
                picker.setCancelTextColor(Color.WHITE);
                picker.setSubmitTextColor(Color.WHITE);
                picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.warningText));
                picker.setAnimationStyle(R.style.BottomDialogAnimation);
                picker.setSelectedItem(mSelectedProvince, mSelectedCity, mSelectedCounty);
                WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
                lineConfig.setColor(Color.RED);
//            lineConfig.setColor(R.color.lucky);//设置分割线颜色
                picker.setLineConfig(lineConfig);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        LocalUser.getUser().getUserInfo().setLand(province.getAreaName() + "-" + city.getAreaName() + "-" + county.getAreaName());
                        mLocation.setText(LocalUser.getUser().getUserInfo().getLand());
                    }
                });
                picker.show();
            } else {
                Toast.makeText(mActivity, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
