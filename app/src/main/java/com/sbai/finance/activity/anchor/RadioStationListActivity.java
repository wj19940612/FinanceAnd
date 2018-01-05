package com.sbai.finance.activity.anchor;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.radio.BuyRadioDetailActivity;
import com.sbai.finance.activity.anchor.radio.RadioStationPlayActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.model.anchor.RadioInfo;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.AliPayHelper;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.HasLabelImageLayout;
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

public class RadioStationListActivity extends MediaPlayActivity implements AdapterView.OnItemClickListener {

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
    HasLabelImageLayout mAvatar;
    TextView mName;
    TextView mListenerNumber;
    TextView mContent;
    TextView mBtnLookMore;
    ImageView mIsNeedPayIcon;
    RelativeLayout mGotoPayLayout;
    TextView mPayNumView;
    View mStub;

    private int mRadioStationId;
    private RadioStationAdapter mRadioStationAdapter;
    private RadioInfo mRadioInfo;
    private Rect mRect;

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (!judgeIsMax() && mContent.getMaxLines() <= 2) {
                mBtnLookMore.setVisibility(View.GONE);
            } else {
                mBtnLookMore.setVisibility(View.VISIBLE);
            }
            mContent.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
            return true;
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMediaPlayService = ((MediaPlayService.MediaBinder) iBinder).getMediaPlayService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    interface OnPlayClickListener {
        public void onPlay(Radio radio);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_radio_station_list);
        ButterKnife.bind(this);
        mRootMissFloatWindow = mMissFloatWindow;
        initData(getIntent());

        mRadioStationAdapter = new RadioStationAdapter(this, new OnPlayClickListener() {
            @Override
            public void onPlay(Radio radio) {
                toggleQuestionVoice(radio);
            }
        });
        mListView.setAdapter(mRadioStationAdapter);
        mListView.setOnItemClickListener(this);
        mRect = new Rect();

        initFloatWindow();
        initHeaderView();
        initSwipeRefreshLayout();
        refreshData();
        requestClickNewInfo(mRadioStationId);

        Intent intent = new Intent(getActivity(), MediaPlayService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        initFloatWindow();
    }

    private void initData(Intent intent) {
        mRadioStationId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        Radio radio = intent.getParcelableExtra(ExtraKeys.RADIO);
        if (radio != null) {
            mRadioStationId = radio.getRadioId();
        }
    }

    private void refreshData() {
        requestRadioStationDetail();
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_radio_station_detail, mListView, false);
        mCover = header.findViewById(R.id.cover);
        mSubscribe = header.findViewById(R.id.subscribe);
        mSubscribed = header.findViewById(R.id.subscribed);
        mAvatar = header.findViewById(R.id.avatar);
        mName = header.findViewById(R.id.name);
        mListenerNumber = header.findViewById(R.id.listenerNumber);
        mContent = header.findViewById(R.id.content);
        mBtnLookMore = header.findViewById(R.id.btnLookMore);
        mIsNeedPayIcon = header.findViewById(R.id.payStatus);
        mGotoPayLayout = header.findViewById(R.id.payLayout);
        mPayNumView = header.findViewById(R.id.payKey);
        mStub = header.findViewById(R.id.stub);
        mBtnLookMore.setTag(false);
        mBtnLookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasExpand = (boolean) view.getTag();
                expandOrStopContent(hasExpand);
            }
        });
        mGotoPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRadioInfo != null)
                    Launcher.with(RadioStationListActivity.this, BuyRadioDetailActivity.class).putExtra(ExtraKeys.BUY_TYPE, AliPayHelper.PAY_DEFAULT).putExtra(ExtraKeys.BUY_ID, mRadioInfo.getId()).putExtra(ExtraKeys.MONEY, mRadioInfo.getRadioPrice())
                            .execute();
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRadioInfo == null) return;
                if (LocalUser.getUser().isLogin()) {
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
                } else {
                    Launcher.with(RadioStationListActivity.this, LoginActivity.class).execute();
                }
            }
        };
        mSubscribe.setOnClickListener(onClickListener);
        mSubscribed.setOnClickListener(onClickListener);
        View.OnClickListener nameOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioInfo == null) return;
                Launcher.with(RadioStationListActivity.this, MissProfileDetailActivity.class).putExtra(Launcher.EX_PAYLOAD, mRadioInfo.getRadioHost()).execute();
            }
        };
        mName.setOnClickListener(nameOnClickListener);
        mAvatar.setOnClickListener(nameOnClickListener);

        mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioInfo == null) return;
                Launcher.with(getActivity(), LookBigPictureActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mRadioInfo.getRadioCover())
                        .execute();
            }
        });
        mContent.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        mListView.addHeaderView(header);
    }

    private void initFloatWindow() {
        if (MissAudioManager.get().isPlaying()) {
            MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
            if (audio != null) {
                if (audio instanceof Radio) {
                    mMissFloatWindow.startAnim();
                    mMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
                } else if (audio instanceof Question) {
                    mMissFloatWindow.startAnim();
                    mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
                }
            }

        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRadioStationDetail();

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
                if (layout.getEllipsisCount(1) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0 && (mRadioInfo.getPaid() == 0 || position < 4 || mRadioInfo.getUserPayment() > 0)) {
            Radio radioInfo = (Radio) parent.getItemAtPosition(position);
            Launcher.with(this, RadioStationPlayActivity.class).putExtra(ExtraKeys.RADIO, radioInfo).executeForResult(222);
        }
    }

    private void requestRadioStationDetail() {
        Client.requestRadioDetail(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<RadioInfo>, RadioInfo>() {
            @Override
            protected void onRespSuccessData(RadioInfo radioInfo) {
                if (radioInfo != null) {
                    updateRadioDetail(radioInfo);
                    requestRadioProgram();
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

        mAvatar.setAvatar(radioInfo.getUserPortrait(), Question.USER_IDENTITY_MISS);

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
        mContent.setText(Html.fromHtml(radioInfo.getRadioIntroduction()).toString());
        mListenerNumber.setText(String.valueOf(radioInfo.getListenNumber()));
        mContent.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        if (radioInfo.getPaid() == 0) {
            mIsNeedPayIcon.setVisibility(View.GONE);
            mGotoPayLayout.setVisibility(View.GONE);
            mStub.setVisibility(View.VISIBLE);
        } else {
            if (radioInfo.getUserPayment() == 0) {
                mIsNeedPayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_need_pay));
                mGotoPayLayout.setVisibility(View.VISIBLE);
                mStub.setVisibility(View.GONE);
                mPayNumView.setText(String.format(getString(R.string.need_pay_radio_key), radioInfo.getRadioPrice()));
            } else {
                mIsNeedPayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_have_pay));
                mGotoPayLayout.setVisibility(View.GONE);
                mStub.setVisibility(View.VISIBLE);
            }
        }
    }

    private void requestRadioProgram() {
        Client.requestRadioDetailAudio(mRadioStationId).setTag(TAG).setCallback(new Callback2D<Resp<List<Radio>>, List<Radio>>() {

            @Override
            protected void onRespSuccessData(List<Radio> data) {
                if (data != null) {
                    updateAudio(data);
                }
            }
        }).fireFree();
    }

    private void requestClickNewInfo(int dataId) {
        Client.readCollect(String.valueOf(dataId)).setTag(TAG).fireFree();
    }

    private void updateAudio(List<Radio> data) {
        if (mRadioStationAdapter != null) {
            mRadioStationAdapter.clear();
        }
        for (Radio audioInfo : data) {
            mRadioStationAdapter.add(audioInfo);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        unbindService(mServiceConnection);
    }

    private void toggleQuestionVoice(Radio radio) {
        if (MissAudioManager.get().isStarted(radio)) {
            if (mMediaPlayService != null) {
                mMediaPlayService.onPausePlay(radio);
            }
        } else if (MissAudioManager.get().isPaused(radio)) {
            if (mMediaPlayService != null) {
                mMediaPlayService.onResume();
            }
        } else {
            updateQuestionListenCount(radio);
            if (mMediaPlayService != null) {
                mMediaPlayService.startPlay(radio, MediaPlayService.MEDIA_SOURCE_RADIO_DETAIL);
            }
        }
    }

    private void updateQuestionListenCount(Radio radio) {
        Client.listenRadioAudio(radio.getAudioId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                })
                .fireFree();
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        mRadioStationAdapter.notifyDataSetChanged();
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        } else if (audio instanceof Radio) {
            mMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
        }
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        startScheduleJob(100);
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        startScheduleJob(100);
        mMissFloatWindow.startAnim();
        mRadioStationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        stopScheduleJob();
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        mRadioStationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        stopScheduleJob();
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        mRadioStationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
        int firstVisiblePosition = mListView.getFirstVisiblePosition() + 1;//有header
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        boolean visibleItemsStarted = false;
        if (firstVisiblePosition >= 0 && lastVisiblePosition >= 0 && mRadioStationAdapter.getCount() > 0) {
            if (mRadioStationAdapter != null && mRadioStationAdapter.getCount() > 0) {
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    if (i > mRadioStationAdapter.getCount()) continue; // Skip header
                    Radio radio = mRadioStationAdapter.getItem(i - 1);
                    if (radio != null && MissAudioManager.get().isStarted(radio)) {
                        View view = mListView.getChildAt(i - firstVisiblePosition + 1);
                        visibleItemsStarted = view.getGlobalVisibleRect(mRect);
                        TextView soundTime = view.findViewById(R.id.countDown);
                        int pastTime = MissAudioManager.get().getCurrentPosition();
                        soundTime.setText(DateUtil.formatMediaLength((radio.getAudioTime() * 1000 - pastTime) / 1000));
                    }
                }
            }
        }

        if (mRadioStationAdapter.getCount() > 0) {
            if (visibleItemsStarted && mMissFloatWindow.getVisibility() == View.VISIBLE) {
                mMissFloatWindow.setVisibility(View.GONE);
            }

            if (!visibleItemsStarted && mMissFloatWindow.getVisibility() == View.GONE) {
                mMissFloatWindow.setVisibility(View.VISIBLE);
            }
        }
    }


    class RadioStationAdapter extends ArrayAdapter<Radio> {
        private Context mContext;
        private OnPlayClickListener mOnPlacyClickListener;

        private RadioStationAdapter(@NonNull Context context, OnPlayClickListener onPlacyClickListener) {
            super(context, 0);
            this.mContext = context;
            this.mOnPlacyClickListener = onPlacyClickListener;
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

            viewHolder.bindingData(mContext, getItem(position), position, getCount(), mRadioInfo, mOnPlacyClickListener);
            return convertView;
        }

        class ViewHolder {
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
            @BindView(R.id.playIcon)
            ImageView mPlayIcon;
            @BindView(R.id.countDown)
            TextView mCountDown;
            @BindView(R.id.keyIcon)
            ImageView mKeyIcon;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, final Radio item, int position, int count, final RadioInfo radioInfo, final OnPlayClickListener onPlacyClickListener) {
                GlideApp.with(context).load(item.getAudioCover())
                        .placeholder(R.drawable.ic_default_image)
                        .centerCrop()
                        .into(mCover);

                mTitle.setText(item.getAudioName());
                mListenerNumber.setText(String.valueOf(item.getViewNumber()));

                mTime.setText(DateUtil.formatDefaultStyleTime(item.getReviewTime()));
                if (position == count - 1) {
                    mSplitView.setVisibility(View.GONE);
                }

                if (MissAudioManager.get().isStarted(item)) {
                    mPlayIcon.setImageResource(R.drawable.ic_pause);
                    int pastTime = MissAudioManager.get().getCurrentPosition();
                    mCountDown.setText(DateUtil.formatMediaLength((item.getAudioTime() * 1000 - pastTime) / 1000));
                } else if (MissAudioManager.get().isPaused(item)) {
                    mPlayIcon.setImageResource(R.drawable.ic_play);
                    int pastTime = MissAudioManager.get().getCurrentPosition();
                    mCountDown.setText(DateUtil.formatMediaLength((item.getAudioTime() * 1000 - pastTime) / 1000));
                } else {
                    mPlayIcon.setImageResource(R.drawable.ic_play);
                    mCountDown.setText(DateUtil.formatMediaLength((item.getAudioTime())));
                }

                mPlayIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onPlacyClickListener != null) {
                            onPlacyClickListener.onPlay(item);
                        }
                    }
                });

                if (radioInfo.getPaid() == 0 || position < 3 || radioInfo.getUserPayment() > 0) {
                    mPlayIcon.setVisibility(View.VISIBLE);
                    mCountDown.setVisibility(View.VISIBLE);
                    mKeyIcon.setVisibility(View.GONE);
                } else {
                    mPlayIcon.setVisibility(View.GONE);
                    mCountDown.setVisibility(View.GONE);
                    mKeyIcon.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}