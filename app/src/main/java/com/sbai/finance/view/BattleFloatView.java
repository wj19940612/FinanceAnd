package com.sbai.finance.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class BattleFloatView extends RelativeLayout {

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
    private OnPraiseListener mOnPraiseListener;
    private onAvatarClickListener mOnAvatarClickListener;

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
                            mOnPraiseListener.addCreatePraiseCount();
                        }
                    }
                });

                mUserPraiseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPraiseListener != null) {
                            mOnPraiseListener.addAgainstPraiseCount();
                        }
                    }
                });

                mCreateAvatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnAvatarClickListener != null) {
                            mOnAvatarClickListener.onCreateAvatarClick();
                        }
                    }
                });

                mAgainstAvatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnAvatarClickListener != null) {
                            mOnAvatarClickListener.onAgainstAvatarClick();
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

    public BattleFloatView initWithModel(VersusGaming model) {
        this.setCreateAvatar(model.getLaunchUserPortrait())
                .setCreateName(model.getLaunchUserName())
                .setAgainstAvatar(model.getAgainstUserPortrait())
                .setAgainstName(model.getAgainstUserName())
                .setDeposit(model.getReward(), model.getCoinType())
                .setPraise(model.getLaunchPraise(), model.getAgainstPraise())
                .setDeadline(model.getGameStatus(), (int) (model.getEndline()/1000));
        return this;
    }

    public BattleFloatView setCreateName(String name) {
        mCreateName.setText(name);
        return this;
    }

    public BattleFloatView setCreateAvatar(String url) {
        Glide.with(getContext())
                .load(url)
                .bitmapTransform(new GlideCircleTransform(getContext()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mCreateAvatar);
        return this;
    }

    public BattleFloatView setAgainstName(String userName) {
        if (TextUtils.isEmpty(userName)){
            mAgainstName.setText(getContext().getString(R.string.wait_to_join));
        }else {
            mAgainstName.setText(userName);
        }
        return this;
    }

    public BattleFloatView setAgainstAvatar(String url) {
        Glide.with(getContext())
                .load(url)
                .bitmapTransform(new GlideCircleTransform(getContext()))
                .placeholder(R.drawable.ic_default_avatar_big)
                .into(mAgainstAvatar);
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
     * @param isInviting    是否正在邀请中
     * @return
     */
    public BattleFloatView setProgress(double myProfit, double fighterProfit, boolean isInviting) {
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
    public BattleFloatView setDeposit(int reward, int coinType) {
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
    public BattleFloatView setDeadline(int gameStatus, int endTime) {
        if (gameStatus == 3) {
            mDeadline.setText("已结束");
        } else if (gameStatus == 2) {
            mDeadline.setText("剩余" + DateUtil.getCountdownTime(endTime, 0));
        }else if (gameStatus == 1){
            mDeadline.setText(DateUtil.getCountdownTime(endTime, 0));
        }
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
            mMyPraise.setText(String.valueOf(myPraiseNumber) + "赞");
            mUserPraise.setText(String.valueOf(fighterPraiseNumber) + "赞");
        } else {
            //默认参观者模式
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(VISIBLE);
            mMyPraiseButton.setText(String.valueOf(myPraiseNumber));
            mUserPraiseButton.setText(String.valueOf(fighterPraiseNumber));
        }
        return this;
    }

    public BattleFloatView setPraiseLight(boolean isLeft) {
        Drawable left = ContextCompat.getDrawable(getContext(), R.drawable.ic_battle_praise);
        if (isLeft) {
            mMyPraiseButton.setSelected(true);
        } else {
            mUserPraiseButton.setSelected(true);
        }
        return this;
    }

    /**
     * 设置输赢
     * 设置此局游戏是否胜利
     * @param result 0 平手  1发起者赢  2对抗者赢
     * @return
     */
    public BattleFloatView setWinResult(int result) {
        if (result == 1) {
            mAgainstKo.setVisibility(VISIBLE);
        } else if (result == 2) {
            mCreateKo.setVisibility(VISIBLE);
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

    public BattleFloatView setOnPraiseListener(OnPraiseListener listener) {
        mOnPraiseListener = listener;
        return this;
    }

    public BattleFloatView setOnAvatarClickListener(onAvatarClickListener listener) {
        mOnAvatarClickListener = listener;
        return this;
    }

    public interface OnPraiseListener {
        void addCreatePraiseCount();

        void addAgainstPraiseCount();
    }

    public interface onAvatarClickListener{
        void onCreateAvatarClick();
        void onAgainstAvatarClick();
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
