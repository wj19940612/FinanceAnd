package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.klinebattle.KlineBattle;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 猜K 对手收益
 */

public class AgainstProfitView extends LinearLayout {
    @BindView(R.id.img1Rank)
    ImageView mImg1Rank;
    @BindView(R.id.img1Avatar)
    ImageView mImg1Avatar;
    @BindView(R.id.userName1)
    TextView mUserName1;
    @BindView(R.id.totalProfit1)
    TextView mTotalProfit1;
    @BindView(R.id.img2Rank)
    ImageView mImg2Rank;
    @BindView(R.id.img2Avatar)
    ImageView mImg2Avatar;
    @BindView(R.id.userName2)
    TextView mUserName2;
    @BindView(R.id.totalProfit2)
    TextView mTotalProfit2;
    @BindView(R.id.img3Rank)
    ImageView mImg3Rank;
    @BindView(R.id.img3Avatar)
    ImageView mImg3Avatar;
    @BindView(R.id.userName3)
    TextView mUserName3;
    @BindView(R.id.totalProfit3)
    TextView mTotalProfit3;
    @BindView(R.id.fourPkArea)
    LinearLayout mFourPkArea;
    @BindView(R.id.imgRank)
    ImageView mImgRank;
    @BindView(R.id.imgAvatar)
    ImageView mImgAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.onePkArea)
    LinearLayout mOnePkArea;
    @BindView(R.id.againstPkArea)
    LinearLayout mAgainstPkArea;
    private Context mContext;

    public AgainstProfitView(Context context) {
        this(context, null);
    }

    public AgainstProfitView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgainstProfitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_guess_kline_against_profit, this, true);
        ButterKnife.bind(this);
        mContext = context;
    }

    public void setPkType(@KlineBattle.BattleType String type) {
        if (type.equals(KlineBattle.TYPE_1V1)) {
            setVisibility(VISIBLE);
            mOnePkArea.setVisibility(VISIBLE);
            mFourPkArea.setVisibility(GONE);
        } else if (type.equals(KlineBattle.TYPE_4V4)) {
            setVisibility(VISIBLE);
            mOnePkArea.setVisibility(GONE);
            mFourPkArea.setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

}
