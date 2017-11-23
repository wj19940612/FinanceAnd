package com.sbai.finance.activity.miss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.miss.AudioInfo;
import com.sbai.finance.model.miss.RadioInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class RadioStationDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.root)
    LinearLayout mRoot;

    ImageView mCover;
    TextView mSubscribe;
    ImageView mAvatar;
    TextView mName;
    TextView mListenerNumber;
    TextView mContent;
    TextView mBtnLookMore;

    private int mRadioStationId;
    private RadioStationAdapter mRadioStationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_radio_station_detail);
        ButterKnife.bind(this);

        initData(getIntent());
        initHeaderView();

        mRadioStationAdapter = new RadioStationAdapter(this);
        mListView.setAdapter(mRadioStationAdapter);
        mListView.setOnItemClickListener(this);

        initSwipeRefreshLayout();
        requestRadioStationDetail();
        requestRadioProgram();
    }

    private void initData(Intent intent) {
        mRadioStationId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_radio_station_detail, mRoot, false);
        mCover = header.findViewById(R.id.cover);
        mSubscribe = header.findViewById(R.id.subscribe);
        mAvatar = header.findViewById(R.id.avatar);
        mName = header.findViewById(R.id.name);
        mListenerNumber = header.findViewById(R.id.listenerNumber);
        mContent = header.findViewById(R.id.content);
        mBtnLookMore = header.findViewById(R.id.btnLookMore);
        mBtnLookMore.setTag(false);
        mBtnLookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasExpand = (boolean) view.getTag();
                expandOrStopContent(hasExpand);
            }
        });
        mListView.addHeaderView(header);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRadioStationDetail();
                requestRadioProgram();
            }
        });
    }

    private void expandOrStopContent(boolean hasExpand) {
        if (hasExpand) {
            //已经展开,收起
            mBtnLookMore.setText(R.string.pack_up);
            mContent.setMaxLines(Integer.MAX_VALUE);
        } else {
            mBtnLookMore.setText(R.string.look_more);
            mContent.setMaxLines(2);
            mContent.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    private void requestRadioStationDetail() {
        Client.requestRadioDetail(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<RadioInfo>, RadioInfo>() {
            @Override
            protected void onRespSuccessData(RadioInfo radioInfo) {
                if (radioInfo != null) {
                    updateRadioDetail(radioInfo);
                }
            }
        }).fireFree();
    }

    private void updateRadioDetail(RadioInfo radioInfo) {
        GlideApp.with(this).load(radioInfo.getRadioCover())
                .placeholder(R.drawable.ic_default_image)
                .into(mCover);

//        GlideApp.with(this).load(radioInfo.get())
//                .placeholder(R.drawable.ic_default_avatar)
//                .into(mAvatar);

//        mName.setText(radioInfo.ge);

        mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 订阅接口
            }
        });
        mContent.setText(radioInfo.getRadioIntroduction());
        mListenerNumber.setText(radioInfo.getListenNumber());
    }

    private void requestRadioProgram() {
        Client.requestRadioDetail(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<List<AudioInfo>>, List<AudioInfo>>() {


            @Override
            protected void onRespSuccessData(List<AudioInfo> data) {
                if (data != null) {
                    updateAudio(data);
                }
            }
        }).fireFree();
    }

    private void updateAudio(List<AudioInfo> data) {
        if(mRadioStationAdapter != null){
            mRadioStationAdapter.clear();
        }
        for(AudioInfo audioInfo : data){
            mRadioStationAdapter.add(audioInfo);
        }
    }

    static class RadioStationAdapter extends ArrayAdapter<AudioInfo> {
        private Context mContext;

        private RadioStationAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_radio_station_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(mContext, getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.cover)
            ImageView mCover;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.time)
            TextView mTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, AudioInfo item) {
                GlideApp.with(context).load(item.getAudioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .into(mCover);

                mTitle.setText(item.getAudioName());

                mListenerNumber.setText(item.getViewNumber());

                mTime.setText(DateUtil.formatDefaultStyleTime(item.getModifyTime()));
            }
        }
    }
}
