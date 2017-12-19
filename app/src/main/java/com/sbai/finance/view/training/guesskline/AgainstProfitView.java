package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

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
    private String mType;

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

    public void setPkType(@BattleKline.BattleType String type) {
        mType = type;
        if (type.equals(BattleKline.TYPE_1V1)) {
            setVisibility(VISIBLE);
            mOnePkArea.setVisibility(VISIBLE);
            mFourPkArea.setVisibility(GONE);
        } else if (type.equals(BattleKline.TYPE_4V4)) {
            setVisibility(VISIBLE);
            mOnePkArea.setVisibility(GONE);
            mFourPkArea.setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    public void setTotalProfit(List<BattleKline.BattleBean> battleBeans) {
        List<BattleKline.BattleBean> battleBeans1 = new ArrayList<>();
        for (BattleKline.BattleBean battleBean : battleBeans) {
            if (battleBean.getUserId() != LocalUser.getUser().getUserInfo().getId()) {
                battleBeans1.add(battleBean);
            }
        }
        for (int i = 0; i < battleBeans1.size(); i++) {
            if (i < 1) {
                if (mType.equalsIgnoreCase(BattleKline.TYPE_1V1)) {
                    setTotalProfit(battleBeans1.get(i), mUserName, mTotalProfit, mImgAvatar, mImgRank);
                    return;
                } else {
                    setTotalProfit(battleBeans1.get(i), mUserName1, mTotalProfit1, mImg1Avatar, mImg1Rank);
                }
            }
            if (i < 2) {
                setTotalProfit(battleBeans1.get(i), mUserName2, mTotalProfit2, mImg2Avatar, mImg2Rank);
            }
            if (i < 3) {
                setTotalProfit(battleBeans1.get(i), mUserName3, mTotalProfit3, mImg3Avatar, mImg3Rank);
            }
        }
    }

    private void setTotalProfit(BattleKline.BattleBean battleBean, TextView userName, TextView totalProfit, ImageView imageAvatar, ImageView imageRank) {
        if (totalProfit.getTag() == null) {
            totalProfit.setTag(battleBean.getUserId());
            setAvatar(battleBean.getUserPortrait(), imageAvatar);
            userName.setText(battleBean.getUserName());
        }
        int useId = (int) totalProfit.getTag();
        if (useId == battleBean.getUserId()) {
            setTotalProfit(battleBean.getProfit(), totalProfit);
            if (userName.getTag() == null || (int) userName.getTag() != battleBean.getSort()) {
                setRankImg(battleBean.getSort(), imageRank);
                userName.setTag(battleBean.getSort());
            }
        }
    }

    private void setTotalProfit(double totalProfit, TextView profitView) {
        totalProfit = Double.valueOf(FinanceUtil.formatWithScale(totalProfit));
        if (totalProfit == 0) {
            profitView.setText("0.00%");
            profitView.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (totalProfit > 0) {
            profitView.setText("+" + FinanceUtil.formatToPercentage(totalProfit));
            profitView.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            profitView.setText(FinanceUtil.formatToPercentage(totalProfit));
            profitView.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
    }

    private void setAvatar(String url, ImageView avatarView) {
        GlideApp.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(avatarView);
    }

    private void setRankImg(int rank, ImageView imgRank) {
        Drawable drawable = null;
        switch (rank) {
            case 1:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_guess_kline_four__rank_1);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_guess_kline_four__rank_2);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_guess_kline_four__rank_3);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_guess_kline_four__rank_4);
                break;
            default:
                break;
        }
        GlideApp.with(mContext)
                .load(drawable)
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(imgRank);
    }

}
