package com.sbai.finance.activity.mine.setting;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.ModifyUserInfoActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.utils.GpsUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

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
    public static final int GPS_REQUEST_CODE=250;

    private static final int REQ_CODE_GPS = 426;

    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.choiceLocation)
    IconTextRow mChoiceLocation;
    private Address mAddress;
    private boolean mIsNeedUpdateLocation;
    //是否更新最新的地址
    private boolean isUpdateLand = false;


    @Override
    protected void onResume() {
        super.onResume();
        updateLocationInfo();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        mIsNeedUpdateLocation = getIntent().getBooleanExtra(Launcher.EX_PAYLOAD, false);
        Log.d(TAG, "onCreate: " + isUpdateLand);
    }
    @OnClick({R.id.choiceLocation,R.id.location})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choiceLocation:
                showLocationPicker();
                break;
            case R.id.location:
                updateLocationInfo();
                break;
        }
    }

    private void updateLocationInfo() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this,permissions[0]);
            if (check == PackageManager.PERMISSION_GRANTED) {
                openGPSSettings();
            } else {
                requestPermissions(permissions, 1);
            }
        } else {
            openGPSSettings();
        }
    }

   private void requestLocation(){
       mLocation.setText("");
       GpsUtils gpsUtils = new GpsUtils();
       mAddress = gpsUtils.getAddress();
       if (mAddress != null) {
           String land = mAddress.getAdminArea() + " " + mAddress.getLocality() + " " + mAddress.getSubLocality();
           mLocation.setText(land);
           if (isUpdateLand && getCallingActivity() != null &&
                   getCallingActivity().getClassName().equalsIgnoreCase(ModifyUserInfoActivity.class.getName())) {
               //更新地址和经纬度
               UserInfo userInfo = LocalUser.getUser().getUserInfo();
               userInfo.setLand(land);
               if (gpsUtils.getlongitude() != 0) {
                   userInfo.setLongitude(gpsUtils.getlongitude());
               }
               if (gpsUtils.getLatitude() != 0) {
                   userInfo.setLatitude(gpsUtils.getLatitude());
               }
               LocalUser.getUser().setUserInfo(userInfo);
           }
       }
       if (TextUtils.isEmpty(mLocation.getText())){
           mLocation.setText(getString(R.string.re_location));
       }
   }


    private void showLocationPicker() {
        AddressInitTask addressInitTask = new AddressInitTask(getActivity());
        addressInitTask.setmIsNeedUpdateLocation(mIsNeedUpdateLocation);
        String province = "";
        String city = "";
        String country = "";
        String[] split;
        if (!TextUtils.isEmpty(mLocation.getText())){
          split = mLocation.getText().toString().split(" ");
          if (split.length == 3) {
            province = split[0];
            city = split[1];
            country = split[2];
          }
        }else{
            split=LocalUser.getUser().getUserInfo().getLand().split("-");
            if (split.length == 3) {
                province = split[0];
                city = split[1];
                country = split[2];
            }
        }
        addressInitTask.execute(province, city, country);
    }

    private void returnAddress() {
        Intent intent = new Intent();
        intent.putExtra(Launcher.EX_PAYLOAD_1, mAddress);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnAddress();
        super.onBackPressed();
    }
    /**
     * GPS设置
     */
    private void openGPSSettings() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            SmartDialog.with(getActivity(), getString(R.string.open_gps))
                    .setMessageTextSize(15)
                    .setPositive(R.string.setting, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegative(R.string.cancel)
                    .show();
        }else{
            requestLocation();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            requestLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        private boolean mIsNeedUpdateLand;

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


        public void setmIsNeedUpdateLocation(boolean isNeedUpdateLocation) {
            this.mIsNeedUpdateLand = isNeedUpdateLocation;
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
                picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
                picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
                picker.setAnimationStyle(R.style.BottomDialogAnimation);
                picker.setSelectedItem(mSelectedProvince, mSelectedCity, mSelectedCounty);
                picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackAssist));
                WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
                lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.background));
                picker.setLineConfig(lineConfig);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        if (mIsNeedUpdateLand) {
                            LocalUser.getUser().getUserInfo().setLand(province.getAreaName() + "-" + city.getAreaName() + "-" + county.getAreaName());
                        }

                        if (mAddress == null){
                            mAddress = new Address(new Locale(Locale.CHINA.getLanguage()));
                            mAddress.setLatitude(0.00);
                            mAddress.setLongitude(0.00);
                        }
                        mAddress.setAdminArea(province.getAreaName());
                        mAddress.setLocality(city.getAreaName());
                        mAddress.setSubLocality(county.getAreaName());
                        returnAddress();
                    }
                });
                picker.show();
            } else {
                Toast.makeText(mActivity, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
