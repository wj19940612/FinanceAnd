package com.sbai.finance.activity.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.EventModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-18.
 */

public class EventActivity extends BaseActivity {
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private EventListAdapter mEventListAdapter;
	private List<EventModel> mListEvent;

	private TextView mFootView;
	private int mPageSize = 15;
	private int mPageNo = 0;
	private HashSet<String> mSet;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mSet = new HashSet<>();
		mEventListAdapter = new EventListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEventListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mEventListAdapter.getItem(position).getUrl();
			}
		});
		initSwipeRefreshLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestEventList();
	}

	private void requestEventList() {
		Client.getBreakingNewsData(mPageNo,mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<EventModel>,EventModel>() {
					@Override
					protected void onRespSuccessData(EventModel data) {
						updateEventInfo((ArrayList<EventModel.DataBean>) data.getData());
					}
					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
					}
				}).fire();
	}
    private void updateEventInfo(ArrayList<EventModel.DataBean> eventList){
        if (eventList.isEmpty()){
			stopRefreshAnimation();
			return;
		}
		if (mFootView == null){
			mFootView = new TextView(this);
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
			mFootView.setPadding(padding, padding, padding, padding);
			mFootView.setText(getText(R.string.load_more));
			mFootView.setGravity(Gravity.CENTER);
			mFootView.setTextColor(Color.WHITE);
			mFootView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPageNo++;
					requestEventList();
				}
			});
			mListView.addFooterView(mFootView);
		}
		if (eventList.size()<mPageSize){
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}
		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mEventListAdapter != null) {
				mEventListAdapter.clear();
				mEventListAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}
		for (EventModel.DataBean data : eventList) {
			if (mSet.add(data.getId())) {
				if (mEventListAdapter != null) {
					mEventListAdapter.add(data);
				}
			}
		}
		mEventListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPageNo = 0;
				requestEventList();
			}
		});
	}


	 static class EventListAdapter extends ArrayAdapter<EventModel.DataBean> {
		Context mContext;
		public EventListAdapter(@NonNull Context context){
			super(context,0);
			mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position), position, mContext);
			return convertView;
		}

		static class ViewHolder{
			@BindView(R.id.eventSource)
			TextView mEventSource;
			@BindView(R.id.eventTime)
			TextView mEventTime;
			@BindView(R.id.eventTitle)
			TextView mEventTitle;
			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
			private void bindDataWithView(EventModel.DataBean item, int position, Context context) {
				mEventSource.setText(item.getSource());
				mEventTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				mEventTitle.setText(item.getTitle());
			}

		}
	}
}
