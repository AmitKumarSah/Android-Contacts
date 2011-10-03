package com.ap41017.c.impl;

import com.ap41017.c.interfaces.IGroup;

/*package*/class ConcretGroup extends ConcretBaseColumn implements IGroup {
	/*package*/ConcretGroup(long id, String systemId, String title, int visible) {
		super(id);
		this.mSystemId = systemId;
		this.mTitle = title;
		this.mVisible = visible > 0;
	}

	/*package*/ConcretGroup setAccount(String acName, String acType) {
		this.mAccountName = acName;
		this.mAccountType = acType;
		return this;
	}

	private String mSystemId, mAccountType, mAccountName, mTitle;
	private boolean mVisible;

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
