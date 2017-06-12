package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/6/12.
 */

public class NewMessage {

	/**
	 * classify : 1
	 * count : 3
	 */

	private int classify;
	private int count;

	public int getClassify() {
		return classify;
	}

	public void setClassify(int classify) {
		this.classify = classify;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "NewMessage{" +
				"classify=" + classify +
				", count=" + count +
				'}';
	}
}
