package com.ap41017.c.impl;

import com.ap41017.c.interfaces.IGroup;
import com.ap41017.c.interfaces.IVisitor;

/*package*/class ConcretGroup extends ConcretBaseColumn implements IGroup {
	/*package*/ConcretGroup(long id) {
		super(id);
	}

	/* package */String mSystemId, mAccountType, mAccountName, mTitle;
	/* package */boolean mVisible;

	@Override
	public <Ret, Arg> Ret accept(IVisitor<Ret, Arg> visitor, Arg arg) {
		return visitor.visit(this, arg);
	}

	@Override
	public String getSystemId() {
		return mSystemId;
	}

	@Override
	public String getAccountType() {
		return mAccountType;
	}

	@Override
	public String getAccountName() {
		return mAccountName;
	}

	@Override
	public String getTitle() {
		return mTitle;
	}

	@Override
	public boolean getVisible() {
		return mVisible;
	}
}
