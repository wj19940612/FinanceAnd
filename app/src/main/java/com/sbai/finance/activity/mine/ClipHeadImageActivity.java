package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.view.clipimage.ClipImageLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClipHeadImageActivity extends BaseActivity {

    public static final String KEY_CLIP_USER_IMAGE = "CLIP_USER_IMAGE";
    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;
    @BindView(R.id.cancel)
    AppCompatTextView mCancel;
    @BindView(R.id.complete)
    AppCompatTextView mComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_head_image);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String bitmapPath = intent.getStringExtra(KEY_CLIP_USER_IMAGE);
        //修复米5的图片显示问题
        String forMi5 = bitmapPath.replace("/raw//", "");
        Log.d(TAG, "传入的地址" + bitmapPath);
        mClipImageLayout.setZoomImageViewImage(forMi5);
    }

    @OnClick({R.id.cancel, R.id.complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.complete:
                Bitmap clipBitmap = mClipImageLayout.clip();
                String bitmapToBase64 = ImageUtils.bitmapToBase64(clipBitmap);
                if (clipBitmap != null) {
                    clipBitmap.recycle();
                }
                confirmUserNewHeadImage(bitmapToBase64);
                break;
        }
    }

    private void confirmUserNewHeadImage(String bitmapToBase64) {
        Log.d(TAG, "confirmUserNewHeadImage: "+bitmapToBase64.length());
        Client.updateUserHeadImage(bitmapToBase64)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<String>, String>() {
                    @Override
                    protected void onRespSuccessData(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            UserInfo userInfo = LocalUser.getUser().getUserInfo();
                            userInfo.setUserPortrait(data);
                            LocalUser.getUser().setUserInfo(userInfo);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .fire();
    }
}
