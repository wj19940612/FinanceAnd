package com.sbai.finance.model;

/**
 * Created by Administrator on 2017-04-25.
 */

public class FutureHq {
	private String codeName;
	private String instrumentId;
	private Double lastPrice;
	private Double upDropSpeed;

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Double getUpDropSpeed() {
		return upDropSpeed;
	}

	public void setUpDropSpeed(Double upDropSpeed) {
		this.upDropSpeed = upDropSpeed;
	}
}
