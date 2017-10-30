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
    private Context mContext;

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


    @OnClick({R.id.rl1, R.id.rl2, R.id.rl3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl1:
                break;
            case R.id.rl2:
                break;
            case R.id.rl3:
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

    private void setOneNews(DailyReport dailyReport, int index) {
        TextView timeView = null;
        ImageView imgView = null;
        TextView titleView = null;
        switch (index) {
            case 0:
                timeView = mTime1;
                imgView = mImgView1;
                titleView = mTitle1;
                break;
            case 1:
                timeView = mTime2;
                imgView = mImgView2;
                titleView = mTitle2;
                break;
            case 2:
                timeView = mTime3;
                imgView = mImgView3;
                titleView = mTitle3;
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
    }
}
