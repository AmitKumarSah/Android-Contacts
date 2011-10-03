package com.ap41017.c.impl;

import java.util.List;

import android.provider.CallLog.Calls;
import android.text.format.DateUtils;

import com.ap41017.c.interfaces.ICallLog;

/*package*/class ConcretCallLog extends ConcretContactData implements ICallLog {

	private String mNumber;
	private CallType mType;
	private ConcretPhone mParent;
	private long mTime, mDuration;

	private ConcretCallLog(long id, String number, ConcretPhone result) {
		super(id, result.getContactParent());
		this.mNumber = number;
		this.mParent = result;
	}

	private ConcretCallLog(long id, String number) {
		super(id, null);
		this.mNumber = number;
	}

	/*package*/static ConcretCallLog newInstance(long id, String number,
			ConcretPhone result, List<ConcretCallLog> unknown) {
		if (result != null)
			return new ConcretCallLog(id, number, result);
		else {
			ConcretCallLog call = new ConcretCallLog(id, number);
			unknown.add(call);
			return call;
		}
	}

	/*package*/ConcretCallLog setType(int type, List<ConcretCallLog> in,
			List<ConcretCallLog> out, List<ConcretCallLog> missed) {
		switch (type) {
		case Calls.INCOMING_TYPE:
			this.mType = CallType.INCOMING;
			in.add(this);
			break;
		case Calls.OUTGOING_TYPE:
			this.mType = CallType.OUTGOING;
			out.add(this);
			break;
		case Calls.MISSED_TYPE:
			this.mType = CallType.MISSED;
			out.add(this);
			break;
		}
		return this;
	}

	/*package*/ConcretCallLog setDate(long date) {
		this.mTime = date;
		return this;
	}

	/*package*/ConcretCallLog setDuration(long duration) {
		this.mDuration = duration;
		return this;
	}

	@Override
	public ConcretPhone getPhoneParent() {
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
