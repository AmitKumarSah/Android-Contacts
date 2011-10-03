package com.ap41017.c.interfaces;

import android.content.res.Resources;

public interface IContactData extends IBaseColumn {

	public IContact getContactParent();

	public interface IPhone extends IContactData {
		public String getNumber();

		public CharSequence getTypeText(Resources res);

		public int getTypeInt();
	}

	public interface IGroupMembership extends IContactData {
		public IGroup getGroupParent();
	}
}
