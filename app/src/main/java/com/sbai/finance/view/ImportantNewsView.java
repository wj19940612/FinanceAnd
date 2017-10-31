package com.sbai.finance.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.DailyReport;
import com.sbai.finance.utils.DateUtil;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\10\30 0030.
 */

public class ImportantNewsView extends RelativeLayout {
    @BindView(R.id.moreBtn)
    TextView mMoreBtn;
    @BindView(R.id.imgView1)
    ImageView mImgView1;
    @BindView(R.id.title1)
    TextView mTitle1;
    @BindView(R.id.time1)
    TextView mTime1;
    @BindView(R.id.imgView2)
    ImageView mImgView2;
    @BindView(R.id.title2)
    TextView mTitle2;
    @BindView(R.id.time2)
    TextView mTime2;
    @BindView(R.id.imgView3)
    ImageView mImgView3;
    @BindView(R.id.title3)
    TextView mTitle3;
    @BindView(R.id.time3)
    TextView mTime3;
    @BindView(R.id.rl1)
    RelativeLayout mRL1;
    @BindView(R.id.rl2)
    RelativeLayout mRL2;
    @BindView(R.id.rl3)
    RelativeLayout mRL3;

    private Context mContext;

    private OnImportantNewsClickListener mOnImportantNewsClickListener;

    public void setOnImportantNewsClickListener(OnImportantNewsClickListener onImportantNewsClickListener) {
        mOnImportantNewsClickListener = onImportantNewsClickListener;
    }

    public interface OnImportantNewsClickListener {
        public void onItemClick(DailyReport dailyReport);

        public void onMoreClick();
    }

    public ImportantNewsView(Context context) {
        this(context, null);
    }

    public ImportantNewsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImportantNewsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_important_news, this, true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.rl1, R.id.rl2, R.id.rl3, R.id.moreBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl1:
                break;
            case R.id.rl2:
                break;
            case R.id.rl3:
                break;
            case R.id.moreBtn:
                if(mOnImportantNewsClickListener!=null){
                    mOnImportantNewsClickListener.onMoreClick();
                }
                break;
        }
    }

    public void setImportantNews(List<DailyReport> data) {
        if (data == null) {
            return;
        }
        //只显示3个
        for (int i = 0; i < 3; i++) {
            DailyReport dailyReport = data.get(i);
            if (dailyReport == null) {
                break;
            }
            setOneNews(dailyReport, i);
        }
    }

    private void setOneNews(final DailyReport dailyReport, int index) {
        TextView timeView = null;
        ImageView imgView = null;
        TextView titleView = null;
        RelativeLayout rl = null;
        switch (index) {
            case 0:
                timeView = mTime1;
                imgView = mImgView1;
                titleView = mTitle1;
                rl = mRL1;
                break;
            case 1:
                timeView = mTime2;
                imgView = mImgView2;
                titleView = mTitle2;
                rl = mRL2;
                break;
            case 2:
                timeView = mTime3;
                imgView = mImgView3;
                titleView = mTitle3;
                rl = mRL3;
                break;
        }
        if (!TextUtils.isEmpty(dailyReport.getCoverUrl())) {
            imgView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(dailyReport.getCoverUrl())
                    .into(imgView);
        } else {
            imgView.setVisibility(View.GONE);
        }

        titleView.setText(dailyReport.getTitle());
        timeView.setText(DateUtil.formatDefaultStyleTime(dailyReport.getCreateTime()));
        rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnImportantNewsClickListener!=null){
                    mOnImportantNewsClickListener.onItemClick(dailyReport);
                }
            }
        });
    }
}
