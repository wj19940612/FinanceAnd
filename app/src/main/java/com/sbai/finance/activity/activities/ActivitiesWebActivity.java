package com.sbai.finance.activity.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.WebView;

import com.sbai.finance.AppJs;
import com.sbai.finance.activity.WebActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 活动页面
 */

public class ActivitiesWebActivity extends WebActivity {
    private WebView mWebView;

    @Override
    protected void initWebView() {
        super.initWebView();
        mWebView = getWebView();
        if (mWebView != null) {
            mWebView.setDrawingCacheEnabled(true);
            mWebView.addJavascriptInterface(new AppJs(this), "AppJs");
        }
    }

    @Override
    protected void screenShot() {
        if (mWebView == null) return;
        mWebView.buildDrawingCache();
        Bitmap bitmap = mWebView.getDrawingCache();
        if (bitmap == null) {
            Picture snapShot = mWebView.capturePicture();
            bitmap = Bitmap.createBitmap(mWebView.getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            snapShot.draw(canvas);
        }
        if (bitmap == null) {
            Log.d(TAG, "获取图片失败");
            return;
        }
        saveImageToGallery(getApplicationContext(), bitmap);
    }

    private void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(getRootPath(), "screenShots");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private String getRootPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return Environment.getRootDirectory().getPath();
        }
    }
}
