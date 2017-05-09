package com.sbai.finance.model.economiccircle;

import java.io.Serializable;

/**
 * Created by lixiaokuan0819 on 2017/5/2.
 */

public class WhetherAttentionShieldOrNot implements Serializable{

	/**
	 * follow : 1
	 * followMutual : 1
	 * sheild : 1
	 * shieldMutual : 1
	 */

	private boolean follow;
	private boolean followMutual;
	private boolean shield;
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

	public boolean isShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
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
				", sheild=" + shield +
				", shieldMutual=" + shieldMutual +
				'}';
	}
}
