package com.ap41017.c.interfaces;

import android.content.res.Resources;

public interface IDataColumn extends IBaseColumn, Comparable<IDataColumn> {

	public IContact getParent();

	public interface IPhone extends IDataColumn {
		public String getNumber();

		public CharSequence getTypeText(Resources res);

		public int getTypeInt();
	}

	public interface IGroupMembership extends IDataColumn {
		public long getGroupId();
	}
}
