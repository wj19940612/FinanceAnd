package com.sbai.finance.activity.miss;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.miss.AudioInfo;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RadioInfo;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class RadioStationListActivity extends BaseActivity implements AdapterView.OnItemClickListener,MissAudioManager.OnAudioListener{

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.root)
    RelativeLayout mRoot;
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;

    ImageView mCover;
    TextView mSubscribe;
    TextView mSubscribed;
    ImageView mAvatar;
    TextView mName;
    TextView mListenerNumber;
    TextView mContent;
    TextView mBtnLookMore;
    RelativeLayout mNameLayout;

    private int mRadioStationId;
    private RadioStationAdapter mRadioStationAdapter;
    private RadioInfo mRadioInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_radio_station_list);
        ButterKnife.bind(this);

        initData(getIntent());

        mRadioStationAdapter = new RadioStationAdapter(this);
        mListView.setAdapter(mRadioStationAdapter);
        mListView.setOnItemClickListener(this);

        initFloatWindow();
        MissAudioManager.get().addAudioListener(this);

        initHeaderView();
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
        mSubscribed = header.findViewById(R.id.subscribed);
        mAvatar = header.findViewById(R.id.avatar);
        mName = header.findViewById(R.id.name);
        mListenerNumber = header.findViewById(R.id.listenerNumber);
        mContent = header.findViewById(R.id.content);
        mBtnLookMore = header.findViewById(R.id.btnLookMore);
        mNameLayout = header.findViewById(R.id.nameLayout);
        mBtnLookMore.setTag(false);
        mBtnLookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasExpand = (boolean) view.getTag();
                expandOrStopContent(hasExpand);
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRadioInfo == null) return;
                Client.collectRadio(String.valueOf(mRadioStationId)).setTag(TAG).setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            if (mRadioInfo.getIsSubscriber() == 0) {
                                mRadioInfo.setIsSubscriber(1);
                                mSubscribe.setVisibility(View.GONE);
                                mSubscribed.setVisibility(View.VISIBLE);
                            } else {
                                mRadioInfo.setIsSubscriber(0);
                                mSubscribe.setVisibility(View.VISIBLE);
                                mSubscribed.setVisibility(View.GONE);
                            }
                        }
                    }
                }).fireFree();
            }
        };
        mSubscribe.setOnClickListener(onClickListener);
        mSubscribed.setOnClickListener(onClickListener);
        mNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioInfo == null) return;
                Launcher.with(RadioStationListActivity.this, MissProfileDetailActivity.class).putExtra(Launcher.EX_PAYLOAD, mRadioInfo.getRadioHost()).execute();
            }
        });

        mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioInfo == null) return;
                Launcher.with(getActivity(), LookBigPictureActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mRadioInfo.getRadioCover())
                        .putExtra(Launcher.EX_PAYLOAD_2, 0)
                        .execute();
            }
        });
        mListView.addHeaderView(header);
    }

    private void initFloatWindow(){
        if (MissAudioManager.get().getAudio() instanceof Question) {
            final Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (MissAudioManager.get().isStarted(playingQuestion)) {
                mMissFloatWindow.setVisibility(View.VISIBLE);
                mMissFloatWindow.setMissAvatar(playingQuestion.getCustomPortrait());
                mMissFloatWindow.startAnim();
                mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);

                        Launcher.with(getActivity(), QuestionDetailActivity.class)
                                .putExtra(ExtraKeys.IS_FROM_MISS_TALK, true)
                                .putExtra(Launcher.EX_PAYLOAD, playingQuestion.getId())
                                .execute();
                    }
                });
            }
        }
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
            mBtnLookMore.setText(R.string.look_more);
            mContent.setMaxLines(2);
            mContent.setEllipsize(TextUtils.TruncateAt.END);
            mBtnLookMore.setTag(false);
        } else if (judgeIsMax()) {
            mBtnLookMore.setText(R.string.pack_up);
            mContent.setMaxLines(Integer.MAX_VALUE);
            mContent.setEllipsize(null);
            mBtnLookMore.setTag(true);
        }
    }

    //查看这个TextView
    private boolean judgeIsMax() {
        Layout layout = mContent.getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0) {
                if (layout.getEllipsisCount(lines - 1) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            //TODO 跳转到电台具体的播放界面
            RadioInfo radioInfo = (RadioInfo) parent.getItemAtPosition(position);
            int radioId = radioInfo.getId();
        }
    }

    private void requestRadioStationDetail() {
        Client.requestRadioDetail(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<RadioInfo>, RadioInfo>() {
            @Override
            protected void onRespSuccessData(RadioInfo radioInfo) {
                if (radioInfo != null) {
                    updateRadioDetail(radioInfo);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).fireFree();
    }

    private void updateRadioDetail(RadioInfo radioInfo) {
        mRadioInfo = radioInfo;
        GlideApp.with(this).load(radioInfo.getRadioCover())
                .placeholder(R.drawable.ic_default_image)
                .centerCrop().fallback(getResources().getDrawable(R.drawable.bg_radio_big_cover))
                .into(mCover);

        GlideApp.with(this).load(radioInfo.getUserPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mAvatar);

        mName.setText(radioInfo.getRadioHostName());

        mTitleBar.setTitle(radioInfo.getRadioName());

        if (radioInfo.getIsSubscriber() == 0) {
            //没订阅
            mSubscribe.setVisibility(View.VISIBLE);
            mSubscribed.setVisibility(View.GONE);
        } else {
            mSubscribe.setVisibility(View.GONE);
            mSubscribed.setVisibility(View.VISIBLE);
        }
        mContent.setText(radioInfo.getRadioIntroduction());
        mListenerNumber.setText(String.valueOf(radioInfo.getListenNumber()));
    }

    private void requestRadioProgram() {
        Client.requestRadioDetailAudio(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<List<AudioInfo>>, List<AudioInfo>>() {


            @Override
            protected void onRespSuccessData(List<AudioInfo> data) {
                if (data != null) {
                    updateAudio(data);
                }
            }
        }).fireFree();
    }

    private void updateAudio(List<AudioInfo> data) {
        if (mRadioStationAdapter != null) {
            mRadioStationAdapter.clear();
        }
        for (AudioInfo audioInfo : data) {
            mRadioStationAdapter.add(audioInfo);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
    }

    @Override
    public void onAudioStart() {
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        }
    }

    @Override
    public void onAudioPlay() {
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onAudioPause() {
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
    }

    @Override
    public void onAudioResume() {
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onAudioStop() {
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
    }

    @Override
    public void onAudioError() {

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

            viewHolder.bindingData(mContext, getItem(position), position, getCount());
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
            @BindView(R.id.split)
            View mSplitView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, AudioInfo item, int position, int count) {
                GlideApp.with(context).load(item.getAudioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .centerCrop()
                        .into(mCover);

                mTitle.setText(item.getAudioName());

                mListenerNumber.setText(String.valueOf(item.getViewNumber()));

                mTime.setText(DateUtil.formatDefaultStyleTime(item.getModifyTime()));
                if (position == count - 1) {
                    mSplitView.setVisibility(View.GONE);
                }
            }
        }
    }
}
