package com.sbai.finance.fragment.dialog;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.ImageSelectActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/5/10.
 */

public class UploadFeedbackImageDialogFragment extends DialogFragment {

    /**
     * 打开相机的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_GALLERY = 600;

    private Unbinder mBind;
    private File mFile;
    private OnDismissListener mOnDismissListener;
    private int mPhotoAmount;

    public interface OnDismissListener {
        void onGetImagePath(String path);
    }

    public UploadFeedbackImageDialogFragment setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public UploadFeedbackImageDialogFragment() {

    }

    public static UploadFeedbackImageDialogFragment newInstance() {
        Bundle args = new Bundle();
        UploadFeedbackImageDialogFragment fragment = new UploadFeedbackImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadFeedbackImageDialogFragment newInstance(int amount) {
        Bundle args = new Bundle();
        args.putInt("amount",amount);
        UploadFeedbackImageDialogFragment fragment = new UploadFeedbackImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.UpLoadHeadImageDialog);
        mPhotoAmount = getArguments().getInt("amount");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_upload_user_image, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.takePhoneFromCamera, R.id.takePhoneFromGallery, R.id.takePhoneCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoneFromCamera:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Intent openCameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    mFile = new File(Environment
                            .getExternalStorageDirectory(), System.currentTimeMillis() + "image.jpg");
                    // 指定照片保存路径（SD卡），image.jpg为一个临时文件，防止拿到
                    Uri mMBitmapUri = Uri.fromFile(mFile);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
                    startActivityForResult(openCameraIntent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
                }else {
                    ToastUtil.show(getString(R.string.please_open_camera_permission));
                }
                this.dismissAllowingStateLoss();
                break;
            case R.id.takePhoneFromGallery:
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    Intent openGalleryIntent = new Intent(getContext(), ImageSelectActivity.class);
                    openGalleryIntent.putExtra(Launcher.EX_PAYLOAD,mPhotoAmount);
                    startActivityForResult(openGalleryIntent, REQ_CODE_TAKE_PHONE_FROM_GALLERY);
                } else {
                    ToastUtil.show(R.string.sd_is_not_useful);
                }
                this.dismissAllowingStateLoss();
                break;
            case R.id.takePhoneCancel:
                this.dismissAllowingStateLoss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, UploadHelpImageDialogFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == FragmentActivity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_TAKE_PHONE_FROM_CAMERA:
                    if (mFile != null) {
                        Uri mMBitmapUri = Uri.fromFile(mFile);
                        if (mMBitmapUri != null) {
                            if (!TextUtils.isEmpty(mMBitmapUri.getPath())) {
                                saveImagePage(mMBitmapUri.getPath());
                            }
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_GALLERY:
                    String mImagePath = data.getStringExtra(Launcher.EX_PAYLOAD);
                    if (!TextUtils.isEmpty(mImagePath)){
                        saveImagePage(mImagePath);
                    }
                    break;
            }
        }

    }

    private void saveImagePage(String imaUri) {
        //修复米5的图片显示问题
        String forMi5 = imaUri.replace("/raw//", "");
        mOnDismissListener.onGetImagePath(forMi5);
        dismiss();
    }
}
