package com.sbai.finance.activity.future;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.fragment.future.FutureBattleDetailFragment;
import com.sbai.finance.fragment.future.FutureBattleFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BattleButtons;
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

    private FutureBattleFragment mFutureBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;
    private StartMatchDialogFragment mStartMatchDialogFragment;

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
          mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
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
        mFutureBattleFragment = FutureBattleFragment.newInstance(mVariety);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleFragment)
                .commit();


        mBattleView.setMode(BattleFloatView.Mode.VISITOR)
                .setCreateAvatar("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3112858211,2849902352&fm=58")
                .setCreateName("松柏牌面哥")
                .setAgainstAvatar("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3112858211,2849902352&fm=58")
                .setAgainstName("狗海天")
                .setDeposit(200, 2)
                .setDeadline(2, 1000)
                .setProgress(30.00, 70.00, false)
                .setOnPraiseListener(new BattleFloatView.OnPraiseListener() {
                    @Override
                    public void addMyPraiseCount() {
                    }

                    @Override
                    public void addUserPraiseCount() {
                    }
                })
                .setPraise(100, 999);

        new Handler(){}.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFutureBattleFragment.setOnBattleButtonClickListener(new BattleButtons.OnViewClickListener() {
                    @Override
                    public void onInviteButtonClick() {

                    }

                    @Override
                    public void onMatchButtonClick() {
                        if (mStartMatchDialogFragment == null) {
                            mStartMatchDialogFragment = StartMatchDialogFragment
                                    .newInstance()
                                    .setOnCancelListener(new StartMatchDialogFragment.OnCancelListener() {
                                        @Override
                                        public void onCancel() {
                                            mStartMatchDialogFragment.dismiss();
                                        }
                                    });
                        }
                        mStartMatchDialogFragment.show(getSupportFragmentManager());
                    }

                    @Override
                    public void onCancelButtonClick() {

                    }
                });
            }
        },100);
    }
}
