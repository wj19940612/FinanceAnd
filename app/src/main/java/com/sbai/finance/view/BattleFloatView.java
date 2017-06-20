package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class BattleFloatView extends RelativeLayout {

    @BindView(R.id.myAvatar)
    ImageView mMyAvatar;
    @BindView(R.id.myName)
    TextView mMyName;
    @BindView(R.id.userAvatar)
    ImageView mUserAvatar;
    @BindView(R.id.userName)
    TextView mUserName;

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
    @BindView(R.id.depositAndTime)
    TextView mDepositAndTime;

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
    private OnPraiseListener mOnPraiseListener;

    public BattleFloatView(Context context) {
        this(context, null);
    }

    public BattleFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BattleFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                mMyPraiseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPraiseListener != null) {
                            mOnPraiseListener.addMyPraiseCount();
                        }
                    }
                });

                mUserPraiseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPraiseListener != null) {
                            mOnPraiseListener.addUserPraiseCount();
                        }
                    }
                });
            }
        }
    }

    private void resetMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFighterDataArea.getLayoutParams();
        params.topMargin = dp2px(getContext(), 10);
        mFighterDataArea.setLayoutParams(params);
    }

    public BattleFloatView setMyName(String name) {
        mMyName.setText(name);
        return this;
    }

    public BattleFloatView setMyAvatar(String url) {
        Glide.with(getContext())
                .load(url)
                .bitmapTransform(new GlideCircleTransform(getContext()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mMyAvatar);
        return this;
    }

    public BattleFloatView setUserName(String userName) {
        mUserName.setText(userName);
        return this;
    }

    public BattleFloatView setUserAvatar(String url) {
        Glide.with(getContext())
                .load(url)
                .bitmapTransform(new GlideCircleTransform(getContext()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mUserAvatar);
        return this;
    }

    public BattleFloatView setVarietyName(String varietyName) {
        mVarietyName.setText(varietyName);
        return this;
    }

    /**
     * 显示对抗状态条
     *
     * @param myProfit      我的盈利状况
     * @param fighterProfit 对抗者盈利状况
     * @param  isInviting 是否正在邀请中
     * @return
     */
    public BattleFloatView setProgress(double myProfit, double fighterProfit,boolean isInviting) {
        if (isInviting) {
            mProgressBar.setProgress(0);
            mProgressBar.setSecondaryProgress(0);
        }else {
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

            mMyProfit.setText(String.valueOf(FinanceUtil.accurateToFloat(myProfit)));
            mUserProfit.setText(String.valueOf(FinanceUtil.accurateToFloat(fighterProfit)));

        }
        return this;
    }

    /**
     * 显示对战信息
     * @param reward     赏金
     * @param coinType   对战类型  2元宝 3积分
     * @param gameStatus 对战状态 1匹配中  2对战中  3结束
     * @param endTime    对战时长
     * @return
     */
    public BattleFloatView setDepositAndTime(int reward, int coinType, int gameStatus, String endTime) {
        StringBuilder builder = new StringBuilder();
        builder.append(reward);
        builder.append(coinType == 2 ? "元宝" : "积分");
        builder.append("    ");
        if (gameStatus == 1) {
            builder.append(endTime);
        }
        builder.append(gameStatus == 2 ? "2对战中" : "结束");
        mDepositAndTime.setText(builder.toString());
        return this;
    }


    public BattleFloatView setPraise(int myPraiseCount, int fighterPraiseCount) {
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
            mMyPraise.setText(String.valueOf(myPraiseNumber));
            mUserPraise.setText(String.valueOf(fighterPraiseNumber));
        } else {
            //默认参观者模式
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(VISIBLE);
            mMyPraiseButton.setText(String.valueOf(myPraiseNumber));
            mUserPraiseButton.setText(String.valueOf(fighterPraiseNumber));
        }
        return this;
    }


    public BattleFloatView setMode(Mode mode) {
        mMode = mode;
        initViews();
        return this;
    }

    public enum Mode {
        HALL,
        VISITOR,
        MINE;
    }

    public void setOnPraiseListener(OnPraiseListener listener) {
        mOnPraiseListener = listener;
    }

    public interface OnPraiseListener {
        void addMyPraiseCount();

        void addUserPraiseCount();
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
