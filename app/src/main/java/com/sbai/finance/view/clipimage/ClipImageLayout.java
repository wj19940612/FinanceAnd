package com.sbai.finance.view.clipimage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.sbai.glide.GlideApp;

/**
 * @author zhy
 */
public class ClipImageLayout extends RelativeLayout {
    private static final String TAG = "ClipImageLayout";

    private ClipZoomImageView mZoomImageView;
    private int mWidthPixels;
    private int mHeightPixels;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new ClipZoomImageView(context);
        ClipImageBorderView clipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        //这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性

//		mZoomImageView.setImageDrawable(getResources().getDrawable(
//				R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(clipImageView, lp);


        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        clipImageView.setHorizontalPadding(mHorizontalPadding);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
        Log.d(TAG, "ClipImageLayout: " + mWidthPixels + " 高 " + mHeightPixels);
    }

    public void setZoomImageViewImage(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void setZoomImageViewImage(String bitmapUrl) {
//        int width = getImageWidthHeight(bitmapUrl)[0];
//        int height = getImageWidthHeight(bitmapUrl)[1];

        GlideApp.with(getContext()).load(bitmapUrl).fitCenter().into(mZoomImageView);
    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

}
