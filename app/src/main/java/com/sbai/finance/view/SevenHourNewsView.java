package com.sbai.finance.view;

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
import android.widget.ImageView;
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
    public static final int MIN_HEIGHT = 300;
    public static final int TEXT_PADDING_DP = 14;
    public static final int TEXT_BIG_SP = 15;
    public static final int TEXT_SMALL_SP = 14;
    public static final int TEXT_MAX_LINES = 3;
    public static final int TEXT_ANIMATE_DURATION = 300;
    @BindView(R.id.contentLL)
    LinearLayout mContentLL;
    @BindView(R.id.moreBtn)
    TextView mMoreBtn;

    private Context mContext;
    private LayoutInflater mInflater;
    private int minHeight;
    private boolean canAnimate;
    private List<TextViewState> mTextViewStates;
    private int ccWidth;

    private OnShareClickListener mOnShareClickListener;

    public void setOnShareClickListener(OnShareClickListener onShareClickListener){
        mOnShareClickListener = onShareClickListener;
    }

    public interface OnShareClickListener{
        public void onShare(DailyReport dailyReport);
    }
    private OnMoreBtnClickListener mOnMoreBtnClickListener;

    public interface OnMoreBtnClickListener {
        public void onMoreClick();
    }

    public void setOnMoreBtnClickListener(OnMoreBtnClickListener onMoreBtnClickListener) {
        mOnMoreBtnClickListener = onMoreBtnClickListener;
    }

    public class TextViewState {
        public TextView textView;
        //        public ViewTreeObserver.OnPreDrawListener onPreDrawListener;
//        public boolean hasMeasure;
//        private int allHeight;
//        public boolean hasGetLinesHeight;
//        public int linesHeight;
        public int index;
        private boolean isExpand;
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
        minHeight = (int) Display.dp2Px(MIN_HEIGHT, getResources());
        mTextViewStates = new ArrayList<TextViewState>();
        canAnimate = true;
        mContentLL.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mContentLL.getMeasuredHeight() != 0) {
                    ccWidth = mContentLL.getMeasuredWidth();
//                    Log.e("zzz", "ccwidth:" + ccWidth);
                    mContentLL.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
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
            mContentLL.removeAllViews();
//            mTextViewStates.clear();
            setTextData(data);
        } else {
            mContentLL.removeAllViews();
//            mTextViewStates.clear();
            setTextData(data);
//            setOldData(data);
        }
    }

    private void setTextData(List<DailyReport> data) {
//        List<DailyReport> data = new ArrayList<DailyReport>();
//        data.add(data2.get(0));
        int canAddCount = 0;
        int addHeight = 0;
        List<RelativeLayout> relativeLayouts = new ArrayList<RelativeLayout>();
        for (final DailyReport dailyReport : data) {
            RelativeLayout contentItemView = (RelativeLayout) mInflater.inflate(R.layout.layout_news_content, mContentLL, false);
            TextView textView = contentItemView.findViewById(R.id.textView);
            getAddTextView(dailyReport, canAddCount, textView);

            setRelativeLayoutUI(contentItemView, dailyReport);

            int widthSpec = MeasureSpec.makeMeasureSpec(ccWidth, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            contentItemView.measure(widthSpec, heightSpec);
            int measureRelativeHeight = contentItemView.getMeasuredHeight();

            if (mTextViewStates.get(canAddCount).isExpand && textView.getLayout().getLineCount() > TEXT_MAX_LINES) {
                //如果是展开的话，这里我们需要计算被加入的高度其实是缩小的，因此这里只要三行的高度
                int textAllHeight = textView.getMeasuredHeight();
                int threeTop = textView.getLayout().getLineTop(TEXT_MAX_LINES) + textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
                measureRelativeHeight = measureRelativeHeight - (textAllHeight - threeTop);
//                Log.e("zzz", "threeTop:" + measureRelativeHeight);
            }
            addHeight += measureRelativeHeight;
//            Log.e("zzz", "old height:" + contentItemView.getMeasuredHeight());
            relativeLayouts.add(contentItemView);
            canAddCount++;
            if (addHeight > minHeight) {
                break;
            }
        }

        dealLastRelativeLayout(relativeLayouts.get(canAddCount - 1));
        for (int i = 0; i < canAddCount; i++) {
            mContentLL.addView(relativeLayouts.get(i));
        }
    }

    //最后一行有些内容不显示以及padding设置
    private void dealLastRelativeLayout(RelativeLayout relativeLayout) {
        relativeLayout.setPadding(0, 0, 0, 0);
        relativeLayout.findViewById(R.id.bottomLine).setVisibility(View.GONE);
    }

    private void setRelativeLayoutUI(RelativeLayout contentItemView, final DailyReport dailyReport) {
        TextView lookView = contentItemView.findViewById(R.id.lookTextView);
        lookView.setText("阅 " + dailyReport.getClicks());
        ImageView shareView = contentItemView.findViewById(R.id.shareView);
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnShareClickListener != null){
                    mOnShareClickListener.onShare(dailyReport);
                }


            }
        });
        TextView timeView = contentItemView.findViewById(R.id.timeView);
        timeView.setText(DateUtil.formatDefaultStyleTime(dailyReport.getCreateTime()));
    }


    private TextView getAddTextView(DailyReport dailyReport, int canAddCount, TextView textView) {
        String title = dailyReport.getTitle() == null ? "" : dailyReport.getTitle();
        String content = dailyReport.getContent() == null ? "" : Html.fromHtml(dailyReport.getContent()).toString().trim();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title + content);
        ForegroundColorSpan bigColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.primaryText));
        spannableStringBuilder.setSpan(bigColorSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

//        StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
//        spannableStringBuilder.setSpan(boldSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(((int) Display.sp2Px(TEXT_BIG_SP, getResources())));
        spannableStringBuilder.setSpan(bigSizeSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ForegroundColorSpan smallColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.newInfoColor));
        spannableStringBuilder.setSpan(smallColorSpan, title.length(), title.length() + content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        AbsoluteSizeSpan smallSizeSpan = new AbsoluteSizeSpan(((int) Display.sp2Px(TEXT_SMALL_SP, getResources())));
        spannableStringBuilder.setSpan(smallSizeSpan, title.length(), title.length() + content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

//        LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        textParams.addRule(BELOW, R.id.iconView);
//        textParams.setMargins(0, (int) Display.dp2Px(TEXT_PADDING_DP, getResources()), 0, 0);
//        textView.setLayoutParams(textParams);

//        int textPadding = (int) Display.dp2Px(TEXT_PADDING_DP, getResources());
//        textView.setPadding(textPadding, textPadding, textPadding, textPadding);
//        textView.setBackgroundColor(getResources().getColor(R.color.background));
        textView.setText(spannableStringBuilder);
        final TextViewState textViewState;
        if (canAddCount >= mTextViewStates.size()) {
            textViewState = new TextViewState();
        } else {
            textViewState = mTextViewStates.get(canAddCount);
        }
        textViewState.textView = textView;

        if (textViewState.isExpand) {
            textView.setMaxLines(Integer.MAX_VALUE);
        } else {
            textView.setMaxLines(TEXT_MAX_LINES);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrbackTextView(textViewState);
            }
        });
        textViewState.index = mTextViewStates.size();
        mTextViewStates.add(textViewState);
        return textView;
    }

    private void expandOrbackTextView(final TextViewState textViewState) {
        if (textViewState.isExpand) {
            //展开的，需要从展开到收缩
            textViewState.textView.setMaxLines(TEXT_MAX_LINES);
            textViewState.textView.setEllipsize(TextUtils.TruncateAt.END);
            textViewState.isExpand = false;
        } else {
            textViewState.textView.setMaxLines(Integer.MAX_VALUE);
            textViewState.isExpand = true;
        }
    }

    private void animateTextView(final TextViewState textViewState) {
//        if (!canAnimate) {
//            return;
//        }
//        if (!textViewState.hasGetLinesHeight) {
//            textViewState.linesHeight = textViewState.textView.getMeasuredHeight();
//            textViewState.hasGetLinesHeight = true;
//        }
//        int nowHeight = textViewState.textView.getMeasuredHeight();
//        final int leftHeight;
//        final int rightHeight;
//        if (nowHeight < textViewState.allHeight) {
//            leftHeight = textViewState.linesHeight;
//            rightHeight = textViewState.allHeight;
//        } else {
//            leftHeight = textViewState.allHeight;
//            rightHeight = textViewState.linesHeight;
//        }
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(leftHeight, rightHeight);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
//                textViewState.textView.setHeight(value);
//            }
//        });
//
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (leftHeight > rightHeight) {
//                    textViewState.textView.setMaxLines(TEXT_MAX_LINES);
//                    textViewState.textView.setEllipsize(TextUtils.TruncateAt.END);
//                }
//                canAnimate = true;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        valueAnimator.setDuration(TEXT_ANIMATE_DURATION);
//        canAnimate = false;
//        valueAnimator.start();
    }

}
