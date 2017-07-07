package com.sbai.finance.activity.battle;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;


/**
 * Created by linrongfang on 2017/7/7.
 */

public class FutureBattleActivity extends BaseActivity {
    
    
    private Battle mBattle;

    private int mBattleId;
    private String mBatchCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        initData();


    }

    private void initData() {
        mBattleId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mBatchCode = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);

        requestLastBattleInfo(mBattleId, mBatchCode);
    }

    private void requestLastBattleInfo(int battleId, String batchCode) {
        Client.getBattleInfo(battleId, batchCode).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        if (data != null) {
                            mBattle = data;
                            if (data.isBattleStop()) {
                                // TODO: 2017/7/7 显示对战详情 
                            } else {
                                // TODO: 2017/7/7 区分对战和观战  
                            }
                            
                        }
                    }
                }).fire();
    }
}
