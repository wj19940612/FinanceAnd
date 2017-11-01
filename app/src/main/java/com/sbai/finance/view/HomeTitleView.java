package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.Dictum;
import com.sbai.finance.model.Greeting;
import com.sbai.finance.model.NoticeRadio;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.utils.FinanceUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\10\25 0025.
 */

public class HomeTitleView extends RelativeLayout {

    public static final int BUTTON_HUSHEN = 1;
    public static final int BUTTON_QIHUO = 2;
    public static final int BUTTON_ZIXUAN = 3;

    public static final int SELECT_LEFT = 1;
    public static final int SELECT_CENTER = 2;
    public static final int SELECT_RIGHT = 3;

    @BindView(R.id.verticalScrollText)
    VerticalScrollTextView mVerticalScrollTextView;
    @BindView(R.id.indexRL)
    RelativeLayout mIndexRL;
    @BindView(R.id.greetingTitle)
    TextView mGreetingTitle;
    @BindView(R.id.broadcastIcon)
    ImageView mBroadcastIcon;
    @BindView(R.id.broadcastText)
    TextView mBroadcastText;
    @BindView(R.id.lookAll)
    TextView mLookAll;
    @BindView(R.id.leftIndex)
    TextView mLeftIndex;
    @BindView(R.id.leftIndexValue)
    TextView mLeftIndexValue;
    @BindView(R.id.leftLeftIndexPer)
    TextView mLeftLeftIndexPer;
    @BindView(R.id.leftRightIndexPer)
    TextView mLeftRightIndexPer;
    @BindView(R.id.centerIndex)
    TextView mCenterIndex;
    @BindView(R.id.centerIndexValue)
    TextView mCenterIndexValue;
    @BindView(R.id.centerLeftIndexPer)
    TextView mCenterLeftIndexPer;
    @BindView(R.id.centerRightIndexPer)
    TextView mCenterRightIndexPer;
    @BindView(R.id.rightIndex)
    TextView mRightIndex;
    @BindView(R.id.rightIndexValue)
    TextView mRightIndexValue;
    @BindView(R.id.rightLeftIndexPer)
    TextView mRightLeftIndexPer;
    @BindView(R.id.rightRightIndexPer)
    TextView mRightRightIndexPer;
    @BindView(R.id.centerSelectRL)
    RelativeLayout mCenterSelectRL;
    @BindView(R.id.leftSelectRL)
    RelativeLayout mLeftSelectRL;
    @BindView(R.id.rightSelectRL)
    RelativeLayout mRightSelectRL;
    @BindView(R.id.rightLine)
    View mRightLine;
    @BindView(R.id.centerLine)
    View mCenterLine;
    @BindView(R.id.stockBtn)
    TextView mStockBtn;
    @BindView(R.id.futureBtn)
    TextView mfutureBtn;
    @BindView(R.id.selectBtn)
    TextView mselectBtn;
    @BindView(R.id.lookAllBtn)
    RelativeLayout mLookAllBtn;


    private Context mContext;
    private int oldButton;
    private Map<String, FutureData> futureDataMap = new HashMap<String, FutureData>();

    private OnDictumClickListener mOnDictumClickListener;

    public void setOnDictumClickListener(OnDictumClickListener onDictumClickListener) {
        mOnDictumClickListener = onDictumClickListener;
    }

    public interface OnDictumClickListener {
        public void onDictumClick(Dictum dictum);
    }

    public void setOnBroadcastListener(VerticalScrollTextView.OnItemClickListener onBroadcastListener) {
        mVerticalScrollTextView.setOnItemClickListener(onBroadcastListener);
    }

    public interface OnLookAllClickListener {
        public void onLookAll();

        public void onSelectClick();

        public void onPractice();

        public void onDaySubjuect();
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

    public int getOldButton() {
        return oldButton;
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
    }

    public void putNewFutureData(FutureData futureData) {
        futureDataMap.put(futureData.getInstrumentId(), futureData);
        updateFutureOrSelectData();
    }

    //问候title
    public void setGreetingTitle(Greeting greetingTitle) {
        if (greetingTitle == null) {
            mGreetingTitle.setText(R.string.app_name);
            return;
        }
        if (!TextUtils.isEmpty(greetingTitle.getGreetings())) {
            mGreetingTitle.setText(greetingTitle.getGreetings());
        } else {
            mGreetingTitle.setText(R.string.app_name);
        }
    }

    //广播内容
    public void setBroadcastContent(List<NoticeRadio> noticeRadios) {
        mBroadcastIcon.setVisibility(View.VISIBLE);
        mBroadcastText.setVisibility(View.GONE);
        mVerticalScrollTextView.setNoticeRadios(noticeRadios);
        mVerticalScrollTextView.startAutoScroll();
    }

    //设置名言
    public void setDictum(final List<Dictum> dictums) {
        mBroadcastIcon.setVisibility(View.GONE);
        mVerticalScrollTextView.setVisibility(View.GONE);
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

    @OnClick({R.id.stockBtn, R.id.futureBtn, R.id.selectBtn, R.id.centerSelectRL, R.id.leftSelectRL, R.id.rightSelectRL, R.id.lookAllBtn, R.id.practiceBtn, R.id.writeTopicBtn})
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
            case R.id.practiceBtn:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onPractice();
                }
                break;
            case R.id.writeTopicBtn:
                if (mOnLookAllClickListener != null) {
                    mOnLookAllClickListener.onDaySubjuect();
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
        switch (buttonIndex) {
            case BUTTON_HUSHEN:
                mIndexRL.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_select_open_one));
                initStockOrFutureUI(BUTTON_HUSHEN);
                break;
            case BUTTON_QIHUO:
                mIndexRL.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_select_open_two));
                initStockOrFutureUI(BUTTON_QIHUO);
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

    //数据有了，更新股票UI,这里只有股票的代码，没行情
    public void updateStockIndexData(List<Variety> data) {
        Log.e("zxx", "updateStockIndexData size" + data.size());
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
            case 3:
                mLeftIndex.setTag(data.get(0));
                mLeftIndex.setText(data.get(0).getVarietyName());
                mCenterIndex.setTag(data.get(1));
                mCenterIndex.setText(data.get(1).getVarietyName());
                mRightIndex.setTag(data.get(2));
                mRightIndex.setText(data.get(2).getVarietyName());
                break;
            default:
                break;
        }
    }

    //更新股票行情
    public void updateStockIndexMarketData(List<StockData> data) {
        Log.e("zxx", "updateStockIndexMarketData " + data.size());
        // 2.判断涨跌
        int redColor = ContextCompat.getColor(mContext, R.color.redPrimary);
        int greenColor = ContextCompat.getColor(mContext, R.color.greenAssist);
        int greyColor = ContextCompat.getColor(mContext, R.color.unluckyText);
        int color;
        Variety variety;
        for (StockData stockData : data) {
            String rateChange = FinanceUtil.formatToPercentage(stockData.getUpDropSpeed());
            String ratePrice = stockData.getUpDropPrice();
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            variety = (Variety) mLeftIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK) && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                mLeftIndexValue.setTextColor(color);
                mLeftIndexValue.setText(stockData.getLastPrice());
                mLeftLeftIndexPer.setText(ratePrice);
                mLeftRightIndexPer.setText(rateChange);
                mLeftLeftIndexPer.setTextColor(color);
                mLeftRightIndexPer.setTextColor(color);
            }
            variety = (Variety) mRightIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK) && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                mRightIndexValue.setTextColor(color);
                mRightIndexValue.setText(stockData.getLastPrice());
                mRightLeftIndexPer.setText(ratePrice);
                mRightRightIndexPer.setText(rateChange);
                mRightLeftIndexPer.setTextColor(color);
                mRightRightIndexPer.setTextColor(color);
            }
            variety = (Variety) mCenterIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK) && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                mCenterIndexValue.setTextColor(color);
                mCenterIndexValue.setText(stockData.getLastPrice());
                mCenterLeftIndexPer.setText(ratePrice);
                mCenterRightIndexPer.setText(rateChange);
                mCenterLeftIndexPer.setTextColor(color);
                mCenterRightIndexPer.setTextColor(color);
            }
        }
    }

    //更新期货名称
    public void updateFutureData(List<Variety> data) {
        if (data == null || data.size() == 0) {
            return;
        } else if (data.size() == 1) {
            mLeftIndex.setText(data.get(0).getVarietyName());
            mLeftIndex.setTag(data.get(0));
        } else if (data.size() == 2) {
            mLeftIndex.setTag(data.get(0));
            mLeftIndex.setText(data.get(0).getVarietyName());
            mCenterIndex.setTag(data.get(1));
            mCenterIndex.setText(data.get(1).getVarietyName());
        } else {
            mLeftIndex.setTag(data.get(0));
            mLeftIndex.setText(data.get(0).getVarietyName());
            mCenterIndex.setTag(data.get(1));
            mCenterIndex.setText(data.get(1).getVarietyName());
            mRightIndex.setTag(data.get(2));
            mRightIndex.setText(data.get(2).getVarietyName());
        }
        updateFutureMarketData();
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
            }
            variety = (Variety) mRightIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) && variety.getContractsCode().equalsIgnoreCase(data.getInstrumentId())) {
                mRightIndexValue.setTextColor(color);
                mRightIndexValue.setText(String.valueOf(data.getLastPrice()));
                mRightLeftIndexPer.setText(ratePrice);
                mRightRightIndexPer.setText(rateChange);
                mRightLeftIndexPer.setTextColor(color);
                mRightRightIndexPer.setTextColor(color);
            }
            variety = (Variety) mCenterIndex.getTag();
            if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) && variety.getContractsCode().equalsIgnoreCase(data.getInstrumentId())) {
                mCenterIndexValue.setTextColor(color);
                mCenterIndexValue.setText(String.valueOf(data.getLastPrice()));
                mCenterLeftIndexPer.setText(ratePrice);
                mCenterRightIndexPer.setText(rateChange);
                mCenterLeftIndexPer.setTextColor(color);
                mCenterRightIndexPer.setTextColor(color);
            }
        }
    }

    public void updateSelectData(List<Variety> data) {
        if (data == null || data.size() == 0) {
            return;
        } else if (data.size() == 1) {
            setIndexViewVisible(SELECT_LEFT, true);
            mLeftSelectRL.setVisibility(View.GONE);
            mLeftIndex.setText(data.get(0).getVarietyName());
            mLeftIndex.setTag(data.get(0));
        } else if (data.size() == 2) {
            setIndexViewVisible(SELECT_LEFT, true);
            mLeftSelectRL.setVisibility(View.GONE);
            mLeftIndex.setTag(data.get(0));
            mLeftIndex.setText(data.get(0).getVarietyName());
            setIndexViewVisible(SELECT_CENTER, true);
            mCenterSelectRL.setVisibility(View.GONE);
            mCenterIndex.setTag(data.get(1));
            mCenterIndex.setText(data.get(1).getVarietyName());
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
    }

    private void updateSelectStockMarketData(List<StockData> result) {
        updateStockIndexMarketData(result);
    }

    private void updateSelectFutureMarketData() {
        updateFutureMarketData();
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
        int greyColor = ContextCompat.getColor(mContext, R.color.unluckyText);
        mLeftIndex.setText("--");
        mLeftIndexValue.setTextColor(greyColor);
        mLeftIndexValue.setText("--");
        mLeftLeftIndexPer.setTextColor(greyColor);
        mLeftLeftIndexPer.setText("--");
        mLeftLeftIndexPer.setTextColor(greyColor);
        mLeftLeftIndexPer.setText("--");
        mLeftRightIndexPer.setTextColor(greyColor);
        mLeftRightIndexPer.setText("--");

        mCenterIndex.setText("--");
        mCenterIndexValue.setTextColor(greyColor);
        mCenterIndexValue.setText("--");
        mCenterLeftIndexPer.setTextColor(greyColor);
        mCenterLeftIndexPer.setText("--");
        mCenterLeftIndexPer.setTextColor(greyColor);
        mCenterLeftIndexPer.setText("--");
        mCenterRightIndexPer.setTextColor(greyColor);
        mCenterRightIndexPer.setText("--");

        mRightIndex.setText("--");
        mRightIndexValue.setTextColor(greyColor);
        mRightIndexValue.setText("--");
        mRightLeftIndexPer.setTextColor(greyColor);
        mRightLeftIndexPer.setText("--");
        mRightLeftIndexPer.setTextColor(greyColor);
        mRightLeftIndexPer.setText("--");
        mRightRightIndexPer.setTextColor(greyColor);
        mRightRightIndexPer.setText("--");
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
