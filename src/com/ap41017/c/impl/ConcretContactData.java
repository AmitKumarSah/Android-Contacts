package com.ap41017.c.impl;

import android.content.res.Resources;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.ap41017.c.interfaces.IContactData;
import com.ap41017.c.interfaces.IGroup;

public abstract class ConcretContactData extends ConcretBaseColumn implements
		IContactData {

	private ConcretContact mContact;
	private boolean mPrimary, mSuperPri;
	private int mDataVersion;

	/*package*/ConcretContactData(long id, ConcretContact contact) {
		super(id);
		this.mContact = contact;
	}

	/*package*/ConcretContactData setPrimary(int primary, int superPri) {
		this.mPrimary = primary > 0;
		this.mSuperPri = superPri > 0;
		return this;
	}

	/*package*/ConcretContactData setDataVersion(int dataVersion) {
		this.mDataVersion = dataVersion;
		return this;
	}

	@Override
	public ConcretContact getContactParent() {
		return this.mContact;
	}

	@Override
	public boolean isPrimary() {
		return this.mPrimary;
	}

	@Override
	public boolean isSuperPrimary() {
		return this.mSuperPri;
	}

	@Override
	public int getDataVersion() {
		return this.mDataVersion;
	}

	/*package*/static class ConcretGroupMembership extends ConcretContactData
			implements IGroupMembership {
		/*package*/ConcretGroupMembership(long id, ConcretContact contact,
				ConcretGroup group) {
			super(id, contact);
			this.mGroup = group;
		}

		private ConcretGroup mGroup;

		@Override
		public IGroup getGroupParent() {
			return this.mGroup;
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(1024);
			b.append("group:").append(mGroup).append(',');
			return b.toString();
		}
	}

	/*package*/static class ConcretPhone extends ConcretContactData implements
			IPhone {
		/*package*/ConcretPhone(long id, ConcretContact contact) {
			super(id, contact);
		}

		/*package*/ConcretPhone setPhone(String number, int type, String label) {
			this.mNumber = number;
			this.mType = type;
			this.mLabel = label;
			return this;
		}

		private String mNumber, mLabel;
		private int mType;

		@Override
		public String getNumber() {
			return this.mNumber;
		}

		@Override
		public CharSequence getTypeText(Resources res) {
			return Phone.getTypeLabel(res, mType, mLabel);
		}

		@Override
		public PhoneType getPhoneType() {
			return PhoneType.values()[this.mType];
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(1024);
			b.append("num:").append(mNumber).append(',');
			b.append("type:").append(mType).append(';');
			return b.toString();
		}
	}
}
