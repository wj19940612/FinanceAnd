package com.sbai.finance.view.training;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.TrainingExperiencePraise;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExperienceView extends LinearLayout {
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.userName)
	TextView mUserName;
	@BindView(R.id.star1)
	ImageView mStar1;
	@BindView(R.id.star2)
	ImageView mStar2;
	@BindView(R.id.star3)
	ImageView mStar3;
	@BindView(R.id.experience)
	TextView mExperience;
	@BindView(R.id.imageView)
	ImageView mImageView;
	@BindView(R.id.publishTime)
	TextView mPublishTime;
	@BindView(R.id.loveNumber)
	TextView mLoveNumber;

	public ExperienceView(Context context) {
		this(context, null, 0);
	}

	public ExperienceView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ExperienceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	private void initView() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.row_train_experience, null, false);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(view, params);
		ButterKnife.bind(this);
	}

	public void setData (final Experience data) {
		if (data != null) {
			if (data.getUserModel() != null) {
				Glide.with(getContext()).load(data.getUserModel().getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(getContext()))
						.into(mAvatar);

				mUserName.setText(data.getUserModel().getUserName());
				mPublishTime.setText(DateUtil.getMissFormatTime(data.getCreateDate()));
			} else {
				Glide.with(getContext()).load(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(getContext()))
						.into(mAvatar);
				mUserName.setText("");
				mPublishTime.setText("");
			}

			mExperience.setText(data.getContent());
			mLoveNumber.setText(StrFormatter.getFormatCount(data.getPraise()));

			if (data.getIsPraise() == 1) {
				mLoveNumber.setSelected(true);
			} else {
				mLoveNumber.setSelected(false);
			}

			mLoveNumber.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (LocalUser.getUser().isLogin()) {
						Client.trainExperiencePraise(data.getId(), data.getIsPraise() == 0 ? 1 : 0)
								.setCallback(new Callback2D<Resp<TrainingExperiencePraise>, TrainingExperiencePraise>() {
									@Override
									protected void onRespSuccessData(TrainingExperiencePraise praise) {
										if (praise.getIsPraise() == 1) {
											mLoveNumber.setSelected(true);
										} else {
											mLoveNumber.setSelected(false);
										}
										data.setIsPraise(praise.getIsPraise());
										mLoveNumber.setText(StrFormatter.getFormatCount(praise.getPraise()));
									}
								}).fire();

					} else {
						Launcher.with(getContext(), LoginActivity.class).execute();
					}
				}
			});

			if (data.getPicture() == null || "".equalsIgnoreCase(data.getPicture())) {
				mImageView.setVisibility(View.GONE);
			} else {
				mImageView.setVisibility(View.VISIBLE);
				Glide.with(getContext()).load(data.getPicture())
						.placeholder(R.drawable.ic_default_image)
						.into(mImageView);

				mImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(getContext(), LookBigPictureActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, data.getPicture())
								.execute();
					}
				});
			}

			switch (data.getStar()) {
				case 1:
					mStar1.setVisibility(View.VISIBLE);
					mStar2.setVisibility(View.GONE);
					mStar3.setVisibility(View.GONE);
					break;
				case 2:
					mStar1.setVisibility(View.VISIBLE);
					mStar2.setVisibility(View.VISIBLE);
					mStar3.setVisibility(View.GONE);
					break;
				case 3:
					mStar1.setVisibility(View.VISIBLE);
					mStar2.setVisibility(View.VISIBLE);
					mStar3.setVisibility(View.VISIBLE);
					break;
			}
		}
	}
}
