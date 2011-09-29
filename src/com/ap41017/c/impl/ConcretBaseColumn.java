package com.ap41017.c.impl;

import com.ap41017.c.interfaces.IBaseColumn;

public abstract class ConcretBaseColumn implements IBaseColumn {
	private long mId;

	/* package*/ConcretBaseColumn(long id) {
		super();
		this.mId = id;
	}

	@Override
	public long getId() {
		return this.mId;
	}
}
