package com.sbai.finance.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linrongfang on 2017/6/9.
 */

public class PermissionUtil {
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

}
