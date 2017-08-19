package com.sbai.finance.fragment.training;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExplanationFragment extends Fragment {

	@BindView(R.id.explanation)
	TextView mExplanation;
	Unbinder unbinder;
	@BindView(R.id.starImage)
	ImageView mStarImage;

	private String mText;
	private int mTag;
	private int mStar;

	public static ExplanationFragment newInstance(String explanation, int tag, int star) {
		Bundle args = new Bundle();
		args.putString("explanation", explanation);
		args.putInt("tag", tag);
		args.putInt("star", star);
		ExplanationFragment fragment = new ExplanationFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mText = getArguments().getString("explanation");
			mTag = getArguments().getInt("tag");
			mStar = getArguments().getInt("star");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_explanation, container, false);
		unbinder = ButterKnife.bind(this, view);
		initView();
		return view;
	}

	private void initView() {
		mExplanation.setText(mText);
		switch (mStar) {
			case 0:
				mStarImage.setImageResource(R.drawable.ic_star_1s);
				break;
			case 1:
				mStarImage.setImageResource(R.drawable.ic_star_2s);
				break;
			case 2:
				mStarImage.setImageResource(R.drawable.ic_star_3s);
				break;
			case 3:
				mStarImage.setImageResource(R.drawable.ic_star_4s);
				break;
			case 4:
				mStarImage.setImageResource(R.drawable.ic_star_5s);
				break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public int getStarTag() {
		return mTag;
	}

	public void setStarVisible() {
		mStarImage.setVisibility(View.VISIBLE);
	}

	public void setTextInvisible() {
		mExplanation.setText("");
	}
}
