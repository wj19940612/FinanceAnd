package com.sbai.finance.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;
import com.sbai.glide.GlideApp;

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
                loadImage(createImageView(0, true), images.get(0));
                break;
            case 2:
                loadImage(createImageView(0, true), images.get(1));
                loadImage(createImageView(1, true), images.get(0));
                break;
            case 3:
                loadImage(createImageView(0, true), images.get(2));
                loadImage(createImageView(1, true), images.get(1));
                loadImage(createImageView(2, true), images.get(0));
                break;
            case 4:
                createImageView(0, false).setImageResource(mDrawble);
                loadImage(createImageView(1, true), images.get(2));
                loadImage(createImageView(2, true), images.get(1));
                loadImage(createImageView(3, true), images.get(0));
                break;
        }
    }

    private ImageView createImageView(int index, boolean hasBackground) {
        ImageView image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams params = new LayoutParams((int) Display.dp2Px(34, getResources()), (int) Display.dp2Px(34, getResources()));
        int padding = (int) Display.dp2Px(1, getResources());
        image.setPadding(padding, padding, padding, padding);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        switch (index) {
            case 0:
                if (hasBackground) {
                    image.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_avatar));
                }
                break;
            case 1:
                image.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_avatar));
                params.setMargins(0, 0, mMarginRights[0], 0);
                break;
            case 2:
                image.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_avatar));
                params.setMargins(0, 0, mMarginRights[1], 0);
                break;
            case 3:
                image.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_avatar));
                params.setMargins(0, 0, mMarginRights[2], 0);
                break;
        }
        addView(image, params);
        return image;
    }

    private void loadImage(ImageView imageView, String url) {
        GlideApp.with(getContext())
                .load(url).placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);
    }

    public ImageListView setDrawble(int resId) {
        mDrawble = resId;
        return this;
    }
}
