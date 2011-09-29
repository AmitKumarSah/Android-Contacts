package com.ap41017.c.impl;

import android.content.res.Resources;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IDataColumn;
import com.ap41017.c.interfaces.IVisitor;

/*package*/abstract class ConcretData extends ConcretBaseColumn implements
		IDataColumn {
	private IContact mContact;

	@Override
	public int compareTo(IDataColumn another) {
		return this.getParent().compareTo(another.getParent());
	}

	/*package*/ConcretData(long id, IContact contact) {
		super(id);
		this.mContact = contact;
	}

	@Override
	public IContact getParent() {
		return this.mContact;
	}

	/*package*/static class ConcretGroupMembership extends ConcretData
			implements IGroupMembership {
		/*package*/ConcretGroupMembership(long id, IContact contact) {
			super(id, contact);
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(1024);
			b.append("groupId:").append(mGroupId).append(',');
			return b.toString();
		}

		/* package */long mGroupId;

		@Override
		public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg) {
			return visitor.visit(this, arg);
		}

		@Override
		public long getGroupId() {
			return mGroupId;
		}
	}

	/*package*/static class ConcretPhone extends ConcretData implements
			IDataColumn.IPhone {
		/*package*/ConcretPhone(long id, IContact contact) {
			super(id, contact);
		}

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(1024);
			b.append("num:").append(mNumber).append(',');
			b.append("type:").append(mType).append(';');
			return b.toString();
		}

		/* package*/String mNumber, mLabel;
		/* package*/int mType;

		@Override
		public String getNumber() {
			return this.mNumber;
		}

		@Override
		public CharSequence getTypeText(Resources res) {
			return Phone.getTypeLabel(res, mType, mLabel);
		}

		@Override
		public int getTypeInt() {
			return this.mType;
		}

		@Override
		public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg) {
			return visitor.visit(this, arg);
		}
	}
}
