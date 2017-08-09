package com.sbai.finance.activity.leaderboard;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_boards);
        ButterKnife.bind(this);
        initView();
        requestBoardData();
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
                    if (LocalUser.getUser().isLogin()) {
                        if (item.getCurr() != null) {
                            mIngotBoard.setText(getString(R.string.your_rank, item.getCurr().getNo()));
                        } else {
                            mIngotBoard.setText(getString(R.string.you_no_enter_leader_board));
                        }
                    }
                    break;
                case LeaderBoardRank.PROFIT:
                    mProfitsImages.setImages(images);
                    if (LocalUser.getUser().isLogin()) {
                        if (item.getCurr() != null) {
                            mProfitBoard.setText(getString(R.string.your_rank, item.getCurr().getNo()));
                        } else {
                            mProfitBoard.setText(getString(R.string.you_no_enter_leader_board));
                        }
                    }
                    break;
                case LeaderBoardRank.SAVANT:
                    mSavantImages.setImages(images);
                    if (LocalUser.getUser().isLogin()) {
                        if (item.getCurr() != null) {
                            mSavantBoard.setText(getString(R.string.your_rank, item.getCurr().getNo()));
                        } else {
                            mSavantBoard.setText(getString(R.string.you_no_enter_leader_board));
                        }
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.ingotBoardArea, R.id.profitBoardArea, R.id.savantBoardArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ingotBoardArea:
                Launcher.with(getActivity(), IngotOrSavantLeaderBoardActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, LeaderBoardRank.INGOT)
                        .execute();
                break;
            case R.id.profitBoardArea:
                Launcher.with(getActivity(), ProfitBoardListActivity.class)
                        .execute();
                break;
            case R.id.savantBoardArea:
                Launcher.with(getActivity(), IngotOrSavantLeaderBoardActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, LeaderBoardRank.SAVANT)
                        .execute();
                break;
        }
    }
}
