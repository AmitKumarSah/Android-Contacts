package com.ap41017.c.impl;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.ContactsContract.Contacts;

import com.ap41017.c.impl.ConcretContactData.ConcretGroupMembership;
import com.ap41017.c.impl.ConcretContactData.ConcretPhone;
import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IContactData.IGroupMembership;
import com.ap41017.c.interfaces.IContactData.IPhone;

/*package*/class ConcretContact extends ConcretBaseColumn implements IContact {
	/*package*/ConcretContact(long id) {
		super(id);
	}

	/*package*/ConcretContact setLastTimeContact(long last) {
		this.mLastTimeContacted = last;
		return this;
	}

	/*package*/ConcretContact setLookupKey(String key) {
		this.mLookupKey = key;
		return this;
	}

	/*package*/ConcretContact setDisplayName(String display) {
		this.mName = display;
		return this;
	}

	/*package*/ConcretContact setTimesContacted(int times) {
		this.mTimesContacted = times;
		return this;
	}

	/*package*/ConcretContact setPhoto(byte[] photo) {
		this.mPhoto = photo;
		return this;
	}

	private long mLastTimeContacted;
	private String mLookupKey, mName;
	private int mTimesContacted, mRawIdsLength;
	private byte[] mPhoto;
	private long[] mRawIds = new long[4];

	/*package*/ConcretContact addRawId(long id) {
		if (this.mRawIdsLength == this.mRawIds.length) {
			long[] expand = new long[2 * this.mRawIdsLength];
			System.arraycopy(mRawIds, 0, expand, 0, mRawIdsLength);
			this.mRawIds = expand;
		}
		this.mRawIds[this.mRawIdsLength] = id;
		++this.mRawIdsLength;
		return this;
	}

	/*package*/ConcretContact addPhone(ConcretPhone phone) {
		if (this.mPhoneList == null)
			this.mPhoneList = new ArrayList<ConcretPhone>();
		this.mPhoneList.add(phone);
		return this;
	}/*package*/

	ConcretContact addGroupMembership(ConcretGroupMembership group) {
		if (this.mGroupsList == null)
			this.mGroupsList = new ArrayList<ConcretGroupMembership>();
		this.mGroupsList.add(group);
		return this;
	}

	private ArrayList<ConcretPhone> mPhoneList;
	private ArrayList<ConcretGroupMembership> mGroupsList;

	@Override
	public Uri getLookupUri() {
		return Contacts.getLookupUri(this.getId(), mLookupKey);
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public int getTimesContacted() {
		return mTimesContacted;
	}

	@Override
	public long getLastTimeContacted() {
		return mLastTimeContacted;
	}

	@Override
	public byte[] getPhoto() {
		return this.mPhoto;
	}

	@Override
	public long[] getRawContactIds() {
		if (this.mRawIdsLength == 0)
			return null;
		else {
			long[] rtn = new long[this.mRawIdsLength];
			System.arraycopy(mRawIds, 0, rtn, 0, mRawIdsLength);
			return rtn;
		}
	}

	@Override
	public IPhone[] getPhones() {
		if (this.mPhoneList == null)
			return null;
		else
			return this.mPhoneList.toArray(new IPhone[this.mPhoneList.size()]);
	}

	@Override
	public IGroupMembership[] getGroupMemberships() {
		if (this.mGroupsList == null)
			return null;
		else
			return this.mGroupsList
					.toArray(new IGroupMembership[this.mGroupsList.size()]);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(1024);
		b.append("id:").append(this.getId()).append(',');
		b.append("name:").append(mName).append(',');
		b.append("lookupUri:").append(getLookupUri()).append('\n');
		b.append("rawId:").append(this.mRawIds).append(',');
		b.append('\n');
		b.append("phones:").append(this.mPhoneList);
		b.append('\n');
		return b.toString();
	}
}
