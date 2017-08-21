package com.sbai.finance.model.training.question;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/8/21.
 * 排序题题目内容
 */

public class SortData implements Parcelable {

    /**
     * content : 法撒旦发是的
     * id : 892321037251899392
     * right : false
     * seq : 1
     */
    private boolean isSelect;
    private String content;
    private long id;
    private boolean right;   //是否是正确答案
    private int seq;         //seq 排序

    //排序页面用来记录背景图的索引位置  自己做的标记
    private int bgPosition;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getBgPosition() {
        return bgPosition;
    }

    public void setBgPosition(int bgPosition) {
        this.bgPosition = bgPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeString(this.content);
        dest.writeLong(this.id);
        dest.writeByte(this.right ? (byte) 1 : (byte) 0);
        dest.writeInt(this.seq);
        dest.writeInt(this.bgPosition);
    }

    public SortData() {
    }

    protected SortData(Parcel in) {
        this.isSelect = in.readByte() != 0;
        this.content = in.readString();
        this.id = in.readLong();
        this.right = in.readByte() != 0;
        this.seq = in.readInt();
        this.bgPosition = in.readInt();
    }

    public static final Creator<SortData> CREATOR = new Creator<SortData>() {
        @Override
        public SortData createFromParcel(Parcel source) {
            return new SortData(source);
        }

        @Override
        public SortData[] newArray(int size) {
            return new SortData[size];
        }
    };

    public static List<SortData> getRandRomResultList(List<SortData> sortDataList) {
        Collections.shuffle(sortDataList);
        ArrayList<SortData> trainingQuestions = new ArrayList<>();
        for (int i = 0; i < sortDataList.size(); i++) {
            SortData sortData = sortDataList.get(i);
            sortData.setBgPosition(i);
            trainingQuestions.add(sortData);
        }
        return trainingQuestions;
    }

}
