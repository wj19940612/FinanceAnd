package com.sbai.finance.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.glide.GlideApp;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 期货对战 页面底部对战双方信息 和 对战实时结果的vie
 */

public class BattleBottomBothInfoView extends RelativeLayout implements View.OnClickListener {

    @BindView(R.id.createAvatar)
    ImageView mCreateAvatar;
    @BindView(R.id.createName)
    TextView mCreateName;
    @BindView(R.id.createKo)
    ImageView mCreateKo;
    @BindView(R.id.againstAvatar)
    ImageView mAgainstAvatar;
    @BindView(R.id.againstName)
    TextView mAgainstName;
    @BindView(R.id.againstKo)
    ImageView mAgainstKo;

    @BindView(R.id.varietyName)
    TextView mVarietyName;
    @BindView(R.id.fighterDataArea)
    RelativeLayout mFighterDataArea;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.myProfit)
    TextView mMyProfit;
    @BindView(R.id.userProfit)
    TextView mUserProfit;
    @BindView(R.id.deposit)
    TextView mDeposit;
    @BindView(R.id.deadline)
    TextView mDeadline;

    @BindView(R.id.myPraise)
    TextView mMyPraise;
    @BindView(R.id.userPraise)
    TextView mUserPraise;
    @BindView(R.id.myPerspective)
    RelativeLayout mMyPerspective;
    @BindView(R.id.myPraiseButton)
    TextView mMyPraiseButton;
    @BindView(R.id.userPraiseButton)
    TextView mUserPraiseButton;
    @BindView(R.id.userPerspective)
    RelativeLayout mUserPerspective;

    private Mode mMode;
    private OnUserPraiseListener mOnUserPraiseListener;

    private Battle mBattle;

    public BattleBottomBothInfoView(Context context) {
        this(context, null);
    }

    public BattleBottomBothInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BattleBottomBothInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.battle_float_view, null, false);
        addView(view);
        ButterKnife.bind(this);
    }

    private void initViews() {
        if (mMode == Mode.HALL) {
            //游戏大厅显示标题
            mVarietyName.setVisibility(VISIBLE);
            resetMargin();
        } else {
            //非游戏大厅不显示标题
            mVarietyName.setVisibility(GONE);
            //参观者可以点赞
            if (mMode == Mode.VISITOR) {
                mMyPraiseButton.setOnClickListener(this);
                mUserPraiseButton.setOnClickListener(this);
            }

            mCreateAvatar.setOnClickListener(this);
            mAgainstAvatar.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myPraiseButton:
                if (mOnUserPraiseListener != null) {
                    mOnUserPraiseListener.onPraiseBattleInitiatorClick();
                }
                break;
            case R.id.userPraiseButton:
                if (mOnUserPraiseListener != null) {
                    mOnUserPraiseListener.onPraiseBattleAgainstClick();
                }
                break;
            case R.id.createAvatar:
            case R.id.againstAvatar:
                MobclickAgent.onEvent(getContext(), UmengCountEventId.BATTLE_USER_AVATAR);
                if (!LocalUser.getUser().isLogin() && !mBattle.isBattleOver()) {
                    Launcher.with(getContext(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void resetMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFighterDataArea.getLayoutParams();
        params.topMargin = dp2px(getContext(), 10);
        mFighterDataArea.setLayoutParams(params);
    }

    public BattleBottomBothInfoView initWithModel(Battle model) {
        this.mBattle = model;
        this.setCreateAvatar(model.getLaunchUserPortrait())
                .setCreateName(model.getLaunchUserName())
                .setAgainstAvatar(model.getAgainstUserPortrait())
                .setAgainstName(model.getAgainstUserName())
                .setDeposit(model.getReward(), model.getCoinType())
                .setPraise(model.getLaunchPraise(), model.getAgainstPraise())
                .setDeadline(model.getGameStatus(), model.getEndline());
        return this;
    }

    public BattleBottomBothInfoView setCreateName(String name) {
        mCreateName.setText(name);
        return this;
    }

    public BattleBottomBothInfoView setCreateAvatar(String url) {
        try {
            GlideApp.with(getContext())
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into(mCreateAvatar);
        } catch (IllegalArgumentException e) {

        }
        return this;
    }

    public BattleBottomBothInfoView setAgainstName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            mAgainstName.setText(getContext().getString(R.string.wait_to_join));
        } else {
            mAgainstName.setText(userName);
        }
        return this;
    }

    public BattleBottomBothInfoView setAgainstAvatar(String url) {
        try {
            GlideApp.with(getContext())
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_avatar_big)
                    .into(mAgainstAvatar);
        } catch (IllegalArgumentException e) {
        }
        return this;
    }

    public BattleBottomBothInfoView setVarietyName(String varietyName) {
        mVarietyName.setText(varietyName);
        return this;
    }

    /**
     * 显示对抗状态条
     *
     * @param myProfit      我的盈利状况
     * @param fighterProfit 对抗者盈利状况
     * @param isInviting    是否正在邀请中
     * @return
     */
    public BattleBottomBothInfoView setProgress(double myProfit, double fighterProfit, boolean isInviting) {
        String myFlag = "";
        String fighterFlag = "";
        if (isInviting) {
            mProgressBar.setProgress(0);
            mProgressBar.setSecondaryProgress(0);
        } else {
            //正正
            if ((myProfit > 0 && fighterProfit >= 0) || (myProfit >= 0 && fighterProfit > 0)) {
                int progress = (int) (myProfit * 100 / (myProfit + fighterProfit));
                mProgressBar.setProgress(progress);
            }
            //正负
            if (myProfit >= 0 && fighterProfit < 0) {
                mProgressBar.setProgress(100);
            }
            //负正
            if (myProfit < 0 && fighterProfit >= 0) {
                mProgressBar.setProgress(0);
            }
            //负负
            if (myProfit < 0 && fighterProfit < 0) {
                int progress = (int) (Math.abs(myProfit) * 100 / (Math.abs(myProfit) + Math.abs(fighterProfit)));
                mProgressBar.setProgress(100 - progress);
            }
            //都为0
            if (myProfit == 0 && fighterProfit == 0) {
                mProgressBar.setProgress(50);
            }

            mProgressBar.setSecondaryProgress(100);

            if (myProfit > 0) {
                myFlag = "+";
            }

            if (fighterProfit > 0) {
                fighterFlag = "+";
            }

            mMyProfit.setText(myFlag + FinanceUtil.formatWithScale(myProfit));
            mUserProfit.setText(fighterFlag + FinanceUtil.formatWithScale(fighterProfit));

        }
        return this;
    }

    /**
     * 显示对战信息
     *
     * @param reward   赏金
     * @param coinType 对战类型  2元宝 3积分
     * @return
     */
    public BattleBottomBothInfoView setDeposit(int reward, int coinType) {
        StringBuilder builder = new StringBuilder();
        builder.append(reward);
        builder.append(coinType == 2 ? "元宝" : "积分");
        mDeposit.setText(builder.toString());
        return this;
    }

    /**
     * 更新对战时间
     *
     * @param gameStatus 对战状态 0 取消 1发起  2对战中  3结束
     * @param endTime    对战剩余时长
     * @return
     */
    public BattleBottomBothInfoView setDeadline(int gameStatus, int endTime) {
        if (gameStatus == 3 || endTime < 0) {
            mDeadline.setText(getContext().getString(R.string.end));
        } else if (gameStatus == 2) {
            mDeadline.setText(getContext().getString(R.string.remaining_time_x, DateUtil.getCountdownTime(endTime, 0)));
        } else if (gameStatus == 1) {
            mDeadline.setText(DateUtil.getMinutes(endTime));
        }
        return this;
    }


    public BattleBottomBothInfoView setPraise(int myPraiseCount, int fighterPraiseCount) {
        String myPraiseNumber = myPraiseCount > 999 ? getContext().getString(R.string.number999) : String.valueOf(myPraiseCount);
        String fighterPraiseNumber = fighterPraiseCount > 999 ? getContext().getString(R.string.number999) : String.valueOf(fighterPraiseCount);
        if (mMode == Mode.HALL) {
            //如果是游戏大厅 不显示赞信息
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(GONE);
        } else if (mMode == Mode.MINE) {
            //自己发起的对战
            mMyPerspective.setVisibility(VISIBLE);
            mUserPerspective.setVisibility(GONE);
            mMyPraise.setText(getContext().getString(R.string._support, String.valueOf(myPraiseCount)));
            mUserPraise.setText(getContext().getString(R.string._support, String.valueOf(fighterPraiseNumber)));
        } else {
            //默认参观者模式
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(VISIBLE);
            mMyPraiseButton.setText(String.valueOf(myPraiseNumber));
            mUserPraiseButton.setText(String.valueOf(fighterPraiseNumber));
        }
        return this;
    }

    /**
     * 设置输赢
     * 设置此局游戏是否胜利
     *
     * @param result 0 平手  1发起者赢  2对抗者赢
     * @return
     */
    public BattleBottomBothInfoView setWinResult(int result) {
        if (result == 1) {
            mAgainstKo.setVisibility(VISIBLE);
        } else if (result == 2) {
            mCreateKo.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * 设置点赞按钮是否可用
     *
     * @param enable
     * @return
     */
    public BattleBottomBothInfoView setPraiseEnable(boolean enable) {
        mMyPraiseButton.setEnabled(enable);
        mUserPraiseButton.setEnabled(enable);
        return this;
    }


    public BattleBottomBothInfoView setMode(Mode mode) {
        mMode = mode;
        initViews();
        return this;
    }


    public enum Mode {
        HALL,
        VISITOR,
        MINE
    }

    public BattleBottomBothInfoView setOnUserPraiseListener(OnUserPraiseListener listener) {
        mOnUserPraiseListener = listener;
        return this;
    }

    public interface OnUserPraiseListener {
        //为房间创建者点赞
        void onPraiseBattleInitiatorClick();

        // 为对战者点赞
        void onPraiseBattleAgainstClick();

    }


    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
