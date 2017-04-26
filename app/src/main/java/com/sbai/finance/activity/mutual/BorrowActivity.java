package com.sbai.finance.activity.mutual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.MyGridView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by Administrator on 2017-04-25.
 */

public class BorrowActivity extends BaseActivity {
	@BindView(R.id.photoGv)
	MyGridView mPhotoGv;
	@BindView(R.id.borrowLimit)
	EditText mBorrowLimit;
	@BindView(R.id.borrowInterest)
	EditText mBorrowInterest;
	@BindView(R.id.borrowTimeLimit)
	EditText mBorrowTimeLimit;
	@BindView(R.id.borrowRemark)
	EditText mBorrowRemark;
	@BindView(R.id.warn)
	TextView mWarn;
	@BindView(R.id.publish)
	TextView mPublish;
	@BindView(R.id.agree)
	CheckBox mAgree;

	private PhotoGridAdapter mPhotoGridAdapter;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mPhotoGridAdapter = new PhotoGridAdapter(this);
		mPhotoGridAdapter.setOnItemClickListener(new PhotoGridAdapter.OnItemClickListener() {
			@Override
			public void onClick(int position) {
				UploadUserImageDialogFragment.newInstance().show(getSupportFragmentManager());
			}
		});
		mPhotoGv.setFocusable(false);
		mPhotoGv.setAdapter(mPhotoGridAdapter);
		mBorrowLimit.addTextChangedListener(mBorrowMoneyValidationWatcher);
		mBorrowInterest.addTextChangedListener(mBorrowInterestValidationWatcher);
		mBorrowTimeLimit.addTextChangedListener(mBorrowTimeLimitValidationWatcher);
        mAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				    setPublishStatus();
				}
		});

		String photo1 = LocalUser.getUser().getUserInfo().getUserPortrait();
		mPhotoGridAdapter.add(photo1);
		mPhotoGridAdapter.add(photo1);
		mPhotoGridAdapter.add(photo1);
		mPhotoGridAdapter.add(photo1);
		mPhotoGridAdapter.add(" ");
		mPhotoGridAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	private void setPublishStatus(){
		boolean enable = checkPublishButtonEnable();
		if (enable != mPublish.isEnabled()) {
			mPublish.setEnabled(enable);
	    }
	}
	private boolean checkPublishButtonEnable(){
		boolean result = true;
		boolean isCanHideWarn = false;
		String borrowMoney = mBorrowLimit.getText().toString().trim();
		boolean isEmpty = TextUtils.isEmpty(borrowMoney);
		if (isEmpty|| Integer.parseInt(borrowMoney)>2000) {
             if (!isEmpty){
				 mWarn.setVisibility(View.VISIBLE);
				 mWarn.setText(getString(R.string.borrow_over_money));
			 }else{
				 isCanHideWarn = true;
			 }
             result = false;
		}
		String borrowInterest = mBorrowInterest.getText().toString().trim();
		isEmpty = TextUtils.isEmpty(borrowInterest);
		if (isEmpty|| Integer.parseInt(borrowInterest)>200){
			if (!isEmpty){
				mWarn.setVisibility(View.VISIBLE);
				mWarn.setText(getString(R.string.borrow_over_interest));
			}else{
				isCanHideWarn = true;
			}
			result = false;
		}
		String borrowTimeLimit = mBorrowTimeLimit.getText().toString().trim();
		isEmpty = TextUtils.isEmpty(borrowTimeLimit);
		if (isEmpty|| Integer.parseInt(borrowTimeLimit)>60){
			if (!isEmpty){
				mWarn.setVisibility(View.VISIBLE);
				mWarn.setText(getString(R.string.borrow_overdue));
			}else{
				isCanHideWarn = true;
			}
			result = false;
		}
		if (result || isCanHideWarn){
			mWarn.setVisibility(View.INVISIBLE);
		}
		if (!mAgree.isChecked()){
			result = false;
		}
		if (mPhotoGridAdapter.getCount()<2){
			result = false;
		}
		return result;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBorrowLimit.removeTextChangedListener(mBorrowMoneyValidationWatcher);
		mBorrowInterest.removeTextChangedListener(mBorrowInterestValidationWatcher);
		mBorrowTimeLimit.removeTextChangedListener(mBorrowTimeLimitValidationWatcher);
	}
	private ValidationWatcher mBorrowMoneyValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			setPublishStatus();
		}
	};
	private ValidationWatcher mBorrowInterestValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			setPublishStatus();
		}
	};
	private ValidationWatcher mBorrowTimeLimitValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			setPublishStatus();
		}
	};


	static class PhotoGridAdapter extends ArrayAdapter<String> {

		public PhotoGridAdapter(@NonNull Context context) {
			super(context, 0);
		}
		interface OnItemClickListener{
			void onClick(int position);
		}
		private OnItemClickListener mOnItemClickListener;
		public void setOnItemClickListener(OnItemClickListener onItemClickListener){
			mOnItemClickListener = onItemClickListener;
		}
		@NonNull
		@Override
		public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			if (position == getCount()-1){
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_photo, null);
				TextView mAddPhoto = (TextView) convertView.findViewById(R.id.addPhoto);
				mAddPhoto.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mOnItemClickListener.onClick(position);
					}
				});
			}else{
				ViewHolder viewHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_photo, null);
					viewHolder = new ViewHolder(convertView, getContext());
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.bindingData(getItem(position));
			}
			return convertView;
		}
		static class ViewHolder {
			@BindView(R.id.photoImg)
			ImageView mPhotoImg;
			private Context mContext;

			ViewHolder(View view, Context context) {
				mContext = context;
				ButterKnife.bind(this, view);
				view.setClickable(false);
			}

			public void bindingData(String item) {
				Glide.with(mContext).load(item).into(mPhotoImg);
			}
		}
	}
}
