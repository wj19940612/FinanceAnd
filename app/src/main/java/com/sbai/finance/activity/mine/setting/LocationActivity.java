package com.sbai.finance.activity.mine.setting;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.utils.GpsUtils;
import com.sbai.finance.view.IconTextRow;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

public class LocationActivity extends BaseActivity {
    public static final int GPS_REQUEST_CODE = 250;


    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.choiceLocation)
    IconTextRow mChoiceLocation;

    private Address mAddress;

    private boolean isClosePage = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        requestLocation();
    }

    @OnClick({R.id.choiceLocation, R.id.location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choiceLocation:
                showLocationPicker();
                break;
            case R.id.location:
                isClosePage = true;
                requestLocation();
                break;
        }
    }


    private void requestLocation() {
        GpsUtils gpsUtils = new GpsUtils(this);
        mAddress = gpsUtils.getAddress();
        if (mAddress != null) {
            String land = mAddress.getAdminArea() + "-" + mAddress.getLocality() + "-" + mAddress.getSubLocality();
            mLocation.setText(land);
            if (isClosePage) {
                //更新地址和经纬度
                UserInfo userInfo = LocalUser.getUser().getUserInfo();
                userInfo.setLand(land);
                if (gpsUtils.getlongitude() != 0) {
                    userInfo.setLongitude(gpsUtils.getlongitude());
                }
                if (gpsUtils.getLatitude() != 0) {
                    userInfo.setLatitude(gpsUtils.getLatitude());
                }
                setResult(RESULT_OK);
                finish();
            }
        } else {
            mLocation.setText(getString(R.string.re_location));
        }
    }


    private void showLocationPicker() {
        AddressInitTask addressInitTask = new AddressInitTask(getActivity());
        String province = "";
        String city = "";
        String country = "";
        String[] split;
        String land = "";
        if (!TextUtils.isEmpty(LocalUser.getUser().getUserInfo().getLand())) {
            land = LocalUser.getUser().getUserInfo().getLand();
        } else {
            land = mLocation.getText().toString();
        }
        if (!TextUtils.isEmpty(land)) {
            split = land.split("-");
            if (split.length == 3) {
                province = split[0];
                city = split[1];
                country = split[2];
            }
        }
        addressInitTask.execute(province, city, country);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            requestLocation();
        }
    }


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
            if (result.size() > 0) {
                AddressPicker picker = new AddressPicker(mActivity, result);
                picker.setHideCounty(mHideCounty);
                if (mHideCounty) {
                    picker.setColumnWeight(1 / 3.0, 2 / 3.0);//将屏幕分为3份，省级和地级的比例为1:2
                } else {
                    picker.setColumnWeight(2 / 8.0, 3 / 8.0, 3 / 8.0);//省级、地级和县级的比例为2:3:3
                }
                picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
                picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
                picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                picker.setAnimationStyle(R.style.BottomDialogAnimation);
                picker.setSelectedItem(mSelectedProvince, mSelectedCity, mSelectedCounty);
                picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
                WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
                lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.background));
                picker.setLineConfig(lineConfig);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        LocalUser.getUser().getUserInfo().setLand(province.getAreaName() + "-" + city.getAreaName() + "-" + county.getAreaName());
                        mChoiceLocation.setSubText(LocalUser.getUser().getUserInfo().getLand());
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                picker.show();
            } else {
                Toast.makeText(mActivity, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
