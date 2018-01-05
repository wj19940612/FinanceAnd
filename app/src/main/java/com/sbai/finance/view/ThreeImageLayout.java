package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;

/**
 * Created by ${wangJie} on 2018/1/2.
 * 最多显示3张图片 不足3张则显示全部图片
 */

public class ThreeImageLayout extends LinearLayout {

    private ArrayList<ImageView> mImageViewList;

    public ThreeImageLayout(Context context) {
        this(context, null);
    }

    public ThreeImageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeImageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int defaultSize = (int) Display.dp2Px(72, getResources());
        mImageViewList = new ArrayList<>();
        LayoutParams layoutParams = new LayoutParams(defaultSize, defaultSize);
        layoutParams.setMargins(0, 0, (int) Display.dp2Px(2, getResources()), 0);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(imageView, layoutParams);
            imageView.setVisibility(GONE);
            mImageViewList.add(imageView);
        }
    }


    public void setImagePath(String content) {
        if (TextUtils.isEmpty(content)) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            final String[] split = content.split(",");
            int contentLength = split.length < 4 ? split.length : 3;
            for (int i = 0; i < contentLength; i++) {
                final String imagePath = split[i];
                ImageView imageView = mImageViewList.get(i);
                imageView.setVisibility(VISIBLE);
                GlideApp.with(getContext())
                        .load(imagePath)
                        .into(imageView);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(getContext(), LookBigPictureActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, imagePath)
                                .execute();
                    }
                });
            }
        }
    }
}
