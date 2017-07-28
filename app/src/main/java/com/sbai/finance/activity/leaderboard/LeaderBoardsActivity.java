package com.sbai.finance.activity.leaderboard;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeaderBoardsActivity extends BaseActivity {

    @BindView(R.id.ingotBoard)
    LinearLayout mIngotBoard;
    @BindView(R.id.profitBoard)
    LinearLayout mProfitBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_boards);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ingotBoard, R.id.profitBoard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ingotBoard:
                Launcher.with(getActivity(), LeaderBoardDetailActivity.class).execute();
                break;
            case R.id.profitBoard:
                break;
        }
    }
}
