package com.sbai.finance.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.view.SmartDialog;

import java.io.IOException;
import java.util.List;

import static com.sbai.finance.activity.mine.setting.LocationActivity.GPS_REQUEST_CODE;

/**
 * Created by ${wangJie} on 2017/6/2.
 */

public class GpsUtils {
    private static final String TAG = "GpsUtils";
    private LocationManager mLocationManager;
    private Address mAddress;

    public Address getAddress() {
        return mAddress;
    }

    public double lat = 0;
    public double lng = 0;

    //public static String cityName = "深圳";  //城市名
    public String cityName;  //城市名

    private Geocoder geocoder;    //此对象能通过经纬度来获取相应的城市等信息

    private AppCompatActivity mContext;

    public GpsUtils(AppCompatActivity context) {
        mContext = context;
        mLocationManager = (LocationManager) App.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            geocoder = new Geocoder(App.getAppContext());
            //用于获取Location对象，以及其他
            LocationManager locationManager;
            String serviceName = Context.LOCATION_SERVICE;
            //实例化一个LocationManager对象
            locationManager = (LocationManager) App.getAppContext().getSystemService(serviceName);

            //provider的类型
            //获取所有可用的位置提供器
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);   //高精度
            criteria.setAltitudeRequired(false);    //不要求海拔
            criteria.setBearingRequired(false); //不要求方位
            criteria.setCostAllowed(false); //不允许有话费
            criteria.setPowerRequirement(Criteria.POWER_LOW);   //低功耗
            String provider = mLocationManager.getBestProvider(criteria, true);
            //通过最后一次的地理位置来获得Location对象
//            Location location = locationManager.getLastKnownLocation(provider);
            Location location;

            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                String queryed_name = updateWithNewLocation(location);
                if ((queryed_name != null) && (0 != queryed_name.length())) {
                    cityName = queryed_name;
                }

        /*
         * 第二个参数表示更新的周期，单位为毫秒；第三个参数的含义表示最小距离间隔，单位是米
         * 设定每30秒进行一次自动定位
         */
                locationManager.requestLocationUpdates(provider, 30000, 50,
                        locationListener);
                //   移除监听器
//            locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.d(TAG, "GpsUtils: " + e.toString());
            } finally {
                SmartDialog.with(mContext, mContext.getString(R.string.open_gps))
                        .setPositive(R.string.setting, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivityForResult(intent, GPS_REQUEST_CODE);
                                dialog.dismiss();
                            }
                        })
                        .setNegative(R.string.cancel)
                        .show();
            }


        } else {
            SmartDialog.with(mContext, mContext.getString(R.string.open_gps))
                    .setPositive(R.string.setting, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivityForResult(intent, GPS_REQUEST_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegative(R.string.cancel)
                    .show();
        }
    }

    public double getlongitude() {
        return lng;
    }

    public double getLatitude() {
        return lat;
    }

    /**
     * 方位改变时触发，进行调用
     */
    private final LocationListener locationListener = new LocationListener() {
        String tempCityName;

        public void onLocationChanged(Location location) {

            tempCityName = updateWithNewLocation(location);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {
                cityName = tempCityName;
            }
        }

        public void onProviderDisabled(String provider) {
            tempCityName = updateWithNewLocation(mLocationManager.getLastKnownLocation(provider));
            if ((tempCityName != null) && (tempCityName.length() != 0)) {
                cityName = tempCityName;
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新location
     *
     * @param location
     * @return cityName
     */
    private String updateWithNewLocation(Location location) {
        String mcityName = "";
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d(TAG, "updateWithNewLocation: " + lat + "  " + lng);
        } else {
            Log.d(TAG, "updateWithNewLocation: 无法获取地理信息");
        }

        try {
            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度
            if (addList != null && addList.size() > 0) {
                String province = "";
                String city = "";
                String country = "";
                for (int i = 0; i < addList.size(); i++) {
                    Address add = addList.get(i);
                    mAddress = add;
                    mcityName += add.getLocality();
                    province = add.getAdminArea();
                    country = add.getSubLocality();
                    city = add.getLocality();

                    Log.d(TAG, "updateWithNewLocation:   getCountryName" + add.getCountryName());
                    Log.d(TAG, "updateWithNewLocation:   具体地址  getFeatureName" + add.getFeatureName());
                    Log.d(TAG, "updateWithNewLocation:   getPhone" + add.getPhone());
                    Log.d(TAG, "updateWithNewLocation:   省份 getAdminArea  " + add.getAdminArea());
                    Log.d(TAG, "updateWithNewLocation:   getCountryCode  " + add.getCountryCode());
                    Log.d(TAG, "updateWithNewLocation:   区  getSubLocality  " + add.getSubLocality());
                    Log.d(TAG, "updateWithNewLocation:     getSubAdminArea  " + add.getSubAdminArea());
                    Log.d(TAG, "updateWithNewLocation:     getLocality  " + add.getLocality());
                }
                Log.d(TAG, "具体地址 : " + province + " " + city + " " + country);
//                UserInfo userInfo = LocalUser.getUser().getUserInfo();
//                userInfo.setLand(province + "-" + city + "-" + country);
//                LocalUser.getUser().setUserInfo(userInfo);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, " e: " + e.getMessage());
        }
        if (mcityName.length() != 0) {
            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName;
        }
    }
}
