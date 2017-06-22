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
import com.sbai.finance.model.versus.VersusTrade;
import com.sbai.finance.utils.Display;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/6/20.
 */

public class BattleTradeView extends LinearLayout {

    //平仓
    public static final int STATE_CLOSE_POSITION = 0;
    //可交易
    public static final int STATE_TRADE = 1;

    @BindView(R.id.listview)
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
        setBackgroundResource(R.drawable.ic_futures_versus_bg);
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

    public void addTradeData(List<VersusTrade> list) {
        mBattleTradeAdapter.addAll(list);
        mBattleTradeAdapter.notifyDataSetChanged();
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

    public void changeTradeState(int state) {
        if (state == STATE_CLOSE_POSITION) {
            mLongPurchase.setVisibility(GONE);
            mShortPurchase.setVisibility(GONE);
            mClosePosition.setVisibility(VISIBLE);
            mNoPosition.setVisibility(GONE);
        } else if (state == STATE_TRADE) {
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
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }


    public static class BattleTradeAdapter extends ArrayAdapter<VersusTrade> {
        Context mContext;

        public BattleTradeAdapter(Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindDataWithView(getItem(position), position, mContext);
            return convertView;
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

            private void bindDataWithView(VersusTrade item, int position, Context context) {
                if (position % 2 == 0) {
                    //判断条件以后会改成是否房主
                    mMyInfo.setVisibility(VISIBLE);
                    mMyInfoTime.setVisibility(VISIBLE);
                    mUserInfo.setVisibility(GONE);
                    mUserInfoTime.setVisibility(GONE);

                    mMyInfo.setText(item.getInfo());
                    mMyInfoTime.setText(item.getTime());
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battle_trade_mine));
                } else {
                    mMyInfo.setVisibility(GONE);
                    mMyInfoTime.setVisibility(GONE);
                    mUserInfo.setVisibility(VISIBLE);
                    mUserInfoTime.setVisibility(VISIBLE);

                    mUserInfo.setText(item.getInfo());
                    mUserInfoTime.setText(item.getTime());
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battle_trade_user));
                }
            }
        }
    }


}
