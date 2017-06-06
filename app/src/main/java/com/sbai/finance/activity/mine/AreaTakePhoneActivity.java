package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.camera.CameraSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AreaTakePhoneActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.cameraSurfaceView)
    CameraSurfaceView mCameraSurfaceView;
//    @BindView(R.id.cameraTopRectView)
//    CameraTopRectView mCameraTopRectView;
    @BindView(R.id.takePhone)
    ImageView mTakePhone;
    @BindView(R.id.cancel)
    AppCompatImageView mCancel;
    @BindView(R.id.confirm)
    AppCompatImageView mConfirm;

    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_take_phone);
        ButterKnife.bind(this);
        mCameraSurfaceView.setOnImageListener(new CameraSurfaceView.OnImageListener() {
            @Override
            public void onImage(String imageUrl) {
                Log.d(TAG, "身份证: " + imageUrl);
                mImageUrl = imageUrl;
            }
        });
    }

    @OnClick({R.id.takePhone, R.id.cancel, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.takePhone:
                mTakePhone.setVisibility(View.GONE);
                mCameraSurfaceView.takePicture();
                mCancel.setVisibility(View.VISIBLE);
                mConfirm.setVisibility(View.VISIBLE);
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                Intent intent = new Intent();
                intent.putExtra(Launcher.EX_PAYLOAD, mImageUrl);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
