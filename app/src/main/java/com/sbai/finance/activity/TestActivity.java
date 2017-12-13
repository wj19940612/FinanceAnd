package com.sbai.finance.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.chart.ColorCfg;
import com.sbai.chart.KlineChart;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.R;
import com.sbai.finance.model.stock.StockKlineData;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.view.game.GameKline;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Modified by john on 12/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TestActivity extends BaseActivity {

    @BindView(R.id.gameKline)
    GameKline mGameKline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(7);
        settings2.setNumberScale(2);
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings2.setColorCfg(new ColorCfg()
                .put(ColorCfg.BASE_LINE, "#2a2a2a"));
        settings2.setIndexesEnable(true);
        settings2.setIndexesBaseLines(2);
        mGameKline.setDayLine(true);
        mGameKline.setSettings(settings2);

        requestKlineDataAndSet(StockKlineData.PERIOD_DAY);
    }

    private void requestKlineDataAndSet(int type) {
        Client.getStockKlineData("000001", type)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new StockCallback<StockResp, List<StockKlineData>>() {
                    @Override
                    public void onDataMsg(List<StockKlineData> result, StockResp.Msg msg) {
                        List<KlineViewData> dataList = new ArrayList<KlineViewData>(result);
                        List<GameKline.GameKlineData> data = new ArrayList<>();
                        for (int i = 0; i < dataList.size(); i++) {
                            GameKline.GameKlineData klineData = new GameKline.GameKlineData(dataList.get(i));
                            if (i == dataList.size() - 25) {
                                klineData.setMark(GameKline.GameKlineData.MARK_BUY);
                            } else if (i == dataList.size() - 15) {
                                klineData.setMark(GameKline.GameKlineData.MARK_SELL);
                            } else if (i == dataList.size() - 10) {
                                klineData.setMark(GameKline.GameKlineData.MARK_BUY);

                            } else if (i == dataList.size() - 2) {
                                klineData.setMark(GameKline.GameKlineData.MARK_SELL);
                                klineData.setPositions(-1);
                            }
                            data.add(klineData);
                        }
                        mGameKline.setKlineDataList(data);
                    }
                }).fire();
    }
}
