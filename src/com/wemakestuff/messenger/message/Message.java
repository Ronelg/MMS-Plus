package com.wemakestuff.messenger.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Message {
	private UUID mUUID = UUID.randomUUID();
	private String mFrom;
	private String mTo;
	private String mSubject;
	private String mContent;
	private Date mDateSent;
	private Date mDateReceived;
	private ArrayList<Message> mRelatedMessages;
	private Status mStatus;
	private Priority mPriority;

	public String getFrom() {
		return mFrom;
	}

	public void setFrom(String mFrom) {
		this.mFrom = mFrom;
	}

	public String getTo() {
		return mTo;
	}

	public void setTo(String mTo) {
		this.mTo = mTo;
	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String mSubject) {
		this.mSubject = mSubject;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public Date getDateSent() {
		return mDateSent;
	}

	public void setDateSent(Date mDateSent) {
		this.mDateSent = mDateSent;
	}

	public Date getDateReceived() {
		return mDateReceived;
	}

	public void setDateReceived(Date mDateReceived) {
		this.mDateReceived = mDateReceived;
	}

	public ArrayList<Message> getRelatedMessages() {
		return mRelatedMessages;
	}

	public void setRelatedMessages(ArrayList<Message> mRelatedMessages) {
		this.mRelatedMessages = mRelatedMessages;
	}

	public int getRelatedMessagesCount() {
		return this.mRelatedMessages.size();
	}

	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public Priority getPriority() {
		return mPriority;
	}

	public void setPriority(Priority mPriority) {
		this.mPriority = mPriority;
	}

	public UUID getUUID() {
		return mUUID;
	}
}
