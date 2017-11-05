package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.TradeOrder;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.local.BattleStatus;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.glide.GlideApp;

/**
 * Modified by john on 26/10/2017
 * <p>
 * Description: 对战的操作控件，包含操作：快速匹配，取消对战，买多空，平仓，点赞，结算显示。同时包含一个对战选手显示栏
 * <p>
 * APIs:
 */
public class BattleOperateView extends LinearLayout implements View.OnClickListener {

    TextView mCountdown;

    BattleProgress mBattleProgress;
    ImageView mOwnerAvatar;
    ImageView mOwnerKo;
    TextView mOwnerName;
    TextView mOwnerPraise;
    ImageView mChallengerAvatar;
    ImageView mChallengerKo;
    TextView mChallengerName;
    TextView mChallengerPraise;

    PraiseView mPraiseOwner;
    PraiseView mPraiseChallenger;

    LinearLayout mTradeButtons;
    TextView mNoPosition;
    Button mClosePositionBtn;
    TextView mDirection;
    TextView mBuyPrice;
    TextView mProfit;
    LinearLayout mHoldingInfo;

    private View mBattleCreatedView;

    private View mBattleOrderOperateView;

    private View mPraiseView;

    private View mBattleSettlingView;

    private View mPlayersView;

    private OnViewClickListener mOnViewClickListener;

    private Battle mBattle;
    private int mBattleStatus;
    private TradeOrder mOwnerOrder;
    private TradeOrder mChallengerOrder;
    private TradeOrder mHoldingOrder;
    private Variety mVariety;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quickMatch:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onQuickMatchClick();
                }
                break;
            case R.id.cancelBattle:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onCancelBattleClick();
                }
                break;
            case R.id.buyLong:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onBuyLongClick();
                }
                break;
            case R.id.sellShort:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onSellShortClick();
                }
                break;
            case R.id.closePositionBtn:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onClosePositionClick();
                }
                break;
            case R.id.praiseOwner:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onPraiseClick(true);
                }
                break;
            case R.id.praiseChallenger:
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onPraiseClick(false);
                }
                break;
        }
    }

    public interface OnViewClickListener {
        void onQuickMatchClick();

        void onCancelBattleClick();

        void onBuyLongClick();

        void onSellShortClick();

        void onClosePositionClick();

        void onPraiseClick(boolean isOwner);
    }

    public BattleOperateView(Context context) {
        super(context);
        init();
    }

    public BattleOperateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mBattleCreatedView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_created, null);
        mBattleOrderOperateView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_order_operate, null);
        mPraiseView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_praise, null);
        mPlayersView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_versus, null);
        mBattleSettlingView = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_settling, null);

        // view find id
        mCountdown = mBattleCreatedView.findViewById(R.id.countdown);

        mBattleProgress = mPlayersView.findViewById(R.id.battleProgress);
        mOwnerAvatar = mPlayersView.findViewById(R.id.ownerAvatar);
        mOwnerKo = mPlayersView.findViewById(R.id.ownerKo);
        mOwnerName = mPlayersView.findViewById(R.id.ownerName);
        mOwnerPraise = mPlayersView.findViewById(R.id.ownerPraise);
        mChallengerAvatar = mPlayersView.findViewById(R.id.challengerAvatar);
        mChallengerKo = mPlayersView.findViewById(R.id.challengerKo);
        mChallengerName = mPlayersView.findViewById(R.id.challengerName);
        mChallengerPraise = mPlayersView.findViewById(R.id.challengerPraise);

        mPraiseOwner = mPraiseView.findViewById(R.id.praiseOwner);
        mPraiseChallenger = mPraiseView.findViewById(R.id.praiseChallenger);

        mTradeButtons = mBattleOrderOperateView.findViewById(R.id.tradeButtons);
        mNoPosition = mBattleOrderOperateView.findViewById(R.id.noPosition);
        mClosePositionBtn = mBattleOrderOperateView.findViewById(R.id.closePositionBtn);
        mDirection = mBattleOrderOperateView.findViewById(R.id.direction);
        mBuyPrice = mBattleOrderOperateView.findViewById(R.id.buyPrice);
        mProfit = mBattleOrderOperateView.findViewById(R.id.profit);
        mHoldingInfo = mBattleOrderOperateView.findViewById(R.id.holdingInfo);

        initListeners();

        hideAllOperationView();

        addView(mBattleCreatedView);
        addView(mBattleOrderOperateView);
        addView(mPraiseView);
        addView(mBattleSettlingView);
        addView(mPlayersView);
    }

    private void initListeners() {
        mBattleCreatedView.findViewById(R.id.quickMatch).setOnClickListener(this);
        mBattleCreatedView.findViewById(R.id.cancelBattle).setOnClickListener(this);
        mBattleOrderOperateView.findViewById(R.id.buyLong).setOnClickListener(this);
        mBattleOrderOperateView.findViewById(R.id.sellShort).setOnClickListener(this);
        mBattleOrderOperateView.findViewById(R.id.closePositionBtn).setOnClickListener(this);
        mPraiseView.findViewById(R.id.praiseOwner).setOnClickListener(this);
        mPraiseView.findViewById(R.id.praiseChallenger).setOnClickListener(this);
    }

    public void showSettlingView() {
        if (mBattleSettlingView.getVisibility() != VISIBLE) {
            mBattleSettlingView.setVisibility(VISIBLE);
            mBattleSettlingView.findViewById(R.id.loading).startAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.loading));
        }
    }

    public void hideSettlingView() {
        if (mBattleSettlingView.getVisibility() != GONE) {
            mBattleSettlingView.findViewById(R.id.loading).clearAnimation();
            mBattleSettlingView.setVisibility(GONE);
        }
    }

    public void updateView(int battleStatus) {
        mBattleStatus = battleStatus;
        if (mBattle != null) {
            updateViewBasedOnStatus();
        }
    }

    private void hideAllOperationView() {
        mBattleCreatedView.setVisibility(GONE);
        mBattleOrderOperateView.setVisibility(GONE);
        mPraiseView.setVisibility(GONE);
        mBattleSettlingView.setVisibility(GONE);
    }

    private void updateViewBasedOnStatus() {
        switch (mBattleStatus) {
            case BattleStatus.CREATED_OWNER:
                hideAllOperationView();
                mBattleCreatedView.setVisibility(VISIBLE);
                updatePlayersAvatarName();
                setPraise(mBattle.getLaunchPraise(), mBattle.getAgainstPraise());
                setBattleProfit(0, 0);
                break;
            case BattleStatus.STARTED_OWNER:
            case BattleStatus.STARTED_CHALLENGER:
                hideAllOperationView();
                mBattleOrderOperateView.setVisibility(VISIBLE);
                updatePlayersAvatarName();
                setPraise(mBattle.getLaunchPraise(), mBattle.getAgainstPraise());
                break;
            case BattleStatus.STARTED_OBSERVER:
                hideAllOperationView();
                mPraiseView.setVisibility(VISIBLE);
                updatePlayersAvatarName();
                setPraise(mBattle.getLaunchPraise(), mBattle.getAgainstPraise());
                break;
            case BattleStatus.OVER_OWNER:
            case BattleStatus.OVER_CHALLENGER:
            case BattleStatus.OVER_OBSERVER:
                hideAllOperationView();
                updatePlayersAvatarKo();
                setBattleProfit(mBattle.getLaunchScore(), mBattle.getAgainstScore());
                break;
        }
    }

    private void updatePlayersAvatarKo() {
        switch (mBattle.getWinResult()) {
            case Battle.WIN_RESULT_OWNER_WIN:
                mChallengerKo.setVisibility(VISIBLE);
                break;
            case Battle.WIN_RESULT_CHALLENGER_WIN:
                mOwnerKo.setVisibility(VISIBLE);
                break;
        }
    }

    private void updatePlayersAvatarName() {
        GlideApp.with(getContext())
                .load(mBattle.getLaunchUserPortrait())
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mOwnerAvatar);
        mOwnerName.setText(mBattle.getLaunchUserName());

        GlideApp.with(getContext())
                .load(mBattle.getAgainstUserPortrait())
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mChallengerAvatar);
        mChallengerName.setText(mBattle.getAgainstUserName());
    }

    public void setPraise(int ownerPraise, int challengerPraise) {
        if (mBattleStatus != BattleStatus.STARTED_OBSERVER) {
            String ownerPraiseNumber = ownerPraise > 999 ? getContext().getString(R.string.number999)
                    : String.valueOf(ownerPraise);
            String challengerPraiseNumber = challengerPraise > 999 ? getContext().getString(R.string.number999)
                    : String.valueOf(challengerPraise);
            mOwnerPraise.setText(
                    getContext().getString(R.string._support, String.valueOf(ownerPraiseNumber)));
            mChallengerPraise.setText(
                    getContext().getString(R.string._support, String.valueOf(challengerPraiseNumber)));
        } else {
            mPraiseOwner.setPraise(ownerPraise);
            mPraiseChallenger.setPraise(challengerPraise);
        }
    }

    public void setBattle(Battle battle) {
        mBattle = battle;
    }

    public void updateBattleWaitingCountDown() {
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long createTime = mBattle.getCreateTime();
        long pastTime = currentTime - createTime;
        long survivalTime = 10 * 60 * 1000; // 对战房间存活时间固定 10 min
        long remainingTime = survivalTime - pastTime;
        SpannableString remainingTimeStr = StrUtil.mergeTextWithColor(
                getContext().getString(R.string.countdown) + " ",
                DateUtil.format(remainingTime, "mm:ss"),
                ContextCompat.getColor(getContext(), R.color.yellowAssist));
        mCountdown.setText(remainingTimeStr);
    }

    public void setBattleProfit(double ownerProfit, double challengerProfit) {
        mBattleProgress.setBattleProfit(ownerProfit, challengerProfit);
    }

    public void setOwnerOrder(TradeOrder ownerOrder) {
        mOwnerOrder = ownerOrder;
        updateBattleOrderOperateView();
    }

    public void setChallengerOrder(TradeOrder challengerOrder) {
        mChallengerOrder = challengerOrder;
        updateBattleOrderOperateView();
    }

    private void updateBattleOrderOperateView() {
        switch (mBattleStatus) {
            case BattleStatus.STARTED_OWNER:
                mHoldingOrder = mOwnerOrder;
                if (mOwnerOrder != null) {
                    showClosePositionView();
                    updateHoldingInfo(mOwnerOrder);
                } else {
                    showOpenPositionView();
                }
                break;

            case BattleStatus.STARTED_CHALLENGER:
                mHoldingOrder = mChallengerOrder;
                if (mChallengerOrder != null) {
                    showClosePositionView();
                    updateHoldingInfo(mChallengerOrder);
                } else {
                    showOpenPositionView();
                }
                break;
        }
    }

    private void updateHoldingInfo(TradeOrder order) {
        if (order.getDirection() == TradeOrder.DIRECTION_LONG) {
            mDirection.setText(R.string.buy_long);
        } else {
            mDirection.setText(R.string.sell_short);
        }
        String orderPrice = String.valueOf(order.getOrderPrice());
        if (mVariety != null) {
            orderPrice = FinanceUtil.formatWithScale(order.getOrderPrice(), mVariety.getPriceScale());
        }
        mBuyPrice.setText(orderPrice);
        mProfit.setText(String.valueOf(0));
    }

    private void showOpenPositionView() {
        mTradeButtons.setVisibility(VISIBLE);
        mNoPosition.setVisibility(VISIBLE);
        mClosePositionBtn.setVisibility(GONE);
        mHoldingInfo.setVisibility(GONE);
    }

    private void showClosePositionView() {
        mTradeButtons.setVisibility(GONE);
        mNoPosition.setVisibility(GONE);
        mClosePositionBtn.setVisibility(VISIBLE);
        mHoldingInfo.setVisibility(VISIBLE);
    }

    public TradeOrder getHoldingOrder() {
        return mHoldingOrder;
    }

    public void updatePlayersProfit(FutureData futureData) {
        if (mVariety == null) return;

        if (mBattleStatus < BattleStatus.STARTED_OWNER
                || mBattleStatus > BattleStatus.STARTED_OBSERVER) return;

        if (mHoldingOrder != null) {
            double profit = getProfit(futureData, mHoldingOrder);
            if (profit >= 0) {
                mProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
                mProfit.setText("+" + FinanceUtil.formatWithScale(profit));
            } else {
                mProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
                mProfit.setText(FinanceUtil.formatWithScale(profit));
            }
        }

        double ownerProfit = mBattle.getLaunchUnwindScore() + getProfit(futureData, mOwnerOrder);
        double challengerProfit = mBattle.getAgainstUnwindScore() + getProfit(futureData, mChallengerOrder);
        setBattleProfit(ownerProfit, challengerProfit);
    }

    private double getProfit(FutureData futureData, TradeOrder order) {
        if (order != null && futureData != null) {
            double profit;
            if (order.getDirection() == TradeOrder.DIRECTION_LONG) {
                profit = FinanceUtil.subtraction(futureData.getLastPrice(), order.getOrderPrice()).doubleValue();
            } else {
                profit = FinanceUtil.subtraction(order.getOrderPrice(), futureData.getLastPrice()).doubleValue();
            }
            return profit * mVariety.getEachPointMoney();
        }
        return 0;
    }

    public void setVariety(Variety variety) {
        mVariety = variety;
    }
}
