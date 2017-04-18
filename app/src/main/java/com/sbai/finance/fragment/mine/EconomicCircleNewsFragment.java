package com.sbai.finance.fragment.mine;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.mine.EconomicCircleNewModel;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EconomicCircleNewsFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private EconomicCircleNewsAdapter mEconomicCircleNewsAdapter;
    private TextView mFootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_economic_circle_news, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmpty);
        mEconomicCircleNewsAdapter = new EconomicCircleNewsAdapter(getActivity());
        mListView.setAdapter(mEconomicCircleNewsAdapter);
        mListView.setOnScrollListener(this);
        requestEconomicCircleNewsList();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestEconomicCircleNewsList();
            }
        });
    }

    // TODO: 2017/4/18 后期删除 
    private String url[] = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510917267&di=d5b3057b37d5c83964230849e42cfead&imgtype=0&src=http%3A%2F%2Fpic1.cxtuku.com%2F00%2F15%2F11%2Fb998b8878108.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510860938&di=64f5b45b80c90746513b448207191e4f&imgtype=0&src=http%3A%2F%2Fpic7.nipic.com%2F20100613%2F3823726_085130049412_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492510590388&di=034d5a13126feef4ed18beff5dfe9e50&imgtype=0&src=http%3A%2F%2Fpic38.nipic.com%2F20140228%2F8821914_204428973000_2.jpg"};

    private void requestEconomicCircleNewsList() {
        ArrayList<EconomicCircleNewModel> economicCircleNewModels = new ArrayList<>();
        for (int i = 0; i < url.length; i++) {
            EconomicCircleNewModel hahahha = new EconomicCircleNewModel(url[i], "" + i + i + i, "hahahha", 1425275145000l);
            economicCircleNewModels.add(hahahha);
        }
        updateEconomicCircleData(economicCircleNewModels);

    }

    private void updateEconomicCircleData(ArrayList<EconomicCircleNewModel> economicCircleNewModels) {
        if (economicCircleNewModels == null) {
            stopRefreshAnimation();
            return;
        }
        if (mFootView == null) {
            mFootView = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            mFootView.setPadding(padding, padding, padding, padding);
            mFootView.setText(getText(R.string.load_more));
            mFootView.setGravity(Gravity.CENTER);
            mFootView.setTextColor(Color.WHITE);
            mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            mFootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
//                    mPageNo++;
                    requestEconomicCircleNewsList();
                }
            });
            mListView.addFooterView(mFootView);
        }

//        if (economicCircleNewModels.size() < mPageSize) {
        if (economicCircleNewModels.size() < 15) {
            mListView.removeFooterView(mFootView);
            mFootView = null;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mEconomicCircleNewsAdapter != null) {
                mEconomicCircleNewsAdapter.clear();
                mEconomicCircleNewsAdapter.notifyDataSetChanged();
            }
            stopRefreshAnimation();
        }
        mEconomicCircleNewsAdapter.addAll(economicCircleNewModels);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
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

    static class EconomicCircleNewsAdapter extends ArrayAdapter<EconomicCircleNewModel> {
        private Context mContext;

        public EconomicCircleNewsAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_new, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindViewWithData(getItem(position), mContext, position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.userHeadImage)
            AppCompatImageView mUserHeadImage;
            @BindView(R.id.userAction)
            AppCompatTextView mUserAction;
            @BindView(R.id.content)
            AppCompatTextView mContent;
            @BindView(R.id.time)
            AppCompatTextView mTime;
            @BindView(R.id.presentation)
            AppCompatTextView mPresentation;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindViewWithData(EconomicCircleNewModel item, Context context, int position) {
                if (!TextUtils.isEmpty(item.getUserImage())) {
                    Glide.with(context).load(item.getUserImage())
                            .bitmapTransform(new GlideCircleTransform(context))
                            .placeholder(R.mipmap.ic_launcher_round)
                            .into(mUserHeadImage);
                }

                SpannableString spannableString = StrUtil.mergeTextWithColor(item.getUserName(), "   " + "关注你", Color.BLACK);
                mUserAction.setText(spannableString);
                if (!TextUtils.isEmpty(item.getContent())) {
                    mContent.setVisibility(View.VISIBLE);
                    mContent.setText(item.getContent());
                } else {
                    mContent.setVisibility(View.GONE);
                }

                mTime.setText(DateUtil.getFormatTime(item.getTime()));

            }
        }
    }

}
