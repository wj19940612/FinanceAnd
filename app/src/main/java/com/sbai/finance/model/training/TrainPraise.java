package com.sbai.finance.model.training;

/**
 * Created by lixiaokuan0819 on 2017/8/10.
 */

public class TrainPraise {
	private int isPraise;
	private int praise;

	public int getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	@Override
	public String toString() {
		return "TrainPraise{" +
				"isPraise=" + isPraise +
				", praise=" + praise +
				'}';
	}
}
