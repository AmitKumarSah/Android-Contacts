package com.ap41017.c.interfaces;

import com.ap41017.c.interfaces.IDataColumn.IPhone;

public interface ICallLog extends IBaseColumn {

	public IPhone getParent();

	public String getNumber();

	public long getTime();

	public CharSequence getTimeText();

	public long getDuration();

	public CallType getType();

	public static enum CallType {
		INCOMING, OUTGOING, MISSED,
	};
}
