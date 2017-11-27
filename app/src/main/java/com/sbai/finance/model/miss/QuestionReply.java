package com.sbai.finance.model.miss;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 * /user/comment/replyList.do
 */

public class QuestionReply {


    /**
     * data : [{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664354913,"dataId":2,"id":"5981946377c87b05eb1b3c0b","remark":"内容正常","replyParentId":"5981941977c85d3b0d864f41","replys":[{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}],"status":1,"sysAuditor":1,"type":1,"userAuditor":1},{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyCount":1,"replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}]
     * pageSize : 2
     * resultCount : 11
     * show : 0
     * total : 6
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;
    private List<DataBean> data;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {

        public static final int QUESTION_REVIEW_NOT_PRISE = 0;
        public static final int QUESTION_REVIEW_PRISE = 1;

        /**
         * content : 发表一个评论yoyoyoyoyoyo333333!
         * createDate : 1501664354913
         * dataId : 2
         * id : 5981946377c87b05eb1b3c0b
         * remark : 内容正常
         * replyParentId : 5981941977c85d3b0d864f41
         * replys : [{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}]
         * status : 1
         * sysAuditor : 1
         * type : 1
         * userAuditor : 1
         * replyCount : 1
         * star : 2
         * userModel : {"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}
         */

        private String content;
        private long createDate;
        private int dataId;
        private String id;
        private String remark;
        private String replyParentId;
        private int status;
        private int sysAuditor;
        private int type;
        private int userAuditor;
        private int replyCount;
        private int star;
        private UserModelBean userModel;
        private List<ReplysBean> replys;

        private int priseCount;
        private boolean isPrise;

        public boolean isPrise() {
            return isPrise;
        }

        public void setPrise(boolean prise) {
            isPrise = prise;
        }

        public int getPriseCount() {
            return priseCount;
        }

        public void setPriseCount(int priseCount) {
            this.priseCount = priseCount;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public int getDataId() {
            return dataId;
        }

        public void setDataId(int dataId) {
            this.dataId = dataId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getReplyParentId() {
            return replyParentId;
        }

        public void setReplyParentId(String replyParentId) {
            this.replyParentId = replyParentId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSysAuditor() {
            return sysAuditor;
        }

        public void setSysAuditor(int sysAuditor) {
            this.sysAuditor = sysAuditor;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUserAuditor() {
            return userAuditor;
        }

        public void setUserAuditor(int userAuditor) {
            this.userAuditor = userAuditor;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public int getStar() {
            return star;
        }

        public void setStar(int star) {
            this.star = star;
        }

        public UserModelBean getUserModel() {
            return userModel;
        }

        public void setUserModel(UserModelBean userModel) {
            this.userModel = userModel;
        }

        public List<ReplysBean> getReplys() {
            return replys;
        }

        public void setReplys(List<ReplysBean> replys) {
            this.replys = replys;
        }

        public static class UserModelBean implements Parcelable {
            /**
             * age : 25
             * certificationStatus : 1
             * createTime : 1494382921000
             * id : 165
             * land : 浙江省-杭州市-上城区
             * status : 0
             * userName : 余潇
             * userPhone : 17767173523
             * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png
             * userSex : 1
             */

            private int age;
            private int certificationStatus;
            private long createTime;
            private int id;
            private String land;
            private int status;
            private String userName;
            private String userPhone;
            private String userPortrait;
            private int userSex;
            private int customId = 0; //主播id，为空则是普通用户

            public int getCustomId() {
                return customId;
            }

            public void setCustomId(int customId) {
                this.customId = customId;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public int getCertificationStatus() {
                return certificationStatus;
            }

            public void setCertificationStatus(int certificationStatus) {
                this.certificationStatus = certificationStatus;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getLand() {
                return land;
            }

            public void setLand(String land) {
                this.land = land;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getUserPhone() {
                return userPhone;
            }

            public void setUserPhone(String userPhone) {
                this.userPhone = userPhone;
            }

            public String getUserPortrait() {
                return userPortrait;
            }

            public void setUserPortrait(String userPortrait) {
                this.userPortrait = userPortrait;
            }

            public int getUserSex() {
                return userSex;
            }

            public void setUserSex(int userSex) {
                this.userSex = userSex;
            }

            public UserModelBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.age);
                dest.writeInt(this.certificationStatus);
                dest.writeLong(this.createTime);
                dest.writeInt(this.id);
                dest.writeString(this.land);
                dest.writeInt(this.status);
                dest.writeString(this.userName);
                dest.writeString(this.userPhone);
                dest.writeString(this.userPortrait);
                dest.writeInt(this.userSex);
                dest.writeInt(this.customId);
            }

            protected UserModelBean(Parcel in) {
                this.age = in.readInt();
                this.certificationStatus = in.readInt();
                this.createTime = in.readLong();
                this.id = in.readInt();
                this.land = in.readString();
                this.status = in.readInt();
                this.userName = in.readString();
                this.userPhone = in.readString();
                this.userPortrait = in.readString();
                this.userSex = in.readInt();
                this.customId = in.readInt();
            }

            public static final Creator<UserModelBean> CREATOR = new Creator<UserModelBean>() {
                @Override
                public UserModelBean createFromParcel(Parcel source) {
                    return new UserModelBean(source);
                }

                @Override
                public UserModelBean[] newArray(int size) {
                    return new UserModelBean[size];
                }
            };
        }

        public static class ReplysBean implements Parcelable {
            /**
             * content : 发表一个评论yoyoyoyoyoyo333333!
             * createDate : 1501664280341
             * dataId : 2
             * id : 5981941977c85d3b0d864f41
             * replyParentId : root_reply
             * replys : []
             * star : 2
             * status : 1
             * sysAuditor : 1
             * type : 1
             * userAuditor : 1
             * userModel : {"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}
             */

            private String content;
            private long createDate;
            private int dataId;
            private String id;
            private String replyParentId;
            private int star;
            private int status;
            private int sysAuditor;
            private int type;
            private int userAuditor;
            private UserModelBeanX userModel;
            private List<ReplysBean> replys;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getCreateDate() {
                return createDate;
            }

            public void setCreateDate(long createDate) {
                this.createDate = createDate;
            }

            public int getDataId() {
                return dataId;
            }

            public void setDataId(int dataId) {
                this.dataId = dataId;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getReplyParentId() {
                return replyParentId;
            }

            public void setReplyParentId(String replyParentId) {
                this.replyParentId = replyParentId;
            }

            public int getStar() {
                return star;
            }

            public void setStar(int star) {
                this.star = star;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getSysAuditor() {
                return sysAuditor;
            }

            public void setSysAuditor(int sysAuditor) {
                this.sysAuditor = sysAuditor;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getUserAuditor() {
                return userAuditor;
            }

            public void setUserAuditor(int userAuditor) {
                this.userAuditor = userAuditor;
            }

            public UserModelBeanX getUserModel() {
                return userModel;
            }

            public void setUserModel(UserModelBeanX userModel) {
                this.userModel = userModel;
            }

            public List<ReplysBean> getReplys() {
                return replys;
            }

            public void setReplys(List<ReplysBean> replys) {
                this.replys = replys;
            }

            public static class UserModelBeanX implements Parcelable {
                /**
                 * age : 25
                 * certificationStatus : 1
                 * createTime : 1494382921000
                 * id : 165
                 * land : 浙江省-杭州市-上城区
                 * status : 0
                 * userName : 余潇
                 * userPhone : 17767173523
                 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png
                 * userSex : 1
                 */

                private int age;
                private int certificationStatus;
                private long createTime;
                private int id;
                private String land;
                private int status;
                private String userName;
                private String userPhone;
                private String userPortrait;
                private int userSex;

                public int getAge() {
                    return age;
                }

                public void setAge(int age) {
                    this.age = age;
                }

                public int getCertificationStatus() {
                    return certificationStatus;
                }

                public void setCertificationStatus(int certificationStatus) {
                    this.certificationStatus = certificationStatus;
                }

                public long getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(long createTime) {
                    this.createTime = createTime;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getLand() {
                    return land;
                }

                public void setLand(String land) {
                    this.land = land;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getUserName() {
                    return userName;
                }

                public void setUserName(String userName) {
                    this.userName = userName;
                }

                public String getUserPhone() {
                    return userPhone;
                }

                public void setUserPhone(String userPhone) {
                    this.userPhone = userPhone;
                }

                public String getUserPortrait() {
                    return userPortrait;
                }

                public void setUserPortrait(String userPortrait) {
                    this.userPortrait = userPortrait;
                }

                public int getUserSex() {
                    return userSex;
                }

                public void setUserSex(int userSex) {
                    this.userSex = userSex;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeInt(this.age);
                    dest.writeInt(this.certificationStatus);
                    dest.writeLong(this.createTime);
                    dest.writeInt(this.id);
                    dest.writeString(this.land);
                    dest.writeInt(this.status);
                    dest.writeString(this.userName);
                    dest.writeString(this.userPhone);
                    dest.writeString(this.userPortrait);
                    dest.writeInt(this.userSex);
                }

                public UserModelBeanX() {
                }

                protected UserModelBeanX(Parcel in) {
                    this.age = in.readInt();
                    this.certificationStatus = in.readInt();
                    this.createTime = in.readLong();
                    this.id = in.readInt();
                    this.land = in.readString();
                    this.status = in.readInt();
                    this.userName = in.readString();
                    this.userPhone = in.readString();
                    this.userPortrait = in.readString();
                    this.userSex = in.readInt();
                }

                public static final Creator<UserModelBeanX> CREATOR = new Creator<UserModelBeanX>() {
                    @Override
                    public UserModelBeanX createFromParcel(Parcel source) {
                        return new UserModelBeanX(source);
                    }

                    @Override
                    public UserModelBeanX[] newArray(int size) {
                        return new UserModelBeanX[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.content);
                dest.writeLong(this.createDate);
                dest.writeInt(this.dataId);
                dest.writeString(this.id);
                dest.writeString(this.replyParentId);
                dest.writeInt(this.star);
                dest.writeInt(this.status);
                dest.writeInt(this.sysAuditor);
                dest.writeInt(this.type);
                dest.writeInt(this.userAuditor);
                dest.writeParcelable(this.userModel, flags);
                dest.writeTypedList(this.replys);
            }

            public ReplysBean() {
            }

            protected ReplysBean(Parcel in) {
                this.content = in.readString();
                this.createDate = in.readLong();
                this.dataId = in.readInt();
                this.id = in.readString();
                this.replyParentId = in.readString();
                this.star = in.readInt();
                this.status = in.readInt();
                this.sysAuditor = in.readInt();
                this.type = in.readInt();
                this.userAuditor = in.readInt();
                this.userModel = in.readParcelable(UserModelBeanX.class.getClassLoader());
                this.replys = in.createTypedArrayList(ReplysBean.CREATOR);
            }

            public static final Creator<ReplysBean> CREATOR = new Creator<ReplysBean>() {
                @Override
                public ReplysBean createFromParcel(Parcel source) {
                    return new ReplysBean(source);
                }

                @Override
                public ReplysBean[] newArray(int size) {
                    return new ReplysBean[size];
                }
            };
        }

        public DataBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.content);
            dest.writeLong(this.createDate);
            dest.writeInt(this.dataId);
            dest.writeString(this.id);
            dest.writeString(this.remark);
            dest.writeString(this.replyParentId);
            dest.writeInt(this.status);
            dest.writeInt(this.sysAuditor);
            dest.writeInt(this.type);
            dest.writeInt(this.userAuditor);
            dest.writeInt(this.replyCount);
            dest.writeInt(this.star);
            dest.writeParcelable(this.userModel, flags);
            dest.writeTypedList(this.replys);
        }

        protected DataBean(Parcel in) {
            this.content = in.readString();
            this.createDate = in.readLong();
            this.dataId = in.readInt();
            this.id = in.readString();
            this.remark = in.readString();
            this.replyParentId = in.readString();
            this.status = in.readInt();
            this.sysAuditor = in.readInt();
            this.type = in.readInt();
            this.userAuditor = in.readInt();
            this.replyCount = in.readInt();
            this.star = in.readInt();
            this.userModel = in.readParcelable(UserModelBean.class.getClassLoader());
            this.replys = in.createTypedArrayList(ReplysBean.CREATOR);
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }
}
