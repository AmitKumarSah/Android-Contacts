package com.ap41017.c.interfaces;

import android.content.res.Resources;

public interface IContactData extends IBaseColumn {

	public IContact getContactParent();

	public boolean isPrimary();

	public boolean isSuperPrimary();

	public int getDataVersion();

	public interface IPhone extends IContactData {
		public static enum PhoneType {
			CUSTOM, HOME, MOBILE, WORK, FAX_WORK, FAX_HOME, PAGER, OTHER, CALLBACK, CAR, COMPANY_MAIN, ISDN, MAIN, OTHER_FAX, RADIO, TELEX, TTY_TDD, WORK_MOBILE, WORK_PAGER, ASSISTANT, MMS,
		};

		public String getNumber();

		public CharSequence getTypeText(Resources res);

		public PhoneType getPhoneType();
	}

	public interface IGroupMembership extends IContactData {
		public IGroup getGroupParent();
	}

	public interface IEmail extends IContactData {
		public static enum EmailType {
			CUSTOM, HOME, WORK, OTHER, MOBILE,
		};

		public String getAddress();

		public CharSequence getTypeText(Resources res);

		public EmailType getEmailType();
	}
}
