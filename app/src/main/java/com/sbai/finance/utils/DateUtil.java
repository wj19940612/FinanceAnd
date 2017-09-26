package com.sbai.finance.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sbai.finance.model.local.SysTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_YEAR = "yyyy年MM月dd日 HH:mm:ss";
    public static final String FORMAT_NOT_SECOND = "MM月dd日 HH:mm";
    public static final String FORMAT_NOT_HOUR = "MM月dd日 ";
    public static final String FORMAT_ONLY_DATE = "dd日 ";
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy年MM月dd日";
    public static final String FORMAT_YEAR_MONTH = "yyyy年MM月";
    public static final String FORMAT_SPECIAL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_SPECIAL_SLASH = "yyyy/MM/dd HH:mm";
    public static final String FORMAT_SPECIAL_SLASH_NO_HOUR = "yyyy/MM/dd";
    public static final String FORMAT_HOUR_MINUTE = "HH:mm";
    public static final String FORMAT_DATE_HOUR_MINUTE = "dd日 HH:mm";


    private static final String TODAY = "今日";
    private static final String YESTODAY = "昨日";

    public static String format(long time, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(toFormat);
        return dateFormat.format(new Date(time));
    }

    public static String format(String time, String fromFormat, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(toFormat);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isToday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isToday(date, todayDate);
    }

    public static boolean isToday(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTomorrow(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isTomorrow(date, todayDate);
    }

    public static boolean isTomorrow(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return isToday(calendar.getTime(), todayCalendar.getTime());
    }

    public static boolean isYesterday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isYesterday(date, todayDate);
    }

    private static boolean isYesterday(Date date, Date todayDate) {
        Calendar toadyCalendar = Calendar.getInstance();
        toadyCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        toadyCalendar.add(Calendar.DAY_OF_YEAR, -1);
        return isToday(calendar.getTime(), toadyCalendar.getTime());
    }

    public static boolean isNextWeek(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isNextWeek(date, todayDate);
    }

    public static boolean isNextWeek(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        return calendar.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isInThisMonth(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isInThisMonth(date, todayDate);
    }

    public static boolean isInThisMonth(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
    }

    public static boolean isInThisYear(long time) {
        Date date = new Date(time);
        return isInThisYear(date);
    }

    public static boolean isInThisYear(String time, String fromFormat) {
        if (time.length() != fromFormat.length()) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            return isInThisYear(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInThisYear(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }

    public static String addOneMinute(String date, String format) {
        if (date.length() != format.length()) {
            return date;
        }
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            long originDate = parser.parse(date).getTime();
            long finalDate = originDate + 60 * 1000; // 1 min
            return parser.format(new Date(finalDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDayOfWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String result = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                result = "一";
                break;
            case Calendar.TUESDAY:
                result = "二";
                break;
            case Calendar.WEDNESDAY:
                result = "三";
                break;
            case Calendar.THURSDAY:
                result = "四";
                break;
            case Calendar.FRIDAY:
                result = "五";
                break;
            case Calendar.SATURDAY:
                result = "六";
                break;
            case Calendar.SUNDAY:
                result = "日";
                break;
            default:
                break;
        }
        return result;
    }

    public static String format(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    public static String formatSlash(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_SPECIAL_SLASH);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    public static String getFormatSpecialSlashNoHour(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_SPECIAL_SLASH_NO_HOUR);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    /**
     * 将日期格式转化为时间(秒数)
     *
     * @param time
     * @return
     */
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 将日期格式转化为时间(秒数)
     *
     * @param time
     * @return
     */
    public static long getStringToDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    /**
     * 判断 time1 是否比 time2 晚了 milliseconds, 即 time1 - time2 <= milliseconds && time1 - time2 >= 0
     *
     * @param time1
     * @param time2
     * @param milliseconds
     * @return
     */
    public static boolean isLessThanTimeInterval(long time1, long time2, long milliseconds) {
        long diff = time1 - time2;
        Log.d("TAG", "isLessThanTimeInterval: " + diff);
        return diff >= 0 && diff <= milliseconds;
    }

    /**
     * @param startTime 最新的时间
     * @param endTime
     * @return
     */
    public static boolean isTimeBetweenFiveMin(long startTime, long endTime) {
        int difference = (int) (startTime - endTime) / (60 * 1000);
        Log.d("dateUtil", "相差数据" + difference + "  开始的时间" + DateUtil.format(startTime) + "  比较时间" + DateUtil.format(endTime));

        return 5 < difference;
    }

    /**
     * 判断指定时间和当前时间是否小于minute分钟
     *
     * @param txtDate 指定的时间
     * @return
     */
    public static boolean isTimeMatchFiveMin(String txtDate) {
        return isTimeMatchFiveMin(txtDate, 1);
    }

    public static boolean isTimeMatchFiveMin(String txtDate, int minute) {
        if (TextUtils.isEmpty(txtDate)) {
            return false;
        }
        if (minute == 0) {
            minute = 5;
        }
        try {
            final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
            final Date workDay = format.parse(txtDate);

            final Calendar c1 = Calendar.getInstance();
            final Calendar c2 = Calendar.getInstance();

            final Date currTime = new Date();

            c1.setTime(workDay);
            c2.setTime(currTime);

            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                    && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)) {
                if (Math.abs(c1.get(Calendar.MINUTE) - c2.get(Calendar.MINUTE)) < minute) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 格式化时间  如果是当天 则显示18:20
     * 如果是昨天 则 昨天 18:20
     * 其他的   12月12日 12:20
     * 不是今年  则 2015年12月18日
     *
     * @param createTime
     * @return
     */
    public static String getFormatTime(long createTime) {
        long systemTime = System.currentTimeMillis();
        if (DateUtil.isInThisYear(createTime)) {
            if (DateUtil.isToday(createTime, systemTime)) {
                return DateUtil.format(createTime, "HH:mm");
            } else if (DateUtil.isYesterday(createTime, systemTime)) {
                return DateUtil.format(createTime, "昨日  " + "HH:mm");
            } else {
                return DateUtil.format(createTime, DateUtil.FORMAT_NOT_HOUR);
            }
        } else {
            return DateUtil.format(createTime, DateUtil.FORMAT_YEAR_MONTH_DAY);
        }
    }

    /**
     * 返回格式 7月15日
     *
     * @param createTime
     * @return
     */

    public static String getStudyFormatTime(long createTime) {
        return DateUtil.format(createTime, DateUtil.FORMAT_NOT_HOUR);
    }

    /**
     * 格式化时间  一分钟之内显示刚刚
     * 一小时内显示多少分钟前
     * 一小时后显示几点几分 06:24
     * 超过零点显示几月几日 07/07
     * 不是今年  则 2015/12/18
     *
     * @param createTime
     * @return
     */
    public static String getMissFormatTime(long createTime) {
        long systemTime = System.currentTimeMillis();
        if (DateUtil.isInThisYear(createTime)) {
            if (DateUtil.isToday(createTime, systemTime)) {
                if (systemTime - createTime < 60 * 1000) {
                    return "刚刚";
                } else if (systemTime - createTime < 60 * 60 * 1000) {
                    return ((systemTime - createTime) / (60 * 1000) + "分钟前");
                } else {
                    return DateUtil.format(createTime, "HH:mm");
                }
            } else {
                return DateUtil.format(createTime, "MM/dd");
            }
        } else {
            return DateUtil.format(createTime, DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR);
        }
    }

    /**
     * 当前是否白天
     *
     * @return
     */
    public static boolean isDayTime() {
        long systemTime = System.currentTimeMillis();
        int time = Integer.valueOf(DateUtil.format(systemTime, "HHmm"));
        return time >= 600 && time <= 1800;
    }

    /**
     * 格式化时间  如果是当天 则显示18:20
     * 如果是昨天 则 昨天 18:20
     * 其他的   12月12日 12:20
     * 不是今年  则 15年12月18日 18:20:20
     *
     * @param createTime
     * @return
     */
    public static String getBattleFormatTime(long createTime) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (DateUtil.isToday(createTime, systemTime)) {
            return DateUtil.format(createTime, "HH:mm:ss");
        } else if (DateUtil.isYesterday(createTime, systemTime)) {
            return DateUtil.format(createTime, "昨日" + "HH:mm:ss");
        } else if (DateUtil.isInThisYear(createTime)) {
            return DateUtil.format(createTime, "MM/dd HH:mm:ss");
        } else {
            return DateUtil.format(createTime, "yy/MM/dd HH:mm:ss");
        }
    }

    /**
     * 格式化时间
     * 如果是当天 则显示 18：20;
     * 如果是本年内的其他时期  12/07;
     * 跨年  2015/12/07
     *
     * @param createTime
     * @return
     */
    public static String formatQuestionStyleTime(long createTime) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (DateUtil.isToday(createTime, systemTime)) {
            return DateUtil.format(createTime, "HH:mm");
        } else if (DateUtil.isInThisYear(createTime)) {
            return DateUtil.format(createTime, "MM/dd ");
        } else {
            return DateUtil.format(createTime, "yyyy/MM/dd ");
        }
    }

    /**
     * 获取明细页面的格式化时间
     * 日期显示：
     * 本日记录：今日00:00；
     * 昨日记录：昨日00:00
     * 两日以前记录：XX日00:00
     *
     * @param createTime
     * @return
     */
    public static String getDetailFormatTime(long createTime) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (isToday(createTime, systemTime)) {
            return TODAY;
        }
        if (isYesterday(createTime, systemTime)) {
            return YESTODAY;
        }
        return DateUtil.format(createTime, FORMAT_ONLY_DATE);
    }

    public static String formatUserFundDetailTime(long time) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (isToday(time, systemTime)) {
            return TODAY + " " + DateUtil.format(time, FORMAT_HOUR_MINUTE);
        }
        if (isYesterday(time, systemTime)) {
            return YESTODAY + " " + DateUtil.format(time, FORMAT_HOUR_MINUTE);
        }
        return DateUtil.format(time, FORMAT_DATE_HOUR_MINUTE);
    }

    /**
     * 获取反馈页面的格式化时间
     * 日期显示：
     * 本日记录：今天
     * 昨日记录：昨天
     * 两日以前记录：XX日00:00
     *
     * @param createTime
     * @return
     */
    public static String getFeedbackFormatTime(long createTime) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (isToday(createTime, systemTime)) {
            return "今天";
        }
        if (isYesterday(createTime, systemTime)) {
            return "昨天";
        }
        if (isInThisYear(createTime)) {
            return DateUtil.format(createTime, FORMAT_NOT_HOUR);
        }
        return DateUtil.format(createTime, FORMAT_YEAR_MONTH_DAY);
    }

    /**
     * 格式化月份  如果是当月 则显示本月
     * 如果是当年中的其他月份  显示x月
     * 如果是跨年月份   则显示xxxx年xx月
     *
     * @param createTime
     * @return
     */
    public static String formatMonth(long createTime) {
        long systemTime = SysTime.getSysTime().getSystemTimestamp();
        if (DateUtil.isInThisMonth(createTime, systemTime)) {
            return "本月";
        } else if (DateUtil.isInThisYear(createTime)) {
            return DateUtil.format(createTime, "M月");
        } else {
            return DateUtil.format(createTime, "yyyy年MM月");
        }

    }

    /**
     * 计算传入的时间与当前时间的相差天数
     *
     * @param date 服务器所传递的时间
     * @return
     */
    public static String compareTimeDifference(String date) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long overTime = getStringToDate(date, DateUtil.FORMAT_SPECIAL) / 1000;
        long time = overTime - curTime;

        if (time < 60 && time >= 0) {
            return "1分钟后";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟后";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时后";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天后";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月后";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年后";
        } else {
            return "0";
        }
    }

    /**
     * 比较两个时间相差的天数
     *
     * @param timestamp
     * @return
     */
    public static int compareDateDifference(long timestamp) {
        long systemTime = System.currentTimeMillis();
        if (timestamp >= systemTime) {
            return 0;
        }
        long days = (systemTime - timestamp) / (1000 * 60 * 60 * 24);
        return (int) days;
    }

    public static String getTodayStartTime(long timestamp) {
        return format(timestamp, "yyyy-MM-dd") + " 00:00:00";
    }

    public static String getTodayEndTime(long timestamp) {
        return format(timestamp, "yyyy-MM-dd") + " 24:00:00";
    }

    /**
     * 比较两个时间相差的分钟 返回格式为11:40
     */
    public static String compareTime(long timestamp) {
        String resultHour;
        String resultMin;
        long systemTime = System.currentTimeMillis();
        if (timestamp == 0 || timestamp < systemTime) {
            return "00:00";
        }
        long minutes = (timestamp - systemTime) / (1000 * 60);
        long hours = minutes / 60;
        long minute = minutes % 60;
        if (hours >= 24) {
            return "24:00";
        }
        if (hours < 10) {
            resultHour = "0" + String.valueOf(hours);
        } else {
            resultHour = String.valueOf(hours);
        }
        if (minute < 10) {
            resultMin = "0" + String.valueOf(minute);
        } else {
            resultMin = String.valueOf(minute);
        }
        return resultHour + ":" + resultMin;
    }


    /**
     * string 类型转换为 long 类型  strTime 的时间格式和 formatType 的时间格式必须相同
     *
     * @param time
     * @param fromFormat 时间格式
     * @return
     * @throws ParseException
     */
    public static long convertString2Long(String time, String fromFormat) {
        Date date = convertString2Date(time, fromFormat); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            return date.getTime();
        }
    }


    /**
     * string 类型转换为 date 类型
     *
     * @param time
     * @param fromFormat 要转换的格式 yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * @return
     * @throws ParseException
     */
    public static Date convertString2Date(String time, String fromFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //xxxx年第几季度
    public static String getYearQuarter(String date) {
        return getYearQuarter(getStringToDate(date, "yyyy-MM-dd"));
    }

    public static String getYearQuarter(long date) {
        Calendar.getInstance().setTime(new Date(date));
        int i = Calendar.getInstance().get(Calendar.MONTH);
        String year = format(date, "yyyy");
        String month = format(date, "yyyy MM");
        month = month.substring(month.length() - 2, month.length());
        if (!TextUtils.isEmpty(month) && month.startsWith("0")) {
            month = month.substring(0, month.length());
        }
        int monthTime = Integer.valueOf(month);
        return year + "年第" + getQuarter(monthTime) + "季度  " + "(截止日期: " + format(date, FORMAT_SPECIAL_SLASH_NO_HOUR + ")");
    }

    public static String getQuarter(int time) {
        String monthTime = String.valueOf(time);
        switch (time) {
            case 1:
            case 2:
            case 3:
                return "—";
            case 4:
            case 5:
            case 6:
                return "二";
            case 7:
            case 8:
            case 9:
                return "三";
            case 10:
            case 11:
            case 12:
                return "四";
        }
        return "";
    }

    //return xx:xx
    public static String getCountdownTime(int total, int spend) {
        String timeStr = null;
        int last = total - spend;
        int minute = 0;
        int second = 0;
        if (last <= 0) {
            return "00:00";
        } else {
            minute = last / 60;
            if (minute < 60) {
                second = last % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 15分钟
     *
     * @param seconds
     * @return
     */
    public static String getMinutes(long seconds) {
        return seconds / 60 + "分钟";
    }

    /**
     * 1分钟内：显示xx秒
     * 60s等整数：显示1分钟
     * 超过60后：显示xx分xx秒
     */

    public static String formatTime(long second) {
        if (second < 60) {
            return second + "秒";
        } else if (second % 60 == 0) {
            return second / 60 + "分钟";
        } else {
            return second / 60 + "分" + second % 60 + "秒";
        }
    }


    /**
     * 获取两段时间相差的秒
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int getDiffSeconds(long time1, long time2) {
        long diff = time1 - time2;
        return (int) (diff / 1000);
    }
}
