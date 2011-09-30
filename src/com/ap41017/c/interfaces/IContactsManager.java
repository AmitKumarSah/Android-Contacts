package com.ap41017.c.interfaces;

import android.content.ContentResolver;

import com.ap41017.c.interfaces.ICallLog.CallType;
import com.ap41017.c.interfaces.IDataColumn.IPhone;

public interface IContactsManager {

	public abstract IContactsManager loadDatas(ContentResolver cr);

	public abstract IPhone[] phoneLookup(String phone);

	public abstract IGroup[] getGroups();
	
	public abstract IContact[] getAllContacts();

	public abstract IContact[] getContactsIn(IGroup group);

	public abstract ICallLog[] getCallLogs(CallType callType, boolean getUnknown);

	public abstract boolean releaseClients();
}
