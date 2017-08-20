package com.sbai.finance.model.miss;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class Prise implements Parcelable{

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.priseCount);
		dest.writeInt(this.isPrise);
	}

	public Prise() {
	}

	protected Prise(Parcel in) {
		this.priseCount = in.readInt();
		this.isPrise = in.readInt();
	}

	public static final Creator<Prise> CREATOR = new Creator<Prise>() {
		@Override
		public Prise createFromParcel(Parcel source) {
			return new Prise(source);
		}

		@Override
		public Prise[] newArray(int size) {
			return new Prise[size];
		}
	};
}
