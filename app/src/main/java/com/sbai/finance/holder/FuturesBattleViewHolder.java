package com.sbai.finance.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.fragment.EconomicCircleFragment;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.view.BattleProgress;
import com.sbai.finance.view.CollapsedTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaokuan0819 on 2017/7/10.
 */

public class FuturesBattleViewHolder {
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.userName)
	TextView mUserName;
	@BindView(R.id.isAttention)
	TextView mIsAttention;
	@BindView(R.id.hotArea)
	RelativeLayout mHotArea;
	@BindView(R.id.content)
	CollapsedTextView mContent;
	@BindView(R.id.varietyName)
	TextView mVarietyName;
	@BindView(R.id.progressBar)
	BattleProgress mProgressBar;
	@BindView(R.id.versus)
	ImageView mVersus;
	@BindView(R.id.bounty)
	TextView mBounty;
	@BindView(R.id.status)
	TextView mStatus;
	@BindView(R.id.againstUserAvatar)
	ImageView mAgainstUserAvatar;
	@BindView(R.id.againstUserName)
	TextView mAgainstUserName;
	@BindView(R.id.publishTime)
	TextView mPublishTime;
	@BindView(R.id.location)
	TextView mLocation;

	public FuturesBattleViewHolder(View view) {
		ButterKnife.bind(this, view);
	}

	public void bindingData(Context context, final EconomicCircle item, final EconomicCircleFragment.EconomicCircleAdapter.Callback callback, int position) {
		if (item == null) return;

		Glide.with(context).load(item.getUserPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(context))
				.into(mAvatar);

		mHotArea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.HotAreaClick(item);
				}
			}
		});

		mUserName.setText(item.getUserName());

		if (item.getIsAttention() == 2) {
			mIsAttention.setText(R.string.is_attention);
		} else {
			mIsAttention.setText("");
		}

		mVarietyName.setText(item.getVarietyName());
		mContent.setShowText(item.getContent().trim());
		mProgressBar.setLeftText(String.valueOf(item.getLaunchScore()));
		mProgressBar.setRightText(String.valueOf(item.getAgainstScore()));

		String reward = "";
		switch (item.getCoinType()) {
			case EconomicCircle.COIN_TYPE_CASH:
				reward = item.getReward() + context.getString(R.string.cash);
				break;
			case EconomicCircle.COIN_TYPE_INGOT:
				reward = item.getReward() + context.getString(R.string.ingot);
				break;
			case EconomicCircle.COIN_TYPE_INTEGRAL:
				reward = item.getReward() + context.getString(R.string.integral);
				break;
			default:
				break;
		}

		mBounty.setText(reward);
		switch (item.getGameStatus()) {
			//取消
			case EconomicCircle.GAME_STATUS_CANCELED:
				mStatus.setText(context.getString(R.string.versus_cancel));
				mAgainstUserAvatar.setImageResource(R.drawable.btn_join_versus);
				mAgainstUserName.setText(context.getString(R.string.join_versus));
				mVersus.setVisibility(View.VISIBLE);
				mProgressBar.showScoreProgress(0, 0, true);
				break;

			//发起对战
			case EconomicCircle.GAME_STATUS_CREATED:
				mStatus.setText(DateUtil.getMinutes(item.getEndline()));
				mAgainstUserAvatar.setImageResource(R.drawable.btn_join_versus);
				mAgainstUserName.setText(context.getString(R.string.join_versus));
				mVersus.setVisibility(View.VISIBLE);
				mProgressBar.showScoreProgress(0, 0, true);
				break;

			//对战中
			case EconomicCircle.GAME_STATUS_STARTED:
				mStatus.setText(context.getString(R.string.versusing));
				Glide.with(context)
						.load(item.getAgainstUserPortrait())
						.placeholder(R.drawable.ic_default_avatar_big)
						.transform(new GlideCircleTransform(context))
						.into(mAgainstUserAvatar);
				mAgainstUserName.setText(item.getAgainstUserName());
				mVersus.setVisibility(View.GONE);
				mProgressBar.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
				break;

			//已结束
			case EconomicCircle.GAME_STATUS_END:
				mStatus.setText(context.getString(R.string.versus_end));
				Glide.with(context)
						.load(item.getAgainstUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAgainstUserAvatar);
				mAgainstUserName.setText(item.getAgainstUserName());
				mVersus.setVisibility(View.GONE);
				mProgressBar.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
				break;

			default:
				break;
		}

		mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
	}
}
