package com.sbai.chart;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sbai.chart.domain.TrendViewData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrendView extends FrameLayout {

    private TrendChart mChart;
    private TouchView mTouchView;
    private TwinkleView mTwinkleView;

    public TrendView(Context context) {
        super(context);
        init();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mChart = new TrendChart(getContext());
        int padding = (int) mChart.dp2Px(14);
        mChart.setPadding(padding, 0, padding, 0);
        addView(mChart, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mTwinkleView = new TwinkleView(getContext(), mChart);
        mTwinkleView.setPadding(padding, 0, padding, 0);
        addView(mTwinkleView, 1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mTouchView = new TouchView(getContext(), mChart);
        mTouchView.setPadding(padding, 0, padding, 0);
        addView(mTouchView, 2, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setSettings(Settings settings) {
        mChart.setSettings(settings);
        mTwinkleView.setSettings(settings);
        mTouchView.setSettings(settings);
    }

    public Settings getSettings() {
        return mChart.getSettings();
    }

    public void setDataList(List<TrendViewData> dataList) {
        mChart.setDataList(dataList);
        mTwinkleView.setDataList(dataList);
    }

    public void setUnstableData(TrendViewData unstableData) {
        mChart.setUnstableData(unstableData);
        mTwinkleView.setUnstableData(unstableData);
        mTouchView.setUnstableData(unstableData);
    }

    public List<TrendViewData> getDataList() {
        return mChart.getDataList();
    }

    public void clearData() {
        mChart.clearData();
        mTouchView.clearData();
        mTwinkleView.clearData();
    }

    public static class Util {

        /**
         * get diff minutes bewteen endDate and startDate. endDate - startDate
         *
         * @param startDate
         * @param endDate
         * @return
         */
        public static int getDiffMinutes(String startDate, String endDate) {
            long diff = 0;
            try {
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                long start = parser.parse(startDate).getTime();
                long end = parser.parse(endDate).getTime();

                if (startDate.compareTo(endDate) <= 0) { // eg. 09:00 <= 09:10
                    diff = end - start;
                } else { // eg. 21:00 ~ 01:00, we should change 01:00 to 25:00
                    diff = end + 24 * 60 * 60 * 1000 - start;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                return (int) (diff / (60 * 1000));
            }
        }

        /**
         * get diff minutes bewteen endDate and startDate. endDate - startDate. 
         * <br/> Note: if endDate <= startDate, endDate will add 24:00
         *
         * @param startDate
         * @param endDate
         * @return
         */
        public static int calculateRangFromMarketTimes(String startDate, String endDate) {
            long diff = 0;
            try {
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                long start = parser.parse(startDate).getTime();
                long end = parser.parse(endDate).getTime();

                if (startDate.compareTo(endDate) < 0) { // eg. 09:00 < 09:10
                    diff = end - start;
                } else { // eg. 21:00 ~ 01:00, we should change 01:00 to 25:00
                    diff = end + 24 * 60 * 60 * 1000 - start;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                return (int) (diff / (60 * 1000));
            }
        }

        /**
         * check if trendView data.date is valid
         *
         * @param date
         * @param openMarketTime
         * @return
         */
        public static boolean isValidDate(String date, String[] openMarketTime) {
            return Util.isBetweenTimes(openMarketTime, date);
        }

        /**
         * check if time is between times[i] and times[i + 1] (open interval)
         * <br/> ps. 数据时间的合法性判断使用开区间
         *
         * @param times
         * @param time
         * @return
         */
        private static boolean isBetweenTimes(String[] times, String time) {
            int size = times.length;

            if (size % 2 != 0) {
                size = size - 1; // make size even
            }

            for (int i = 0; i < size; i += 2) {
                if (isBetweenTimes(times[i], times[i + 1], time)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * check if time is between time1 and time2 (open interval), time1 <= time < time2
         *
         * @param time1
         * @param time2
         * @param time
         * @return
         */
        private static boolean isBetweenTimes(String time1, String time2, String time) {
            if (time1.compareTo(time2) < 0) {
                return time.compareTo(time1) >= 0 && time.compareTo(time2) < 0;
            } else if (time1.compareTo(time2) > 0) {
                return time.compareTo(time1) >= 0 || time.compareTo(time2) < 0;
            } else { // equal
                return true;
            }
        }

        /**
         * check if time is between time1 and time2 (close interval), time1 <= time <= time2
         * <br/> ps. 数据时间的坐标的计算使用闭区间，因为要计算闭市时间的坐标
         *
         * @param time1
         * @param time2
         * @param time
         * @return
         */
        public static boolean isBetweenTimesClose(String time1, String time2, String time) {
            if (time1.compareTo(time2) < 0) {
                return time.compareTo(time1) >= 0 && time.compareTo(time2) <= 0;
            } else if (time1.compareTo(time2) > 0) {
                return time.compareTo(time1) >= 0 || time.compareTo(time2) <= 0;
            } else { // equal
                return true;
            }
        }

        /**
         * 平均分割时间段为 segments 段 <br/>
         * eg. 06:00~18:00 split segments(4) to [06:00,09:00)[09:00,12:00)[12:00,15:00)[15:00,18:00]<br/>
         * return 06:00+09:00+12:00+15:00+18:00
         *
         * @param startTime
         * @param endTime
         * @param segments
         * @return
         */
        public static String[] splitTimeEvenly(String startTime, String endTime, int segments) {
            int diff = calculateRangFromMarketTimes(startTime, endTime);
            int oneDiff = diff / segments;
            String[] times = new String[segments + 1];
            times[0] = startTime;
            times[times.length - 1] = endTime;
            for (int i = 1; i < segments; i++) {
                times[i] = addMinutes(times[i - 1], oneDiff);
            }
            return times;
        }

        /**
         * 开始时间 startTime 加上 minutes 分钟后的时间，格式都是HH:mm
         *
         * @param startTime
         * @param minutes
         * @return
         */
        public static String addMinutes(String startTime, int minutes) {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            try {
                long start = parser.parse(startTime).getTime();
                start += minutes * 60 * 1000;
                startTime = parser.format(new Date(start));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                return startTime;
            }
        }
    }

    public static class Settings extends ChartSettings {

        private float mLimitUpPercent;
        private String mOpenMarketTimes;
        private String mDisplayMarketTimes;
        private boolean mCalculateXAxisFromOpenMarketTime;
        private boolean mXAxisRefresh;
        private boolean mGameMode;

        public Settings() {
            super();
            mLimitUpPercent = 0;
        }

        public void setLimitUpPercent(float limitUpPercent) {
            mLimitUpPercent = limitUpPercent;
        }

        public void setOpenMarketTimes(String openMarketTimes) {
            mOpenMarketTimes = openMarketTimes;
            mXAxisRefresh = true;
        }

        public String[] getOpenMarketTimes() {
            String[] result = new String[0];
            if (!TextUtils.isEmpty(mOpenMarketTimes)) {
                return mOpenMarketTimes.split(";");
            }
            return result;
        }

        public String[] getDisplayMarketTimes() {
            if (TextUtils.isEmpty(mDisplayMarketTimes)) {
                String[] openMarketTimes = getOpenMarketTimes();
                return Util.splitTimeEvenly(openMarketTimes[0], openMarketTimes[1], 5);
            } else {
                return mDisplayMarketTimes.split(";");
            }
        }

        public void setDisplayMarketTimes(String displayMarketTimes) {
            mDisplayMarketTimes = displayMarketTimes;
        }

        public float getLimitUp() {
            return getPreClosePrice() * mLimitUpPercent / 100f;
        }

        public void setCalculateXAxisFromOpenMarketTime(boolean value) {
            mCalculateXAxisFromOpenMarketTime = value;
            mXAxisRefresh = true;
        }

        @Override
        public int getXAxis() {
            if (mCalculateXAxisFromOpenMarketTime) {
                if (mXAxisRefresh) {
                    String[] openMarketTime = getOpenMarketTimes();
                    int size = openMarketTime.length % 2 == 0 ?
                            openMarketTime.length : openMarketTime.length - 1;
                    int xAxis = 0;
                    for (int i = 0; i < size; i += 2) {
                        xAxis += Util.calculateRangFromMarketTimes(openMarketTime[i], openMarketTime[i + 1]);
                    }
                    setXAxis(xAxis - 1);
                    mXAxisRefresh = false;

                }
                return super.getXAxis();
            }
            return super.getXAxis();
        }
    }
}
