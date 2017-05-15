package com.sbai.finance.model;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class Detail {


   /**
    * createTime : 2017-04-13 16:14:12
    * id : 5
    * money : 5
    * remark : 111
    * type : 1
    * typeDetail : 1
    * userId : 98
    */

   private long createTime;
   private int id;
   private int money;
   private String remark;
   private int type;
   private int typeDetail;
   private int userId;

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

   public int getMoney() {
      return money;
   }

   public void setMoney(int money) {
      this.money = money;
   }

   public String getRemark() {
      return remark;
   }

   public void setRemark(String remark) {
      this.remark = remark;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getTypeDetail() {
      return typeDetail;
   }

   public void setTypeDetail(int typeDetail) {
      this.typeDetail = typeDetail;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }
}
