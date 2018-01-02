package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.Dictum;
import com.sbai.finance.model.Greeting;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.NoticeRadio;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017\10\25 0025.
 */

public class HomeTitleView extends RelativeLayout {

    public static final String SPLIT = ";";

    public static final int BUTTON_HUSHEN = 1;
    public static final int BUTTON_QIHUO = 2;
    public static final int BUTTON_ZIXUAN = 3;

    public static final int SELECT_LEFT = 1;
    public static final int SELECT_CENTER = 2;
    public static final int SELECT_RIGHT = 3;

    public static final String MEIYUANYOU = "CL";
    public static final String MEIHUANGJIN = "GC";
    public static final String HENGZHI = "HSI";
    @BindView(R.id.titleBg)
    ImageView mTitleBg;
    @BindView(R.id.secondBg)
    ImageView mSecondBg;
    @BindView(R.id.greetingTitle)
    TextView mGreetingTitle;
    @BindView(R.id.broadcastIcon)
    ImageView mBroadcastIcon;
    @BindView(R.id.verticalScrollText)
    VerticalScrollTextView mVerticalScrollText;
    @BindView(R.id.broadcastText)
    TextView mBroadcastText;
    @BindView(R.id.stockBtn)
    TextView mStockBtn;
    @BindView(R.id.futureBtn)
    TextView mFutureBtn;
    @BindView(R.id.selectBtn)
    TextView mSelectBtn;
    @BindView(R.id.lineBottom)
    View mLineBottom;
    @BindView(R.id.lookAll)
    TextView mLookAll;
    @BindView(R.id.lookAllBtn)
    RelativeLayout mLookAllBtn;
    @BindView(R.id.leftIndex)
    TextView mLeftIndex;
    @BindView(R.id.leftIndexValue)
    TextView mLeftIndexValue;
    @BindView(R.id.leftLeftIndexPer)
    TextView mLeftLeftIndexPer;
    @BindView(R.id.leftRightIndexPer)
    TextView mLeftRightIndexPer;
    @BindView(R.id.leftSelectAddIcon)
    TextView mLeftSelectAddIcon;
    @BindView(R.id.leftSelectAddText)
    TextView mLeftSelectAddText;
    @BindView(R.id.leftSelectRL)
    RelativeLayout mLeftSelectRL;
    @BindView(R.id.leftRL)
    RelativeLayout mLeftRL;
    @BindView(R.id.centerLine)
    View mCenterLine;
    @BindView(R.id.centerLineRL)
    RelativeLayout mCenterLineRL;
    @BindView(R.id.centerIndex)
    TextView mCenterIndex;
    @BindView(R.id.centerIndexValue)
    TextView mCenterIndexValue;
    @BindView(R.id.centerLeftIndexPer)
    TextView mCenterLeftIndexPer;
    @BindView(R.id.centerRightIndexPer)
    TextView mCenterRightIndexPer;
    @BindView(R.id.centerSelectAddIcon)
    TextView mCenterSelectAddIcon;
    @BindView(R.id.centerSelectAddText)
    TextView mCenterSelectAddText;
    @BindView(R.id.centerSelectRL)
    RelativeLayout mCenterSelectRL;
    @BindView(R.id.centerRL)
    RelativeLayout mCenterRL;
    @BindView(R.id.rightLine)
    View mRightLine;
    @BindView(R.id.rightLineRL)
    RelativeLayout mRightLineRL;
    @BindView(R.id.rightIndex)
    TextView mRightIndex;
    @BindView(R.id.rightIndexValue)
    TextView mRightIndexValue;
    @BindView(R.id.rightLeftIndexPer)
    TextView mRightLeftIndexPer;
    @BindView(R.id.rightRightIndexPer)
    TextView mRightRightIndexPer;
    @BindView(R.id.rightSelectAddIcon)
    TextView mRightSelectAddIcon;
    @BindView(R.id.rightSelectAddText)
    TextView mRightSelectAddText;
    @BindView(R.id.rightSelectRL)
    RelativeLayout mRightSelectRL;
    @BindView(R.id.rightRL)
    RelativeLayout mRightRL;
    @BindView(R.id.indexRL)
    RelativeLayout mIndexRL;
    @BindView(R.id.stockImg)
    GifImageView mStockImg;
    @BindView(R.id.simulateTrade)
    RelativeLayout mSimulateTrade;
    @BindView(R.id.practice)
    TextView mPractice;
    @BindView(R.id.studyRoomLayout)
    RelativeLayout mStudyRoom;
    @BindView(R.id.grailGuessLayout)
    RelativeLayout mGrailGuessLayout;
    @BindView(R.id.guessImg)
    ImageView mGuess;

    private GifDrawable mGifFromResource;
    private Context mContext;
    private int oldButton;
    private Map<String, FutureData> futureDataMap = new HashMap<String, FutureData>();
    private Map<Integer, StockData> mStockCacheData = new HashMap<Integer, StockData>();
    private Map<Integer, CacheFutureData> mFutureCacheData = new HashMap<Integer, CacheFutureData>();
    private Map<Integer, Object> mOptionCacheData = new HashMap<Integer, Object>();

    private OnDictumClickListener mOnDictumClickListener;
    private VerticalScrollTextView.OnItemClickListener mOnBroadcastListener;

    public class CacheFutureData {
        private Variety mVariety;
        private FutureData mFutureData;

        public CacheFutureData(Variety variety, FutureData futureData) {
            mVariety = variety;
            mFutureData = futureData;
        }
    }

    public int getOldButton() {
        return oldButton;
    }

    public void setOnDictumClickListener(OnDictumClickListener onDictumClickListener) {
        mOnDictumClickListener = onDictumClickListener;
    }

    public interface OnDictumClickListener {
        public void onDictumClick(Dictum dictum);
    }

    public void setOnBroadcastListener(VerticalScrollTextView.OnItemClickListener onBroadcastListener) {
        mOnBroadcastListener = onBroadcastListener;
        mVerticalScrollText.setOnItemClickListener(onBroadcastListener);
    }

    private OnClickItemListener mOnClickItemListener;

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        mOnClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener {
        public void onItemClick(int button, Object t);
    }

    public interface OnLookAllClickListener {
        public void onLookAll();

        public void onSelectClick();

        public void onPractice();

        public void onDaySubjuect();

        public void onStockSimulate();

        public void onGuess();
    }

    private OnLookAllClickListener mOnLookAllClickListener;

    public void setOnLookAllClickListener(OnLookAllClickListener onLookAllClickListener) {
        mOnLookAllClickListener = onLookAllClickListener;
    }

    private IndexClickListener mIndexClickListener;

    public interface IndexClickListener {
        public void onStockClick();

        public void onFutureClick();

        public void onSelectClick();
    }

    public void setIndexClickListener(IndexClickListener indexClickListener) {
        mIndexClickListener = indexClickListener;
    }

    public void stopVerticalSrcoll() {
        mVerticalScrollText.stopAutoScroll();
    }

    public Map<String, FutureData> getFutureDataMap() {
        return futureDataMap;
    }

    public HomeTitleView(Context context) {
        this(context, null);
    }

    public HomeTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_home_title, this, true);
        ButterKnife.bind(this);
        initGif();
    }

    private void initGif() {
        try {
            mGifFromResource = new GifDrawable(getResources(), R.drawable.ic_stock_simulate);
            mGifFromResource.setLoopCount(0);
            mStockImg.setImageDrawable(mGifFromResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void freeGif() {
        mGifFromResource.recycle();
    }

//    public void putNewFutureData(FutureData futureData) {
//        futureDataMap.put(futureData.getInstrumentId(), futureData);
//        updateFutureOrSelectData();
//    }

    public void putNewFutureData(List<FutureData> data) {
        if (data != null && data.size() != 0) {
            for (FutureData futureData : data) {
                futureDataMap.put(futureData.getInstrumentId(), futureData);
            }
        }
        updateFutureOrSelectData();
    }

    //问候title
    public void setGreetingTitle(Greeting greetingTitle) {
        if (greetingTitle == null) {
            mGreetingTitle.setText(R.string.welcome_lemi);
            return;
        }
        boolean nowShow = judgeShowTime(greetingTitle);
        if (TextUtils.isEmpty(greetingTitle.getGreetings()) || !nowShow) {
            String helloString = null;
            int dayAndNight = DateUtil.getDayAndNight(SysTime.getSysTime().getSystemTimestamp());
            switch (dayAndNight) {
                case 1:
                    helloString = getResources().getString(R.string.hello_morning);
                    break;
                case 2:
                    helloString = getResources().getString(R.string.hello_afternoon);
                    break;
                case 3:
                    helloString = getResources().getString(R.string.hello_evening);
                    break;
            }
            String stringResult = String.format(helloString, LocalUser.getUser().getUserInfo().getUserName());
            mGreetingTitle.setText(stringResult);
        } else {
            String[] greetingTitles = greetingTitle.getGreetings().split(SPLIT);
            int showIndex = (int) (SysTime.getSysTime().getSystemTimestamp() % greetingTitles.length);
            mGreetingTitle.setText(greetingTitles[showIndex]);
        }
    }

    public void setGreetingTitle(int id) {
        mGreetingTitle.setText(id);
    }

    private boolean judgeShowTime(Greeting greetingTitle) {
        long startTime = 0;
        long endTime = 0;
        if (!TextUtils.isEmpty(greetingTitle.getStartTime())) {
            startTime = DateUtil.convertString2Long(greetingTitle.getStartTime(), DateUtil.DEFAULT_FORMAT);
        }
        if (!TextUtils.isEmpty(greetingTitle.getEndTime())) {
            endTime = DateUtil.convertString2Long(greetingTitle.getEndTime(), DateUtil.DEFAULT_FORMAT);
        }
        long nowTime = SysTime.getSysTime().getSystemTimestamp();
        if (nowTime >= startTime && nowTime <= endTime) {
            return true;
        } else {
            return false;
        }
    }

    //广播内容
    public void setBroadcastContent(final List<NoticeRadio> noticeRadios) {
        if (noticeRadios != null && noticeRadios.size() > 1) {
            mBroadcastIcon.setVisibility(View.VISIBLE);
            mBroadcastText.setVisibility(View.GONE);
            mVerticalScrollText.setVisibility(View.VISIBLE);
            mVerticalScrollText.setNoticeRadios(noticeRadios);
        } else if (noticeRadios != null) {
            mVerticalScrollText.setVisibility(View.GONE);
            mBroadcastIcon.setVisibility(View.VISIBLE);
            mBroadcastText.setVisibility(View.VISIBLE);
            mBroadcastText.setText(noticeRadios.get(0).getTitle());
            mBroadcastText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnBroadcastListener != null) {
                        mOnBroadcastListener.onItemClick(noticeRadios.get(0));
                    }
                }
            });
        }
    }

    //设置名言
    public void setDictum(final List<Dictum> dictums) {
        mBroadcastIcon.setVisibility(View.GONE);
        mVerticalScrollText.setVisibility(View.GONE);
        mBroadcastText.setVisibility(View.VISIBLE);
        if (dictums != null && dictums.get(0) != null && !TextUtils.isEmpty(dictums.get(0).getContent())) {
            mBroadcastText.setText(dictums.get(0).getContent());
            mBroadcastText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnDictumClickListener != null) {
                        mOnDictumClickListener.onDictumClick(dictums.get(0));
                    }
                }
            });
        }
    }

    //更新大盘竞猜按钮状态
    public void updateGuessStatus(int status) {
        mGrailGuessLayout.setVisibility(status > 0 ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.stockBtn, R.id.futureBtn, R.id.selectBtn, R.id.centerSelectRL, R.id.leftSelectRL, R.id.rightSelectRL, R.id.lookAllBtn, R.id.practice, R.id.studyRoomLayout, R.id.simulateTrade, R.id.grailGuessLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stockBtn:
                if (oldButton == BUTTON_HUSHEN) {
                    return;
                }
                clickIndexButton(BUTTON_HUSHEN);
                if (mIndexClickListener != null) {
                    mIndexClickListener.onStockClick();
                }
                break;
            case R.id.futureBtn:
                if (oldButton == BUTTON_QIHUO) {
                    return;
                }
                clickIndexButton(BUTTON_QIHUO);
                if (mIndexClickListener != null) {
                    mIndexClickListener.onFutureClick();
                }
                break;
            case R.id.selectBtn:
                if (oldButton == BUTTON_ZIXUAN) {
                    return;
                }
                clickIndexButton(BUTTON_ZIXUAN);
                if (mIndexClickListener != null) {
                    mIndexClickListener.onSelectClick();
                }
                break;
            case R.id.leftSelectRL:
            case R.id.centerSelectRL:
            case R.id.rightSelectRL:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onSelectClick();
                }
                break;
            case R.id.lookAllBtn:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onLookAll();
                }
                break;
            case R.id.simulateTrade:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onStockSimulate();
                }
                break;
            case R.id.practice:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onPractice();
                }
                break;
            case R.id.studyRoomLayout:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onDaySubjuect();
                }
                break;
            case R.id.grailGuessLayout:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onGuess();
                }
                break;
        }
    }

    //点击了沪深、期货、自选按钮,初始化这部分UI
    public void clickIndexButton(int buttonIndex) {
        if (oldButton == buttonIndex) {
            return;
        }
        oldButton = buttonIndex;
        forceRefershUI(buttonIndex);
    }

    public void forceRefershUI(int buttonIndex) {
        switch (buttonIndex) {
            case BUTTON_HUSHEN:
                mIndexRL.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_select_open_one));
                initStockeUI(BUTTON_HUSHEN);
                break;
            case BUTTON_QIHUO:
                mIndexRL.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_select_open_two));
                initFutureUI(BUTTON_QIHUO);
                break;
            case BUTTON_ZIXUAN:
                mIndexRL.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_select_open_three));
                initSelectUI();
                break;
        }
    }

    //数据有更新，根据按钮处于哪一块选择更新的UI
    public void updateStockOrSelectData(List<StockData> result) {
        if (oldButton == BUTTON_HUSHEN) {
            updateStockIndexMarketData(result);
        } else if (oldButton == BUTTON_ZIXUAN) {
            updateSelectStockMarketData(result);
        }
    }

    //数据有更新，根据按钮处于哪一块选择更新的UI
    public void updateFutureOrSelectData() {
        if (oldButton == BUTTON_QIHUO) {
            updateFutureMarketData();
        } else if (oldButton == BUTTON_ZIXUAN) {
            updateSelectFutureMarketData();
        }
    }

    private void updateSelectStockMarketData(List<StockData> result) {
        updateStockIndexMarketData(result);
    }

    private void updateSelectFutureMarketData() {
        updateFutureMarketData();
    }

    //更新股票名称
    public void updateStockIndexData(List<Stock> data) {
        switch (data.size()) {
            case 0:
                return;
            case 1:
                mLeftIndex.setText(data.get(0).getVarietyName());
                mLeftIndex.setTag(data.get(0));
                break;
            case 2:
                mLeftIndex.setTag(data.get(0));
                mLeftIndex.setText(data.get(0).getVarietyName());
                mCenterIndex.setTag(data.get(1));
                mCenterIndex.setText(data.get(1).getVarietyName());
                break;
            default:
                mLeftIndex.setTag(data.get(0));
                mLeftIndex.setText(data.get(0).getVarietyName());
                mCenterIndex.setTag(data.get(1));
                mCenterIndex.setText(data.get(1).getVarietyName());
                mRightIndex.setTag(data.get(2));
                mRightIndex.setText(data.get(2).getVarietyName());
                break;
        }
        setRLClick(data);
    }

    //更新期货名称
    public void updateFutureData(List<Variety> data) {
        final SelectFuture selectFuture = selectFutureData(data);
        if (selectFuture.leftVarity != null) {
            mLeftIndex.setText(selectFuture.leftVarity.getVarietyName());
            mLeftIndex.setTag(selectFuture.leftVarity);
            mLeftRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickItemListener.onItemClick(oldButton, selectFuture.leftVarity);
                }
            });
        } else {
            mLeftRL.setOnClickListener(null);
        }
        if (selectFuture.centerVarity != null) {
            mCenterIndex.setTag(selectFuture.centerVarity);
            mCenterIndex.setText(selectFuture.centerVarity.getVarietyName());
            mCenterRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickItemListener.onItemClick(oldButton, selectFuture.centerVarity);
                }
            });
        } else {
            mCenterRL.setOnClickListener(null);
        }
        if (selectFuture.rightVarity != null) {
            mRightIndex.setTag(selectFuture.rightVarity);
            mRightIndex.setText(selectFuture.rightVarity.getVarietyName());
            mRightRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickItemListener.onItemClick(oldButton, selectFuture.rightVarity);
                }
            });
        } else {
            mRightRL.setOnClickListener(null);
        }
//        if (data == null || data.size() == 0) {
//            return;
//        } else if (data.size() == 1) {
//            mLeftIndex.setText(data.get(0).getVarietyName());
//            mLeftIndex.setTag(data.get(0));
//        } else if (data.size() == 2) {
//            mLeftIndex.setTag(data.get(0));
//            mLeftIndex.setText(data.get(0).getVarietyName());
//            mCenterIndex.setTag(data.get(1));
//            mCenterIndex.setText(data.get(1).getVarietyName());
//        } else {
//            mLeftIndex.setTag(data.get(0));
//            mLeftIndex.setText(data.get(0).getVarietyName());
//            mCenterIndex.setTag(data.get(1));
//            mCenterIndex.setText(data.get(1).getVarietyName());
//            mRightIndex.setTag(data.get(2));
//            mRightIndex.setText(data.get(2).getVarietyName());
//        }
//        setRLClick(data);
        updateFutureMarketData();
    }

    public class SelectFuture {
        public Variety leftVarity;
        public Variety centerVarity;
        public Variety rightVarity;
    }

    //当前只显示美原油，美黄金，恒指
    public SelectFuture selectFutureData(List<Variety> data) {
        SelectFuture selectFuture = new SelectFuture();
        for (Variety variety : data) {
            if (variety.getVarietyType().equals(MEIYUANYOU)) {
                selectFuture.leftVarity = variety;
            } else if (variety.getVarietyType().equals(MEIHUANGJIN)) {
                selectFuture.centerVarity = variety;
            } else if (variety.getVarietyType().equals(HENGZHI)) {
                selectFuture.rightVarity = variety;
            }
            if (selectFuture.leftVarity != null && selectFuture.centerVarity != null && selectFuture.rightVarity != null) {
                return selectFuture;
            }
        }

        return selectFuture;

    }

    private void setRLClick(final List data) {
        if (data.size() == 1) {
            mLeftRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, data.get(0));
                    }
                }
            });
            mCenterRL.setOnClickListener(null);
            mRightRL.setOnClickListener(null);
        } else if (data.size() == 2) {
            mLeftRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, data.get(0));
                    }
                }
            });
            mCenterRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, data.get(1));
                    }
                }
            });
            mRightRL.setOnClickListener(null);
        } else if (data.size() > 2) {
            mLeftRL.setTag(data.get(0));
            mLeftRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, mLeftRL.getTag());
                    }
                }
            });
            mCenterRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, data.get(1));
                    }
                }
            });
            mRightRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemListener != null) {
                        mOnClickItemListener.onItemClick(oldButton, data.get(2));
                    }
                }
            });
        } else {
            mLeftRL.setOnClickListener(null);
            mCenterRL.setOnClickListener(null);
            mRightRL.setOnClickListener(null);
        }
    }

    //更新股票行情
    public void updateStockIndexMarketData(List<StockData> data) {
        Stock stock;
        for (StockData stockData : data) {
            stock = (Stock) mLeftIndex.getTag();
            if (stock != null && TextUtils.isEmpty(stock.getVarietyCode())) {
                continue;
            }
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                updateIndexDataToUI(SELECT_LEFT, stockData);
                if (oldButton == BUTTON_HUSHEN) {
                    mStockCacheData.put(SELECT_LEFT, stockData);
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_LEFT, stockData);
                }
            }
            stock = (Stock) mCenterIndex.getTag();
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                updateIndexDataToUI(SELECT_CENTER, stockData);
                if (oldButton == BUTTON_HUSHEN) {
                    mStockCacheData.put(SELECT_CENTER, stockData);
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_CENTER, stockData);
                }
            }
            stock = (Stock) mRightIndex.getTag();
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                updateIndexDataToUI(SELECT_RIGHT, stockData);
                if (oldButton == BUTTON_HUSHEN) {
                    mStockCacheData.put(SELECT_RIGHT, stockData);
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_RIGHT, stockData);
                }
            }

        }
    }

    private void updateIndexDataToUI(int selectRange, StockData data) {
        TextView indexValue = null;
        TextView leftIndexPer = null;
        TextView rightIndexPer = null;
        switch (selectRange) {
            case SELECT_LEFT:
                indexValue = mLeftIndexValue;
                leftIndexPer = mLeftLeftIndexPer;
                rightIndexPer = mLeftRightIndexPer;
                break;
            case SELECT_CENTER:
                indexValue = mCenterIndexValue;
                leftIndexPer = mCenterLeftIndexPer;
                rightIndexPer = mCenterRightIndexPer;
                break;
            case SELECT_RIGHT:
                indexValue = mRightIndexValue;
                leftIndexPer = mRightLeftIndexPer;
                rightIndexPer = mRightRightIndexPer;
                break;
        }
        int redColor = ContextCompat.getColor(mContext, R.color.redPrimary);
        int greenColor = ContextCompat.getColor(mContext, R.color.greenAssist);
        int greyColor = ContextCompat.getColor(mContext, R.color.unluckyText);
        int color;
        String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
        String ratePrice = data.getFormattedUpDropPrice();
        if (rateChange.startsWith("-")) {
            color = greenColor;
        } else {
            color = redColor;
            rateChange = "+" + rateChange;
            ratePrice = "+" + ratePrice;
        }
//        if (data.isDelist()) {
//            indexValue.setText("停牌");
//            indexValue.setTextColor(greyColor);
//            leftIndexPer.setText(ratePrice);
//            rightIndexPer.setText(rateChange);
//            leftIndexPer.setTextColor(greyColor);
//            rightIndexPer.setTextColor(greyColor);
//        } else {
        indexValue.setTextColor(color);
        indexValue.setText(data.getFormattedLastPrice());
        leftIndexPer.setText(ratePrice);
        rightIndexPer.setText(rateChange);
        leftIndexPer.setTextColor(color);
        rightIndexPer.setTextColor(color);
//        }
    }

    public void updateFutureMarketData() {
        if (futureDataMap.size() == 0) {
            return;
        }
        // 2.判断涨跌
        int redColor = ContextCompat.getColor(mContext, R.color.redPrimary);
        int greenColor = ContextCompat.getColor(mContext, R.color.greenAssist);
        int greyColor = ContextCompat.getColor(mContext, R.color.unluckyText);
        int color;
        Variety variety;
        FutureData data;
        Iterator<Map.Entry<String, FutureData>> it = futureDataMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, FutureData> entry = it.next();
            data = entry.getValue();
            String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
            String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            variety = (Variety) mLeftIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) && variety.getContractsCode().equalsIgnoreCase(data.getInstrumentId())) {
                mLeftIndexValue.setTextColor(color);
                mLeftIndexValue.setText(String.valueOf(data.getLastPrice()));
                mLeftLeftIndexPer.setText(ratePrice);
                mLeftRightIndexPer.setText(rateChange);
                mLeftLeftIndexPer.setTextColor(color);
                mLeftRightIndexPer.setTextColor(color);
                if (oldButton == BUTTON_QIHUO) {
                    mFutureCacheData.put(SELECT_LEFT, new CacheFutureData(variety, data));
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_LEFT, new CacheFutureData(variety, data));
                }
            }

            variety = (Variety) mCenterIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) && variety.getContractsCode().equalsIgnoreCase(data.getInstrumentId())) {
                mCenterIndexValue.setTextColor(color);
                mCenterIndexValue.setText(String.valueOf(data.getLastPrice()));
                mCenterLeftIndexPer.setText(ratePrice);
                mCenterRightIndexPer.setText(rateChange);
                mCenterLeftIndexPer.setTextColor(color);
                mCenterRightIndexPer.setTextColor(color);
                if (oldButton == BUTTON_QIHUO) {
                    mFutureCacheData.put(SELECT_CENTER, new CacheFutureData(variety, data));
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_CENTER, new CacheFutureData(variety, data));
                }
            }

            variety = (Variety) mRightIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) && variety.getContractsCode().equalsIgnoreCase(data.getInstrumentId())) {
                mRightIndexValue.setTextColor(color);
                mRightIndexValue.setText(String.valueOf(data.getLastPrice()));
                mRightLeftIndexPer.setText(ratePrice);
                mRightRightIndexPer.setText(rateChange);
                mRightLeftIndexPer.setTextColor(color);
                mRightRightIndexPer.setTextColor(color);
                if (oldButton == BUTTON_QIHUO) {
                    mFutureCacheData.put(SELECT_RIGHT, new CacheFutureData(variety, data));
                } else if (oldButton == BUTTON_ZIXUAN) {
                    mOptionCacheData.put(SELECT_RIGHT, new CacheFutureData(variety, data));
                }
            }
        }
    }

    public void updateSelectData(List<Stock> data) {
        if (data == null || data.size() == 0) {
            setIndexViewVisible(SELECT_LEFT, false);
            mLeftSelectRL.setVisibility(View.VISIBLE);
            setIndexViewVisible(SELECT_RIGHT, false);
            mCenterSelectRL.setVisibility(View.VISIBLE);
            setIndexViewVisible(SELECT_CENTER, false);
            mRightSelectRL.setVisibility(View.VISIBLE);

            mOptionCacheData.put(SELECT_LEFT, null);
            mOptionCacheData.put(SELECT_CENTER, null);
            mOptionCacheData.put(SELECT_RIGHT, null);
            return;
        } else if (data.size() == 1) {
            setIndexViewVisible(SELECT_LEFT, true);
            mLeftSelectRL.setVisibility(View.GONE);
            mLeftIndex.setText(data.get(0).getVarietyName());
            mLeftIndex.setTag(data.get(0));

            setIndexViewVisible(SELECT_CENTER, false);
            mCenterSelectRL.setVisibility(View.VISIBLE);
            setIndexViewVisible(SELECT_RIGHT, false);
            mRightSelectRL.setVisibility(View.VISIBLE);

            mOptionCacheData.put(SELECT_CENTER, null);
            mOptionCacheData.put(SELECT_RIGHT, null);
        } else if (data.size() == 2) {
            setIndexViewVisible(SELECT_LEFT, true);
            mLeftSelectRL.setVisibility(View.GONE);
            mLeftIndex.setTag(data.get(0));
            mLeftIndex.setText(data.get(0).getVarietyName());
            mCenterIndex.setTag(data.get(1));
            mCenterIndex.setText(data.get(1).getVarietyName());

            setIndexViewVisible(SELECT_CENTER, true);
            mCenterSelectRL.setVisibility(View.GONE);
            setIndexViewVisible(SELECT_RIGHT, false);
            mRightSelectRL.setVisibility(View.VISIBLE);
            mOptionCacheData.put(SELECT_RIGHT, null);
        } else {
            setIndexViewVisible(SELECT_LEFT, true);
            mLeftSelectRL.setVisibility(View.GONE);
            mLeftIndex.setTag(data.get(0));
            mLeftIndex.setText(data.get(0).getVarietyName());
            setIndexViewVisible(SELECT_CENTER, true);
            mCenterSelectRL.setVisibility(View.GONE);
            mCenterIndex.setTag(data.get(1));
            mCenterIndex.setText(data.get(1).getVarietyName());
            setIndexViewVisible(SELECT_RIGHT, true);
            mRightSelectRL.setVisibility(View.GONE);
            mRightIndex.setTag(data.get(2));
            mRightIndex.setText(data.get(2).getVarietyName());
        }
        setRLClick(data);
    }

    private void initStockeUI(int button) {
        if (mStockCacheData.isEmpty()) {
            initStockOrFutureUI(button);
            return;
        }
        setIndexViewVisible(SELECT_LEFT, true);
        setIndexViewVisible(SELECT_CENTER, true);
        setIndexViewVisible(SELECT_RIGHT, true);
        mLeftSelectRL.setVisibility(View.GONE);
        mCenterSelectRL.setVisibility(View.GONE);
        mRightSelectRL.setVisibility(View.GONE);
        mLeftIndex.setTag(null);
        mCenterIndex.setTag(null);
        mRightIndex.setTag(null);
        mLeftRL.setOnClickListener(null);
        mCenterRL.setOnClickListener(null);
        mRightRL.setOnClickListener(null);

        if (mStockCacheData.get(SELECT_LEFT) != null) {
            StockData leftData = mStockCacheData.get(SELECT_LEFT);
            mLeftIndex.setText(leftData.getName());
            updateIndexDataToUI(SELECT_LEFT, leftData);
        } else {
            mLeftIndex.setText("");
            mLeftIndexValue.setText("");
            mLeftLeftIndexPer.setText("");
            mLeftRightIndexPer.setText("");
        }
        if (mStockCacheData.get(SELECT_CENTER) != null) {
            StockData centerData = mStockCacheData.get(SELECT_CENTER);
            mCenterIndex.setText(centerData.getName());
            updateIndexDataToUI(SELECT_CENTER, centerData);
        } else {
            mCenterIndex.setText("");
            mCenterIndexValue.setText("");
            mCenterLeftIndexPer.setText("");
            mCenterRightIndexPer.setText("");
        }
        if (mStockCacheData.get(SELECT_RIGHT) != null) {
            StockData rightData = mStockCacheData.get(SELECT_RIGHT);
            mRightIndex.setText(rightData.getName());
            updateIndexDataToUI(SELECT_RIGHT, rightData);
        } else {
            mRightIndex.setText("");
            mRightIndexValue.setText("");
            mRightLeftIndexPer.setText("");
            mRightRightIndexPer.setText("");
        }
    }

    private void initFutureUI(int button) {
        if (mFutureCacheData.isEmpty()) {
            initStockOrFutureUI(button);
            return;
        }
        setIndexViewVisible(SELECT_LEFT, true);
        setIndexViewVisible(SELECT_CENTER, true);
        setIndexViewVisible(SELECT_RIGHT, true);
        mLeftSelectRL.setVisibility(View.GONE);
        mCenterSelectRL.setVisibility(View.GONE);
        mRightSelectRL.setVisibility(View.GONE);
        mLeftIndex.setTag(null);
        mCenterIndex.setTag(null);
        mRightIndex.setTag(null);
        mLeftRL.setOnClickListener(null);
        mCenterRL.setOnClickListener(null);
        mRightRL.setOnClickListener(null);

        int redColor = ContextCompat.getColor(mContext, R.color.redPrimary);
        int greenColor = ContextCompat.getColor(mContext, R.color.greenAssist);
        int color;

        CacheFutureData cacheLeftFutureData = mFutureCacheData.get(SELECT_LEFT);
        CacheFutureData cacheCenterFutureData = mFutureCacheData.get(SELECT_CENTER);
        CacheFutureData cacheRightFutureData = mFutureCacheData.get(SELECT_RIGHT);
        if (cacheLeftFutureData != null) {
            Variety variety = cacheLeftFutureData.mVariety;
            FutureData data = cacheLeftFutureData.mFutureData;
            String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
            String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            mLeftIndex.setText(variety.getVarietyName());
            mLeftIndexValue.setTextColor(color);
            mLeftIndexValue.setText(String.valueOf(data.getLastPrice()));
            mLeftLeftIndexPer.setText(ratePrice);
            mLeftRightIndexPer.setText(rateChange);
            mLeftLeftIndexPer.setTextColor(color);
            mLeftRightIndexPer.setTextColor(color);
        } else {
            mLeftIndex.setText("");
            mLeftIndexValue.setText("");
            mLeftLeftIndexPer.setText("");
            mLeftRightIndexPer.setText("");
        }

        if (cacheCenterFutureData != null) {
            Variety variety = cacheCenterFutureData.mVariety;
            FutureData data = cacheCenterFutureData.mFutureData;
            String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
            String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            mCenterIndex.setText(variety.getVarietyName());
            mCenterIndexValue.setTextColor(color);
            mCenterIndexValue.setText(String.valueOf(data.getLastPrice()));
            mCenterLeftIndexPer.setText(ratePrice);
            mCenterRightIndexPer.setText(rateChange);
            mCenterLeftIndexPer.setTextColor(color);
            mCenterRightIndexPer.setTextColor(color);
        } else {
            mCenterIndex.setText("");
            mCenterIndexValue.setText("");
            mCenterLeftIndexPer.setText("");
            mCenterRightIndexPer.setText("");
        }

        if (cacheRightFutureData != null) {
            Variety variety = cacheLeftFutureData.mVariety;
            FutureData data = cacheLeftFutureData.mFutureData;
            String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
            String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            mRightIndex.setText(variety.getVarietyName());
            mRightIndexValue.setTextColor(color);
            mRightIndexValue.setText(String.valueOf(data.getLastPrice()));
            mRightLeftIndexPer.setText(ratePrice);
            mRightRightIndexPer.setText(rateChange);
            mRightLeftIndexPer.setTextColor(color);
            mRightRightIndexPer.setTextColor(color);
        } else {
            mRightIndex.setText("");
            mRightIndexValue.setText("");
            mRightLeftIndexPer.setText("");
            mRightRightIndexPer.setText("");
        }
    }

    private void initStockOrFutureUI(int button) {
        setIndexViewVisible(SELECT_LEFT, true);
        setIndexViewVisible(SELECT_CENTER, true);
        setIndexViewVisible(SELECT_RIGHT, true);
        revertIndexUI();
        mLeftSelectRL.setVisibility(View.GONE);
        mCenterSelectRL.setVisibility(View.GONE);
        mRightSelectRL.setVisibility(View.GONE);
        mLeftIndex.setTag(null);
        mCenterIndex.setTag(null);
        mRightIndex.setTag(null);
    }

    private void initSelectUI() {
        if (mOptionCacheData.isEmpty()) {
            forceInitSelectUI();
            return;
        }
        mLeftIndex.setTag(null);
        mLeftRL.setOnClickListener(null);
        mCenterIndex.setTag(null);
        mCenterRL.setOnClickListener(null);
        mRightIndex.setTag(null);
        mRightRL.setOnClickListener(null);

        int redColor = ContextCompat.getColor(mContext, R.color.redPrimary);
        int greenColor = ContextCompat.getColor(mContext, R.color.greenAssist);
        int color;
        if (mOptionCacheData.get(SELECT_LEFT) != null) {
            Object leftObjectData = mOptionCacheData.get(SELECT_LEFT);
            if (leftObjectData instanceof StockData) {
                StockData leftData = (StockData) leftObjectData;
                mLeftIndex.setText(leftData.getName());
                updateIndexDataToUI(SELECT_LEFT, leftData);
            } else if (leftObjectData instanceof CacheFutureData) {
                CacheFutureData cacheLeftFutureData = (CacheFutureData) leftObjectData;
                Variety variety = cacheLeftFutureData.mVariety;
                FutureData data = cacheLeftFutureData.mFutureData;
                String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
                String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
                if (rateChange.startsWith("-")) {
                    color = greenColor;
                } else {
                    color = redColor;
                    rateChange = "+" + rateChange;
                    ratePrice = "+" + ratePrice;
                }
                mLeftIndex.setText(variety.getVarietyName());
                mLeftIndexValue.setTextColor(color);
                mLeftIndexValue.setText(String.valueOf(data.getLastPrice()));
                mLeftLeftIndexPer.setText(ratePrice);
                mLeftRightIndexPer.setText(rateChange);
                mLeftLeftIndexPer.setTextColor(color);
                mLeftRightIndexPer.setTextColor(color);
            }
        } else {
            setIndexViewVisible(SELECT_LEFT, false);
            mLeftSelectRL.setVisibility(View.VISIBLE);
        }

        if (mOptionCacheData.get(SELECT_CENTER) != null) {
            Object centerObjectData = mOptionCacheData.get(SELECT_CENTER);
            if (centerObjectData instanceof StockData) {
                StockData centerData = (StockData) centerObjectData;
                mCenterIndex.setText(centerData.getName());
                updateIndexDataToUI(SELECT_CENTER, centerData);
            } else if (centerObjectData instanceof CacheFutureData) {
                CacheFutureData cacheCenterFutureData = (CacheFutureData) centerObjectData;
                Variety variety = cacheCenterFutureData.mVariety;
                FutureData data = cacheCenterFutureData.mFutureData;
                String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
                String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
                if (rateChange.startsWith("-")) {
                    color = greenColor;
                } else {
                    color = redColor;
                    rateChange = "+" + rateChange;
                    ratePrice = "+" + ratePrice;
                }
                mCenterIndex.setText(variety.getVarietyName());
                mCenterIndexValue.setTextColor(color);
                mCenterIndexValue.setText(String.valueOf(data.getLastPrice()));
                mCenterLeftIndexPer.setText(ratePrice);
                mCenterRightIndexPer.setText(rateChange);
                mCenterLeftIndexPer.setTextColor(color);
                mCenterRightIndexPer.setTextColor(color);
            }
        } else {
            setIndexViewVisible(SELECT_CENTER, false);
            mCenterSelectRL.setVisibility(View.VISIBLE);
        }

        if (mOptionCacheData.get(SELECT_RIGHT) != null) {
            Object rightObjectData = mOptionCacheData.get(SELECT_RIGHT);
            if (rightObjectData instanceof StockData) {
                StockData rightData = (StockData) rightObjectData;
                mRightIndex.setText(rightData.getName());
                updateIndexDataToUI(SELECT_RIGHT, rightData);
            } else if (rightObjectData instanceof CacheFutureData) {
                CacheFutureData cacheRightFutureData = (CacheFutureData) rightObjectData;
                Variety variety = cacheRightFutureData.mVariety;
                FutureData data = cacheRightFutureData.mFutureData;
                String rateChange = FinanceUtil.formatToPercentage(data.getUpDropSpeed());
                String ratePrice = FinanceUtil.formatWithScale(data.getUpDropPrice());
                if (rateChange.startsWith("-")) {
                    color = greenColor;
                } else {
                    color = redColor;
                    rateChange = "+" + rateChange;
                    ratePrice = "+" + ratePrice;
                }
                mRightIndex.setText(variety.getVarietyName());
                mRightIndexValue.setTextColor(color);
                mRightIndexValue.setText(String.valueOf(data.getLastPrice()));
                mRightLeftIndexPer.setText(ratePrice);
                mRightRightIndexPer.setText(rateChange);
                mRightLeftIndexPer.setTextColor(color);
                mRightRightIndexPer.setTextColor(color);
            }
        } else {
            setIndexViewVisible(SELECT_RIGHT, false);
            mRightSelectRL.setVisibility(View.VISIBLE);
        }
    }

    public void forceInitSelectUI() {
        setIndexViewVisible(SELECT_LEFT, false);
        setIndexViewVisible(SELECT_CENTER, false);
        setIndexViewVisible(SELECT_RIGHT, false);
        revertIndexUI();
        mLeftSelectRL.setVisibility(View.VISIBLE);
        mCenterSelectRL.setVisibility(View.VISIBLE);
        mRightSelectRL.setVisibility(View.VISIBLE);
        mLeftIndex.setTag(null);
        mCenterIndex.setTag(null);
        mRightIndex.setTag(null);
    }

    private void revertIndexUI() {
        mLeftRL.setOnClickListener(null);
        mCenterRL.setOnClickListener(null);
        mRightRL.setOnClickListener(null);
        int greyColor = ContextCompat.getColor(mContext, R.color.unluckyText);
        mLeftIndex.setText("");
        mLeftIndexValue.setTextColor(greyColor);
        mLeftIndexValue.setText("");
        mLeftLeftIndexPer.setTextColor(greyColor);
        mLeftLeftIndexPer.setText("");
        mLeftLeftIndexPer.setTextColor(greyColor);
        mLeftLeftIndexPer.setText("");
        mLeftRightIndexPer.setTextColor(greyColor);
        mLeftRightIndexPer.setText("");

        mCenterIndex.setText("");
        mCenterIndexValue.setTextColor(greyColor);
        mCenterIndexValue.setText("");
        mCenterLeftIndexPer.setTextColor(greyColor);
        mCenterLeftIndexPer.setText("");
        mCenterLeftIndexPer.setTextColor(greyColor);
        mCenterLeftIndexPer.setText("");
        mCenterRightIndexPer.setTextColor(greyColor);
        mCenterRightIndexPer.setText("");

        mRightIndex.setText("");
        mRightIndexValue.setTextColor(greyColor);
        mRightIndexValue.setText("");
        mRightLeftIndexPer.setTextColor(greyColor);
        mRightLeftIndexPer.setText("");
        mRightLeftIndexPer.setTextColor(greyColor);
        mRightLeftIndexPer.setText("");
        mRightRightIndexPer.setTextColor(greyColor);
        mRightRightIndexPer.setText("");
    }

    private void setIndexViewVisible(int locationIndexVisible, boolean isVisible) {
        int visible = isVisible ? View.VISIBLE : View.GONE;
        switch (locationIndexVisible) {
            case SELECT_LEFT:
                mLeftIndex.setVisibility(visible);
                mLeftIndexValue.setVisibility(visible);
                mLeftLeftIndexPer.setVisibility(visible);
                mLeftRightIndexPer.setVisibility(visible);
                break;
            case SELECT_CENTER:
                mCenterIndex.setVisibility(visible);
                mCenterIndexValue.setVisibility(visible);
                mCenterLeftIndexPer.setVisibility(visible);
                mCenterRightIndexPer.setVisibility(visible);
                mCenterLine.setVisibility(visible);
                break;
            case SELECT_RIGHT:
                mRightIndex.setVisibility(visible);
                mRightIndexValue.setVisibility(visible);
                mRightLeftIndexPer.setVisibility(visible);
                mRightRightIndexPer.setVisibility(visible);
                mRightLine.setVisibility(visible);
                break;
        }
    }

}
