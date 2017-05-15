package com.sbai.finance.activity.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.market.FutureData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ValidationWatcher;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishOpinionActivity extends BaseActivity {

    public static final String REFRESH_POINT = "refresh_point";

    @BindView(R.id.opinionType)
    ImageView mOpinionType;
    @BindView(R.id.opinionContent)
    EditText mOpinionContent;
    @BindView(R.id.submitButton)
    Button mSubmitButton;

    Prediction mPredict;
    Variety mVariety;
    FutureData mFutureData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_opinion);
        ButterKnife.bind(this);

        initData();
        initViews();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Netty.get().subscribe(Netty.REQ_SUB, mVariety.getContractsCode());
        Netty.get().addHandler(mNettyHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Netty.get().subscribe(Netty.REQ_UNSUB, mVariety.getContractsCode());
        Netty.get().removeHandler(mNettyHandler);
    }

    private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
        @Override
        public void onReceiveData(Resp<FutureData> data) {
            if (data.getCode() == Netty.REQ_QUOTA) {
                mFutureData = data.getData();
            }
        }
    };

    private void initData() {
        mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        mPredict = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD_1);
        mFutureData = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD_2);

        Log.d(TAG, "mVariety: " + mVariety.toString());
    }

    private void initViews() {
        mOpinionContent.addTextChangedListener(mOptionContentWatcher);
        if (mPredict.getDirection() == Prediction.DIRECTION_LONG) {
            mOpinionType.setImageResource(R.drawable.ic_opinion_up);
        } else {
            mOpinionType.setImageResource(R.drawable.ic_opinion_down);
        }
    }

    private ValidationWatcher mOptionContentWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                mSubmitButton.setEnabled(true);
            } else {
                mSubmitButton.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.submitButton)
    public void onClick(View view) {
        final String content = mOpinionContent.getText().toString().trim();
        final String calcuId = mPredict.isCalculate() ? String.valueOf(mPredict.getCalcuId()) : null;


        //股票的时候是轮训
        if (!mVariety.ifProductIsStock()) {
            //最新价
            String lastPriceStr = null;
            //	涨幅
            String risePriceStr = null;
            //涨幅百分比
            String risePercentStr = null;
            if (mFutureData != null) {
                double lastPrice = mFutureData.getLastPrice();
                double risePrice = FinanceUtil.subtraction(lastPrice, mFutureData.getPreSetPrice()).doubleValue();
                double priceChangePercent = FinanceUtil.divide(risePrice, mFutureData.getPreSetPrice(), 4)
                        .multiply(new BigDecimal(100)).doubleValue();

                lastPriceStr = FinanceUtil.formatWithScale(lastPrice, mVariety.getPriceScale());
                risePriceStr = FinanceUtil.formatWithScale(risePrice, mVariety.getPriceScale());
                risePercentStr = FinanceUtil.formatWithScale(priceChangePercent) + "%";
                if (risePrice >= 0) {
                    risePriceStr = "+" + risePriceStr;
                    risePercentStr = "+" + risePercentStr;
                }
            }
            submitViewpoint(content, calcuId, lastPriceStr, risePriceStr, risePercentStr);
        } else {
            // TODO: 2017/5/12  获取股票的实时行情 未建对应的moedl  
//            Client.getStockRealTimeQuotes(mVariety.getVarietyType())
//                    .setTag(TAG)
//                    .setCallback(new StockCallback<StockResp, StockData>() {
//                        @Override
//                        public void onDataMsg(StockData result, StockResp.Msg msg) {
//                            Log.d(TAG, "onDataMsg: " + result.toString());
//                            String lastPriceStr = null;
//                            //	涨幅
//                            String risePriceStr = null;
//                            //涨幅百分比
//                            String risePercentStr = null;
//                            lastPriceStr = String.valueOf(result.getLast_price());
//                            risePriceStr = String.valueOf(result.getRise_price());
//                            risePercentStr = String.valueOf(result.getRise_pre());
//                            submitViewpoint(content, calcuId, lastPriceStr, risePriceStr, risePercentStr);
//                        }
//                    })
//                    .fire();
        }


    }

    private void submitViewpoint(String content, String calcuId, String lastPriceStr, String risePriceStr, String risePercentStr) {
        Client.publishPoint(mVariety.getBigVarietyTypeCode(), calcuId,
                content, mPredict.getDirection(),
                lastPriceStr, risePriceStr, risePercentStr,
                mVariety.getVarietyId(), mVariety.getVarietyType())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            Intent intent = new Intent(REFRESH_POINT);
                            LocalBroadcastManager.getInstance(PublishOpinionActivity.this)
                                    .sendBroadcast(intent);
                            finish();
                        }
                    }
                }).fire();
    }
}
