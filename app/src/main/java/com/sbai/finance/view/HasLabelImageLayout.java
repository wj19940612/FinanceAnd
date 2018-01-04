package com.sbai.finance.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.utils.Display;
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
        int defaultSize = (int) Display.dp2Px(10, getResources());
        mLabelDrawableResId = typedArray.getResourceId(R.styleable.HasLabelImageLayout_label_drawable, R.drawable.ic_label_v);
        mLabelWidth = typedArray.getDimensionPixelSize(R.styleable.HasLabelImageLayout_label_view_width, defaultSize);
        mLabelHeight = typedArray.getDimensionPixelSize(R.styleable.HasLabelImageLayout_label_view_height, defaultSize);
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
        mLabelImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mLabelImageView.setVisibility(GONE);
        addView(mLabelImageView, layoutParams);
    }

    public void setCircleUrl(String url) {
        GlideApp.with(getContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(mCircleImageView);
    }

    public void setLabelDrawableResId(int resId) {
        mLabelImageView.setImageResource(resId);
    }

    public void setAvatar(String avatarUrl, int userIdentity) {
        if (userIdentity == Question.USER_IDENTITY_MISS) {
            mLabelImageView.setVisibility(VISIBLE);
        } else {
            mLabelImageView.setVisibility(GONE);
        }
        if (getContext() == null || ((Activity) getContext()).isFinishing()) {
            return;
        }
        GlideApp.with(getContext())
                .load(!TextUtils.isEmpty(avatarUrl)?avatarUrl:R.drawable.ic_default_avatar)
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(mCircleImageView);
    }
}
