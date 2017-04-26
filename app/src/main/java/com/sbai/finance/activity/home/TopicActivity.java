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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.FutureHq;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-18.
 */

public class TopicActivity extends BaseActivity {
	@BindView(R.id.top)
	LinearLayout mTop;
	@BindView(R.id.back)
	ImageView mBack;
	@BindView(R.id.title)
	TextView mTitle;
	@BindView(R.id.topicTitle)
	TextView mTopicTitle;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	private TopicListAdapter mTopicListAdapter;
	private List<FutureHq> mListHq;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		ButterKnife.bind(this);
		translucentStatusBar();
		initView();
	}

	private void initView() {
		mTopicListAdapter = new TopicListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mTopicListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateTopicInfo();
	}
	@OnClick(R.id.back)
	public void onClick(View view){
		this.onBackPressed();
	}

	private void updateTopicInfo() {
		if (mListHq==null){
			mListHq = new ArrayList<>();
		}
		for (int i =0;i<10;i++){
			FutureHq futureHq = new FutureHq();
			futureHq.setCodeName("美原油");
			futureHq.setInstrumentId("CL1709");
			futureHq.setLastPrice(66.66);
			futureHq.setUpDropSpeed(25.00);
			mListHq.add(futureHq);
		}
		mTopicListAdapter.clear();
		mTopicListAdapter.addAll(mListHq);
		mTopicListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	static class TopicListAdapter extends ArrayAdapter<FutureHq>{

		Context mContext;
		public TopicListAdapter(@NonNull Context context){
			super(context,0);
			mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
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
			ViewHolder(View view) {
				ButterKnife.bind(this, view);
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
