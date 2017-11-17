package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.glide.GlideApp;


/**
 * Created by ${wangJie} on 2017/11/17.
 * 有标签的imageView
 */

public class HasLabelImageLayout extends RelativeLayout {

    private static final String TAG = "HasLabelImageView";

    private int mLabelDrawableResId;
    private ImageView mCircleImageView;
    private ImageView mLabelImageView;
    private int mLabelWidth;
    private int mLabelHeight;

    public HasLabelImageLayout(Context context) {
        this(context, null);
    }

    public HasLabelImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HasLabelImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HasLabelImageLayout);
        mLabelDrawableResId = typedArray.getResourceId(R.styleable.HasLabelImageLayout_label_drawable, -1);
        mLabelWidth = typedArray.getDimensionPixelSize(R.styleable.HasLabelImageLayout_label_view_width, 5);
        mLabelHeight = typedArray.getDimensionPixelSize(R.styleable.HasLabelImageLayout_label_view_height, 5);
        typedArray.recycle();
    }

    private void init() {
        mCircleImageView = new ImageView(getContext());
        addView(mCircleImageView);

        mLabelImageView = new ImageView(getContext());
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(mLabelWidth, mLabelHeight);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mLabelImageView.setImageResource(mLabelDrawableResId);
        addView(mLabelImageView, layoutParams);
    }

    public void setCircleUrl(String url) {
        GlideApp.with(getContext())
                .load(url)
                .circleCrop()
                .into(mCircleImageView);
    }

    public void setLabelDrawableResId(int resId) {
        mLabelImageView.setImageResource(resId);
    }
}
