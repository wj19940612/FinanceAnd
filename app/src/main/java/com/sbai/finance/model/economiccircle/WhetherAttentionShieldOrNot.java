package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/5/2.
 */

public class WhetherAttentionShieldOrNot {

	/**
	 * follow : 1
	 * followMutual : 1
	 * sheild : 1
	 * shieldMutual : 1
	 */

	private boolean follow;
	private boolean followMutual;
	private boolean sheild;
	private boolean shieldMutual;

	public boolean isFollow() {
		return follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	public boolean isFollowMutual() {
		return followMutual;
	}

	public void setFollowMutual(boolean followMutual) {
		this.followMutual = followMutual;
	}

	public boolean isSheild() {
		return sheild;
	}

	public void setSheild(boolean sheild) {
		this.sheild = sheild;
	}

	public boolean isShieldMutual() {
		return shieldMutual;
	}

	public void setShieldMutual(boolean shieldMutual) {
		this.shieldMutual = shieldMutual;
	}

	@Override
	public String toString() {
		return "WhetherAttentionShieldOrNot{" +
				"follow=" + follow +
				", followMutual=" + followMutual +
				", sheild=" + sheild +
				", shieldMutual=" + shieldMutual +
				'}';
	}
}
