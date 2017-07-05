package com.sbai.finance.fragment.dialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
import com.sbai.finance.utils.ToastUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/12/19.
 * 上传用户头像
 */

public class UploadHelpImageDialogFragment extends DialogFragment {

    /**
     * 打开相机的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_PHONES = 600;
    /**
     * 打开裁剪界面的请求码
     */
    private static final int REQ_CODE_CROP_IMAGE = 204;
    /**
     * 打开自定义裁剪页面的请求码
     */
    public static final int REQ_CLIP_HEAD_IMAGE_PAGE = 144;
    private Unbinder mBind;
    private File mFile;
    private OnDismissListener mOnDismissListener;
    public interface OnDismissListener{
         void onGetImagePath(String path);
    }
    public UploadHelpImageDialogFragment setOnDismissListener(OnDismissListener onDismissListener){
        mOnDismissListener = onDismissListener;
        return this;
    }
    public UploadHelpImageDialogFragment() {

    }

    public static UploadHelpImageDialogFragment newInstance() {
        Bundle args = new Bundle();
        UploadHelpImageDialogFragment fragment = new UploadHelpImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.UpLoadHeadImageDialog);
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
                } else {
                    ToastUtil.show(getString(R.string.please_open_camera_permission));
                }
                break;
            case R.id.takePhoneFromGallery:
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    Intent openAlbumIntent = new Intent(
                            Intent.ACTION_PICK);
                    openAlbumIntent.setType("image/*");
                    startActivityForResult(openAlbumIntent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
                } else {
                    ToastUtil.show(R.string.sd_is_not_useful);
                }
                break;
            case R.id.takePhoneCancel:
                this.dismiss();
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
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    String galleryBitmapPath = getGalleryBitmapPath(data);
                    if (!TextUtils.isEmpty(galleryBitmapPath)) {
                        saveImagePage(galleryBitmapPath);
                    }
                    break;
            }
        }

    }

    private String getGalleryBitmapPath(Intent data) {
        Uri photosUri = data.getData();
        if (photosUri != null) {
            if (!TextUtils.isEmpty(photosUri.getPath()) && photosUri.getPath().endsWith("jpg")) {
                return photosUri.getPath();
            } else {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(photosUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    if (!TextUtils.isEmpty(path)) {
                        return path;
                    }
                    cursor.close();
                }
            }
        }
        return null;
    }

    private void saveImagePage(String imaUri) {
        //修复米5的图片显示问题
        String forMi5 = imaUri.replace("/raw//", "");
        mOnDismissListener.onGetImagePath(forMi5);
        dismiss();
    }
}
