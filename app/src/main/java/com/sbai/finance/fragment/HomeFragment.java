package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sbai.finance.R;
import com.sbai.finance.model.Information;
import com.sbai.finance.view.HomeBanner;
import com.sbai.finance.view.HomeHeader;
import com.sbai.finance.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017-04-14.
 */

public class HomeFragment extends BaseFragment {
	@BindView(R.id.event)
	TextView mEvent;
	@BindView(R.id.homeBanner)
	HomeBanner mHomeBanner;
	@BindView(R.id.homeHeader)
	HomeHeader mHomeHeader;
	@BindView(R.id.topicGv)
	MyGridView mTopicGv;
	@BindView(R.id.nestedScrollView)
	ScrollView mNestedScrollView;
	@BindView(R.id.borrowMoney)
	LinearLayout mBorrowMoney;
	@BindView(R.id.borrowTitle)
	TextView mBorrowTitle;
	@BindView(R.id.borrowDetail)
	TextView mBorrowDetail;
	@BindView(R.id.idea)
	LinearLayout mIdea;
	@BindView(R.id.ideaTitle)
	TextView mIdeaTitle;
	@BindView(R.id.ideaDetail)
	TextView mIdeaDetail;
	private Unbinder unbinder;
	private TopicGridAdapter mTopicGridAdapter;
	private List<String> mListStrs;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTopicGridAdapter = new TopicGridAdapter(getContext());
		mTopicGv.setAdapter(mTopicGridAdapter);
		mNestedScrollView.post(new Runnable() {
			@Override
			public void run() {
				mNestedScrollView.fullScroll(ScrollView.FOCUS_UP);
			}
		});
		mHomeBanner.setListener(new HomeBanner.OnViewClickListener() {
			@Override
			public void onBannerClick(Information information) {

			}
		});
		mHomeHeader.setOnViewClickListener(new HomeHeader.OnViewClickListener() {
			@Override
			public void onFutureClick() {

			}

			@Override
			public void onStockClick() {

			}

			@Override
			public void onHelpClick() {

			}

			@Override
			public void onSelfChoiceClick() {

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mListStrs = new ArrayList<>();
		mListStrs.add(new String("aaaaa"));
		mListStrs.add(new String("bbbbb"));
		mListStrs.add(new String("cccc"));
		mListStrs.add(new String("ddddd"));
		mListStrs.add(new String("aaaaa"));
		mListStrs.add(new String("bbbbb"));
		mListStrs.add(new String("cccc"));
		mListStrs.add(new String("ddddd"));
		mListStrs.add(new String("aaaaa"));
		mListStrs.add(new String("bbbbb"));
		mListStrs.add(new String("cccc"));
		mListStrs.add(new String("ddddd"));
		mListStrs.add(new String("aaaaa"));
		mListStrs.add(new String("bbbbb"));
		mListStrs.add(new String("cccc"));
		mTopicGridAdapter.addAll(mListStrs);
		mTopicGridAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
	@OnClick({R.id.borrowMoney,R.id.idea})
	public void onClick(View view){

	}
	static class TopicGridAdapter extends ArrayAdapter<String>{

		public TopicGridAdapter(@NonNull Context context) {
			super(context,0);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder ;
			if (convertView == null){
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_topic,null);
				viewHolder = new ViewHolder(convertView,getContext());
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(getItem(position));
			return  convertView;
		}
}
		static class ViewHolder{
			@BindView(R.id.topicTitle)
			TextView mTopicTitle;
			@BindView(R.id.topicDetail)
			TextView mTopicDetail;
			@BindView(R.id.topicBg)
			LinearLayout mTopicBg;
			private Context mContext;
			ViewHolder(View view,Context context){
				mContext = context;
				ButterKnife.bind(this,view);
			}
			public void bindingData(String stockInfo){
				mTopicTitle.setText(stockInfo);
				mTopicDetail.setText(stockInfo);
				mTopicBg.setBackgroundResource(R.mipmap.ic_launcher_round);
			}
			@OnClick(R.id.topicBg)
			public void onClick(View view){
				Toast.makeText(mContext, String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
			}
		}
}
