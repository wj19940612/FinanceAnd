package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 头像堆叠显示
 */

public class ImageListView extends RelativeLayout {
    private int[] mMarginRights;
    private int mDrawble;

    public ImageListView(Context context) {
        super(context);
    }

    public ImageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setImages(List<String> images, int resId) {
        if (images.isEmpty()) return;
        setDrawble(resId);
        createView(images);
    }

    public void setImages(List<String> images) {
        if (images.isEmpty()) return;
        setImages(images, R.drawable.ic_board_head_more);
    }

    private void createView(List<String> images) {
        mMarginRights = new int[]{(int) Display.dp2Px(21, getResources()), (int) Display.dp2Px(46, getResources()), (int) Display.dp2Px(74, getResources())};
        switch (images.size()) {
            case 0:
                return;
            case 1:
                createImageView(0).setImageResource(mDrawble);
                loadImage(createImageView(1), images.get(0));
                break;
            case 2:
                createImageView(0).setImageResource(mDrawble);
                loadImage(createImageView(1), images.get(1));
                loadImage(createImageView(2), images.get(0));
                break;
            case 3:
                createImageView(0).setImageResource(mDrawble);
                loadImage(createImageView(1), images.get(2));
                loadImage(createImageView(2), images.get(1));
                loadImage(createImageView(3), images.get(0));
                break;
        }
    }

    private ImageView createImageView(int index) {
        ImageView image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams params = new LayoutParams((int) Display.dp2Px(32, getResources()), (int) Display.dp2Px(32, getResources()));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        switch (index) {
            case 1:
                params.setMargins(0, 0, mMarginRights[0], 0);
                break;
            case 2:
                params.setMargins(0, 0, mMarginRights[1], 0);
                break;
            case 3:
                params.setMargins(0, 0, mMarginRights[2], 0);
                break;
        }
        addView(image, params);
        return image;
    }

    private void loadImage(ImageView imageView, String url) {
        Glide.with(getContext())
                .load(url).placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);
    }

    public ImageListView setDrawble(int resId) {
        mDrawble = resId;
        return this;
    }
}
