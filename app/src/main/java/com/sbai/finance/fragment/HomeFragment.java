package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureActivity;
import com.sbai.finance.activity.home.EventActivity;
import com.sbai.finance.activity.home.OptionActivity;
import com.sbai.finance.activity.home.TopicActivity;
import com.sbai.finance.activity.mutual.MutualActivity;
import com.sbai.finance.activity.stock.StockActivity;
import com.sbai.finance.model.BannerModel;
import com.sbai.finance.model.TopicModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
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
    @BindView(R.id.idea)
    LinearLayout mIdea;
    @BindView(R.id.ideaTitle)
    TextView mIdeaTitle;
    @BindView(R.id.bigEvent)
    LinearLayout mBigEvent;
    private Unbinder unbinder;
    private TopicGridAdapter mTopicGridAdapter;

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
        initView();
        mTopicGridAdapter = new TopicGridAdapter(getContext());
        mTopicGv.setAdapter(mTopicGridAdapter);
        mTopicGv.setFocusable(false);
        mTopicGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Launcher.with(getContext(), TopicActivity.class)
                        .putExtra(Launcher.KEY_TOPIC,mTopicGridAdapter.getItem(position)).execute();
            }
        });
        mHomeBanner.setListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(BannerModel information) {

            }
        });
        mHomeHeader.setOnViewClickListener(new HomeHeader.OnViewClickListener() {
            @Override
            public void onFutureClick() {
                Launcher.with(getActivity(), FutureActivity.class).execute();
            }

            @Override
            public void onStockClick() {
                Launcher.with(getActivity(), StockActivity.class).execute();
            }

            @Override
            public void onHelpClick() {
                Launcher.with(getActivity(), MutualActivity.class).execute();
            }

            @Override
            public void onSelfChoiceClick() {
                Launcher.with(getActivity(), OptionActivity.class).execute();
            }
        });
    }

    private void initView() {
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.borrow_title),
                "\n" + getString(R.string.borrow_detail), 0.733f, Color.parseColor("#B6B6B6"));
        mBorrowTitle.setText(attentionSpannableString);
        SpannableString fansSpannableString = StrUtil.mergeTextWithRatioColor(getString(R.string.idea_title),
                "\n" + getString(R.string.idea_detail), 0.733f, Color.parseColor("#B6B6B6"));
        mIdeaTitle.setText(fansSpannableString);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHomeInfo();
    }

    private void updateHomeInfo() {
        //获取banner数据
        Client.getBannerData().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<BannerModel>>, List<BannerModel>>() {
                    @Override
                    protected void onRespSuccessData(List<BannerModel> data) {
                        mHomeBanner.setHomeAdvertisement(data);
                    }
                }).fire();

       //获取最新事件标题  // TODO: 2017/4/27 服务器返回数据问题 后期做修改 
        Client.getBreakingNewsTitleData().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<String>,String>() {
                    @Override
                    protected void onRespSuccessData(String data) {
                        mEvent.setText(data);
                    }
                }).fire();
       //获取主题信息
        Client.getTopicData().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TopicModel>>,List<TopicModel>>() {
                    @Override
                    protected void onRespSuccessData(List<TopicModel> data) {
                        updateTopicInfo((ArrayList<TopicModel>) data);
                    }
                }).fire();
    }

    private void updateTopicInfo(ArrayList<TopicModel> topicModels) {
        mTopicGridAdapter.clear();
        mTopicGridAdapter.addAll(topicModels);
        mTopicGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @OnClick({R.id.borrowMoney, R.id.idea, R.id.bigEvent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bigEvent:
                Launcher.with(getActivity(), EventActivity.class).execute();
                break;
            case R.id.borrowMoney:
                break;
            case R.id.idea:
                break;
            default:
                break;

        }
    }

    static class TopicGridAdapter extends ArrayAdapter<TopicModel> {

        public TopicGridAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_topic, null);
                viewHolder = new ViewHolder(convertView, getContext());
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.topicTitle)
            TextView mTopicTitle;
            @BindView(R.id.topicDetail)
            TextView mTopicDetail;
            @BindView(R.id.topicImg)
            ImageView mTopicImg;
            private Context mContext;

            ViewHolder(View view, Context context) {
                mContext = context;
                ButterKnife.bind(this, view);
            }

            public void bindingData(TopicModel item) {
                mTopicTitle.setText(item.getTitle());
                mTopicDetail.setText(item.getSubTitle());
                mTopicImg.setBackgroundResource(R.drawable.bg_topic);
            }
        }
    }

}