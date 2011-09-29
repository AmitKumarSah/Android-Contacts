package com.ap41017.c.interfaces;

import android.content.ContentResolver;

import com.ap41017.c.interfaces.ICallLog.CallType;
import com.ap41017.c.interfaces.IDataColumn.IPhone;

public abstract class IContactsManager {

	protected abstract IContactsManager loadInBackground(ContentResolver cr);

	public abstract IPhone[] phoneLookup(String phone);

	public abstract IBaseColumn[] getGroupContact(int numOfColumn);

	public abstract IGroup[] getGroups();

	public abstract IContact[] getContacts(IGroup group);

	public abstract ICallLog[] getCallLogs(CallType callType, boolean getUnknown);

	public abstract boolean releaseClients();
}
