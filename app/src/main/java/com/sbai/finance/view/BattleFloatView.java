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
import com.sbai.finance.utils.GlideCircleTransform;

import butterknife.BindView;

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
        super(context, attrs, -1);
    }

    public BattleFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.battle_float_view, this, true);
        addView(view);
    }

    private void initViews() {
        if (mMode == Mode.GAMEHALL) {
            //游戏大厅显示标题
            mVarietyName.setVisibility(VISIBLE);
            resetMargin();
        } else {
            mVarietyName.setVisibility(GONE);
        }

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
     * @param fighterProfit 对抗者盈利状况 可以为null
     * @return
     */
    public BattleFloatView setProgress(double myProfit, Double fighterProfit) {
        // TODO: 2017/6/19 设置比例逻辑没写
        return this;
    }

    /**
     * 显示对战信息
     *
     * @param coinType   对战类型 1现金 2元宝 3积分
     * @param gameStatus 对战状态
     * @param endtime    对战时长 1匹配中  2对战中  3结束
     * @return
     */
    public BattleFloatView setDepositAndTime(int coinType, int gameStatus, String endtime) {
        // TODO: 2017/6/19 显示逻辑没写
        return this;
    }


    public BattleFloatView setPraise(int myPraiseCount, int fighterPraiseCount) {
        if (mMode == Mode.GAMEHALL) {
            //如果是游戏大厅 不显示赞信息
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(GONE);
        } else if (mMode == Mode.MINE) {
            //自己发起的对战
            mMyPerspective.setVisibility(VISIBLE);
            mUserPerspective.setVisibility(GONE);
            mMyPraise.setText(String.valueOf(myPraiseCount));
            mUserPraise.setText(String.valueOf(fighterPraiseCount));
        } else {
            //默认参观者模式
            mMyPerspective.setVisibility(GONE);
            mUserPerspective.setVisibility(VISIBLE);
            mMyPraiseButton.setText(String.valueOf(myPraiseCount));
            mUserPraiseButton.setText(String.valueOf(fighterPraiseCount));
        }
        return this;
    }


    public BattleFloatView setMode(Mode mode) {
        mMode = mode;
        initViews();
        return this;
    }

    public enum Mode {
        GAMEHALL,
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
