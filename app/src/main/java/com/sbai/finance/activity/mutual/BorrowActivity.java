package com.sbai.finance.activity.mutual;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.fragment.dialog.UploadHelpImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mutual.BorrowProtocol;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.GrapeGridView;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-25.
 */

public class BorrowActivity extends BaseActivity {
	@BindView(R.id.photoGv)
	GrapeGridView mPhotoGv;
	@BindView(R.id.borrowLimit)
	EditText mBorrowLimit;
	@BindView(R.id.borrowInterest)
	EditText mBorrowInterest;
	@BindView(R.id.borrowTimeLimit)
	EditText mBorrowTimeLimit;
	@BindView(R.id.borrowRemark)
	EditText mBorrowRemark;
	@BindView(R.id.publish)
	TextView mPublish;
	@BindView(R.id.agree)
	CheckBox mAgree;
	@BindView(R.id.location)
	TextView mLocation;
	@BindView(R.id.contentDays)
	LinearLayout mContentDays;
    private LocalBroadcastManager mLocalBroadcastManager;
	private DelPhotoBroadcastReceiver mDelPhotoBroadcastReceiver;
	private IntentFilter mIntentFilter;
	private PhotoGridAdapter mPhotoGridAdapter;
	private String mImagePath;
	private String mProtocolUrl = API.getHost()+"/mobi/mutual/rules?nohead=1";
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mPhotoGridAdapter = new PhotoGridAdapter(this);
		mPhotoGridAdapter.add("");
		mPhotoGridAdapter.setOnItemClickListener(new PhotoGridAdapter.OnItemClickListener() {
			@Override
			public void onClick(int position) {
				if (mPhotoGridAdapter.getCount()>4){
					return;
				}
				UploadHelpImageDialogFragment.newInstance()
						.setOnDismissListener(new UploadHelpImageDialogFragment.OnDismissListener() {
							@Override
							public void onGetImagePath(String path) {
								mImagePath= path;
								updateHelpImage(mImagePath);
							}
						})
				        .show(getSupportFragmentManager());
			}
		});
		mPhotoGv.setFocusable(false);
		mPhotoGv.setAdapter(mPhotoGridAdapter);
		mPhotoGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int photoCount=mPhotoGridAdapter.getCount()-1;
				if (position<photoCount){
					StringBuilder builder = new StringBuilder();
					for (int i=0;i<photoCount;i++){
						builder.append(mPhotoGridAdapter.getItem(i)).append(",");
					}
					builder.deleteCharAt(builder.length()-1);
					Launcher.with(getActivity(), ContentImgActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, builder.toString().split(","))
							.putExtra(Launcher.EX_PAYLOAD_1, position)
							.putExtra(Launcher.EX_PAYLOAD_2, position)
							.execute();
				}
			}
		});
		mBorrowLimit.addTextChangedListener(mBorrowMoneyValidationWatcher);
		mBorrowLimit.requestFocus();
		mBorrowLimit.setFocusable(true);
		mBorrowInterest.addTextChangedListener(mBorrowInterestValidationWatcher);
		mBorrowTimeLimit.addTextChangedListener(mBorrowTimeLimitValidationWatcher);
		mBorrowRemark.addTextChangedListener(mBorrowRemarkValidationWatcher);
        mAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				    setPublishStatus();
				}
		});

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mDelPhotoBroadcastReceiver = new DelPhotoBroadcastReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(ContentImgActivity.DEL_IMAGE);
		mLocalBroadcastManager.registerReceiver(mDelPhotoBroadcastReceiver,mIntentFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@OnClick({R.id.publish,R.id.contentDays,R.id.protocol,R.id.location})
	public void onClick(View view){
		switch (view.getId()){
			case R.id.publish:
				mPublish.setEnabled(false);
				int money = Integer.valueOf( mBorrowLimit.getText().toString());
				String interest =  mBorrowInterest.getText().toString();
				int days = Integer.valueOf( mBorrowTimeLimit.getText().toString());
				String content = mBorrowRemark.getText().toString();
				if (content.length()>=300){
					content = content.substring(0,300);
				}
				while (content.startsWith("\n")){
					content = content.substring(1,content.length());
				}
				while (content.endsWith("\n")){
					content = content.substring(0,content.length()-1);
				}
				StringBuilder contentImg = new StringBuilder();
				int photoAmount =mPhotoGridAdapter.getCount();
				for (int i = 0; i<photoAmount-1;i++){
					String image = ImageUtils.compressImageToBase64( mPhotoGridAdapter.getItem(i),400f);
					Log.d(TAG, "image: "+image.length());
					contentImg.append(image+",");
				}
				if(contentImg.length()>0){
					contentImg.deleteCharAt(contentImg.length()-1);
				}
				requestPublishBorrow(content,contentImg.toString(),days,interest,money,String.valueOf(LocalUser.getUser().getUserInfo().getId()));
				break;
			case R.id.location:
//				mBorrowTimeLimit.requestFocus();
//				mBorrowTimeLimit.setFocusable(true);
				break;
			case R.id.protocol:
				Client.getBorrowProcotol().setTag(TAG)
						.setCallback(new Callback2D<Resp<BorrowProtocol>,BorrowProtocol>() {
							@Override
							protected void onRespSuccessData(BorrowProtocol data) {
								Launcher.with(getActivity(), WebActivity.class)
										.putExtra(WebActivity.EX_TITLE,getString(R.string.protocol))
										.putExtra(WebActivity.EX_HTML,data.getContent())
										.putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
										.execute();
							}
							@Override
							public void onFailure(VolleyError volleyError) {
								super.onFailure(volleyError);
								Launcher.with(getActivity(), WebActivity.class)
										.putExtra(WebActivity.EX_TITLE,getString(R.string.protocol))
										.putExtra(WebActivity.EX_URL,mProtocolUrl)
										.putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
										.execute();
							}
						}).fire();
				break;
			default:
				break;
		}
	}
	private void requestPublishBorrow(String content,String contentImg,Integer days,String interest,Integer money,String userId){
		Client.borrowIn(content,contentImg,days,interest,money,userId).setTag(TAG)
				.setIndeterminate(this)
				.setCallback(new Callback<Resp<Object>>() {
					@Override
					protected void onRespSuccess(Resp<Object> resp) {
						if (resp.isSuccess()){
							CustomToast.getInstance().showText(getActivity(),getString(R.string.publish_success));
							Launcher.with(getActivity(),BorrowInActivity.class).execute();
							finish();
						}else{
							mPublish.setEnabled(true);
							ToastUtil.curt(resp.getMsg());
						}
					}
				}).fire();
	}
	private void setPublishStatus(){
		boolean enable = checkPublishButtonEnable();
		if (enable != mPublish.isEnabled()) {
			mPublish.setEnabled(enable);
	    }
	}
	private boolean checkPublishButtonEnable(){
		String borrowMoney = mBorrowLimit.getText().toString().trim();
		boolean isEmpty = TextUtils.isEmpty(borrowMoney);
		if (isEmpty||borrowMoney.length()>4|| Integer.parseInt(borrowMoney)>2000||Integer.parseInt(borrowMoney)<500) {
             if (!isEmpty){
				 ToastUtil.curt(getString(R.string.borrow_over_money));
			 }
			return false;
		}else{
			//mWarn.setVisibility(View.INVISIBLE);

		}
		String borrowInterest = mBorrowInterest.getText().toString().trim();
		isEmpty = TextUtils.isEmpty(borrowInterest);
		if (isEmpty||borrowInterest.length()>3||Integer.valueOf(borrowInterest)<1||Integer.valueOf(borrowInterest)>200){
			if (!isEmpty){
				ToastUtil.curt(getString(R.string.borrow_over_interest));
			}
			return false;
		}else{
			//mWarn.setVisibility(View.INVISIBLE);
		}
		String borrowTimeLimit = mBorrowTimeLimit.getText().toString().trim();
		isEmpty = TextUtils.isEmpty(borrowTimeLimit);
		if (isEmpty||borrowTimeLimit.length()>3|| Integer.parseInt(borrowTimeLimit)>60){
			if (!isEmpty){
				ToastUtil.curt(getString(R.string.borrow_overdue));
			}
			return false;
		}else{
			//mWarn.setVisibility(View.INVISIBLE);
		}
		if (!mAgree.isChecked()){
			return false;
		}
		if (TextUtils.isEmpty(mBorrowRemark.getText())){
			ToastUtil.curt(getString(R.string.no_remark));
			return false;
		}else{
		//	mWarn.setVisibility(View.INVISIBLE);
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBorrowLimit.removeTextChangedListener(mBorrowMoneyValidationWatcher);
		mBorrowInterest.removeTextChangedListener(mBorrowInterestValidationWatcher);
		mBorrowTimeLimit.removeTextChangedListener(mBorrowTimeLimitValidationWatcher);
		mBorrowRemark.removeTextChangedListener(mBorrowRemarkValidationWatcher);
		mLocalBroadcastManager.unregisterReceiver(mDelPhotoBroadcastReceiver);
	}
	private void updateHelpImage(String helpImagePath) {
        if (!TextUtils.isEmpty(helpImagePath)){
			mPhotoGridAdapter.insert(helpImagePath,mPhotoGridAdapter.getCount()-1);
		}

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
	private ValidationWatcher mBorrowRemarkValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			setPublishStatus();
		}
	};
	class DelPhotoBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int index = intent.getExtras().getInt(ContentImgActivity.DEL_IMAGE);
			if (index>=0){
				mPhotoGridAdapter.remove(mPhotoGridAdapter.getItem(index));
			}
		}
	}
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
				 convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_photo, null);
				 ImageView mPhotoImg = (ImageView) convertView.findViewById(R.id.photoImg);
				 Glide.with(getContext()).load(getItem(position)).into(mPhotoImg);
			}
			return convertView;
		}
	}
}
