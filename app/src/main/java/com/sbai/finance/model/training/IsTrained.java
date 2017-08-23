package com.sbai.finance.model.training;

/**
 * 是否训练过
 */

public class IsTrained {

	private int isPerception;

	public int getIsPerception() {
		return isPerception;
	}

	public void setIsPerception(int isPerception) {
		this.isPerception = isPerception;
	}

	@Override
	public String toString() {
		return "IsTrained{" +
				"isPerception=" + isPerception +
				'}';
	}
}
