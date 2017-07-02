package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.battle.TradeRecord;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.battle.TradeRecord.DIRECTION_UP;
import static com.sbai.finance.model.battle.TradeRecord.STATUS_TAKE_MORE_CLOSE_POSITION;
import static com.sbai.finance.model.battle.TradeRecord.STATUS_TAKE_MORE_POSITION;
import static com.sbai.finance.model.battle.TradeRecord.STATUS_TAKE_SHOET_CLOSE_POSITION;
import static com.sbai.finance.model.battle.TradeRecord.STATUS_TAKE_SHORT_POSITION;

/**
 * Created by linrongfang on 2017/6/20.
 */

public class BattleTradeView extends LinearLayout {

    //平仓
    public static final int STATE_CLOSE_POSITION = 0;
    //可交易
    public static final int STATE_TRADE = 1;

    @BindView(R.id.listView)
    ListView mListView;


    @BindView(R.id.tradeArea)
    LinearLayout mTradeArea;
    @BindView(R.id.longPurchase)
    Button mLongPurchase;
    @BindView(R.id.shortPurchase)
    Button mShortPurchase;
    @BindView(R.id.closePosition)
    Button mClosePosition;

    @BindView(R.id.tradeDataArea)
    LinearLayout mTradeDataArea;
    @BindView(R.id.closePositionData)
    LinearLayout mClosePositionDataArea;
    @BindView(R.id.direction)
    TextView mDirection;
    @BindView(R.id.buyPrice)
    TextView mBuyPrice;
    @BindView(R.id.profit)
    TextView mProfit;
    @BindView(R.id.noPosition)
    TextView mNoPosition;

    private BattleTradeAdapter mBattleTradeAdapter;

    private OnViewClickListener mOnViewClickListener;

    private int mState;

    public interface OnViewClickListener {
        void onLongPurchaseButtonClick();

        void onShortPurchaseButtonClick();

        void onClosePositionButtonClick();
    }

    public BattleTradeView(Context context) {
        super(context);
        init();
    }

    public BattleTradeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BattleTradeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_battle_trade, null, false);
        addView(view);
        ButterKnife.bind(this);

        mBattleTradeAdapter = new BattleTradeAdapter(getContext());
        mListView.setAdapter(mBattleTradeAdapter);

        mLongPurchase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onLongPurchaseButtonClick();
                }
            }
        });

        mShortPurchase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onShortPurchaseButtonClick();
                }
            }
        });

        mClosePosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onClosePositionButtonClick();
                }
            }
        });
    }

    public void addTradeData(List<TradeRecord> list, int creatorId, int againstId) {
        mBattleTradeAdapter.setUserId(creatorId, againstId);
        mBattleTradeAdapter.clear();
        mBattleTradeAdapter.addAll(list);
        mListView.setSelection(mBattleTradeAdapter.getCount() - 1);
    }

    public void setVisitor(boolean isVisitor) {
        //参观模式
        if (isVisitor) {
            updateViewHeight();
        }
    }

    private void updateViewHeight() {
        //调整Listview高度 隐藏交易区域;
        LayoutParams params = (LayoutParams) mListView.getLayoutParams();
        params.height = (int) Display.dp2Px(210f, getResources());
        mTradeArea.setVisibility(GONE);
        mListView.setLayoutParams(params);
    }

    public int getTradeState() {
        return mState;
    }

    public void changeTradeState(int state) {
        mState = state;
        if (state == STATE_CLOSE_POSITION) {
            //持仓中  只能卖
            mLongPurchase.setVisibility(GONE);
            mShortPurchase.setVisibility(GONE);
            mClosePosition.setVisibility(VISIBLE);
            mTradeDataArea.removeAllViews();
            mTradeDataArea.addView(mClosePositionDataArea);
            mClosePositionDataArea.setVisibility(VISIBLE);
        } else if (state == STATE_TRADE) {
            //未持仓 可以买
            mLongPurchase.setVisibility(VISIBLE);
            mShortPurchase.setVisibility(VISIBLE);
            mClosePosition.setVisibility(GONE);
            mTradeDataArea.removeAllViews();
            mNoPosition.setVisibility(VISIBLE);
            mTradeDataArea.addView(mNoPosition);
        }
    }

    public void setTradeData(int direction, double buyPrice, double profit) {
        //设置方向  买入价格 盈利
        if (direction == DIRECTION_UP) {
            mDirection.setText(getContext().getString(R.string.take_more));
        } else {
            mDirection.setText(getContext().getString(R.string.take_short));
        }

        mBuyPrice.setText(FinanceUtil.formatWithScale(buyPrice));

        if (profit >= 0) {
            mProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            mProfit.setText("+" + FinanceUtil.formatWithScale(profit));
        } else {
            mProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
            mProfit.setText(FinanceUtil.formatWithScale(profit));
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }


    public static class BattleTradeAdapter extends ArrayAdapter<TradeRecord> {
        private int mCreatorId;
        private int mAgainstId;
        private Context mContext;

        public BattleTradeAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }

        public void setUserId(int creatorId, int againstId) {
            mCreatorId = creatorId;
            mAgainstId = againstId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_battle_trade, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), isLeft(getItem(position)), mContext);
            return convertView;
        }

        private boolean isLeft(TradeRecord record) {
            return record.getUserId() == mCreatorId;
        }

        static class ViewHolder {
            @BindView(R.id.myInfo)
            TextView mMyInfo;
            @BindView(R.id.myInfoTime)
            TextView mMyInfoTime;
            @BindView(R.id.userInfo)
            TextView mUserInfo;
            @BindView(R.id.userInfoTime)
            TextView mUserInfoTime;
            @BindView(R.id.point)
            ImageView mPoint;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(TradeRecord item, boolean isLeft, Context context) {

                StringBuilder info = new StringBuilder();
                info.append(FinanceUtil.formatWithScale(item.getOptPrice(),item.getMarketPoint()));
                String time = DateUtil.getBattleFormatTime(item.getOptTime());

                switch (item.getOptStatus()) {
                    case STATUS_TAKE_MORE_POSITION:
                        info.append(context.getString(R.string.take_more_position));
                        break;
                    case STATUS_TAKE_SHORT_POSITION:
                        info.append(context.getString(R.string.take_short_position));
                        break;
                    case STATUS_TAKE_MORE_CLOSE_POSITION:
                        info.append(context.getString(R.string.take_more_close_position));
                        break;
                    case STATUS_TAKE_SHOET_CLOSE_POSITION:
                        info.append(context.getString(R.string.take_short_close_position));
                        break;
                }


                if (isLeft) {
                    //left为房主
                    mMyInfo.setVisibility(VISIBLE);
                    mMyInfoTime.setVisibility(VISIBLE);
                    mUserInfo.setVisibility(GONE);
                    mUserInfoTime.setVisibility(GONE);

                    mMyInfo.setText(info.toString());
                    mMyInfoTime.setText(time);
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battle_trade_mine));
                } else {
                    mMyInfo.setVisibility(GONE);
                    mMyInfoTime.setVisibility(GONE);
                    mUserInfo.setVisibility(VISIBLE);
                    mUserInfoTime.setVisibility(VISIBLE);

                    mUserInfo.setText(info.toString());
                    mUserInfoTime.setText(time);
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battle_trade_user));
                }
            }
        }
    }


}
