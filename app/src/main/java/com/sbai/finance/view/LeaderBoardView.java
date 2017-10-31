package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.model.leaderboard.LeaderThreeRank;
import com.sbai.finance.utils.Network;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.model.leaderboard.LeaderThreeRank.INGOT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.PROFIT;
import static com.sbai.finance.model.leaderboard.LeaderThreeRank.SAVANT;

/**
 * Created by Administrator on 2017\10\27 0027.
 */

public class LeaderBoardView extends LinearLayout {
    @BindView(R.id.leftHead)
    ImageView mLeftHead;
    @BindView(R.id.leftMobaiBtn)
    ImageView mLeftMobaiBtn;
    @BindView(R.id.leftLookRankBtn)
    RelativeLayout mLeftLookRankBtn;
    @BindView(R.id.centerHead)
    ImageView mCenterHead;
    @BindView(R.id.centerMobaiBtn)
    ImageView mCenterMobaiBtn;
    @BindView(R.id.centerLookRankBtn)
    RelativeLayout mCenterLookRankBtn;
    @BindView(R.id.rightHead)
    ImageView mRightHead;
    @BindView(R.id.rightMobaiBtn)
    ImageView mRightMobaiBtn;
    @BindView(R.id.rightLookRankBtn)
    RelativeLayout mRightLookRankBtn;

    private Context mContext;

    private MobaiListener mMobaiRankListener;

    public interface MobaiListener {
        public void mobai(String rankType, LeaderThreeRank dataBean);
    }

    public void setMobaiListener(MobaiListener mobaiListener) {
        mMobaiRankListener = mobaiListener;
    }

    private LookRankListener mLookRankListener;

    public interface LookRankListener {
        public void lookRank(String rankType);
    }

    public void setLookRankListener(LookRankListener lookRankListener) {
        mLookRankListener = lookRankListener;
    }

    public LeaderBoardView(Context context) {
        this(context, null);
    }

    public LeaderBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeaderBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_leader_boards, this, true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.leftLookRankBtn, R.id.centerLookRankBtn, R.id.rightLookRankBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.leftLookRankBtn:
                if (mLookRankListener != null) {
                    mLookRankListener.lookRank(INGOT);
                }
                break;
            case R.id.centerLookRankBtn:
                if (mLookRankListener != null) {
                    mLookRankListener.lookRank(PROFIT);
                }
                break;
            case R.id.rightLookRankBtn:
                if (mLookRankListener != null) {
                    mLookRankListener.lookRank(SAVANT);
                }
                break;
        }
    }


    //土豪榜更新
    public void updateLeaderBoardData(List<LeaderThreeRank> data) {
        if (data == null) {
            return;
        }
        ImageView mobaiBtn = null;
        ImageView headView = null;
        for (final LeaderThreeRank dataBean : data) {
            if (dataBean.getType().equals(INGOT)) {
                mobaiBtn = mLeftMobaiBtn;
                headView = mLeftHead;
            } else if (dataBean.getType().equals(PROFIT)) {
                mobaiBtn = mCenterMobaiBtn;
                headView = mCenterHead;
            } else if (dataBean.getType().equals(SAVANT)) {
                mobaiBtn = mRightMobaiBtn;
                headView = mRightHead;
            }
            GlideApp.with(mContext)
                    .load(dataBean.getUser().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .circleCrop()
                    .into(headView);
            if (dataBean.isWorship()) {
                mobaiBtn.setEnabled(false);
            } else {
                mobaiBtn.setEnabled(true);
            }
            final ImageView finalMobaiBtn = mobaiBtn;
            finalMobaiBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LocalUser.getUser().isLogin() && Network.isNetworkAvailable()) {
                        finalMobaiBtn.setEnabled(false);
                    }
                    if(mMobaiRankListener!=null) {
                        mMobaiRankListener.mobai(dataBean.getType(), dataBean);
                    }
                }
            });
        }
    }
}
