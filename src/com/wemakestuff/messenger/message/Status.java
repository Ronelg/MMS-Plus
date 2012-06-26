package com.wemakestuff.messenger.message;

import java.util.UUID;

public class Status {
	private UUID mUUID = UUID.randomUUID();
	private String mName;
	private String mDescription;
	private int mSequence;

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public int getSequence() {
		return mSequence;
	}

	public void setSequence(int mSequence) {
		this.mSequence = mSequence;
	}

	public UUID getUUID() {
		return mUUID;
	}
}
