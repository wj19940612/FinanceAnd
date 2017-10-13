package com.sbai.finance.view.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sbai.finance.utils.image.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/6/6.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraSurfaceView";

    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;
    private CameraTopRectView mCameraTopRectView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private OnImageListener mOnImageListener;

    public interface OnImageListener {
        void onImage(String imageUrl);
    }

    public void setOnImageListener(OnImageListener onImageListener) {
        mOnImageListener = onImageListener;
    }

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrics((Activity) context);
        mContext = context;
        mCameraTopRectView = new CameraTopRectView(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    private void getScreenMetrics(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) {
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(mHolder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(TAG, "onWindowVisibilityChanged: " + visibility);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
        Log.d(TAG, "surfaceChanged: ");
    }

    public void cameraClose() {
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        mHolder.removeCallback(this);
        mHolder = null;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraClose();
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);
    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }
        return result;
    }

    public void takePicture() {
        //设置参数,并拍照
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        mCamera.takePicture(null, null, mPictureCallback);
//        mCamera.stopPreview();//停止预览
//        mCamera.release();//释放相机资源

    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (OutOfMemoryError error) {
                Log.d(TAG, "onPictureTaken: " + error.toString());
            }

            if (bitmap != null) {
                //图片存储前旋转
                Matrix m = new Matrix();
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                m.setRotate(90);
                //旋转后的图片
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
                Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap,
                        mCameraTopRectView.getViewWidth(), mCameraTopRectView.getViewHeight(), true);
                bitmap = Bitmap.createBitmap(sizeBitmap, mCameraTopRectView.getRectLeft(),
                        mCameraTopRectView.getRectTop(),
                        mCameraTopRectView.getRectRight() - mCameraTopRectView.getRectLeft(),
                        mCameraTopRectView.getRectBottom() - mCameraTopRectView.getRectTop());// 截取
                File file = ImageUtils.getUtil().saveBitmap(bitmap, System.currentTimeMillis() + "");
                if (file.exists()) {
                    if (mOnImageListener != null) {
                        mOnImageListener.onImage(file.getAbsolutePath());
                    }
                }
                mCamera.stopPreview();//停止预览
            }
        }
    };
}
