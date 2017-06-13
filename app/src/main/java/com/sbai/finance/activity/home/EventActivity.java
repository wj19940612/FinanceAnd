package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.web.EventDetailActivity;
import com.sbai.finance.model.EventModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.httplib.CookieManger;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017-04-18.
 */

public class EventActivity extends BaseActivity  implements AbsListView.OnScrollListener,CustomSwipeRefreshLayout.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener{
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	CustomSwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.titleBar)
	TitleBar mTitleBar;

	private EventListAdapter mEventListAdapter;
	private int mPageSize = 30;
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
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setOnLoadMoreListener(this);
		mSwipeRefreshLayout.setAdapter(mListView,mEventListAdapter);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEventListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EventModel dataBean = mEventListAdapter.getItem(position);
				Client.getBreakingNewsDetailData(dataBean.getId()).setTag(TAG)
						.setCallback(new Callback2D<Resp<EventModel>,EventModel>() {
							@Override
							protected void onRespSuccessData(EventModel data) {
								//统计点击次数
							}
						}).fireSync();
				Launcher.with(getActivity(), EventDetailActivity.class)
							.putExtra(EventDetailActivity.EX_EVENT, dataBean)
							.putExtra(EventDetailActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
							.execute();
			}
		});
	//	mListView.setOnScrollListener(this);
		initSwipeRefreshLayout();

		reset();
		requestEventList();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void requestEventList() {
		Client.getBreakingNewsData(mPageNo,mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<EventModel>>,List<EventModel>>() {
					@Override
					protected void onRespSuccessData(List<EventModel> data) {
						updateEventInfo(data);
					}
					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
					}
				}).fire();
	}
    private void updateEventInfo(List<EventModel> eventList){
		stopRefreshAnimation();
		if (mSet.isEmpty()){
			mEventListAdapter.clear();
		}
		for (EventModel eventModel:eventList){
			if (mSet.add(eventModel.getId())){
				mEventListAdapter.add(eventModel);
			}
		}
		if (eventList.size() < mPageSize) {
			mSwipeRefreshLayout.setLoadMoreEnable(false);
		} else {
			mPageNo++;
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
		if (mSwipeRefreshLayout.isLoading()) {
			mSwipeRefreshLayout.setLoading(false);
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int topRowVerticalPosition =
				(mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
		mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
	}

	@Override
	public void onLoadMore() {
		requestEventList();
	}

	@Override
	public void onRefresh() {
		reset();
		requestEventList();
	}

	private void reset() {
		mPageNo = 0;
		mSet.clear();
		mSwipeRefreshLayout.setLoadMoreEnable(true);
	}
	@OnClick(R.id.titleBar)
	public void OnClick(View view){
		mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListView.smoothScrollToPosition(0);
			}
		});

	}

	static class EventListAdapter extends ArrayAdapter<EventModel> {
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
			private void bindDataWithView(EventModel item, int position, Context context) {
				if (TextUtils.isEmpty(item.getSource())){
					mEventSource.setVisibility(View.GONE);
				}else{
					mEventSource.setVisibility(View.VISIBLE);
				}
				mEventSource.setText(item.getSource());
				mEventTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				mEventTitle.setText(item.getTitle());
			}

		}
	}
}
