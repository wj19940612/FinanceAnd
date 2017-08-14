package com.sbai.finance.model.miss;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class Prise {

	/**
	 * priseCount : 1
	 * isPrise : 1
	 */

	private int priseCount;
	private int isPrise;

	public int getPriseCount() {
		return priseCount;
	}

	public void setPriseCount(int priseCount) {
		this.priseCount = priseCount;
	}

	public int getIsPrise() {
		return isPrise;
	}

	public void setIsPrise(int isPrise) {
		this.isPrise = isPrise;
	}

	@Override
	public String toString() {
		return "Prise{" +
				"priseCount=" + priseCount +
				", isPrise=" + isPrise +
				'}';
	}
}
