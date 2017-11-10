package com.sbai.finance.view;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.model.Banner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\10\27 0027.
 */

public class BusinessBanner extends RelativeLayout {
    public static final String USER_COUNT_LOCATION_SIGN = "%s";
    public static final int LEFT_LOCATION = 1;
    public static final int TOP_RIGHT_LOCATION = 2;
    public static final int BOTTOM_RIGHT_LOCATION = 3;

    @BindView(R.id.leftSubTitle)
    TextView mLeftSubTitle;
    @BindView(R.id.topLine)
    View mTopLine;
    @BindView(R.id.leftTitle)
    TextView mLeftTitle;
    @BindView(R.id.leftImage)
    ImageView mLeftImage;
    @BindView(R.id.topRightTitle)
    TextView mTopRightTitle;
    @BindView(R.id.topRightSubTitle)
    TextView mTopRightSubTitle;
    @BindView(R.id.topRightImg)
    ImageView mTopRightImg;
    @BindView(R.id.bottomRightTitle)
    TextView mBottomRightTitle;
    @BindView(R.id.bottomRightSubTitle)
    TextView mBottomRightSubTitle;
    @BindView(R.id.bottomRightImg)
    ImageView mBottomRightImg;
    @BindView(R.id.leftRL)
    RelativeLayout mLeftRL;
    @BindView(R.id.topRightRL)
    RelativeLayout mtopRightRL;
    @BindView(R.id.bottomRightRL)
    RelativeLayout mbottomRightRL;

    private Context mContext;

    public interface OnViewClickListener {
        void onBannerClick(Banner information);
    }

    private HomeBanner.OnViewClickListener mOnViewClickListener;

    public void setOnViewClickListener(HomeBanner.OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    public BusinessBanner(Context context) {
        this(context, null);
    }

    public BusinessBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BusinessBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_business_banner, this, true);
        ButterKnife.bind(this);
        setBackgroundColor(getResources().getColor(R.color.white));
    }


    public void setBusinessBannerData(List<Banner> data) {
        if (data.size() == 0 || data.get(0) == null) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }
        final Banner leftBanner = data.get(0);
        setTitle(leftBanner, LEFT_LOCATION);
        setSubTitle(leftBanner, LEFT_LOCATION);
        setImage(leftBanner, LEFT_LOCATION);
        mLeftRL.setVisibility(View.VISIBLE);

        if (data.size() < 2 || data.get(1) == null) {
            clearViews(TOP_RIGHT_LOCATION);
            clearViews(BOTTOM_RIGHT_LOCATION);
            return;
        }

        final Banner topRightBanner = data.get(1);
        setTitle(topRightBanner, TOP_RIGHT_LOCATION);
        setSubTitle(topRightBanner, TOP_RIGHT_LOCATION);
        setImage(topRightBanner, TOP_RIGHT_LOCATION);
        mtopRightRL.setVisibility(View.VISIBLE);

        if (data.size() < 3 || data.get(2) == null) {
            clearViews(BOTTOM_RIGHT_LOCATION);
            return;
        }
        final Banner bottomRightBanner = data.get(2);
        setTitle(bottomRightBanner, BOTTOM_RIGHT_LOCATION);
        setSubTitle(bottomRightBanner, BOTTOM_RIGHT_LOCATION);
        setImage(bottomRightBanner, BOTTOM_RIGHT_LOCATION);
        mbottomRightRL.setVisibility(View.VISIBLE);
    }

    private void clearViews(int location) {
        if (location == LEFT_LOCATION) {
            mLeftRL.setVisibility(View.INVISIBLE);
        } else if (location == TOP_RIGHT_LOCATION) {
            mtopRightRL.setVisibility(View.INVISIBLE);
        } else if (location == BOTTOM_RIGHT_LOCATION) {
            mbottomRightRL.setVisibility(View.INVISIBLE);
        }
    }

    private void setTitle(Banner banner, int location) {
        String title = banner.getTitle();
        if (TextUtils.isEmpty(title)) {
            return;
        }

        TextView textView;
        switch (location) {
            case LEFT_LOCATION:
                textView = mLeftTitle;
                break;
            case TOP_RIGHT_LOCATION:
                textView = mTopRightTitle;
                break;
            case BOTTOM_RIGHT_LOCATION:
                textView = mBottomRightTitle;
                break;
            default:
                textView = mLeftTitle;
                break;
        }
        textView.setText(Html.fromHtml(title).toString().trim());
    }

    private void setSubTitle(Banner banner, int location) {
        String subTitle = banner.getSubTitle();
        if (TextUtils.isEmpty(subTitle)) {
            return;
        }
        TextView textView;
        switch (location) {
            case LEFT_LOCATION:
                textView = mLeftSubTitle;
                break;
            case TOP_RIGHT_LOCATION:
                textView = mTopRightSubTitle;
                break;
            case BOTTOM_RIGHT_LOCATION:
                textView = mBottomRightSubTitle;
                break;
            default:
                textView = mLeftSubTitle;
                break;
        }
        int userCountLocation = subTitle.indexOf(USER_COUNT_LOCATION_SIGN);
        if (userCountLocation == -1) {
            //没有特殊标志位说明副标题不需要富文本
            textView.setText(subTitle);
        } else if (banner.getMontageData() != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(subTitle.substring(0, userCountLocation));
            stringBuilder.append(banner.getMontageData());
            stringBuilder.append(subTitle.substring(userCountLocation + USER_COUNT_LOCATION_SIGN.length(), subTitle.length()));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.banner_number));
            spannableStringBuilder.setSpan(foregroundColorSpan, userCountLocation, userCountLocation + banner.getMontageData().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            textView.setText(spannableStringBuilder);
        } else if (banner.getMontageData() == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(subTitle.substring(0, userCountLocation));
            stringBuilder.append(subTitle.substring(userCountLocation + USER_COUNT_LOCATION_SIGN.length(), subTitle.length()));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            textView.setText(spannableStringBuilder);
        }
    }

    private void setImage(final Banner banner, int location) {
        RelativeLayout rl;
        ImageView imageView;
        switch (location) {
            case LEFT_LOCATION:
                imageView = mLeftImage;
                rl = mLeftRL;
                break;
            case TOP_RIGHT_LOCATION:
                imageView = mTopRightImg;
                rl = mtopRightRL;
                break;
            case BOTTOM_RIGHT_LOCATION:
                imageView = mBottomRightImg;
                rl = mbottomRightRL;
                break;
            default:
                imageView = mLeftImage;
                rl = mLeftRL;
                break;
        }
        if (!TextUtils.isEmpty(banner.getCover())) {
            Glide.with(mContext).load(banner.getCover()).into(imageView);
        }
        rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onBannerClick(banner);
                }
            }
        });
    }
}
