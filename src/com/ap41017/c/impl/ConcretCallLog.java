package com.ap41017.c.impl;

import android.provider.CallLog.Calls;
import android.text.format.DateUtils;

import com.ap41017.c.interfaces.ICallLog;
import com.ap41017.c.interfaces.IDataColumn.IPhone;
import com.ap41017.c.interfaces.IVisitor;

public class ConcretCallLog extends ConcretBaseColumn implements ICallLog {
	private String mNumber;
	private CallType mType;
	/*package*/IPhone mParent;
	/*package*/long mTime, mDuration;

	/*package*/ConcretCallLog(long id) {
		super(id);
	}

	/*package*/void setupNumber(String number, int type, IPhone result) {
		this.mNumber = number;
		switch (type) {
		case Calls.INCOMING_TYPE:
			this.mType = CallType.INCOMING;
			break;
		case Calls.OUTGOING_TYPE:
			this.mType = CallType.OUTGOING;
			break;
		case Calls.MISSED_TYPE:
			this.mType = CallType.MISSED;
			break;
		}
		this.mParent = result;
	}

	@Override
	public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

	@Override
	public IPhone getParent() {
		return this.mParent;
	}

	@Override
	public String getNumber() {
		return this.mNumber;
	}

	@Override
	public long getTime() {
		return mTime;
	}

	@Override
	public CharSequence getTimeText() {
		return DateUtils.getRelativeTimeSpanString(this.mTime,
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS,
				DateUtils.FORMAT_NUMERIC_DATE);
	}

	@Override
	public long getDuration() {
		return mDuration;
	}

	@Override
	public CallType getType() {
		return mType;
	}
}
