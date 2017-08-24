package com.sbai.finance.model.training;

/**
 * 训练心得点赞javabean
 */

public class TrainingExperiencePraise {
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
		return "TrainingExperiencePraise{" +
				"isPraise=" + isPraise +
				", praise=" + praise +
				'}';
	}
}
