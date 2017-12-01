package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.miss.radio.RadioStationPlayActivity;
import com.sbai.finance.model.mine.MyCollect;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.ListEmptyView;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\11\27 0027.
 */

public class MyCollectAudioFragment extends BaseFragment {

    private Unbinder mBinder;

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(R.id.listEmptyView)
    ListEmptyView mListEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private CollectAudioAdapter mCollectAudioAdapter;
    private int mPage;
    private HashSet<Integer> mSet;

    public MyCollectAudioFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_audio, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        mSet = new HashSet<>();
        initListEmptyView();
        mListView.setEmptyView(mListEmptyView);
        mCollectAudioAdapter = new CollectAudioAdapter(getActivity());
        mListView.setAdapter(mCollectAudioAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCollect question = (MyCollect) parent.getAdapter().getItem(position);
                if (question.getShow() != 0) {
                    Launcher.with(getActivity(), RadioStationPlayActivity.class).putExtra(ExtraKeys.IAudio, question.getDataId()).execute();
                }
            }
        });

        initSwipeRefreshLayout();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestMyAudioCollect();
            }
        });
    }

    private void refreshData() {
        mPage = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
        requestMyAudioCollect();
    }

    private void requestMyAudioCollect() {
        Client.requestMyCollection(MyCollect.COLLECTI_TYPE_AUDIO, mPage)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<MyCollect>>, List<MyCollect>>() {
                    @Override
                    protected void onRespSuccessData(List<MyCollect> data) {
                        updateAudioCollectionList(data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateAudioCollectionList(List<MyCollect> data) {
        if (data == null) return;
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (mSet.isEmpty()) {
            mCollectAudioAdapter.clear();
        }

        for (MyCollect result : data) {
            if (mSet.add(result.getId())) {
                mCollectAudioAdapter.add(result);
            }
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initListEmptyView() {
        mListEmptyView.setGoingText(R.string.look_audio);
        mListEmptyView.setContentText(R.string.you_not_has_audio_collection);
        mListEmptyView.setHintText(R.string.collect_favorite_audio_here);
        mListEmptyView.setOnGoingViewClickListener(new ListEmptyView.OnGoingViewClickListener() {
            @Override
            public void onGoingViewClick() {
                Launcher.with(getActivity(), MainActivity.class)
                        .putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, MainActivity.PAGE_POSITION_MISS)
                        .execute();
                getActivity().finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    static class CollectAudioAdapter extends ArrayAdapter<MyCollect> {

        private Context mContext;

        public CollectAudioAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_collect_audio, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, position, getCount());
            return convertView;
        }


        class ViewHolder {
            @BindView(R.id.cover)
            ImageView mCover;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.hostName)
            TextView mHostName;
            @BindView(R.id.status)
            TextView mStatus;
            @BindView(R.id.split)
            View mSplit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(MyCollect item, Context context, int position, int count) {
                GlideApp.with(context).load(item.getAudioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .centerCrop()
                        .into(mCover);

                mTitle.setText(item.getAudioName());

                mHostName.setText(String.valueOf(item.getRadioHostName()));

                mStatus.setVisibility(item.getShow() == 1 ? View.GONE : View.VISIBLE); //该音频已下架
                if (position == count - 1) {
                    mSplit.setVisibility(View.GONE);
                }
            }
        }
    }
}
