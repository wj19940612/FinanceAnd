package com.sbai.finance.activity.future;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureBattleDetailFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.BattleFloatView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.Variety.FUTURE_FOREIGN;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class FutureBattleActivity extends BaseActivity {

    @BindView(R.id.futureArea)
    LinearLayout mFutureArea;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    private Variety mVariety;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);

        initData();

        requestVarietyList();
    }

    private void initData() {

    }

    public void requestVarietyList() {
        Client.getVarietyList(Variety.VAR_FUTURE, 0, FUTURE_FOREIGN).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mVariety = data.get(0);
                        initViews();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                    }

                }).fireSync();
    }

    private void initViews() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, FutureBattleDetailFragment.newInstance(111))
                .commit();

        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .setMyAvatar("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3112858211,2849902352&fm=58")
                .setMyName("松柏牌面哥")
                .setUserAvatar("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3112858211,2849902352&fm=58")
                .setUserName("狗海天")
                .setDepositAndTime(200, 2, 1, "15分钟")
                .setProgress(15.00, 70.00, false)
                .setPraise(100, 999);
    }
}
