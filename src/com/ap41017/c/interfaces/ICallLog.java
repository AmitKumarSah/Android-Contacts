package com.ap41017.c.interfaces;


public interface ICallLog extends IContactData {

	public IPhone getPhoneParent();

	public String getNumber();

	public long getTime();

	public CharSequence getTimeText();

	public long getDuration();

	public CallType getType();

	public static enum CallType {
		INCOMING, OUTGOING, MISSED,
	};
}
