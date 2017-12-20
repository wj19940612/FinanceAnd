package com.sbai.finance.activity.arena.klinebattle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.finance.model.klinebattle.BattleKlineData;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * k线单人练习
 */

public class SingleKlineExerciseActivity extends BattleKlineDetailActivity {
    private List<BattleKlineData> mBattleUserMarkList;
    private int mPositionIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestKlineData();
    }

    private void requestKlineData() {
        Client.getSingleKlineBattleData().setTag(TAG)
                .setCallback(new Callback2D<Resp<BattleKline>, BattleKline>() {
                    @Override
                    protected void onRespSuccessData(BattleKline data) {
                        if (data != null) {
                            updateBattleData(data);
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    @Override
    protected void updateBattleData(BattleKline data) {
        if (data == null) return;
        mBattleKline = data;
        mBattleUserMarkList = mBattleKline.getUserMarkList();
        if (mBattleUserMarkList != null && mBattleUserMarkList.size() >= 40) {
            mRemainKlineAmount = mBattleKline.getUserMarkList().size() - 40;
            List<BattleKlineData> subList = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                subList.add(mBattleUserMarkList.get(i));
            }
            mKlineView.initKlineDataList(subList);
        }
        updateCountDownTime();
        updateRemainKlineAmount();
    }

    @Override
    protected void buyOperate() {
        updateMyLastOperateData(BattleKline.BUY);
    }

    @Override
    protected void clearOperate() {
        updateMyLastOperateData(BattleKline.SELL);
    }

    @Override
    protected void passOperate() {
        updateMyLastOperateData(BattleKline.PASS);
    }

    private void updateMyLastOperateData(String type) {
        if (mBattleUserMarkList == null) return;
        if (mCurrentIndex + 1 < mBattleUserMarkList.size()) {
            if (type.equalsIgnoreCase(BattleKline.BUY)) {
                mPositionIndex = mCurrentIndex;
            }
            if (mCurrentIndex == mBattleUserMarkList.size() - 2) {
                mKlineView.setLastInvisibleData(mBattleUserMarkList.get(mBattleUserMarkList.size() - 1));
                battleFinish();
                return;
            }
            updateOperateView(type);
            updateLastProfit(type);
            updateNextKlineView(mBattleUserMarkList.get(mCurrentIndex++));
        }
    }

    private void updateLastProfit(String type) {
        BattleKlineData positionKlineData = null;
        BattleKlineData nextKlineData = mBattleUserMarkList.get(mCurrentIndex + 1);
        if (type.equalsIgnoreCase(BattleKline.PASS)) {
            if (mHasPosition) {
                positionKlineData = mBattleUserMarkList.get(mPositionIndex);
            }
        } else {
            positionKlineData = mBattleUserMarkList.get(mCurrentIndex);
        }
        double positionProfit = (nextKlineData.getClosePrice() - positionKlineData.getClosePrice()) / positionKlineData.getClosePrice();
        mOperateView.setTotalProfit(positionProfit - mOperateView.getLastPosition() + mOperateView.getTotalProfit());
        if (mHasPosition) {
            nextKlineData.setPositions(positionProfit);
            mOperateView.setPositionProfit(positionProfit);
        }
    }
}
