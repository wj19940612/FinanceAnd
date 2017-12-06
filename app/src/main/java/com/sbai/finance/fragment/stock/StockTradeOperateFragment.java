package com.sbai.finance.fragment.stock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.stock.StockTradeOperateActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.local.StockOrder;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.StockUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.TextViewUtils;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.PlusMinusEditText;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.dialog.TradeConfirmDialog;
import com.sbai.finance.view.stock.StockSearchPopup;
import com.sbai.finance.view.stock.TotalPricePopup;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.utils.StockUtil.FEE_RATE;
import static com.sbai.finance.utils.StockUtil.MINIMUM_FEE;
import static com.sbai.finance.utils.StockUtil.NULL_VALUE;
import static com.sbai.finance.utils.StockUtil.STAMP_TAX_RATE;

/**
 * Modified by john on 27/11/2017
 * <p>
 * <p>
 * Description: 股票交易页面买入卖出 fragment
 */
public class StockTradeOperateFragment extends BaseFragment {

    public interface OnPostTradeSuccessListener {
        void onPostTradeSuccess();

        void onCheckOrderClick();
    }

    public interface OnSearchStockClickListener {
        void onSearchStockClick(Stock stock);
    }

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

    Unbinder unbinder;

    private int mTradeType;
    private Stock mStock;
    private StockRTData mStockRTData;

    private int mMaxTradeVolume;
    private TotalPricePopup mTotalPricePopup;
    private StockSearchPopup mStockSearchPopup;

    private OnPostTradeSuccessListener mOnPostTradeSuccessListener;
    private OnSearchStockClickListener mOnSearchStockClickListener;

    private TextWatcher mVolumeWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean tradeButtonEnable = checkTradeButtonEnable();
            if (tradeButtonEnable != mTradeButton.isEnabled()) {
                mTradeButton.setEnabled(tradeButtonEnable);
            }

            updateFee();
            updateTotalPrice();
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTotalPricePopup != null) {
                mTotalPricePopup.dismiss();
            }
        }
    };

    private void updateTotalPrice() {
        String tradePrice = mTradePrice.getText();
        String tradeVolume = mTradeVolume.getText();
        if (!TextUtils.isEmpty(tradePrice) && !TextUtils.isEmpty(tradeVolume)) {
            if (mTotalPricePopup == null) {
                mTotalPricePopup = new TotalPricePopup(getActivity());
            }
            double buyPrice = Double.parseDouble(tradePrice);
            double buyVolume = Double.parseDouble(tradeVolume);
            mTradeVolume.removeCallbacks(mRunnable);
            mTotalPricePopup.setTotalPrice(FinanceUtil.formatWithScale(buyPrice * buyVolume));
            mTotalPricePopup.showAbove(mTradeVolume);
            mTradeVolume.postDelayed(mRunnable, 2000);
        } else {
            if (mTotalPricePopup != null) {
                mTotalPricePopup.dismiss();
            }
        }
    }

    private TextWatcher mPriceWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean tradeButtonEnable = checkTradeButtonEnable();
            if (tradeButtonEnable != mTradeButton.isEnabled()) {
                mTradeButton.setEnabled(tradeButtonEnable);
            }

            updateMaxBuyableVolume();
            updateFee();
            updateTotalPrice();
        }
    };

    private TextWatcher mStockNameWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean tradeButtonEnable = checkTradeButtonEnable();
            if (tradeButtonEnable != mTradeButton.isEnabled()) {
                mTradeButton.setEnabled(tradeButtonEnable);
            }

            String searchKey = TextViewUtils.getTrim(mStockNameCode);
            if (mStockNameCode.hasFocus() && !TextUtils.isEmpty(searchKey)) {
                searchStocks(searchKey);
            } else {
                if (mStockSearchPopup != null) {
                    mStockSearchPopup.dismiss();
                }
            }
        }
    };

    private void searchStocks(String searchKey) {
        String encodeSearchKey = null;
        try {
            encodeSearchKey = URLEncoder.encode(searchKey, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Client.searchStocks(encodeSearchKey).setTag(TAG)
                .setId(searchKey)
                .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                    @Override
                    protected void onRespSuccessData(List<Stock> data) {
                        String curSearchKey = TextViewUtils.getTrim(mStockNameCode);
                        if (curSearchKey.equals(getId())) {
                            showSearchResultPopup(data);
                        }
                    }
                }).fire();
    }

    private void showSearchResultPopup(List<Stock> data) {
        if (mStockSearchPopup == null) {
            mStockSearchPopup = new StockSearchPopup(getActivity());
            mStockSearchPopup.setOnStockSelectListener(new StockSearchPopup.OnStockSelectListener() {
                @Override
                public void onStockSelect(Stock stock) {
                    mStockSearchPopup.dismiss();
                    if (mOnSearchStockClickListener != null) {
                        mOnSearchStockClickListener.onSearchStockClick(stock);
                    }
                }
            });
        }
        mStockSearchPopup.showBelow(mStockNameCode);
        mStockSearchPopup.setStocks(data);
    }

    public void updateMaxBuyableVolume() {
        String tradePrice = mTradePrice.getText();
        StockUser stockUser = LocalUser.getUser().getStockUser();
        if (mTradeType == StockTradeOperateActivity.TRADE_TYPE_BUY) { // 买入
            if (!TextUtils.isEmpty(tradePrice) && stockUser != null) {
                double availableFund = stockUser.getUsableMoney();
                double buyPrice = Double.parseDouble(tradePrice);
                mMaxTradeVolume = calculateSharesCanBuy(availableFund, buyPrice); // 可买股数
                mTradeVolume.setHint(getString(R.string.volume_can_buy_x, mMaxTradeVolume));
            } else {
                mTradeVolume.setHint(R.string.buy_volume);
            }
        }
    }

    public void updateMaxSalableVolume(int salableVolume) {
        if (mTradeType == StockTradeOperateActivity.TRADE_TYPE_SELL) {
            mMaxTradeVolume = salableVolume;
            mTradeVolume.setHint(getString(R.string.volume_can_sell_x, mMaxTradeVolume));
        }
    }

    private void updateFee() {
        String tradePrice = mTradePrice.getText();
        String tradeVolume = mTradeVolume.getText();
        if (!TextUtils.isEmpty(tradePrice) && !TextUtils.isEmpty(tradeVolume)) {
            double price = Double.parseDouble(tradePrice);
            double volume = Double.parseDouble(tradeVolume);
            if (mTradeType == StockTradeOperateActivity.TRADE_TYPE_BUY) { // 买入手续费：成交金额 * 0.3%
                String fee = FinanceUtil.formatWithScale(Math.max(volume * price * FEE_RATE, MINIMUM_FEE));
                mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), fee,
                        ContextCompat.getColor(getActivity(), R.color.redPrimary)));
            } else { // 卖出手续费：印花税（成交金额 * 0.1%）+ 成交金额 * 0.3%
                String fee = FinanceUtil.formatWithScale(Math.max(volume * price * FEE_RATE, MINIMUM_FEE)
                        + volume * price * STAMP_TAX_RATE);
                mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), fee,
                        ContextCompat.getColor(getActivity(), R.color.redPrimary)));
            }
        } else {
            mFee.setText(StrUtil.mergeTextWithColor(getString(R.string.fee_x), NULL_VALUE,
                    ContextCompat.getColor(getActivity(), R.color.redPrimary)));
        }
    }

    public void updateRealTimeData(StockRTData stockRTData) {
        if (mStock == null) return;

        // 第一次更新数据 以及切换股票的时候
        if (mStockRTData == null || !mStockRTData.getInstrumentId().equals(mStock.getVarietyCode())) {
            if (mTradeType == StockTradeOperateActivity.TRADE_TYPE_BUY) {
                mTradePrice.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice()));
            } else {
                mTradePrice.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice()));
            }
            mLimitUp.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_up_x),
                    StockUtil.getStockDecimal(stockRTData.getUpLimitPrice()),
                    ContextCompat.getColor(getActivity(), R.color.redPrimary)));
            mLimitDown.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_down_x),
                    StockUtil.getStockDecimal(stockRTData.getDownLimitPrice()),
                    ContextCompat.getColor(getActivity(), R.color.greenAssist)));

            resetPositionSelectors();
        }

        if (stockRTData.getInstrumentId().equals(mStock.getVarietyCode())) {
            mAskPrice1.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice()));
            mAskPrice2.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice2()));
            mAskPrice3.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice3()));
            mAskPrice4.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice4()));
            mAskPrice5.setText(StockUtil.getStockDecimal(stockRTData.getAskPrice5()));

            mBidPrice1.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice()));
            mBidPrice2.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice2()));
            mBidPrice3.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice3()));
            mBidPrice4.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice4()));
            mBidPrice5.setText(StockUtil.getStockDecimal(stockRTData.getBidPrice5()));

            mAskVolume1.setText(getFormattedVolume(stockRTData.getAskVolume()));
            mAskVolume2.setText(getFormattedVolume(stockRTData.getAskVolume2()));
            mAskVolume3.setText(getFormattedVolume(stockRTData.getAskVolume3()));
            mAskVolume4.setText(getFormattedVolume(stockRTData.getAskVolume4()));
            mAskVolume5.setText(getFormattedVolume(stockRTData.getAskVolume5()));

            mBidVolume1.setText(getFormattedVolume(stockRTData.getBidVolume()));
            mBidVolume2.setText(getFormattedVolume(stockRTData.getBidVolume2()));
            mBidVolume3.setText(getFormattedVolume(stockRTData.getBidVolume3()));
            mBidVolume4.setText(getFormattedVolume(stockRTData.getBidVolume4()));
            mBidVolume5.setText(getFormattedVolume(stockRTData.getBidVolume5()));
        }

        mStockRTData = stockRTData;
    }

    private int calculateSharesCanBuy(double availableFund, double buyPrice) {
        if (buyPrice > 0) {
            int totalShares = (int) (availableFund / buyPrice);
            totalShares -= totalShares % 100;
            double fee = Math.max(totalShares * buyPrice * FEE_RATE, MINIMUM_FEE);
            availableFund -= fee;
            int sharesCanBuy = (int) (availableFund / buyPrice);
            sharesCanBuy -= sharesCanBuy % 100;
            return sharesCanBuy;
        }
        return 0;
    }


    private boolean checkTradeButtonEnable() {
        String tradePrice = mTradePrice.getText();
        String tradeVolume = mTradeVolume.getText();
        String stockName = TextViewUtils.getTrim(mStockNameCode);
        return !TextUtils.isEmpty(tradePrice) && !TextUtils.isEmpty(tradeVolume) && !TextUtils.isEmpty(stockName)
                && LocalUser.getUser().getStockUser() != null && mStock != null;
    }

    public static StockTradeOperateFragment newInstance(Stock stock, int tradeType) {
        StockTradeOperateFragment fragment = new StockTradeOperateFragment();
        Bundle args = new Bundle();
        args.putParcelable("var", stock);
        args.putInt("tradeType", tradeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostTradeSuccessListener) {
            mOnPostTradeSuccessListener = (OnPostTradeSuccessListener) context;
        }
        if (context instanceof OnSearchStockClickListener) {
            mOnSearchStockClickListener = (OnSearchStockClickListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTradeType = getArguments().getInt("tradeType");
            mStock = getArguments().getParcelable("var");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_trade_operate, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextViewUtils.setTextViewSize(getView().findViewById(R.id.fivePriceView), 12);
        setBackground4FiveMarketData();

        if (mTradeType == StockTradeOperateActivity.TRADE_TYPE_BUY) {
            mTradePrice.setHint(R.string.buy_price);
            mTradeVolume.setHint(R.string.buy_volume);
            mTradeButton.setText(R.string.buy_in_right_now);
        } else {
            mTradePrice.setHint(R.string.sell_price);
            mTradeVolume.setHint(R.string.sell_volume);
            mTradeButton.setText(R.string.sell_out_right_now);
        }

        mStockNameCode.addTextChangedListener(mStockNameWatcher);
        mTradePrice.addTextChangedListener(mPriceWatcher);
        mTradeVolume.addTextChangedListener(mVolumeWatcher);
        mTradeVolume.setOnPlusMinusClickListener(new PlusMinusEditText.OnPlusMinusClickListener() {
            @Override
            public void onPlusClick() {
                mFullPosition.setSelected(false);
                mHalfPosition.setSelected(false);
                mQuarterPosition.setSelected(false);
            }

            @Override
            public void onMinusClick() {
                mFullPosition.setSelected(false);
                mHalfPosition.setSelected(false);
                mQuarterPosition.setSelected(false);
            }
        });

        updateStock(mStock);
    }

    private void setBackground4FiveMarketData() {
        getView().findViewById(R.id.ask5).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.ask4).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.ask3).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.ask2).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.ask1).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.bid1).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.bid2).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.bid3).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.bid4).setBackgroundResource(R.drawable.btn_transparent);
        getView().findViewById(R.id.bid5).setBackgroundResource(R.drawable.btn_transparent);
    }

    public void updateStock(Stock stock) {
        mStockNameCode.removeTextChangedListener(mStockNameWatcher);

        mStock = stock;

        if (mStock != null) {
            mStockNameCode.setText(mStock.getVarietyName() + " " + mStock.getVarietyCode());
            mStockNameCode.setSelection(mStockNameCode.getText().length());
            KeyBoardUtils.closeKeyboard(mStockNameCode);

        } else {
            mStockNameCode.setText(null);
            clearMarketData();
        }

        // keep
        Bundle args = new Bundle();
        args.putParcelable("var", mStock);
        args.putInt("tradeType", mTradeType);
        setArguments(args);
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

    private void clearMarketData() {
        mTradePrice.setText(null);
        mTradeVolume.setText(null);

        mLimitUp.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_up_x), NULL_VALUE,
                ContextCompat.getColor(getActivity(), R.color.redPrimary)));
        mLimitDown.setText(StrUtil.mergeTextWithColor(getString(R.string.limit_down_x), NULL_VALUE,
                ContextCompat.getColor(getActivity(), R.color.greenAssist)));

        resetPositionSelectors();

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


    @Override
    public void onDestroyView() {
        mStockNameCode.removeTextChangedListener(mStockNameWatcher);
        mTradePrice.removeTextChangedListener(mPriceWatcher);
        mTradeVolume.removeTextChangedListener(mVolumeWatcher);
        mTradeVolume.removeCallbacks(mRunnable);

        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ask5, R.id.ask4, R.id.ask3, R.id.ask2, R.id.ask1,
            R.id.bid1, R.id.bid2, R.id.bid3, R.id.bid4, R.id.bid5,
            R.id.fullPosition, R.id.halfPosition, R.id.quarterPosition,
            R.id.tradeButton, R.id.stockNameCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stockNameCode:
                mStockNameCode.removeTextChangedListener(mStockNameWatcher);
                mStockNameCode.addTextChangedListener(mStockNameWatcher);
                break;

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
                resetPositionSelectors();
                mFullPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;
            case R.id.halfPosition:
                resetPositionSelectors();
                mHalfPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;
            case R.id.quarterPosition:
                resetPositionSelectors();
                mQuarterPosition.setSelected(true);
                onPositionSelectorsSelected();
                break;

            case R.id.tradeButton:
                doTrade();
                break;
        }
    }

    private void doTrade() {
        StockUser stockUser = LocalUser.getUser().getStockUser();
        int deputeType = mTradeType == StockTradeOperateActivity.TRADE_TYPE_BUY ?
                StockOrder.DEPUTE_TYPE_ENTRUST_BUY : StockOrder.DEPUTE_TYPE_ENTRUST_SELL;
        long volume = Long.parseLong(mTradeVolume.getText());
        double price = Double.parseDouble(mTradePrice.getText());
        String uuid = UUID.randomUUID().toString().replace("-", "");

        final StockOrder stockOrder = new StockOrder.Builder()
                .positionType(stockUser.getType())
                .userAccount(stockUser.getAccount())
                .activityCode(stockUser.getActivityCode())
                .varietyCode(mStock.getVarietyCode())
                .varietyName(mStock.getVarietyName())
                .quantity(volume)
                .price(price)
                .deputeType(deputeType)
                .signId(uuid).build();

        TradeConfirmDialog.with(getActivity(), stockOrder)
                .setOnConfirmClickListener(new TradeConfirmDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(final SmartDialog dialog) {
                        makeStockOrder(stockOrder);
                    }
                }).show();
    }

    private void makeStockOrder(final StockOrder stockOrder) {
        Client.markStockOrder(stockOrder).setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        String successMsg;
                        if (stockOrder.getDeputeType() == StockOrder.DEPUTE_TYPE_ENTRUST_BUY) {
                            successMsg = getString(R.string.buy_success_check_entrust);
                        } else {
                            successMsg = getString(R.string.sell_success_check_entrust);
                        }
                        SmartDialog.single(getActivity(), successMsg)
                                .setTitle(R.string.tips)
                                .setNegative(R.string.stay_current_page)
                                .setPositive(R.string.check_entrust, new SmartDialog.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        if (mOnPostTradeSuccessListener != null) {
                                            mOnPostTradeSuccessListener.onCheckOrderClick();
                                        }
                                    }
                                }).show();

                        resetPositionSelectors();

                        if (mOnPostTradeSuccessListener != null) {
                            mOnPostTradeSuccessListener.onPostTradeSuccess();
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        SmartDialog.single(getActivity(), failedResp.getMsg())
                                .setTitle(R.string.tips)
                                .setNegativeVisible(View.GONE)
                                .show();
                    }
                }).fire();
    }

    private void onPositionSelectorsSelected() {
        if (mFullPosition.isSelected()) {
            int selectShares = mMaxTradeVolume;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            }
        } else if (mHalfPosition.isSelected()) {
            int selectShares = mMaxTradeVolume / 2;
            selectShares -= selectShares % 100;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            }
        } else if (mQuarterPosition.isSelected()) {
            int selectShares = mMaxTradeVolume / 4;
            selectShares -= selectShares % 100;
            if (selectShares >= 100) {
                mTradeVolume.setText(String.valueOf(selectShares));
            }
        }
    }

    public void resetPositionSelectors() {
        mFullPosition.setSelected(false);
        mHalfPosition.setSelected(false);
        mQuarterPosition.setSelected(false);
        mTradeVolume.setText(null);
    }
}
