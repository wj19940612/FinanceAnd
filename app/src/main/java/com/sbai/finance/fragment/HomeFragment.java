package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureListActivity;
import com.sbai.finance.activity.home.BorrowMoneyActivity;
import com.sbai.finance.activity.home.EventActivity;
import com.sbai.finance.activity.home.OptionActivity;
import com.sbai.finance.activity.home.TopicActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mutual.MutualActivity;
import com.sbai.finance.activity.opinion.OpinionActivity;
import com.sbai.finance.activity.stock.StockListActivity;
import com.sbai.finance.activity.web.BannerActivity;
import com.sbai.finance.activity.web.HideTitleWebActivity;
import com.sbai.finance.model.BannerModel;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Topic;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.HomeBanner;
import com.sbai.finance.view.HomeHeader;
import com.sbai.finance.view.MyGridView;
import com.sbai.httplib.CookieManger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
                Topic topic = (Topic) parent.getItemAtPosition(position);
                if (topic != null) {
                    Launcher.with(getContext(), TopicActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, topic)
                            .execute();
                }
            }
        });
        mHomeBanner.setListener(new HomeBanner.OnViewClickListener() {
            @Override
            public void onBannerClick(BannerModel information) {
                if (information.isH5Style()) {
                    Launcher.with(getActivity(), HideTitleWebActivity.class)
                            .putExtra(HideTitleWebActivity.EX_URL, information.getContent())
                            .putExtra(HideTitleWebActivity.EX_TITLE, information.getTitle())
                            .putExtra(HideTitleWebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                } else {
                    Launcher.with(getActivity(), BannerActivity.class)
                            .putExtra(BannerActivity.EX_HTML, information.getContent())
                            .putExtra(BannerActivity.EX_TITLE, information.getTitle())
                            .putExtra(BannerActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                            .execute();
                }
            }
        });
        mHomeHeader.setOnViewClickListener(new HomeHeader.OnViewClickListener() {
            @Override
            public void onFutureClick() {
                Launcher.with(getActivity(), FutureListActivity.class).execute();
            }

            @Override
            public void onStockClick() {
                Launcher.with(getActivity(), StockListActivity.class).execute();
            }

            @Override
            public void onHelpClick() {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), MutualActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onSelfChoiceClick() {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), OptionActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
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
    public void onResume() {
        super.onResume();
        startScheduleJob(5 * 1000);
        updateHomeInfo();
    }


    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (getUserVisibleHint()) {
            mHomeBanner.nextAdvertisement();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            startScheduleJob(5 * 1000);
        } else {
            stopScheduleJob();
        }
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
                .setCallback(new Callback<Resp<String>>() {
                    @Override
                    protected void onRespSuccess(Resp<String> resp) {
                        if (!TextUtils.isEmpty(resp.getData())) {
                            updateEventInfo(resp.getData());
                        }
                    }
                }).fire();
        //获取主题信息
        Client.getTopicData().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Topic>>, List<Topic>>() {
                    @Override
                    protected void onRespSuccessData(List<Topic> data) {
                        updateTopicInfo((ArrayList<Topic>) data);
                    }
                }).fire();
    }

    private void updateEventInfo(String data) {
        mEvent.setText(data);
    }

    private void updateTopicInfo(ArrayList<Topic> topics) {
        mTopicGridAdapter.clear();
        mTopicGridAdapter.addAll(topics);
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
                Launcher.with(getActivity(), BorrowMoneyActivity.class).execute();
                break;
            case R.id.idea:
                Launcher.with(getActivity(), OpinionActivity.class).execute();
                break;
            default:
                break;

        }
    }

    static class TopicGridAdapter extends ArrayAdapter<Topic> {

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

            public void bindingData(Topic item) {
                mTopicTitle.setText(item.getTitle());
                mTopicDetail.setText(item.getSubTitle());
                Glide.with(mContext).load(item.getBackgroundImg()).placeholder(R.drawable.bg_topic).into(mTopicImg);
            }
        }
    }

}