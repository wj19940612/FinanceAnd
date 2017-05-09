package com.sbai.finance.model;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class Detail {

   /**
    * createTime : 1
    * money : 42818
    * remark : 测试内容i7h7
    * type : 11543
    */

   private int createTime;
   private int money;
   private String remark;
   private int type;

   public int getCreateTime() {
      return createTime;
   }

   public void setCreateTime(int createTime) {
      this.createTime = createTime;
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
}
