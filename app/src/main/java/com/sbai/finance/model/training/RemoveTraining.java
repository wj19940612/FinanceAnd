package com.sbai.finance.model.training;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 消消乐题结构
 */

public class RemoveTraining implements Parcelable {
    private String content;
    private String id;
    private int seq;
    private int type;
    private String imageUrl;

    public boolean isImage() {
        return type == 1;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "RemoveTraining{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", seq=" + seq +
                ", type=" + type +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.id);
        dest.writeInt(this.seq);
        dest.writeInt(this.type);
        dest.writeString(this.imageUrl);
    }

    public RemoveTraining() {
    }

    protected RemoveTraining(Parcel in) {
        this.content = in.readString();
        this.id = in.readString();
        this.seq = in.readInt();
        this.type = in.readInt();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<RemoveTraining> CREATOR = new Parcelable.Creator<RemoveTraining>() {
        @Override
        public RemoveTraining createFromParcel(Parcel source) {
            return new RemoveTraining(source);
        }

        @Override
        public RemoveTraining[] newArray(int size) {
            return new RemoveTraining[size];
        }
    };
}
