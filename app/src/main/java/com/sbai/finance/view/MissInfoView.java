package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.transform.GlideCircleTransform;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by houcc on 2017-07-27.
 */

public class MissInfoView extends LinearLayout {
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.selectImg)
    ImageView mSelectImg;
    @BindView(R.id.userName)
    TextView mUserName;
    private boolean mSelected;

    public MissInfoView(Context context) {
        super(context);
        init();
    }

    public MissInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MissInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_miss_info, this, true);
        ButterKnife.bind(this);
    }

    public MissInfoView setImgRes(String url) {
        GlideApp.with(getContext())
                .load(url).placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getContext()))
                .into(mAvatar);
        return this;
    }

    public MissInfoView setImgRes(Integer resourceId) {
        GlideApp.with(getContext())
                .load(resourceId).placeholder(R.drawable.ic_default_avatar)
                .transform(new GlideCircleTransform(getContext()))
                .into(mAvatar);
        return this;

    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
        if (mSelected) {
            mSelectImg.setVisibility(VISIBLE);
        } else {
            mSelectImg.setVisibility(GONE);
        }
    }

    public MissInfoView setUserName(String userName) {
        mUserName.setText(userName);
        return this;
    }
}
