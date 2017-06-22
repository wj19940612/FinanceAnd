package com.sbai.finance.model.future;

/**
 * Created by lixiaokuan0819 on 2017/6/22.
 */

public class FutureBattleConfig {

	/**
	 * gold : 300,600,120
	 * money : 100,200,300
	 * integral : 150,300,600
	 * bigVarietyTypeCode : 期货
	 * time : 10,20,30
	 */

	private String gold;
	private String money;
	private String integral;
	private String bigVarietyTypeCode;
	private String time;

	public String getGold() {
		return gold;
	}

	public void setGold(String gold) {
		this.gold = gold;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getBigVarietyTypeCode() {
		return bigVarietyTypeCode;
	}

	public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
		this.bigVarietyTypeCode = bigVarietyTypeCode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "FutureBattleConfig{" +
				"gold='" + gold + '\'' +
				", money='" + money + '\'' +
				", integral='" + integral + '\'' +
				", bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
				", time='" + time + '\'' +
				'}';
	}
}
