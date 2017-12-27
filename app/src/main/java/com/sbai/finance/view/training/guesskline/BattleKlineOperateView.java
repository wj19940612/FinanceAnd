package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * k线训练操作
 */

public class BattleKlineOperateView extends FrameLayout {
    @BindView(R.id.imgRank)
    ImageView mImgRank;
    @BindView(R.id.imgAvatar)
    ImageView mImgAvatar;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.positionProfit)
    TextView mPositionProfit;
    @BindView(R.id.buy)
    ImageView mBuy;
    @BindView(R.id.clear)
    ImageView mClear;
    @BindView(R.id.remainKAmount)
    TextView mRemainKAmount;
    @BindView(R.id.pass)
    ImageView mPass;
    @BindView(R.id.endPk)
    TextView mEndPk;
    @BindView(R.id.operate)
    RelativeLayout mOperate;
    private View mBattleSettlingView;
    private Context mContext;
    private OperateListener mOperateListener;
    private double mProfit;
    private double mLastPosition;
    private int mRank;

    public interface OperateListener {
        void buy();

        void clear();

        void pass();
    }

    public BattleKlineOperateView(Context context) {
        this(context, null);
    }

    public BattleKlineOperateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BattleKlineOperateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_battle_kline_operate, this, true);
        ButterKnife.bind(this);
        mContext = context;

        mBattleSettlingView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_settling, null);
        mBattleSettlingView.setVisibility(GONE);
        addView(mBattleSettlingView);
    }

    public void showWaitFinishView() {
        mBattleSettlingView.setVisibility(VISIBLE);
        mBattleSettlingView.findViewById(R.id.loading).startAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.loading));
    }

    public void setOperateListener(OperateListener operateListener) {
        mOperateListener = operateListener;
    }

    public void complete() {
        mEndPk.setVisibility(VISIBLE);
        mOperate.setVisibility(GONE);
    }

    public void battling() {
        mEndPk.setVisibility(GONE);
        mOperate.setVisibility(VISIBLE);
    }


    public void buySuccess() {
        mBuy.setVisibility(GONE);
        mClear.setVisibility(VISIBLE);
    }

    public void clearSuccess() {
        mBuy.setVisibility(VISIBLE);
        mClear.setVisibility(GONE);
        clearPositionProfit();
    }

    public void setRemainKline(int amount) {
        if (amount < 0) amount = 0;
        mRemainKAmount.setText(String.valueOf(amount));
    }

    public void setSelfAvatar() {
        GlideApp.with(mContext)
                .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mImgAvatar);
    }

    public void setTotalProfit(double totalProfit) {
        mProfit = totalProfit;
        if (totalProfit == 0) {
            mTotalProfit.setText("0.00%");
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else if (totalProfit > 0) {
            mTotalProfit.setText("+" + FinanceUtil.formatToPercentage(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mTotalProfit.setText(FinanceUtil.formatToPercentage(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
    }

    public void clearTotalProfit() {
        mTotalProfit.setText(mContext.getString(R.string.no_this_data));
        mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    public double getTotalProfit() {
        return mProfit;
    }

    public double getLastPosition() {
        return mLastPosition;
    }

    public void setPositionProfit(double positionProfit) {
        mLastPosition = positionProfit;
        if (positionProfit == 0) {
            mPositionProfit.setText("0.00%");
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (positionProfit > 0) {
            mPositionProfit.setText("+" + FinanceUtil.formatToPercentage(positionProfit));
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mPositionProfit.setText(FinanceUtil.formatToPercentage(positionProfit));
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
    }

    public void setRank(int rank) {
        if (mRank == rank) return;
        mRank = rank;
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
        mImgRank.setBackground(drawable);
    }

    public void clearPositionProfit() {
        mLastPosition = 0;
        mPositionProfit.setText(R.string.no_this_data);
        mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
    }

    public void enableOperateView() {
        mBuy.setEnabled(true);
        mClear.setEnabled(true);
        mPass.setEnabled(true);
    }

    public void disableOperateView() {
        mBuy.setEnabled(false);
        mClear.setEnabled(false);
        mPass.setEnabled(false);
    }

    @OnClick({R.id.buy, R.id.clear, R.id.pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buy:
                if (mOperateListener != null) {
                    mOperateListener.buy();
                }
                break;
            case R.id.clear:
                if (mOperateListener != null) {
                    mOperateListener.clear();
                }
                break;
            case R.id.pass:
                if (mOperateListener != null) {
                    mOperateListener.pass();
                }
                break;
        }
    }
}
