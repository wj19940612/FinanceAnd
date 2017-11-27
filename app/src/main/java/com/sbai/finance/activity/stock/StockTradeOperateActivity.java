package com.sbai.finance.activity.stock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StockUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.PlusMinusEditText;
import com.sbai.finance.view.TitleBar;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.StockUtil.NULL_VALUE;

/**
 * Modified by john on 24/11/2017
 * <p>
 * 股票交易操作页面，买入卖出，如果从股票详情页面进入，当前账户为模拟，如果从账户页面进入，依赖于当前保存是什么账户
 */
public class StockTradeOperateActivity extends BaseActivity {

    public static final String TRADE_TYPE = "trade_type";
    public static final int TRADE_TYPE_BUY = 80;
    public static final int TRADE_TYPE_SELL = 81;

    private static final int MINIMUM_FEE = 5;
    private static final float FEE_RATE = 0.003f;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.stockNameCode)
    EditText mStockNameCode;
    @BindView(R.id.tradePrice)
    PlusMinusEditText mTradePrice;
    @BindView(R.id.limitUp)
    TextView mLimitUp;
    @BindView(R.id.limitDown)
    TextView mLimitDown;
    @BindView(R.id.tradeVolume)
    PlusMinusEditText mTradeVolume;
    @BindView(R.id.fullPosition)
    TextView mFullPosition;
    @BindView(R.id.halfPosition)
    TextView mHalfPosition;
    @BindView(R.id.quarterPosition)
    TextView mQuarterPosition;
    @BindView(R.id.fee)
    TextView mFee;

    @BindView(R.id.askPrice5)
    TextView mAskPrice5;
    @BindView(R.id.askVolume5)
    TextView mAskVolume5;
    @BindView(R.id.askPrice4)
    TextView mAskPrice4;
    @BindView(R.id.askVolume4)
    TextView mAskVolume4;
    @BindView(R.id.askPrice3)
    TextView mAskPrice3;
    @BindView(R.id.askVolume3)
    TextView mAskVolume3;
    @BindView(R.id.askPrice2)
    TextView mAskPrice2;
    @BindView(R.id.askVolume2)
    TextView mAskVolume2;
    @BindView(R.id.askPrice1)
    TextView mAskPrice1;
    @BindView(R.id.askVolume1)
    TextView mAskVolume1;
    @BindView(R.id.bidPrice1)
    TextView mBidPrice1;
    @BindView(R.id.bidVolume1)
    TextView mBidVolume1;
    @BindView(R.id.bidPrice2)
    TextView mBidPrice2;
    @BindView(R.id.bidVolume2)
    TextView mBidVolume2;
    @BindView(R.id.bidPrice3)
    TextView mBidPrice3;
    @BindView(R.id.bidVolume3)
    TextView mBidVolume3;
    @BindView(R.id.bidPrice4)
    TextView mBidPrice4;
    @BindView(R.id.bidVolume4)
    TextView mBidVolume4;
    @BindView(R.id.bidPrice5)
    TextView mBidPrice5;
    @BindView(R.id.bidVolume5)
    TextView mBidVolume5;

    @BindView(R.id.tradeButton)
    Button mTradeButton;
    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;
    @BindView(R.id.listView)
    ListView mListView;

    private TextView mBuyInTab;
    private TextView mSellOutTab;

    private int mTradeType;
    private Variety mVariety;
    private StockRTData mStockRTData;
    private boolean mInitTradePrice;

    private StockUser mStockUser;
    private int mSharesCanBuy;
    private int mShareCanSell;

    private TextWatcher mVolumeWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean tradeButtonEnable = checkTradeButtonEnable();
            if (tradeButtonEnable != mTradeButton.isEnabled()) {
                mTradeButton.setEnabled(tradeButtonEnable);
            }
        }
    };

    private TextWatcher mPriceWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean tradeButtonEnable = checkTradeButtonEnable();
            if (tradeButtonEnable != mTradeButton.isEnabled()) {
                mTradeButton.setEnabled(tradeButtonEnable);
            }

            updateTradeVolumeView();
        }
    };

    private void updateTradeVolumeView() {
        String tradePrice = mTradePrice.getText();

        if (mBuyInTab.isSelected()) { // 买入
            if (!TextUtils.isEmpty(tradePrice) && mStockUser != null) {
                double availableFund = mStockUser.getUsableMoney();
                double buyPrice = Double.parseDouble(tradePrice);
                mSharesCanBuy = calculateSharesCanBuy(availableFund, buyPrice); // 可买股数
                mTradeVolume.setHint(getString(R.string.volume_can_buy_x, mSharesCanBuy));
                String fee = FinanceUtil.formatWithScale(mSharesCanBuy * buyPrice * FEE_RATE);
                mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), fee,
                        ContextCompat.getColor(getActivity(), R.color.redPrimary)));
            } else {
                mTradeVolume.setHint(R.string.buy_volume);
                mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), NULL_VALUE,
                        ContextCompat.getColor(getActivity(), R.color.redPrimary)));
            }
        } else {

        }
    }

    private int calculateSharesCanBuy(double availableFund, double buyPrice) {
        int totalShares = (int) (availableFund / buyPrice);
        totalShares -= totalShares % 100;
        double fee = Math.max(totalShares * buyPrice * FEE_RATE, MINIMUM_FEE);
        availableFund -= fee;
        int sharesCanBuy = (int) (availableFund / buyPrice);
        sharesCanBuy -= sharesCanBuy % 100;
        return sharesCanBuy;
    }

    private boolean checkTradeButtonEnable() {
        String tradePrice = mTradePrice.getText();
        String tradeVolume = mTradeVolume.getText();
        return !TextUtils.isEmpty(tradePrice) && !TextUtils.isEmpty(tradeVolume);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade_operate);
        ButterKnife.bind(this);
        adjustFivePriceViewTextSize();

        initData(getIntent());

        initTitleBar();
        initTradeViews();

        initViewWithVariety();

        StockUser stockUser = LocalUser.getUser().getStockUser();
        if (stockUser != null) {
            mStockUser = stockUser;
            updateTradeVolumeView();
            
        } else {
            requestMockStockUser();
        }

        mTradePrice.addTextChangedListener(mPriceWatcher);
        mTradeVolume.addTextChangedListener(mVolumeWatcher);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTradePrice.removeTextChangedListener(mPriceWatcher);
        mTradeVolume.removeTextChangedListener(mVolumeWatcher);
    }

    private void requestMockStockUser() {
        Client.getStockAccount(StockUser.ACCOUNT_TYPE_MOCK, null)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockUser>>, List<StockUser>>() {
                    @Override
                    protected void onRespSuccessData(List<StockUser> data) {
                        if (!data.isEmpty()) {
                            mStockUser = data.get(0);
                            LocalUser.getUser().setStockUser(mStockUser);
                            updateTradeVolumeView();
                        }
                    }
                }).fireFree();
    }

    private void initTradeViews() {
        if (mBuyInTab.isSelected()) {
            mTradePrice.setHint(R.string.buy_price);
            mTradeVolume.setHint(R.string.buy_volume);
            mTradeButton.setText(R.string.buy_in_right_now);
        } else {
            mTradePrice.setHint(R.string.sell_price);
            mTradeVolume.setHint(R.string.sell_volume);
            mTradeButton.setText(R.string.sell_out_right_now);
        }
    }

    private void initViewWithVariety() {
        if (mVariety != null) {
            mStockNameCode.setText(mVariety.getVarietyName() + " " + mVariety.getVarietyType());
        } else {
            mStockNameCode.setText(null);
        }

        clearMarketDataViews();
        mInitTradePrice = false;
        requestStockRTData();
    }

    private void clearMarketDataViews() {
        mTradePrice.setText(null);
        mTradeVolume.setText(null);
        mLimitUp.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_up_x), NULL_VALUE,
                ContextCompat.getColor(getActivity(), R.color.redPrimary)));
        mLimitDown.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_down_x), NULL_VALUE,
                ContextCompat.getColor(getActivity(), R.color.greenAssist)));
        unselectPositionSelectors();
        mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), NULL_VALUE,
                ContextCompat.getColor(getActivity(), R.color.redPrimary)));

        mAskPrice1.setText(StockUtil.NULL_VALUE);
        mAskPrice2.setText(StockUtil.NULL_VALUE);
        mAskPrice3.setText(StockUtil.NULL_VALUE);
        mAskPrice4.setText(StockUtil.NULL_VALUE);
        mAskPrice5.setText(StockUtil.NULL_VALUE);

        mBidPrice1.setText(StockUtil.NULL_VALUE);
        mBidPrice2.setText(StockUtil.NULL_VALUE);
        mBidPrice3.setText(StockUtil.NULL_VALUE);
        mBidPrice4.setText(StockUtil.NULL_VALUE);
        mBidPrice5.setText(StockUtil.NULL_VALUE);

        mAskVolume1.setText(StockUtil.NULL_VALUE);
        mAskVolume2.setText(StockUtil.NULL_VALUE);
        mAskVolume3.setText(StockUtil.NULL_VALUE);
        mAskVolume4.setText(StockUtil.NULL_VALUE);
        mAskVolume5.setText(StockUtil.NULL_VALUE);

        mBidVolume1.setText(StockUtil.NULL_VALUE);
        mBidVolume2.setText(StockUtil.NULL_VALUE);
        mBidVolume3.setText(StockUtil.NULL_VALUE);
        mBidVolume4.setText(StockUtil.NULL_VALUE);
        mBidVolume5.setText(StockUtil.NULL_VALUE);

    }

    private void unselectPositionSelectors() {
        mFullPosition.setSelected(false);
        mHalfPosition.setSelected(false);
        mQuarterPosition.setSelected(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startScheduleJob(1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.STOCK_RT_PULL_TIME == 0) {
            requestStockRTData();
        }
    }

    private void requestStockRTData() {
        if (mVariety == null) return;

        Client.getStockRealtimeData(mVariety.getVarietyType())
                .setCallback(new Callback2D<Resp<StockRTData>, StockRTData>() {
                    @Override
                    protected void onRespSuccessData(StockRTData result) {
                        mStockRTData = result;
                        updateFivePricesView();

                        if (!mInitTradePrice) {
                            updateTradePriceWithTradeType();
                            mInitTradePrice = true;
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
    }

    private void updateTradePriceWithTradeType() {
        if (mBuyInTab.isSelected()) {
            mTradePrice.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice()));
        } else {
            mTradePrice.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice()));
        }
        mLimitUp.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_up_x),
                StockUtil.getStockDecimal(mStockRTData.getUpLimitPrice()),
                ContextCompat.getColor(getActivity(), R.color.redPrimary)));
        mLimitDown.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_down_x),
                StockUtil.getStockDecimal(mStockRTData.getDownLimitPrice()),
                ContextCompat.getColor(getActivity(), R.color.greenAssist)));
    }

    private void updateFivePricesView() {
        if (mStockRTData.getInstrumentId().equals(mVariety.getVarietyType())) {
            mAskPrice1.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice()));
            mAskPrice2.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice2()));
            mAskPrice3.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice3()));
            mAskPrice4.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice4()));
            mAskPrice5.setText(StockUtil.getStockDecimal(mStockRTData.getAskPrice5()));

            mBidPrice1.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice()));
            mBidPrice2.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice2()));
            mBidPrice3.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice3()));
            mBidPrice4.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice4()));
            mBidPrice5.setText(StockUtil.getStockDecimal(mStockRTData.getBidPrice5()));

            mAskVolume1.setText(getFormattedVolume(mStockRTData.getAskVolume()));
            mAskVolume2.setText(getFormattedVolume(mStockRTData.getAskVolume2()));
            mAskVolume3.setText(getFormattedVolume(mStockRTData.getAskVolume3()));
            mAskVolume4.setText(getFormattedVolume(mStockRTData.getAskVolume4()));
            mAskVolume5.setText(getFormattedVolume(mStockRTData.getAskVolume5()));

            mBidVolume1.setText(getFormattedVolume(mStockRTData.getBidVolume()));
            mBidVolume2.setText(getFormattedVolume(mStockRTData.getBidVolume2()));
            mBidVolume3.setText(getFormattedVolume(mStockRTData.getBidVolume3()));
            mBidVolume4.setText(getFormattedVolume(mStockRTData.getBidVolume4()));
            mBidVolume5.setText(getFormattedVolume(mStockRTData.getBidVolume5()));
        }
    }

    private String getFormattedVolume(String ask_volume) {
        long askVolume = 0;
        try {
            askVolume = Long.valueOf(ask_volume).longValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            askVolume = askVolume / 100;
            if (askVolume >= 10000 || askVolume <= -10000) {
                return formatWithUnit(askVolume);
            }
            return String.valueOf(askVolume);
        }
    }

    private String formatWithUnit(long askVolume) {
        BigDecimal decimal = BigDecimal.valueOf(askVolume)
                .divide(new BigDecimal(10000), 1, BigDecimal.ROUND_DOWN);
        return decimal.toString() + FinanceUtil.UNIT_WANG;
    }

    private void adjustFivePriceViewTextSize() {
        View view = findViewById(R.id.fivePriceView);
        adjustTextSize(view);
    }

    private void adjustTextSize(View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                adjustTextSize(((ViewGroup) view).getChildAt(i));
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextSize(12);
        }
    }

    private void initData(Intent intent) {
        mTradeType = intent.getIntExtra(TRADE_TYPE, TRADE_TYPE_BUY);
        mVariety = intent.getParcelableExtra(ExtraKeys.VARIETY);
    }

    private void initTitleBar() {
        View customView = mTitleBar.getCustomView();
        mBuyInTab = customView.findViewById(R.id.buyIn);
        mSellOutTab = customView.findViewById(R.id.sellOut);
        if (mTradeType == TRADE_TYPE_BUY) {
            mBuyInTab.setSelected(true);
            mSellOutTab.setSelected(false);
        } else {
            mBuyInTab.setSelected(false);
            mSellOutTab.setSelected(true);
        }
        mBuyInTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBuyInTab.isSelected()) {
                    mBuyInTab.setSelected(true);
                    mSellOutTab.setSelected(false);
                    mTradeVolume.setText(null);
                    initTradeViews();
                    unselectPositionSelectors();
                    updateTradeVolumeView();
                }
            }
        });
        mSellOutTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSellOutTab.isSelected()) {
                    mBuyInTab.setSelected(false);
                    mSellOutTab.setSelected(true);
                    mTradeVolume.setText(null);
                    initTradeViews();
                    unselectPositionSelectors();
                    updateTradeVolumeView();
                }
            }
        });

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 23/11/2017 添加交易规则显示
            }
        });
    }

    @OnClick({R.id.ask5, R.id.ask4, R.id.ask3, R.id.ask2, R.id.ask1,
            R.id.bid1, R.id.bid2, R.id.bid3, R.id.bid4, R.id.bid5,
            R.id.fullPosition, R.id.halfPosition, R.id.quarterPosition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ask5:
                mTradePrice.setText(mAskPrice5.getText().toString());
                break;
            case R.id.ask4:
                mTradePrice.setText(mAskPrice4.getText().toString());
                break;
            case R.id.ask3:
                mTradePrice.setText(mAskPrice3.getText().toString());
                break;
            case R.id.ask2:
                mTradePrice.setText(mAskPrice2.getText().toString());
                break;
            case R.id.ask1:
                mTradePrice.setText(mAskPrice1.getText().toString());
                break;
            case R.id.bid1:
                mTradePrice.setText(mBidPrice1.getText().toString());
                break;
            case R.id.bid2:
                mTradePrice.setText(mBidPrice2.getText().toString());
                break;
            case R.id.bid3:
                mTradePrice.setText(mBidPrice3.getText().toString());
                break;
            case R.id.bid4:
                mTradePrice.setText(mBidPrice4.getText().toString());
                break;
            case R.id.bid5:
                mTradePrice.setText(mBidPrice5.getText().toString());
                break;
            case R.id.fullPosition:
                unselectPositionSelectors();
                mFullPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;
            case R.id.halfPosition:
                unselectPositionSelectors();
                mHalfPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;
            case R.id.quarterPosition:
                unselectPositionSelectors();
                mQuarterPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;
        }
    }

    private void onPositionSelectorsSelected() {
        if (mFullPosition.isSelected()) {
            int selectShares = mSharesCanBuy;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            } else {
                mTradeVolume.setText(null);
            }
        } else if (mHalfPosition.isSelected()) {
            int selectShares = mSharesCanBuy / 2;
            selectShares -= selectShares % 100;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            } else {
                mTradeVolume.setText(null);
            }
        } else if (mQuarterPosition.isSelected()) {
            int selectShares = mSharesCanBuy / 4;
            selectShares -= selectShares % 100;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            } else {
                mTradeVolume.setText(null);
            }
        }
    }
}
