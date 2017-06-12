package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lixiaokuan0819 on 2017/6/12.
 */

public class EconomicCircleNewMessageActivity extends BaseActivity {
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	AppCompatTextView mEmpty;
	private List<HistoryNewsModel> mHistoryNewsModelList;
	private EconomicCircleNewsAdapter mEconomicCircleNewsAdapter;

	private int mPage = 0;
	private int mSize = 15;
	private Long mCreateTime;
	private HashSet<Integer> mSet;
	private View mFootView;
	private View mCheckoutEarlierNewsFootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_economic_circle_news);
		ButterKnife.bind(this);
		mHistoryNewsModelList = new ArrayList<>();
		mSet = new HashSet<>();
		mListView.setEmptyView(mEmpty);
		mEconomicCircleNewsAdapter = new EconomicCircleNewsAdapter(this);
		mListView.setAdapter(mEconomicCircleNewsAdapter);
		requestEconomicCircleNewsList();
	}

	private void requestEconomicCircleNewsList() {
		Client.getHistoryNews(HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE_NEWS, true, null, null, 0, null)
				.setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
					@Override
					protected void onRespSuccessData(List<HistoryNewsModel> historyNewsModelList) {
						mHistoryNewsModelList = historyNewsModelList;
						updateEconomicCircleNewsList();
					}
				}).fire();
	}

	private void loadMoreEconomicCircleNewsList() {
		Client.getHistoryNews(HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE_NEWS, true, mPage, mSize, 1, mCreateTime)
				.setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<HistoryNewsModel>>, List<HistoryNewsModel>>() {
					@Override
					protected void onRespSuccessData(List<HistoryNewsModel> historyNewsModelList) {
						mHistoryNewsModelList = historyNewsModelList;
						loadMore();
					}
				}).fire();
	}

	private void loadMore() {
		if (mFootView == null) {
			mFootView = View.inflate(getActivity(), R.layout.view_footer_load_more, null);
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCreateTime = mHistoryNewsModelList.get(mHistoryNewsModelList.size() - 1).getCreateDate();
					updateEconomicCircleNewsList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (mHistoryNewsModelList.size() < mSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		for (HistoryNewsModel historyNewsModel : mHistoryNewsModelList) {
			if (mSet.add(historyNewsModel.getId())) {
				mEconomicCircleNewsAdapter.add(historyNewsModel);
			}
		}
	}

	private void updateEconomicCircleNewsList() {
		if (mCheckoutEarlierNewsFootView == null) {
			mCheckoutEarlierNewsFootView =  View.inflate(this, R.layout.view_footer_economic_circle_news, null);
			mCheckoutEarlierNewsFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCreateTime = mHistoryNewsModelList.get(mHistoryNewsModelList.size() - 1).getCreateDate();
					loadMoreEconomicCircleNewsList();
					mListView.removeFooterView(mCheckoutEarlierNewsFootView);
					mCheckoutEarlierNewsFootView = null;
				}
			});

			mListView.addFooterView(mCheckoutEarlierNewsFootView, null, true);
		}

		mEconomicCircleNewsAdapter.clear();
		mEconomicCircleNewsAdapter.addAll(mHistoryNewsModelList);
	}


	static class EconomicCircleNewsAdapter extends ArrayAdapter<HistoryNewsModel> {

		private EconomicCircleNewsAdapter(@NonNull Context context) {
			super(context, 0);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_economic_circle_news, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position), parent.getContext());
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.userHeadImage)
			AppCompatImageView mUserHeadImage;
			@BindView(R.id.userName)
			AppCompatTextView mUserName;
			@BindView(R.id.content)
			AppCompatTextView mContent;
			@BindView(R.id.time)
			AppCompatTextView mTime;
			@BindView(R.id.presentation)
			AppCompatTextView mPresentation;
			@BindView(R.id.rightPicture)
			AppCompatImageView mRightPicture;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindDataWithView(HistoryNewsModel item, Context context) {
				if (item == null) return;

				Glide.with(context).load(item.getData().getContentImg())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mUserHeadImage);

				mUserName.setText(item.getSourceUser().getUserName());
				mContent.setText(item.getMsg());
				mTime.setText(DateUtil.getFormatTime(item.getCreateDate()));


			}
		}
	}
}
