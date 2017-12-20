package com.sbai.finance.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.klinebattle.BattleKline;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KLineTopResultView extends RelativeLayout {

    @BindView(R.id.rank)
    TextView mRank;
    @BindView(R.id.rankTip)
    TextView mRankTip;

    private Context mContext;

    public KLineTopResultView(Context context) {
        this(context, null);
    }

    public KLineTopResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineTopResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_kline_top_result, this, true);
        ButterKnife.bind(this);
    }

    public void setRankInfo(String goinType, int sort) {
        if (goinType == BattleKline.TYPE_1V1) {
            if (sort == 1) {
                mRank.setText(R.string.win_the_kline);
                mRankTip.setText(R.string.eat_chicken);
                mRank.setTextColor(ContextCompat.getColor(mContext,R.color.yellowColor2));
                mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.yellowColor2));
                ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_one);
            } else {
                mRank.setText(R.string.fail_the_kline);
                mRankTip.setText(R.string.more_attention_miss);
                mRank.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_four);
            }
        } else {
            switch (sort){
                case 1:
                    mRank.setText(R.string.kline_first);
                    mRankTip.setText(R.string.eat_chicken);
                    mRank.setTextColor(ContextCompat.getColor(mContext,R.color.yellowColor2));
                    mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.yellowColor2));
                    ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_one);
                    break;
                case 2:
                    mRank.setText(R.string.kline_second);
                    mRankTip.setText(R.string.very_nice_deal);
                    mRank.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_two);
                    break;
                case 3:
                    mRank.setText(R.string.kline_third);
                    mRankTip.setText(R.string.deal_not_bad);
                    mRank.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_three);
                    break;
                case 4:
                    mRank.setText(R.string.kline_four);
                    mRankTip.setText(R.string.more_attention_miss);
                    mRank.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    mRankTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                    ContextCompat.getDrawable(mContext,R.drawable.bg_kline_result_top_four);
                    break;
            }
        }
    }
}
