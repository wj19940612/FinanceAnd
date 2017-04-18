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
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.home.ForeignFutureFragment;
import com.sbai.finance.model.BigEvent;
import com.sbai.finance.model.FutureHq;

import java.util.ArrayList;
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

	private EventListAdapter mEventListAdapter;
	private List<BigEvent> mListEvent;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mEventListAdapter = new EventListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEventListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateEventInfo();
	}

	private void updateEventInfo() {
		if (mListEvent == null){
			mListEvent =  new ArrayList<>();
		}
		for (int i= 0;i<5;i++){
			BigEvent bigEvent = new BigEvent();
			bigEvent.setEventSource("中国财经网");
			bigEvent.setEventTime("2017/04/18 22:22");
			bigEvent.setEventTitle("证监会刘主席发言:鼓励上市公司分红");
			mListEvent.add(bigEvent);
		}
		mEventListAdapter.clear();
		mEventListAdapter.addAll(mListEvent);
		mEventListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	 static class EventListAdapter extends ArrayAdapter<BigEvent> {
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
			private void bindDataWithView(BigEvent item, int position, Context context) {
				mEventSource.setText(item.getEventSource());
				mEventTime.setText(item.getEventTime());
				mEventTitle.setText(item.getEventTitle());

			}

		}
	}
}
