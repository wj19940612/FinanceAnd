package com.sbai.finance.activity.arena;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineReviewActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.KlineBattleResult;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.KLineTopResultView;
import com.sbai.finance.view.KlineBottomResultView;
import com.sbai.finance.view.NoScrollListView;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KLineResultActivity extends BaseActivity {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.top)
    KLineTopResultView mTop;
    @BindView(R.id.listView)
    NoScrollListView mListView;
    @BindView(R.id.moreOneBtn)
    TextView mMoreOneBtn;
    @BindView(R.id.topType)
    LinearLayout mTopType;
    @BindView(R.id.bottomView)
    KlineBottomResultView mBottomView;

    private ResultAdapter mResultAdapter;
    private String mGoinType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_result);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData();
        initView();
        refreshData();
    }

    private void initData() {
        mGoinType = getIntent().getStringExtra(ExtraKeys.GUESS_TYPE);
    }

    private void initView() {
        mResultAdapter = new ResultAdapter(this);
        mResultAdapter.setOnMoreClickListener(new ResultAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick(KlineBattleResult.Ranking ranking) {
                Launcher.with(KLineResultActivity.this, BattleKlineReviewActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, mGoinType)
                        .putExtra(ExtraKeys.USER_ID, ranking.getUserId())
                        .putExtra(ExtraKeys.USER_NAME, ranking.getUserName())
                        .putExtra(ExtraKeys.User_Portrait, ranking.getUserPortrait())
                        .execute();
            }
        });
        mListView.setAdapter(mResultAdapter);
    }

    private void refreshData() {
        Client.requestKlineBattleResult().setTag(TAG).setCallback(new Callback2D<Resp<KlineBattleResult>, KlineBattleResult>() {
            @Override
            protected void onRespSuccessData(KlineBattleResult data) {
                updateData(data);
            }
        }).fireFree();
    }

    private void updateData(KlineBattleResult data) {
        //搜索自己的数据
        if (data.getRanking() != null && data.getRanking().size() != 0 && LocalUser.getUser().getUserInfo() != null) {
            KlineBattleResult.Ranking meResult = null;
            for (KlineBattleResult.Ranking ranking : data.getRanking()) {
                if (ranking.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                    meResult = ranking;
                    break;
                }
            }

            if (meResult == null) return;
            mTop.setRankInfo(mGoinType, meResult.getSort());
            mBottomView.updateStock(data.getBattleStockName(), data.getBattleStockCode(), data.getBattleStockStartTime(), data.getBattleStockEndTime(), data.getRise());
            mResultAdapter.clear();
            mResultAdapter.addAll(data.getRanking());
        }
    }

    @OnClick(R.id.moreOneBtn)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moreOneBtn:
                Intent intent = new Intent(this, BattleKlineActivity.class);
                intent.putExtra(ExtraKeys.GUESS_TYPE, mGoinType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

    static class ResultAdapter extends ArrayAdapter<KlineBattleResult.Ranking> {
        interface OnMoreClickListener {
            public void onMoreClick(KlineBattleResult.Ranking userId);
        }

        private Context mContext;
        private OnMoreClickListener mOnMoreClickListener;

        public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
            mOnMoreClickListener = onMoreClickListener;
        }

        public ResultAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kline_result_rank, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, position, getCount(), mOnMoreClickListener);
            return convertView;
        }


        static class ViewHolder {
            @BindView(R.id.content)
            RelativeLayout mContent;
            @BindView(R.id.rank)
            TextView mRank;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.upDown)
            TextView mUpDown;
            @BindView(R.id.reward)
            TextView mReward;
            @BindView(R.id.moreBtn)
            ImageView mMoreBtn;
            @BindView(R.id.viewStub)
            View mViewStub;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final KlineBattleResult.Ranking data, Context context, int position, int count, final OnMoreClickListener onMoreClickListener) {
                if (data == null) return;
                mRank.setText(String.valueOf(position + 1));
                GlideApp.with(context).load(data.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop().into(mAvatar);
                mName.setText(data.getUserName());
                if (data.getProfit() >= 0) {
                    mUpDown.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                } else {
                    mUpDown.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                }
                mUpDown.setText(String.format("%.2f", data.getProfit()) + "%");
                mReward.setText(String.valueOf(data.getMoney()) + "元宝");
                if (position == count - 1) {
                    mViewStub.setVisibility(View.GONE);
                } else {
                    mViewStub.setVisibility(View.VISIBLE);
                }
                mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onMoreClickListener != null) {
                            onMoreClickListener.onMoreClick(data);
                        }
                    }
                });
            }
        }
    }
}
