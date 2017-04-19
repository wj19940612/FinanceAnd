package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
	@BindView(R.id.listView)
	SlideListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	private SlideListAdapter mSlideListAdapter;
	private List<FutureHq> mListHq;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mSlideListAdapter = new SlideListAdapter(this);
		mSlideListAdapter.setOnDelClickListener(new SlideListAdapter.OnDelClickListener() {
			@Override
			public void onClick(int position) {
				mSlideListAdapter.remove(mSlideListAdapter.getItem(position));
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
		updateOptionInfo();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private void updateOptionInfo() {
		if (mListHq==null){
			mListHq = new ArrayList<>();
		}
		for (int i =0;i<10;i++){
			FutureHq futureHq = new FutureHq();
			futureHq.setCodeName("美原油"+i);
			futureHq.setInstrumentId("CL1709");
			futureHq.setLastPrice(66.66);
			futureHq.setUpDropSpeed(25.00);
			mListHq.add(futureHq);
		}
		mSlideListAdapter.clear();
		mSlideListAdapter.addAll(mListHq);
		mSlideListAdapter.notifyDataSetChanged();

	}
	public static class SlideListAdapter extends ArrayAdapter<FutureHq> {
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
				View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hq, parent, false);
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
			private void bindDataWithView(FutureHq item, int position, Context context) {
				mFutureName.setText(item.getCodeName());
				mFutureCode.setText(item.getInstrumentId());
				mLastPrice.setText(item.getLastPrice().toString());
				mRate.setText("+"+item.getUpDropSpeed().toString()+"%");
			}
		}
	}

}
