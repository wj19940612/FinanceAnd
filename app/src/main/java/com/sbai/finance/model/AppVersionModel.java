package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.App;
import com.sbai.finance.utils.AppInfo;

/**
 * Created by ${wangJie} on 2017/7/4.
 */

public class AppVersionModel implements Parcelable {
    private static final String TAG = "AppVersionModel";

    /**
     * createTime : 1499085818000
     * downloadUrl : www.baidu.com
     * forceUpdateAllPreVersions : 0
     * forceUpdatePreVersions : 1.0，1.1，1.2
     * id : 1
     * lastVersion : 1.5
     * modifyTime : 1499085818000
     * modifyUser : 0
     * platform : 0
     * remark : 备注
     * updateAllPreVersions : 0
     * updateLog : 这是一次非常重要的更新
     */

    private long createTime;
    private String downloadUrl;
    //是否强制更新之前版本  0 false   1 true
    private int forceUpdateAllPreVersions;
    //强制更新版本
    private String forceUpdatePreVersions;
    private int id;
    //最新版本
    private String lastVersion;
    private long modifyTime;
    private int modifyUser;
    private int platform;
    private String remark;
    //更新所有之前版本   0 false   1 true
    private int updateAllPreVersions;
    private String updateLog;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getForceUpdateAllPreVersions() {
        return forceUpdateAllPreVersions;
    }

    public void setForceUpdateAllPreVersions(int forceUpdateAllPreVersions) {
        this.forceUpdateAllPreVersions = forceUpdateAllPreVersions;
    }

    public String getForceUpdatePreVersions() {
        return forceUpdatePreVersions;
    }

    public void setForceUpdatePreVersions(String forceUpdatePreVersions) {
        this.forceUpdatePreVersions = forceUpdatePreVersions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(int modifyUser) {
        this.modifyUser = modifyUser;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUpdateAllPreVersions() {
        return updateAllPreVersions;
    }

    public void setUpdateAllPreVersions(int updateAllPreVersions) {
        this.updateAllPreVersions = updateAllPreVersions;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    //是否强制更新
    public boolean isForceUpdate() {
        String versionName = AppInfo.getVersionName(App.getAppContext());
        if (getForceUpdateAllPreVersions() == 1) {
            return true;
        } else if (getForceUpdatePreVersions().contains(versionName)) {
            return true;
        }
        return false;
    }

    public boolean isLatestVersion() {
        return AppInfo.getVersionName(App.getAppContext()).equalsIgnoreCase(getLastVersion());
    }

    @Override
    public String toString() {
        return "AppVersionModel{" +
                "createTime=" + createTime +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", forceUpdateAllPreVersions=" + forceUpdateAllPreVersions +
                ", forceUpdatePreVersions='" + forceUpdatePreVersions + '\'' +
                ", id=" + id +
                ", lastVersion='" + lastVersion + '\'' +
                ", modifyTime=" + modifyTime +
                ", modifyUser=" + modifyUser +
                ", platform=" + platform +
                ", remark='" + remark + '\'' +
                ", updateAllPreVersions=" + updateAllPreVersions +
                ", updateLog='" + updateLog + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.createTime);
        dest.writeString(this.downloadUrl);
        dest.writeInt(this.forceUpdateAllPreVersions);
        dest.writeString(this.forceUpdatePreVersions);
        dest.writeInt(this.id);
        dest.writeString(this.lastVersion);
        dest.writeLong(this.modifyTime);
        dest.writeInt(this.modifyUser);
        dest.writeInt(this.platform);
        dest.writeString(this.remark);
        dest.writeInt(this.updateAllPreVersions);
        dest.writeString(this.updateLog);
    }

    public AppVersionModel() {
    }

    protected AppVersionModel(Parcel in) {
        this.createTime = in.readLong();
        this.downloadUrl = in.readString();
        this.forceUpdateAllPreVersions = in.readInt();
        this.forceUpdatePreVersions = in.readString();
        this.id = in.readInt();
        this.lastVersion = in.readString();
        this.modifyTime = in.readLong();
        this.modifyUser = in.readInt();
        this.platform = in.readInt();
        this.remark = in.readString();
        this.updateAllPreVersions = in.readInt();
        this.updateLog = in.readString();
    }

    public static final Creator<AppVersionModel> CREATOR = new Creator<AppVersionModel>() {
        @Override
        public AppVersionModel createFromParcel(Parcel source) {
            return new AppVersionModel(source);
        }

        @Override
        public AppVersionModel[] newArray(int size) {
            return new AppVersionModel[size];
        }
    };
}
