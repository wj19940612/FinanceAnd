package com.sbai.chart;

import java.util.HashMap;

/**
 * Modified by john on 11/12/2017
 *
 * 图表颜色配置类
 */
public class ColorCfg {
    public static final String DASH_LINE = "dashLine";
    public static final String DEFAULT_TXT = "defaultText";
    public static final String UNSTABLE_PRICE = "unstablePrice";
    public static final String UNSTABLE_PRICE_BG = "unstablePriceBg";
    public static final String REAL_TIME_LINE = "realTimeLine";
    public static final String TOUCH_LINE_TXT = "touchLineText";
    public static final String TOUCH_LINE_TXT_BG = "touchLineTextBg";
    public static final String BASE_LINE = "baseLine";
    public static final String TOUCH_LINE = "touchLine";

    private HashMap<String, String> mCfg;

    public ColorCfg() {
        mCfg = new HashMap<>();
    }

    public ColorCfg put(String elementName, String color) {
        mCfg.put(elementName, color);
        return this;
    }

    public String get(String elementName) {
        return mCfg.get(elementName);
    }

    public ColorCfg put(String[] elementNames, String color) {
        for (String elementName : elementNames) {
            mCfg.put(elementName, color);
        }
        return this;
    }
}
