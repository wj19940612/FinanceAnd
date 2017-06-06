package com.sbai.finance.fragment.dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.AreaTakePhoneActivity;
import com.sbai.finance.activity.mine.ClipHeadImageActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/12/19.
 * 上传用户头像
 */

public class UploadUserImageDialogFragment extends DialogFragment {
    private static final String TAG = "UploadUserImageDialogFr";

    private static final String KEY_IF_CLIP_IMAGE = "IF_CLIP_IMAGE";
    private static final String KEY_IMAGE_URL_INDEX = "KEY_IMAGE_URL_INDEX";
    private static final String KEY_IS_AREA_TAKE_PHONE = "KEY_isAreaTakePhone";

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
    //打开区域拍照页面的请求吗
    private static final int REQ_CODE_AREA_TAKE_PHONE = 46605;

    @BindView(R.id.takePhoneFromCamera)
    AppCompatTextView mTakePhoneFromCamera;
    @BindView(R.id.takePhoneFromGallery)
    AppCompatTextView mTakePhoneFromGallery;
    @BindView(R.id.takePhoneCancel)
    AppCompatTextView mTakePhoneCancel;

    private Unbinder mBind;
    private File mFile;
    private boolean mNeedClipImage;
    //用来标记传回的图片地址加载到哪一个image
    private int mImagePathIndex;
    //是否区域拍照  上传身份证
    private boolean mIsAreaTakePhone;
    private OnImagePathListener mOnImagePathListener;

    public interface OnImagePathListener {
        void onImagePath(int index, String imagePath);
    }

    public UploadUserImageDialogFragment() {

    }

    public static UploadUserImageDialogFragment newInstance(boolean ifClipImage) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IF_CLIP_IMAGE, ifClipImage);
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadUserImageDialogFragment newInstance(int index, boolean ifClipImage) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IF_CLIP_IMAGE, ifClipImage);
        args.putInt(KEY_IMAGE_URL_INDEX, index);
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadUserImageDialogFragment newInstance(int index, boolean ifClipImage, boolean isAreaTakePhone) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IF_CLIP_IMAGE, ifClipImage);
        args.putInt(KEY_IMAGE_URL_INDEX, index);
        args.putBoolean(KEY_IS_AREA_TAKE_PHONE, isAreaTakePhone);
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImagePathListener) {
            mOnImagePathListener = (OnImagePathListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNeedClipImage = getArguments().getBoolean(KEY_IF_CLIP_IMAGE, true);
            mImagePathIndex = getArguments().getInt(KEY_IMAGE_URL_INDEX, -1);
            mIsAreaTakePhone = getArguments().getBoolean(KEY_IS_AREA_TAKE_PHONE, false);
        }
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
                if (mIsAreaTakePhone) {
                    startActivityForResult(new Intent(getActivity(), AreaTakePhoneActivity.class), REQ_CODE_AREA_TAKE_PHONE);
                } else {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        mFile = new File(Environment
                                .getExternalStorageDirectory(), System.currentTimeMillis() + "image.jpg");
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，防止拿到
                        Uri mMBitmapUri = Uri.fromFile(mFile);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
                        startActivityForResult(openCameraIntent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
                    }
                }
                break;
            case R.id.takePhoneFromGallery:
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    Intent openAlbumIntent = new Intent(
                            Intent.ACTION_PICK);
                    openAlbumIntent.setType("image/*");
                    startActivityForResult(openAlbumIntent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
                } else {
                    ToastUtil.curt(R.string.sd_is_not_useful);
                }
                break;
            case R.id.takePhoneCancel:
                this.dismiss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, UploadUserImageDialogFragment.class.getSimpleName());
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
                                openClipImagePage(mMBitmapUri.getPath());
                            }
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    String galleryBitmapPath = getGalleryBitmapPath(data);
                    if (!TextUtils.isEmpty(galleryBitmapPath)) {
                        openClipImagePage(galleryBitmapPath);
                    }
                    break;
                case REQ_CODE_AREA_TAKE_PHONE:
                    String imageUrl = data.getStringExtra(Launcher.EX_PAYLOAD);
                    if (!TextUtils.isEmpty(imageUrl)) {
                        openClipImagePage(imageUrl);
                    }
                    break;
            }
        }

    }

    private String getGalleryBitmapPath(Intent data) {
        if (data != null && data.getData() != null) {
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
        }
        return null;
    }

    private void openClipImagePage(String imaUri) {
        if (mNeedClipImage) {
            Intent intent = new Intent(getActivity(), ClipHeadImageActivity.class);
            intent.putExtra(ClipHeadImageActivity.KEY_CLIP_USER_IMAGE, imaUri);
            getActivity().startActivityForResult(intent, REQ_CLIP_HEAD_IMAGE_PAGE);
        } else {
            if (mOnImagePathListener != null) {
                mOnImagePathListener.onImagePath(mImagePathIndex, imaUri.replace("/raw//", ""));
            }
        }
        dismiss();
    }

    //调用系统裁剪，有问题，有些手机不支持裁剪后获取图片
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 600);//图片输出大小
        intent.putExtra("outputY", 600);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回格式
        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
    }
}
