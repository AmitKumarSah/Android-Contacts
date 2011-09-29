package com.ap41017.c.interfaces;

import android.net.Uri;

public interface IContact extends IBaseColumn, Comparable<IContact> {
	public <Ret, Arg> Ret[] acceptIDataColumns(IVisitor<Ret, Arg> visitor, Arg arg);

	public Uri getLookupUri();

	public String getName();

	public int getTimesContacted();

	public long getLastTimeContacted();
	
	public byte[] getPhoto();
}
