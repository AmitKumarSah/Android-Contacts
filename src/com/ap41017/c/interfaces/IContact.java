package com.ap41017.c.interfaces;

import android.net.Uri;

import com.ap41017.c.interfaces.IContactData.IGroupMembership;
import com.ap41017.c.interfaces.IContactData.IPhone;

public interface IContact extends IBaseColumn {

	public Uri getLookupUri();

	public String getName();

	public int getTimesContacted();

	public long getLastTimeContacted();

	public byte[] getPhoto();

	public long[] getRawContactIds();

	public IPhone[] getPhones();

	public IGroupMembership[] getGroupMemberships();
	
	public ICallLog[] getCallLogs();
}
