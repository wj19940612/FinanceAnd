package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mutual.BorrowDetailsActivity;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lixiaokuan0819 on 2017/6/12.
 */

public class EconomicCircleNewMessageActivity extends BaseActivity implements AdapterView.OnItemClickListener {
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	AppCompatTextView mEmpty;
	private List<HistoryNewsModel> mHistoryNewsModelList;
	private EconomicCircleNewsAdapter mEconomicCircleNewsAdapter;

	private Long mCreateTime = null;
	private HashSet<Integer> mSet;
	private View mFootView;
	private View mCheckoutEarlierNewsFootView;
	private int mSize = 15;

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
		mListView.setOnItemClickListener(this);
		requestEconomicCircleNewsList();
	}

	private void requestEconomicCircleNewsList() {
		Client.requestHistoryNews(true, HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE_NEWS, null, null, 0, mCreateTime)
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
		Client.requestHistoryNews(true, HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE_NEWS, null, mSize, 1, mCreateTime)
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
					mCreateTime = mHistoryNewsModelList.get(mHistoryNewsModelList.size() - 1).getCreateTime();
					loadMoreEconomicCircleNewsList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (mHistoryNewsModelList.size() < 15) {
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
			mCheckoutEarlierNewsFootView = View.inflate(this, R.layout.view_footer_economic_circle_news, null);
			mCheckoutEarlierNewsFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCreateTime = mHistoryNewsModelList.get(mHistoryNewsModelList.size() - 1).getCreateTime();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HistoryNewsModel item = (HistoryNewsModel) parent.getItemAtPosition(position);
		if (item.getClassify() == 3) {
			Launcher.with(this, OpinionDetailsActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getDataId())
					.execute();
		} else if (item.getClassify() == 2) {
			Launcher.with(this, BorrowDetailsActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getDataId())
					.execute();
		}
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
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.message)
			TextView mMessage;
			@BindView(R.id.time)
			TextView mTime;
			@BindView(R.id.content)
			TextView mContent;
			@BindView(R.id.borrowMoneyImg)
			ImageView mBorrowMoneyImg;
			@BindView(R.id.contentArea)
			RelativeLayout mContentArea;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindDataWithView(HistoryNewsModel item, Context context) {
				if (item == null) return;

				if (item.getSourceUser() != null) {
					Glide.with(context).load(item.getSourceUser().getUserPortrait())
							.placeholder(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);
					mUserName.setText(item.getSourceUser().getUserName());
				}

				mMessage.setText(item.getMsg());
				mTime.setText(DateUtil.getFormatTime(item.getCreateTime()));

				if (item.getClassify() == 3) {
					mContent.setVisibility(View.VISIBLE);
					if (item.getData() != null) {
						mContent.setText(item.getData().getContent());
					}
				} else if (item.getClassify() == 2) {
					if (!TextUtils.isEmpty(item.getData().getContentImg())) {
						mBorrowMoneyImg.setVisibility(View.VISIBLE);
						mContent.setVisibility(View.GONE);
						Glide.with(context).load(item.getData().getContentImg().split(",")[0])
								.placeholder(R.drawable.ic_loading_pic)
								.into(mBorrowMoneyImg);
					} else {
						mContent.setVisibility(View.VISIBLE);
						mBorrowMoneyImg.setVisibility(View.GONE);
						HistoryNewsModel.DataBean data = item.getData();
						if (data != null) {
							String content = context.getString(R.string.amount, FinanceUtil.formatWithScaleNoZero(data.getMoney())) + "\n"
									+ context.getString(R.string.limit, FinanceUtil.formatWithScaleNoZero(data.getDays())) + "\n"
									+ context.getString(R.string.interest, FinanceUtil.formatWithScaleNoZero(data.getInterest()));
							mContent.setText(content);
						}
					}
				}
			}
		}
	}
}
