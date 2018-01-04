package com.sbai.finance.model.anchor;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class Attention {
	private int isAttention;
	private int attentionCount;

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
	}

	public int getAttentionCount() {
		return attentionCount;
	}

	public void setAttentionCount(int attentionCount) {
		this.attentionCount = attentionCount;
	}

	@Override
	public String toString() {
		return "Attention{" +
				"isAttention=" + isAttention +
				", attentionCount=" + attentionCount +
				'}';
	}
}
