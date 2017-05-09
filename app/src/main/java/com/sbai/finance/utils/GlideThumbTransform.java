package com.sbai.finance.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by linrongfang on 2017/5/9.
 */

public class GlideThumbTransform extends BitmapTransformation {

    private Context mContext;

    public GlideThumbTransform(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return thumbBitmap(pool, toTransform);
    }

    private Bitmap thumbBitmap(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        //找到宽高中最大的
        int size = Math.max(source.getWidth(), source.getHeight());
        //宽高最大值
        int maxValue = (int) Display.dp2Px(200, mContext.getResources());
        //如果最大值都比maxValue小 则显示原图
        if (size > maxValue) {
            return source;
        }
        //压缩比例
        float scale = size / (float) maxValue;
        //最终得到的宽高
        int width = (int) (source.getWidth() / scale);
        int height = (int) (source.getHeight() / scale);
        //从BitmapPool中尝试获取bitmap
        Bitmap result = pool.get(width, height, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        return result;

    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
