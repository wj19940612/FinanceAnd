package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question<T extends Parcelable> implements Parcelable {

    /**
     * content : [{"key":{"content":"11111","id":"897705151689707522","seq":1,"type":0},"value":{"content":"11111","id":"897705151689707523","seq":1,"type":0}},{"key":{"content":"ceshi","id":"897705151689707524","seq":2,"type":0},"value":{"content":"rrrrrrrrrrrrrrrrrrrrrr","id":"897705151689707525","seq":2,"type":0}},{"key":{"content":"ggggggggggggg","id":"897705151689707526","seq":3,"type":0},"value":{"content":"rrrrrrrrrrrrrr","id":"897705151689707527","seq":3,"type":0}}]
     * id : 5981874668fa2534449545bf
     * levelRatio : 5
     * modifyTime : 1502864561375
     * option : false
     * status : 1
     * title : 33333333333333333
     * type : 4
     */

    private String analysis;
    private String digest;

    private String id;
    private double levelRatio;
    private long modifyTime;
    private String createTime;
    private boolean option;
    private int status;
    private String title;
    private int type;
    private T[] content;

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLevelRatio() {
        return levelRatio;
    }

    public void setLevelRatio(double levelRatio) {
        this.levelRatio = levelRatio;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isOption() {
        return option;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<T> getContent() {
        return new ArrayList<>(Arrays.asList(content));
    }

    public void setContent(List<T> content) {
        this.content = (T[]) content.toArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private String clazz;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.analysis);
        dest.writeString(this.digest);
        dest.writeString(this.id);
        dest.writeDouble(this.levelRatio);
        dest.writeLong(this.modifyTime);
        dest.writeString(this.createTime);
        dest.writeByte(this.option ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.type);

        if (clazz == null) {
            clazz = content.getClass().getName();
        }
        dest.writeString(clazz);
        dest.writeParcelableArray(this.content, flags);
    }

    public Question() {
    }

    protected Question(Parcel in) {
        this.analysis = in.readString();
        this.digest = in.readString();
        this.id = in.readString();
        this.levelRatio = in.readDouble();
        this.modifyTime = in.readLong();
        this.createTime = in.readString();
        this.option = in.readByte() != 0;
        this.status = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();

        this.clazz = in.readString();
        try {
            this.content = (T[]) in.readParcelableArray(Class.forName(clazz).getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public String toString() {
        return "Question{" +
                "analysis='" + analysis + '\'' +
                ", digest='" + digest + '\'' +
                ", id='" + id + '\'' +
                ", levelRatio=" + levelRatio +
                ", modifyTime=" + modifyTime +
                ", createTime='" + createTime + '\'' +
                ", option=" + option +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", content=" + content +
                '}';
    }
}
