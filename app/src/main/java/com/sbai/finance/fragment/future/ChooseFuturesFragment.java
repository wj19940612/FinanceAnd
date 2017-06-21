package com.sbai.finance.fragment.future;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class ChooseFuturesFragment extends BaseFragment implements AdapterView.OnItemClickListener {


	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	private Unbinder unbinder;

	private FutureListAdapter mFutureListAdapter;

	private String mFutureType;
	private int mPage = 0;
	private HashSet<String> mSet;

	public static ChooseFuturesFragment newInstance(String type) {
		ChooseFuturesFragment fragment = new ChooseFuturesFragment();
		Bundle bundle = new Bundle();
		bundle.putString("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mFutureType = getArguments().getString("type");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_choose_futures, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mSet = new HashSet<>();
		mFutureListAdapter = new FutureListAdapter(getActivity());
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mFutureListAdapter);
		mListView.setOnItemClickListener(this);
		requestVarietyList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Variety item = (Variety) parent.getItemAtPosition(position);

		mFutureListAdapter.setChecked(position);
		mFutureListAdapter.notifyDataSetInvalidated();

		Intent intent = new Intent();
		intent.putExtra(Launcher.EX_PAYLOAD,item.getVarietyName());
		getActivity().setResult(RESULT_OK, intent);
		getActivity().finish();
	}

	public void scrollToTop() {
		mListView.smoothScrollToPosition(0);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public void requestVarietyList() {
		Client.getVarietyList(Variety.VAR_FUTURE, mPage, mFutureType).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
					@Override
					protected void onRespSuccessData(List<Variety> varietyList) {
						updateVarietyList(varietyList);
					}

				}).fire();
	}

	private void updateVarietyList(List<Variety> varietyList) {
		mFutureListAdapter.clear();
		mFutureListAdapter.addAll(varietyList);
		mFutureListAdapter.notifyDataSetChanged();
	}


	static class FutureListAdapter extends ArrayAdapter<Variety> {

		private Context mContext;
		private int mChecked = -1;

		private FutureListAdapter(Context context) {
			super(context, 0);
			mContext = context;
		}

		private void setChecked(int checked) {
			this.mChecked = checked;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_choose_futures, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, getItem(position), mChecked, position);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.futureName)
			TextView mFutureName;
			@BindView(R.id.futureCode)
			TextView mFutureCode;
			@BindView(R.id.checkboxClick)
			ImageView mCheckboxClick;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(Context context, Variety item, int checked, int position) {

				mFutureName.setText(item.getVarietyName());
				if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
					mFutureCode.setText(item.getContractsCode());
				} else if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
					mFutureCode.setText(item.getVarietyType());
				}

				if (checked == position) {
					mCheckboxClick.setVisibility(View.VISIBLE);
				} else {
					mCheckboxClick.setVisibility(View.GONE);
				}
			}
		}
	}
}
