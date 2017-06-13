package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeHeader extends FrameLayout {
	@BindView(R.id.future)
	TextView mFuture;
	@BindView(R.id.stock)
	TextView mStock;
	@BindView(R.id.help)
	TextView mHelp;
	@BindView(R.id.selfChoice)
	TextView mSelfChoice;

	private OnViewClickListener mListener;
	public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
		mListener = onViewClickListener;
	}
	public interface OnViewClickListener {

		void onFutureClick();

		void onStockClick();

		void onHelpClick();

		void onSelfChoiceClick();
	}

	public HomeHeader(@NonNull Context context) {
		super(context);
        init();
	}

	public HomeHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.home_header, this, true);
		ButterKnife.bind(this);
	}
	@OnClick({R.id.future, R.id.stock, R.id.help, R.id.selfChoice})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.future:
				if (mListener != null) {
					mListener.onFutureClick();
				}
				break;
			case R.id.stock:
				if (mListener != null) {
					mListener.onStockClick();
				}
				break;
			case R.id.help:
				if (mListener != null) {
					mListener.onHelpClick();
				}
				break;
			case R.id.selfChoice:
				if (mListener != null) {
					mListener.onSelfChoiceClick();
				}
				break;
		}
	}
}
