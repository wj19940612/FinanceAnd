package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderThreeRank;
import com.sbai.finance.utils.Network;
import com.sbai.glide.GlideApp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    Button mLeftMobaiBtn;
    @BindView(R.id.leftLookRankBtn)
    RelativeLayout mLeftLookRankBtn;
    @BindView(R.id.centerHead)
    ImageView mCenterHead;
    @BindView(R.id.centerMobaiBtn)
    Button mCenterMobaiBtn;
    @BindView(R.id.centerLookRankBtn)
    RelativeLayout mCenterLookRankBtn;
    @BindView(R.id.rightHead)
    ImageView mRightHead;
    @BindView(R.id.rightMobaiBtn)
    Button mRightMobaiBtn;
    @BindView(R.id.rightLookRankBtn)
    RelativeLayout mRightLookRankBtn;
    @BindView(R.id.leftGoneText)
    TextView mLeftGoneText;
    @BindView(R.id.centerGoneText)
    TextView mCenterGoneText;
    @BindView(R.id.rightGoneText)
    TextView mRightGoneText;
    @BindView(R.id.leftTopIcon)
    ImageView mLeftTopIcon;
    @BindView(R.id.centerTopIcon)
    ImageView mCenterTopIcon;
    @BindView(R.id.rightTopIcon)
    ImageView mRightTopIcon;
    @BindView(R.id.leftRL)
    RelativeLayout mLeftRL;
    @BindView(R.id.centerRL)
    RelativeLayout mCenterRL;
    @BindView(R.id.rightRL)
    RelativeLayout mRightRL;

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

    @OnClick({R.id.leftRL, R.id.centerRL, R.id.rightRL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.leftRL:
                if (mLookRankListener != null) {
                    mLookRankListener.lookRank(INGOT);
                }
                break;
            case R.id.centerRL:
                if (mLookRankListener != null) {
                    mLookRankListener.lookRank(PROFIT);
                }
                break;
            case R.id.rightRL:
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
        Map<String, LeaderThreeRank> haveDataMap = new HashMap<String, LeaderThreeRank>();
        haveDataMap.put(INGOT, null);
        haveDataMap.put(PROFIT, null);
        haveDataMap.put(SAVANT, null);
        ImageView topIcon = null;
        Button mobaiBtn = null;
        ImageView headView = null;
        TextView goneView = null;
        for (final LeaderThreeRank dataBean : data) {
            if (dataBean.getType().equals(INGOT)) {
                mobaiBtn = mLeftMobaiBtn;
                headView = mLeftHead;
                goneView = mLeftGoneText;
                topIcon = mLeftTopIcon;
                haveDataMap.put(INGOT, dataBean);
            } else if (dataBean.getType().equals(PROFIT)) {
                mobaiBtn = mCenterMobaiBtn;
                headView = mCenterHead;
                goneView = mCenterGoneText;
                topIcon = mCenterTopIcon;
                haveDataMap.put(PROFIT, dataBean);
            } else if (dataBean.getType().equals(SAVANT)) {
                mobaiBtn = mRightMobaiBtn;
                headView = mRightHead;
                goneView = mRightGoneText;
                topIcon = mRightTopIcon;
                haveDataMap.put(SAVANT, dataBean);
            }
            mobaiBtn.setVisibility(View.VISIBLE);
            headView.setVisibility(View.VISIBLE);
            topIcon.setVisibility(View.VISIBLE);
            goneView.setVisibility(View.GONE);
            GlideApp.with(mContext)
                    .load(dataBean.getUser().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .circleCrop()
                    .into(headView);
            if (LocalUser.getUser().isLogin() && LocalUser.getUser().getUserInfo() != null && LocalUser.getUser().getUserInfo().getId() == dataBean.getUserId()) {
                //没膜拜过但是 榜首是自己
                mobaiBtn.setEnabled(false);
                mobaiBtn.setBackgroundResource(R.drawable.btn_home_ash);
            }else if (!dataBean.isWorship()) {//已膜拜过
                mobaiBtn.setEnabled(false);
                mobaiBtn.setBackgroundResource(R.drawable.btn_home_noworship);
            }  else if (dataBean.isWorship()) {
                mobaiBtn.setEnabled(true);
                mobaiBtn.setBackgroundResource(R.drawable.btn_home_worship);
            }
            final Button finalMobaiBtn = mobaiBtn;
            finalMobaiBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LocalUser.getUser().isLogin() && Network.isNetworkAvailable()) {
                        finalMobaiBtn.setEnabled(false);
                        finalMobaiBtn.setBackgroundResource(R.drawable.btn_home_noworship);
                    }
                    if (mMobaiRankListener != null) {
                        mMobaiRankListener.mobai(dataBean.getType(), dataBean);
                    }
                }
            });
        }

        //不存在的榜首作空显示处理
        String type;
        LeaderThreeRank leaderThreeRank;
        Iterator<Map.Entry<String, LeaderThreeRank>> it = haveDataMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, LeaderThreeRank> entry = it.next();
            type = entry.getKey();
            leaderThreeRank = entry.getValue();
            if (leaderThreeRank == null) {
                if (type.equals(INGOT)) {
                    mobaiBtn = mLeftMobaiBtn;
                    headView = mLeftHead;
                    goneView = mLeftGoneText;
                    topIcon = mLeftTopIcon;
                } else if (type.equals(PROFIT)) {
                    mobaiBtn = mCenterMobaiBtn;
                    headView = mCenterHead;
                    goneView = mCenterGoneText;
                    topIcon = mCenterTopIcon;
                } else if (type.equals(SAVANT)) {
                    mobaiBtn = mRightMobaiBtn;
                    headView = mRightHead;
                    goneView = mRightGoneText;
                    topIcon = mRightTopIcon;
                }
                mobaiBtn.setVisibility(View.INVISIBLE);
                headView.setVisibility(View.INVISIBLE);
                topIcon.setVisibility(View.INVISIBLE);
                goneView.setVisibility(View.VISIBLE);
            }
        }
    }
}
