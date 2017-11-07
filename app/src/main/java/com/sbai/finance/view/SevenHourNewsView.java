package com.sbai.finance.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\10\30 0030.
 */

public class SevenHourNewsView extends RelativeLayout {
    public static final int MIN_HEIGHT = 344;
    public static final int TEXT_PADDING_DP = 14;
    public static final int TEXT_BIG_SP = 14;
    public static final int TEXT_SMALL_SP = 12;
    public static final int TEXT_MAX_LINES = 3;
    public static final int TEXT_ANIMATE_DURATION = 300;
    @BindView(R.id.contentLL)
    LinearLayout mContentLL;
    @BindView(R.id.moreBtn)
    TextView mMoreBtn;

    private Context mContext;
    private LayoutInflater mInflater;
    private int minHeight;
    private boolean nowAdding;
    private boolean canBeAdd;
    private boolean canAnimate;

    private List<DailyReport> mLeaveReports;
    private List<DailyReport> mDailyReports;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private List<TextViewState> mTextViewStates;

    private OnMoreBtnClickListener mOnMoreBtnClickListener;

    public interface OnMoreBtnClickListener {
        public void onMoreClick();
    }

    public void setOnMoreBtnClickListener(OnMoreBtnClickListener onMoreBtnClickListener) {
        mOnMoreBtnClickListener = onMoreBtnClickListener;
    }

    public class TextViewState {
        public TextView textView;
        public ViewTreeObserver.OnPreDrawListener onPreDrawListener;
        public boolean hasMeasure;
        private int allHeight;
        public boolean hasGetLinesHeight;
        public int linesHeight;
        public int index;
    }

    public SevenHourNewsView(Context context) {
        this(context, null);
    }

    public SevenHourNewsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SevenHourNewsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        init();
    }

    private void init() {
        mInflater.inflate(R.layout.layout_homenews, this, true);
        ButterKnife.bind(this);
        addGlobalListener();
        minHeight = (int) Display.dp2Px(MIN_HEIGHT, getResources());
        mLeaveReports = new ArrayList<DailyReport>();
        mTextViewStates = new ArrayList<TextViewState>();
        canAnimate = true;
    }

    @OnClick(R.id.moreBtn)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moreBtn:
                if (mOnMoreBtnClickListener != null) {
                    mOnMoreBtnClickListener.onMoreClick();
                }
                break;
        }
    }

    public void setTextNews(List<DailyReport> data) {
        if (data == null) {
            return;
        }
        if (mTextViewStates.size() == 0) {
            mDailyReports = data;
            mContentLL.removeAllViews();
            mLeaveReports.clear();
            mTextViewStates.clear();
            for (DailyReport dailyReport : data) {
                addText(dailyReport);
            }
        } else {
            mDailyReports = data;
            setTextData(data);
        }
    }

    private void setTextData(List<DailyReport> data){

    }

    private void addGlobalListener() {
        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = mContentLL.getMeasuredHeight();
                if (!nowAdding) {
                    return;
                }
                if (height <= minHeight) {
                    canBeAdd = true;
                } else {
                    canBeAdd = false;
                }
                nowAdding = false;
                if (mLeaveReports.size() != 0) {
                    judgeAddView(mLeaveReports.get(0), true);
                }
            }
        };
        mContentLL.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void addText(DailyReport dailyReport) {
        if (mContentLL.getChildCount() == 0) {
            canBeAdd = true;
        }
        //逻辑：addView但是ViewTree还未来得及重新绘制，所以要等ViewTree绘制完成后获得新的ContentLayout高度有没有超过设置的minHeight，超过了就只允许再添加一次了
        judgeAddView(dailyReport, false);
    }

    private void judgeAddView(DailyReport dailyReport, boolean isFromList) {
        RelativeLayout contentItemView = (RelativeLayout) mInflater.inflate(R.layout.layout_news_content, mContentLL, false);
        if (canBeAdd && !nowAdding) {
            TextView textView = getAddTextView(dailyReport);
            TextView timeView = (TextView) contentItemView.findViewById(R.id.timeView);
            timeView.setText(DateUtil.formatDefaultStyleTime(dailyReport.getCreateTime()));
            contentItemView.addView(textView);
            mContentLL.addView(contentItemView);
            if (isFromList) {
                mLeaveReports.remove(0);
            }
        } else if (!isFromList) {
            mLeaveReports.add(dailyReport);
        }
    }

    private TextView getAddTextView(DailyReport dailyReport) {
        final TextView textView = new TextView(mContext);
        String title = dailyReport.getTitle() == null ? "" : dailyReport.getTitle();
        String content = dailyReport.getContent() == null ? "" : Html.fromHtml(dailyReport.getContent()).toString().trim();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title + content);
        ForegroundColorSpan bigColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.primaryText));
        spannableStringBuilder.setSpan(bigColorSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(((int) Display.sp2Px(TEXT_BIG_SP, getResources())));
        spannableStringBuilder.setSpan(bigSizeSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ForegroundColorSpan smallColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.news_content));
        spannableStringBuilder.setSpan(smallColorSpan, title.length(), title.length() + content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        AbsoluteSizeSpan smallSizeSpan = new AbsoluteSizeSpan(((int) Display.sp2Px(TEXT_SMALL_SP, getResources())));
        spannableStringBuilder.setSpan(smallSizeSpan, title.length(), title.length() + content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textParams.addRule(BELOW, R.id.iconView);
        textParams.setMargins((int) Display.dp2Px(TEXT_PADDING_DP, getResources()), 0, (int) Display.dp2Px(TEXT_PADDING_DP, getResources()), 0);
        textView.setLayoutParams(textParams);

        int textPadding = (int) Display.dp2Px(TEXT_PADDING_DP, getResources());
        textView.setPadding(textPadding, textPadding, textPadding, textPadding);
        textView.setBackgroundColor(getResources().getColor(R.color.background));
        textView.setText(spannableStringBuilder);
        final TextViewState textViewState = new TextViewState();
        textViewState.textView = textView;
        textViewState.onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //只需要获取一次就可以了
                if (textViewState.index == 0) {
                }
                if (textViewState.index == 1) {
                }
                if (!textViewState.hasMeasure && textView.getMeasuredHeight() != 0) {
                    //这里获取到完全展示的maxLine
                    textViewState.allHeight = textView.getMeasuredHeight();
                    //设置maxLine的默认值，这样用户看到View就是限制了maxLine的TextView
                    textView.setMaxLines(TEXT_MAX_LINES);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textViewState.hasMeasure = true;
                    textView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        };
        textView.getViewTreeObserver().addOnPreDrawListener(textViewState.onPreDrawListener);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextView(textViewState);
            }
        });
        textViewState.index = mTextViewStates.size();
        mTextViewStates.add(textViewState);

        nowAdding = true;
        return textView;
    }

    private void animateTextView(final TextViewState textViewState) {
        if (!canAnimate) {
            return;
        }
        if (!textViewState.hasGetLinesHeight) {
            textViewState.linesHeight = textViewState.textView.getMeasuredHeight();
            textViewState.hasGetLinesHeight = true;
        }
        int nowHeight = textViewState.textView.getMeasuredHeight();
        final int leftHeight;
        final int rightHeight;
        if (nowHeight < textViewState.allHeight) {
            leftHeight = textViewState.linesHeight;
            rightHeight = textViewState.allHeight;
        } else {
            leftHeight = textViewState.allHeight;
            rightHeight = textViewState.linesHeight;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(leftHeight, rightHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                textViewState.textView.setHeight(value);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (leftHeight > rightHeight) {
                    textViewState.textView.setMaxLines(TEXT_MAX_LINES);
                    textViewState.textView.setEllipsize(TextUtils.TruncateAt.END);
                }
                canAnimate = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(TEXT_ANIMATE_DURATION);
        canAnimate = false;
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllGlobalListener();
    }

    private void removeAllGlobalListener() {
//        for (int i = 0; i < mTextViewStates.size(); i++) {
//            TextView textView = mTextViewStates.get(i).textView;
//            textView.getViewTreeObserver().removeOnPreDrawListener(mTextViewStates.get(i).onPreDrawListener);
//        }
        mContentLL.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }
}
