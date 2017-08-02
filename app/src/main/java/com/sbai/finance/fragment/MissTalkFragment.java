package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.miss.MessagesActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.NewMessage;
import com.sbai.finance.model.missTalk.Miss;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.mediaPlayerUtil;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.R.id.missAvatar;

public class MissTalkFragment extends BaseFragment {

	@BindView(R.id.more)
	ImageView mMore;
	@BindView(R.id.message)
	ImageView mMessage;
	@BindView(R.id.redPoint)
	ImageView mRedPoint;
	Unbinder unbinder;
	@BindView(R.id.hotListView)
	MyListView mHotListView;
	@BindView(R.id.LatestListView)
	MyListView mLatestListView;

	private List<Miss> mMissList;
	private MissListAdapter mMissListAdapter;
	private HotQuestionListAdapter mHotQuestionListAdapter;
	private LatestQuestionListAdapter mLatestQuestionListAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_miss_talk, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMissList = new ArrayList<>();
		mMissListAdapter = new MissListAdapter(getActivity(), mMissList);
		mHotQuestionListAdapter = new HotQuestionListAdapter(getActivity());
		mLatestQuestionListAdapter = new LatestQuestionListAdapter(getActivity());
		//initHeaderView();
		mHotListView.setAdapter(mHotQuestionListAdapter);
		mLatestListView.setAdapter(mLatestQuestionListAdapter);
		mHotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Launcher.with(getActivity(),QuestionDetailActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item).execute();
				}
			}
		});

		mLatestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Launcher.with(getActivity(),QuestionDetailActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item).execute();
				}
			}
		});

		requestMissList();
		requestHotQuestionList();
		requestLatestQuestionList();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
			startScheduleJob(10 * 1000);
		}
	}

	@Override
	public void onTimeUp(int count) {
		super.onTimeUp(count);
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
		}
	}

	private void requestNewMessageCount() {
		Client.getNewMessageCount().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<NewMessage>>, List<NewMessage>>(false) {

					@Override
					protected void onRespSuccessData(List<NewMessage> newMessagesList) {
						int count = 0;
						for (NewMessage newMessage : newMessagesList) {
							if (newMessage.getClassify() == 4) {
								count += newMessage.getCount();
							}
						}

						if (count > 0) {
							mRedPoint.setVisibility(View.VISIBLE);
						}
					}
				}).fire();

	}

	private void requestMissList() {
		Client.getMissList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
					@Override
					protected void onRespSuccessData(List<Miss> missList) {
						updateMissList(missList);
					}
				}).fire();
	}


	private void requestHotQuestionList() {
		Client.getHotQuestionList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						updateHotQuestionList(questionList);
					}
				}).fire();
	}

	private void requestLatestQuestionList() {
		Client.getLatestQuestionList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						updateLatestQuestionList(questionList);
					}
				}).fire();
	}

	private void updateMissList(List<Miss> missList) {
		mMissListAdapter.clear();
		mMissListAdapter.addAll(missList);
	}

	private void updateHotQuestionList(List<Question> questionList) {
		mHotQuestionListAdapter.clear();
		mHotQuestionListAdapter.addAll(questionList);
	}

	private void updateLatestQuestionList(List<Question> questionList) {
		mLatestQuestionListAdapter.clear();
		mLatestQuestionListAdapter.addAll(questionList);
	}

	private void initHeaderView() {
		LinearLayout header = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.view_header_miss_talk, null);
		RecyclerView recyclerView = (RecyclerView) header.findViewById(R.id.recyclerView);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
		gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(mMissListAdapter);
		mMissListAdapter.setOnItemClickListener(new MissListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Miss item) {
				Launcher.with(getActivity(), MissProfileActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
			}
		});
		mHotListView.addHeaderView(header);
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public static class MissListAdapter extends RecyclerView.Adapter<MissListAdapter.ViewHolder> {

		private OnItemClickListener mOnItemClickListener;

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.mOnItemClickListener = onItemClickListener;
		}

		public interface OnItemClickListener {
			void onItemClick(Miss miss);
		}

		private List<Miss> mMissList;
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public MissListAdapter(Context context, List<Miss> missList) {
			this.mMissList = missList;
			this.mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		public void clear() {
			mMissList.clear();
			notifyItemRangeRemoved(0, mMissList.size());
		}

		public void addAll(List<Miss> missList) {
			mMissList.addAll(missList);
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = mLayoutInflater.inflate(R.layout.row_misstalk_list, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.bindDataWithView(mContext, mMissList.get(position), mOnItemClickListener);
		}

		@Override
		public int getItemCount() {
			return mMissList != null ? mMissList.size() : 0;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;

			public ViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}

			public void bindDataWithView(Context context, final Miss item, final OnItemClickListener onItemClickListener) {
				if (item == null) return;

				Glide.with(context).load(item.getPortrait())
						.placeholder(R.drawable.ic_default_avatar_big)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);
				mName.setText(item.getName());

				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onItemClickListener != null) {
							onItemClickListener.onItemClick(item);
						}
					}
				});
			}
		}
	}

	static class HotQuestionListAdapter extends ArrayAdapter<Question> {

		private Context mContext;

		private HotQuestionListAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_misstalk_answer, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}
			viewHolder.bindingData(mContext, getItem(position));
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;
			@BindView(R.id.askTime)
			TextView mAskTime;
			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(missAvatar)
			ImageView mMissAvatar;
			@BindView(R.id.voice)
			TextView mVoice;
			@BindView(R.id.listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.loveNumber)
			TextView mLoveNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(R.id.split)
			View mSplit;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				Glide.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mMissAvatar);

				mName.setText(item.getUserName());
				mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (item.getIsPrise() == 0) {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
				} else {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
				}

				mMissAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(context, MissProfileActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
								.execute();
					}
				});

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

							@Override
							protected void onRespSuccessData(Prise prise) {
								if (prise.getIsPrise() == 0) {
									mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
								} else {
									mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
								}
								mLoveNumber.setText(StrFormatter.getFormatCount(prise.getPriseCount()));
							}
						}).fire();
					}
				});

				mVoice.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (item.getIsPlaying() == false) {
							mediaPlayerUtil.play(item.getAnswerContext());
							item.setIsPlaying(true);
						} else {
							mediaPlayerUtil.release();
							item.setIsPlaying(false);
						}
					}
				});
			}
		}
	}

	static class LatestQuestionListAdapter extends ArrayAdapter<Question> {

		private Context mContext;

		private LatestQuestionListAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

			HotQuestionListAdapter.ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				viewHolder = (HotQuestionListAdapter.ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_misstalk_answer, null);
				viewHolder = new HotQuestionListAdapter.ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}
			viewHolder.bindingData(mContext, getItem(position));
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;
			@BindView(R.id.askTime)
			TextView mAskTime;
			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(missAvatar)
			ImageView mMissAvatar;
			@BindView(R.id.voice)
			TextView mVoice;
			@BindView(R.id.listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.loveNumber)
			TextView mLoveNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(R.id.split)
			View mSplit;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				Glide.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mMissAvatar);

				mName.setText(item.getUserName());
				mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (item.getIsPrise() == 0) {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
				} else {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
				}

				mMissAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(context, MissProfileActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
								.execute();
					}
				});

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

							@Override
							protected void onRespSuccessData(Prise prise) {
								if (prise.getIsPrise() == 0) {
									mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
								} else {
									mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
								}
								mLoveNumber.setText(StrFormatter.getFormatCount(prise.getPriseCount()));
							}
						}).fire();
					}
				});


				mVoice.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (item.getIsPlaying() == false) {
							mediaPlayerUtil.play(item.getAnswerContext());
							item.setIsPlaying(true);
						} else {
							mediaPlayerUtil.release();
							item.setIsPlaying(false);
						}
					}
				});
			}
		}
	}


	@OnClick({R.id.more, R.id.message})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.more:
				break;
			case R.id.message:
				Launcher.with(getActivity(), MessagesActivity.class).execute();
				mRedPoint.setVisibility(View.INVISIBLE);
				break;
		}
	}
}
