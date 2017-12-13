package com.sbai.finance.utils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sbai.finance.App;

import java.io.File;

/**
 * Created by ${wangJie} on 2017/7/4.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    private static File sFile;

    /**
     * ExternalStorage: Traditionally SD card, or a built-in storage in device that is distinct from
     * the protected internal storage and can be mounted as a filesystem on a computer.
     */
    private static boolean sExternalStorageAvailable = false;
    private static boolean sExternalStorageWriteable = false;

    public static boolean isExteralStorageAvailable() {
        updateExternalStorageState();
        return sExternalStorageAvailable;
    }

    public static boolean isExternalStorageWriteable() {
        updateExternalStorageState();
        return sExternalStorageWriteable;
    }

    private static void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sExternalStorageAvailable = true;
            sExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            sExternalStorageAvailable = true;
            sExternalStorageWriteable = false;
        } else {
            sExternalStorageAvailable = false;
            sExternalStorageWriteable = false;
        }
    }

    public static void registerExternalStorageWatcher(Activity activity, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        if (activity != null) {
            activity.registerReceiver(receiver, filter);
        }
    }

    public static void unregisterExternalStorageWatcher(Activity activity, BroadcastReceiver receiver) {
        if (activity != null) {
            try {
                activity.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) { // throw when receiver not register
                e.printStackTrace();
            }
        }
    }

    public static final int REQ_CODE_ASK_PERMISSION = 1;

    public static boolean isStoragePermissionGranted(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        }
    }

    public static File createFile(String fileName) {
        return createFile(fileName, "");
    }

    /**
     * @param fileName
     * @param type     Environment.DIRECTORY_PICTURES 图库、音频地址等
     * @return
     */
    public static File createFile(String fileName, String type) {
        File rootFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // Ues external storage first
            try {
                if (!TextUtils.isEmpty(type)) {
                    rootFile = Environment.getExternalStoragePublicDirectory(type);
                    if (!rootFile.mkdirs()) {
                        Log.d(TAG, "ImageUtil: external picture storage is exist");
                    }
                } else {
                    rootFile = App.getAppContext().getExternalCacheDir();
                    if (rootFile == null) {
                        rootFile = Environment.getExternalStorageDirectory();
                    }
                }
            } catch (Exception e) { // In case of folder missing, should not be call
                rootFile = Environment.getExternalStorageDirectory();
                e.printStackTrace();
            } finally {
                if (rootFile != null) {
                    Log.d(TAG, "ImageUtil: external storage is " + rootFile.getAbsolutePath());
                }
            }
        } else {
            rootFile = App.getAppContext().getExternalCacheDir();
        }
        return createFile(rootFile, fileName);
    }


    private static File createFile(File root, String fileName) {
        int lastIndexOfSeparator = fileName.lastIndexOf(File.separator);
        if (lastIndexOfSeparator != -1) {
            String subDir = fileName.substring(0, lastIndexOfSeparator);
            String newFileName = fileName.substring(lastIndexOfSeparator + 1, fileName.length());
            File fullDir = new File(root, subDir);
            if (!fullDir.mkdirs()) {
                Log.d(TAG, "createFile: directory create failure or directory had created");
            }

            if (fullDir.exists()) {
                sFile = new File(fullDir, newFileName);
                return sFile;
            }
            sFile = new File(root, newFileName);
            return sFile;

        } else {
            sFile = new File(root, fileName);
            return sFile;
        }
    }

    public static boolean deleteFile() {
        if (sFile != null) {
            return sFile.delete();
        }
        return false;
    }
}
