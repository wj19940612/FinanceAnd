package com.sbai.finance.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linrongfang on 2017/6/9.
 */

public class PermissionUtil {

    public static final int REQ_CODE_ASK_PERMISSION = 1;

    /**
     * 检测单项权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        int targetSdkVersion = -1;
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    //6.0 以下判断照相机权限是否打开
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 检测多项权限 只要有一项权限拒绝 立即返回
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissions(Context context, String... permission) {
        boolean result = false;
        int targetSdkVersion = -1;
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        }

        for (int i = 0; i < permission.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    result = context.checkSelfPermission(permission[i])
                            == PackageManager.PERMISSION_GRANTED;
                } else {
                    result = PermissionChecker.checkSelfPermission(context, permission[i])
                            == PermissionChecker.PERMISSION_GRANTED;
                }
            }

            if (!result) {
                break;
            }
        }
        return result;
    }


    /**
     * 检查权限
     *
     * @param context
     * @param permissions
     * @return 所有被拒绝的权限
     */
    public static String[] checkDenyPermissions(Context context, String... permissions) {
        PackageInfo info = null;
        int targetSdkVersion = -1;
        List<String> denyPermissions = new ArrayList<>();

        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        }

        boolean result = true;
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    result = context.checkSelfPermission(permissions[i])
                            == PackageManager.PERMISSION_GRANTED;
                } else {
                    result = PermissionChecker.checkSelfPermission(context, permissions[i])
                            == PermissionChecker.PERMISSION_GRANTED;
                }
            }

            if (!result) {
                denyPermissions.add(permissions[i]);
            }

        }
        return denyPermissions.toArray(new String[]{});
    }

    public static boolean isStoragePermissionGranted(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        }
    }

}
