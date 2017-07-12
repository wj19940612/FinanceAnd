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
import com.sbai.finance.view.CollapsedTextLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpinionViewHolder {
	@BindView(R.id.hotArea)
	public RelativeLayout mHotArea;
	@BindView(R.id.avatar)
	public ImageView mAvatar;
	@BindView(R.id.userName)
	public TextView mUserName;
	@BindView(R.id.isAttention)
	public TextView mIsAttention;
	@BindView(R.id.publishTime)
	public TextView mPublishTime;
	@BindView(R.id.opinionContent)
	public CollapsedTextLayout mOpinionContent;
	@BindView(R.id.label)
	public ImageView mLabel;
	@BindView(R.id.bigVarietyName)
	public TextView mBigVarietyName;
	@BindView(R.id.varietyName)
	public TextView mVarietyName;

	public OpinionViewHolder(View view) {
		ButterKnife.bind(this, view);
	}

	public void bindingData(Context context, final EconomicCircle item, final EconomicCircleFragment.EconomicCircleAdapter.Callback callback) {
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


		mOpinionContent.setContentText(item.getContent());

		if (item.getDirection() == 1) {
			if (item.getGuessPass() == 1) {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_up_succeed);
			} else if (item.getGuessPass() == 2) {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_up_failed);
			} else {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_up);
			}
		} else {
			if (item.getGuessPass() == 1) {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_down_succeed);
			} else if (item.getGuessPass() == 2) {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_down_failed);
			} else {
				mLabel.setBackgroundResource(R.drawable.ic_opinion_down);
			}
		}

		mBigVarietyName.setText(context.getString(R.string.big_variety_name, item.getBigVarietyTypeName()));
		mVarietyName.setText(item.getVarietyName());
		mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
	}
}
