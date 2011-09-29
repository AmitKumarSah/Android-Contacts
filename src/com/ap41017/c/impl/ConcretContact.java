package com.ap41017.c.impl;

import android.net.Uri;
import android.provider.ContactsContract.Contacts;

import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IDataColumn;
import com.ap41017.c.interfaces.IVisitor;

/*package*/class ConcretContact extends ConcretBaseColumn implements IContact {
	/*package*/ConcretContact(long id) {
		super(id);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(1024);
		b.append("id:").append(this.getId()).append(',');
		b.append("name:").append(mName).append(',');
		b.append("lookupUri:").append(getLookupUri()).append('\n');
		for (long raw : mRawContactIds)
			if (raw != 0)
				b.append("rawId:").append(raw).append(',');
		b.append('\n');
		for (IDataColumn d : mDatas)
			if (d != null)
				b.append("datas:").append(d);
		b.append('\n');
		return b.toString();
	}

	@Override
	public int compareTo(IContact another) {
		final int dt = another.getTimesContacted() - this.getTimesContacted();
		if (dt != 0)
			return dt;
		final int dn = another.getName().compareTo(this.getName());
		if (dn != 0)
			return dn;
		else
			return (int) (another.getLastTimeContacted() - this
					.getLastTimeContacted());
	}

	/* package */long mLastTimeContacted;
	/* package */String mLookupKey, mName;
	/* package */int mTimesContacted;
	/* package */long[] mRawContactIds = new long[4];
	/* package */byte[] mPhoto;
	/* package */IDataColumn[] mDatas = new IDataColumn[4];
	/* package */Object mCreateColumn;

	@Override
	public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

	@Override
	public <Ret, Arg> Ret[] acceptIDataColumns(IVisitor<Ret, Arg> visitor,
			Arg arg) {
		for (IDataColumn d : this.mDatas)
			if (d != null)
				d.accept(visitor, arg);
		return null;
	}

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
}
