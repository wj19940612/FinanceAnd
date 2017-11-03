package com.sbai.finance.model.arena;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sbai.finance.net.Client;

import java.util.ArrayList;

/**
 * Created by ${wangJie} on 2017/11/3.
 * {@link Client# /user/dictionary/findDictionaryForJson.do?type=pvp_names}
 * 竞技场兑换虚拟奖品名称
 */

public class ArenaVirtualAwardName implements Parcelable {
    private String pvp;

    public String getPvp() {
        return pvp;
    }

    public void setPvp(String pvp) {
        this.pvp = pvp;
    }

    public ArrayList<VirtualAwardName> getVirtualAwardNameList() {
        ArrayList<VirtualAwardName> virtualAwardNames = new ArrayList<>();
        if (!TextUtils.isEmpty(getPvp())) {
            String[] split = getPvp().split(";");
            for (int i = 0; i < split.length; i++) {
                VirtualAwardName virtualAwardName = new VirtualAwardName();
                virtualAwardName.setAwardName(split[i]);
                virtualAwardName.setSelect(false);
                virtualAwardNames.add(virtualAwardName);
            }

        }
        return virtualAwardNames;
    }

   public static class VirtualAwardName implements Parcelable {
        private String awardName;
        private boolean isSelect;

        public String getAwardName() {
            return awardName;
        }

        public void setAwardName(String awardName) {
            this.awardName = awardName;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.awardName);
            dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        }

        public VirtualAwardName() {
        }

        protected VirtualAwardName(Parcel in) {
            this.awardName = in.readString();
            this.isSelect = in.readByte() != 0;
        }

        public static final Creator<VirtualAwardName> CREATOR = new Creator<VirtualAwardName>() {
            @Override
            public VirtualAwardName createFromParcel(Parcel source) {
                return new VirtualAwardName(source);
            }

            @Override
            public VirtualAwardName[] newArray(int size) {
                return new VirtualAwardName[size];
            }
        };
    }

    @Override
    public String toString() {
        return "ArenaVirtualAwardName{" +
                "pvp='" + pvp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pvp);
    }

    public ArenaVirtualAwardName() {
    }

    protected ArenaVirtualAwardName(Parcel in) {
        this.pvp = in.readString();
    }

    public static final Creator<ArenaVirtualAwardName> CREATOR = new Creator<ArenaVirtualAwardName>() {
        @Override
        public ArenaVirtualAwardName createFromParcel(Parcel source) {
            return new ArenaVirtualAwardName(source);
        }

        @Override
        public ArenaVirtualAwardName[] newArray(int size) {
            return new ArenaVirtualAwardName[size];
        }
    };
}
