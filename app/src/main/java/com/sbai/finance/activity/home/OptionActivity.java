package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.FutureHq;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.slidingListView.SlideItem;
import com.sbai.finance.view.slidingListView.SlideListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-18.
 */

public class OptionActivity extends BaseActivity {
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.listView)
	SlideListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	private SlideListAdapter mSlideListAdapter;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestOptionalData();
			}
		});
		mSlideListAdapter = new SlideListAdapter(this);
		mSlideListAdapter.setOnDelClickListener(new SlideListAdapter.OnDelClickListener() {
			@Override
			public void onClick(final int position) {
				requestDelOptionalData(mSlideListAdapter.getItem(position).getVarietyId());
			}
		});
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mSlideListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestOptionalData();
	}

	private void requestOptionalData() {
		Client.getOptional(Variety.VAR_FUTURE).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Variety>>,List<Variety>>() {
					@Override
					protected void onRespSuccessData(List<Variety> data) {
                          updateOptionInfo((ArrayList<Variety>) data);
					}
				}).fireSync();
	}
	private void requestDelOptionalData(Integer varietyId ) {
		Client.delOptional(varietyId).setTag(TAG)
				.setCallback(new Callback<Resp<Object>>() {
					@Override
					protected void onRespSuccess(Resp<Object> resp) {
						if (resp.isSuccess()){
							requestOptionalData();
						}else{
							ToastUtil.curt(resp.getMsg());
							stopRefreshAnimation();
						}
					}
				}).fire();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private void updateOptionInfo(ArrayList<Variety> data) {
		stopRefreshAnimation();
		mSlideListAdapter.clear();
		mSlideListAdapter.addAll(data);
		mSlideListAdapter.notifyDataSetChanged();

	}
	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
	public static class SlideListAdapter extends ArrayAdapter<Variety> {
		Context mContext;
		private OnDelClickListener mOnDelClickListener;
		interface OnDelClickListener {
			void onClick(int position);
		}
		public SlideListAdapter(@NonNull Context context){
			super(context,0);
			mContext = context;
		}
        public void setOnDelClickListener(OnDelClickListener onDelClickListener){
		   mOnDelClickListener = onDelClickListener;
		}
		@NonNull
		@Override
		public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
				View menu = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_delete_btn, parent, false);
				viewHolder = new ViewHolder(content,menu);
				SlideItem slideItem = new SlideItem(mContext);
				slideItem.setContentView(content,menu);
				convertView = slideItem;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mDel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnDelClickListener.onClick(position);
				}
			});
			viewHolder.bindDataWithView(getItem(position), position, mContext);
			return convertView;
		}


		static class ViewHolder{
			@BindView(R.id.futureName)
			TextView mFutureName;
			@BindView(R.id.futureCode)
			TextView mFutureCode;
			@BindView(R.id.lastPrice)
			TextView mLastPrice;
			@BindView(R.id.rate)
			TextView mRate;
			@BindView(R.id.stopTrade)
			TextView mStopTrade;
			@BindView(R.id.trade)
			LinearLayout mTrade;
			private Button mDel;
			ViewHolder(View content,View menu) {
				ButterKnife.bind(this, content);
				mDel = (Button)menu.findViewById(R.id.del);
			}
			private void bindDataWithView(Variety item, int position, Context context) {
				mFutureName.setText(item.getVarietyName());
				mFutureCode.setText(item.getContractsCode());
				mLastPrice.setText("66.66");
				mRate.setText("+66.00%");
			}
		}
	}

}
