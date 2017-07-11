package com.sbai.finance.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.fragment.EconomicCircleFragment;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.view.CollapsedTextLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyViewHolder {

	@BindView(R.id.hotArea)
	public RelativeLayout mHotArea;
	@BindView(R.id.avatar)
	public ImageView mAvatar;
	@BindView(R.id.userName)
	public TextView mUserName;
	@BindView(R.id.publishTime)
	public TextView mPublishTime;
	@BindView(R.id.location)
	public TextView mLocation;
	@BindView(R.id.borrowMoneyContent)
	public CollapsedTextLayout mBorrowMoneyContent;
	@BindView(R.id.needAmount)
	public TextView mNeedAmount;
	@BindView(R.id.borrowDeadline)
	public TextView mBorrowDeadline;
	@BindView(R.id.borrowInterest)
	public TextView mBorrowInterest;
	@BindView(R.id.isAttention)
	public TextView mIsAttention;
	@BindView(R.id.borrowingImg)
	public ImageView mBorrowingImg;
	@BindView(R.id.circleMoreIcon)
	public ImageView mCircleMoreIcon;


	public BorrowMoneyViewHolder(View view) {
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

		if (TextUtils.isEmpty(item.getContent())) {
			mBorrowMoneyContent.setVisibility(View.GONE);
		} else {
			mBorrowMoneyContent.setVisibility(View.VISIBLE);
			mBorrowMoneyContent.setContentText(item.getContent().trim());
		}

		mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getMoney())));
		mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(item.getDays())));
		mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getInterest())));

		mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
		if (TextUtils.isEmpty(item.getLocation())) {
			mLocation.setText(R.string.no_location_information);
		} else {
			mLocation.setText(item.getLocation());
		}

		if (!TextUtils.isEmpty(item.getContentImg())) {
			String[] images = item.getContentImg().split(",");
			if (images.length >= 2) {
				mBorrowingImg.setVisibility(View.VISIBLE);
				mCircleMoreIcon.setVisibility(View.VISIBLE);
			} else {
				mBorrowingImg.setVisibility(View.VISIBLE);
				mCircleMoreIcon.setVisibility(View.GONE);
			}
			Glide.with(context).load(images[0])
					.placeholder(R.drawable.ic_loading_pic)
					.into(mBorrowingImg);
		} else {
			mBorrowingImg.setVisibility(View.GONE);
			mCircleMoreIcon.setVisibility(View.GONE);
		}
	}
}
