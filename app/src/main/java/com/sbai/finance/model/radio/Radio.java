package com.sbai.finance.model.radio;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台信息
 */

public class Radio {

    private String radioCover;
    private String radioTitle;
    private long time;
    private long radioLength;
    private String radioOwner;
    private String radioName;

    public String getRadioCover() {
        return radioCover;
    }

    public void setRadioCover(String radioCover) {
        this.radioCover = radioCover;
    }

    public String getRadioTitle() {
        return radioTitle;
    }

    public void setRadioTitle(String radioTitle) {
        this.radioTitle = radioTitle;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getRadioLength() {
        return radioLength;
    }

    public void setRadioLength(long radioLength) {
        this.radioLength = radioLength;
    }

    public String getRadioOwner() {
        return radioOwner;
    }

    public void setRadioOwner(String radioOwner) {
        this.radioOwner = radioOwner;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }
}
