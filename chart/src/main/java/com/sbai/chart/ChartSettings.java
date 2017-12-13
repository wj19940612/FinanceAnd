package com.sbai.chart;

public class ChartSettings {

    private float[] mBaseLines;
    private long[] mIndexesBaseLines;
    private boolean mIndexesEnable;
    private int mNumberScale;
    private int mXAxis;
    private float mPreClosePrice;
    private ColorCfg mColorCfg;
    private int mTimeLineHeight;

    public ChartSettings() {
        mBaseLines = new float[0];
        mIndexesBaseLines = new long[0];
        mIndexesEnable = false;
        mNumberScale = 2;
        mXAxis = 0;
        mPreClosePrice = 0;
    }

    public float getPreClosePrice() {
        return mPreClosePrice;
    }

    public void setPreClosePrice(float preClosePrice) {
        mPreClosePrice = preClosePrice;
    }

    public float[] getBaseLines() {
        return mBaseLines;
    }

    public void setBaseLines(int baseLines) {
        if (baseLines < 2) {
            baseLines = 2;
        }
        mBaseLines = new float[baseLines];
    }

    public long[] getIndexesBaseLines() {
        return mIndexesBaseLines;
    }

    public void setIndexesBaseLines(int indexesBaseLines) {
        if (indexesBaseLines < 2) {
            indexesBaseLines = 2;
        }
        mIndexesBaseLines = new long[indexesBaseLines];
    }

    public boolean isIndexesEnable() {
        return mIndexesEnable;
    }

    public void setIndexesEnable(boolean indexesEnable) {
        mIndexesEnable = indexesEnable;
    }

    public int getNumberScale() {
        return mNumberScale;
    }

    public void setNumberScale(int numberScale) {
        mNumberScale = numberScale;
    }

    public void setXAxis(int XAxis) {
        mXAxis = XAxis;
    }

    public int getXAxis() {
        return mXAxis;
    }

    public void setColorCfg(ColorCfg colorCfg) {
        mColorCfg = colorCfg;
    }

    public ColorCfg getColorCfg() {
        return mColorCfg;
    }
}
