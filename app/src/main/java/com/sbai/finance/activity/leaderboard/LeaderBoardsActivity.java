package com.sbai.finance.activity.leaderboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.ImageListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 排行榜
 */

public class LeaderBoardsActivity extends BaseActivity {


    @BindView(R.id.ingotBoard)
    TextView mIngotBoard;
    @BindView(R.id.ingotImages)
    ImageListView mIngotImages;
    @BindView(R.id.ingotBoardArea)
    LinearLayout mIngotBoardArea;
    @BindView(R.id.profitBoard)
    TextView mProfitBoard;
    @BindView(R.id.profitsImages)
    ImageListView mProfitsImages;
    @BindView(R.id.profitBoardArea)
    LinearLayout mProfitBoardArea;
    @BindView(R.id.savantBoard)
    TextView mSavantBoard;
    @BindView(R.id.savantImages)
    ImageListView mSavantImages;
    @BindView(R.id.savantBoardArea)
    LinearLayout mSavantBoardArea;
    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestBoardData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_boards);
        ButterKnife.bind(this);
        initView();
        initLoginReceiver();
        requestBoardData();
    }

    private void initLoginReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginReceiver, intentFilter);
    }

    private void initView() {
        mIngotBoardArea.setBackground(createDrawable(new int[]{Color.parseColor("#F6D75E"), Color.parseColor("#FDB168")}));
        mProfitBoardArea.setBackground(createDrawable(new int[]{Color.parseColor("#A485FF"), Color.parseColor("#C05DD8")}));
        mSavantBoardArea.setBackground(createDrawable(new int[]{Color.parseColor("#64A0FE"), Color.parseColor("#995BF4")}));
    }

    private Drawable createDrawable(int[] colors) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradient.setCornerRadius(Display.dp2Px(8, getActivity().getResources()));
        return gradient;
    }

    private void requestBoardData() {
        Client.getLeaderLists().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<LeaderBoardRank>>, List<LeaderBoardRank>>() {
                    @Override
                    protected void onRespSuccessData(List<LeaderBoardRank> data) {
                        updateBoardData(data);
                    }
                }).fireFree();
    }

    private void updateBoardData(List<LeaderBoardRank> data) {
        for (LeaderBoardRank item : data) {
            List<String> images = new ArrayList<>();
            for (LeaderBoardRank.DataBean dataBean : item.getData()) {
                if (dataBean.getUser() != null) {
                    images.add(dataBean.getUser().getUserPortrait());
                }
            }
            switch (item.getType()) {
                case LeaderBoardRank.INGOT:
                    mIngotImages.setImages(images);
                    updateSelfLeaderInfo(mIngotBoard, item);
                    break;
                case LeaderBoardRank.PROFIT:
                    mProfitsImages.setImages(images);
                    updateSelfLeaderInfo(mProfitBoard, item);
                    break;
                case LeaderBoardRank.SAVANT:
                    mSavantImages.setImages(images);
                    updateSelfLeaderInfo(mSavantBoard, item);
                    break;
            }
        }
    }

    private void updateSelfLeaderInfo(TextView textView, LeaderBoardRank item) {
        if (LocalUser.getUser().isLogin()) {
            if (item.getCurr() == null || item.getCurr().getNo() <= 0) {
                textView.setText(getString(R.string.you_no_enter_leader_board));
            } else {
                textView.setText(getString(R.string.your_rank, item.getCurr().getNo()));
            }
        } else {
            textView.setText(getString(R.string.click_see_your_rank));
        }
    }

    @OnClick({R.id.ingotBoardArea, R.id.profitBoardArea, R.id.savantBoardArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ingotBoardArea:
                umengEventCount(UmengCountEventId.DISCOVERY_INGOT_BOARD);
                Launcher.with(getActivity(), IngotOrSavantLeaderBoardActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, LeaderBoardRank.INGOT)
                        .execute();
                break;
            case R.id.profitBoardArea:
                umengEventCount(UmengCountEventId.DISCOVERY_PROFIT_BOARD);
                Launcher.with(getActivity(), ProfitBoardListActivity.class)
                        .execute();
                break;
            case R.id.savantBoardArea:
                umengEventCount(UmengCountEventId.DISCOVERY_SAVANT);
                Launcher.with(getActivity(), IngotOrSavantLeaderBoardActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, LeaderBoardRank.SAVANT)
                        .execute();
                break;
            case R.id.ingotBoard:
            case R.id.profitBoard:
            case R.id.savantBoard:
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    if (textView.getText().toString().equalsIgnoreCase(getString(R.string.click_see_your_rank))) {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mLoginReceiver);
    }
}
