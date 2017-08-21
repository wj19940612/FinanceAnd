package com.sbai.finance.fragment.training;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExplanationFragment extends Fragment {

	@BindView(R.id.explanation)
	TextView mExplanation;
	Unbinder unbinder;

	private String mText;
	private int mTag;

	public static ExplanationFragment newInstance(String explanation, int tag) {
		Bundle args = new Bundle();
		args.putString("explanation", explanation);
		args.putInt("tag", tag);
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
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_explanation, container, false);
		unbinder = ButterKnife.bind(this, view);
		mExplanation.setText(mText);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public int getStarTag() {
		return mTag;
	}
}
