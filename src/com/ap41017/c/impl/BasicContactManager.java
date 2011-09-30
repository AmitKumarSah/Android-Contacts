package com.ap41017.c.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import com.ap41017.c.impl.ConcretData.ConcretGroupMembership;
import com.ap41017.c.impl.ConcretData.ConcretPhone;
import com.ap41017.c.interfaces.ICallLog;
import com.ap41017.c.interfaces.ICallLog.CallType;
import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IContactsManager;
import com.ap41017.c.interfaces.IDataColumn;
import com.ap41017.c.interfaces.IDataColumn.IPhone;
import com.ap41017.c.interfaces.IGroup;

public class BasicContactManager implements IContactsManager {

	@Override
	public IContactsManager loadDatas(ContentResolver cr) {
		this.mCCClient = cr
				.acquireContentProviderClient(ContactsContract.AUTHORITY);
		this.mCLClient = cr.acquireContentProviderClient(CallLog.AUTHORITY);
		try {
			Log.w("gc", "visitRaw");
			visitRawContacts();
			visitContacts();
			visitGroups();
			visitData();
			visitCallLogs();
			Log.i("gc", "finish");
		} catch (RemoteException e) {
		} finally {
		}
		return this;
	}

	@Override
	public boolean releaseClients() {
		try {
			return mCCClient.release() && mCLClient.release();
		} finally {
			mCCClient = null;
			mCLClient = null;
		}
	}

	@Override
	public IPhone[] phoneLookup(String phone) {
		do {
			if (TextUtils.isEmpty(phone))
				break;
			Cursor p = null;
			try {
				p = this.mCCClient.query(
						Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
								Uri.encode(phone)),
						new String[] { Phone.NUMBER }, null, null, null);
			} catch (RemoteException e) {
				break;
			}
			if (p == null)
				break;
			//
			HashMap<String, ConcretPhone> phonesMap = this.mPhonesMap;
			IPhone[] phones = null;
			final int count = p.getCount();
			if (p.moveToFirst() && count > 0) {
				phones = new IPhone[count];
				for (int i = 0; i < count; ++i) {
					phones[i] = phonesMap.get(p.getString(0));
					p.moveToNext();
				}
			}
			p.close();
			return phones;
		} while (false);
		return null;
	}

	@Override
	public IGroup[] getGroups() {
		Collection<ConcretGroup> values = this.mGroupsMap.values();
		return values.toArray(new IGroup[values.size()]);
	}

	@Override
	public IContact[] getAllContacts() {
		return this.mAllContacts;
	}

	@Override
	public IContact[] getContactsIn(IGroup group) {
		ArrayList<ConcretContact> list = this.mGroupListMap.get(group.getId());
		return list.toArray(new IContact[list.size()]);
	}

	@Override
	public ICallLog[] getCallLogs(CallType callType, boolean getUnknown) {
		if (callType != null)
			return this.mCalls[callType.ordinal()];
		if (getUnknown)
			return this.mCalls[4];
		else
			return this.mCalls[3];
	}

	private ContentProviderClient mCCClient, mCLClient;
	private HashMap<Long, ConcretContact> mConMap, mRawMap;
	private HashMap<Long, ConcretGroup> mGroupsMap;
	private HashMap<Long, ArrayList<ConcretContact>> mGroupListMap;
	private HashMap<String, ConcretPhone> mPhonesMap;
	private ConcretContact[] mAllContacts;
	private ConcretCallLog[][] mCalls;

	private void visitRawContacts() throws RemoteException {
		Cursor c = mCCClient.query(RawContacts.CONTENT_URI, new String[] {
				RawContacts._ID, RawContacts.CONTACT_ID, }, null, null, null);
		if (c == null)
			return;
		if (!c.moveToFirst()) {
			c.close();
			return;
		}
		try {
			mConMap = new HashMap<Long, ConcretContact>(c.getCount());
			mRawMap = new HashMap<Long, ConcretContact>(c.getCount());
			HashMap<Long, ConcretContact> conMap = mConMap;
			HashMap<Long, ConcretContact> rawMap = mRawMap;
			ConcretContact contact;
			long[] list;
			int index;
			long id, conid;
			for (int i = 0; i < c.getCount(); ++i) {
				id = c.getLong(0);
				conid = c.getLong(1);
				contact = conMap.get(conid);
				if (contact == null)
					conMap.put(conid, contact = new ConcretContact(conid));
				index = contact.mRawContactLength;
				list = contact.mRawContactIds;
				if (index == list.length) {
					list = contact.mRawContactIds = expand(list);
				}
				list[index] = id;
				++contact.mRawContactLength;
				//
				rawMap.put(id, contact);
				c.moveToNext();
			}
		} finally {
			c.close();
		}
	}

	private static long[] expand(long[] src) {
		long[] expand = new long[src.length * 2];
		System.arraycopy(src, 0, expand, 0, src.length);
		return expand;
	}

	private void visitContacts() throws RemoteException {
		Cursor c = mCCClient.query(Contacts.CONTENT_URI, new String[] {
				Contacts._ID, Contacts.DISPLAY_NAME, Contacts.LOOKUP_KEY,
				Contacts.TIMES_CONTACTED, Contacts.LAST_TIME_CONTACTED }, null,
				null, null);
		if (c == null)
			return;
		if (!c.moveToFirst()) {
			c.close();
			return;
		}
		HashMap<Long, ConcretContact> conMap = mConMap;
		try {
			ConcretContact contact;
			long id, last;
			String dname = null, lookup = null;
			int times;
			for (int i = 0; i < c.getCount(); ++i) {
				id = c.getLong(0);
				dname = c.getString(1);
				lookup = c.getString(2);
				times = c.getInt(3);
				last = c.getLong(4);
				contact = conMap.get(id);
				if (contact == null) {
					Log.d("gc", "null con: " + dname);
				} else {
					contact.mName = dname;
					contact.mLookupKey = lookup;
					contact.mTimesContacted = times;
					contact.mLastTimeContacted = last;
				}
				c.moveToNext();
			}
		} finally {
			c.close();
		}
		mAllContacts = new ConcretContact[conMap.size()];
		mAllContacts = conMap.values().toArray(mAllContacts);
	}

	private void visitGroups() throws RemoteException {
		Cursor g = mCCClient.query(Groups.CONTENT_URI, new String[] {
				Groups._ID, Groups.SYSTEM_ID, Groups.ACCOUNT_NAME,
				Groups.ACCOUNT_TYPE, Groups.TITLE, Groups.GROUP_VISIBLE, },
				null, null, null);
		if (g == null)
			return;
		if (!g.moveToFirst()) {
			g.close();
			return;
		}
		try {
			HashMap<Long, ConcretGroup> groupsMap = new HashMap<Long, ConcretGroup>(
					g.getCount());
			mGroupsMap = groupsMap;
			long id;
			ConcretGroup group;
			for (int i = 0; i < g.getCount(); ++i) {
				id = g.getLong(0);
				group = new ConcretGroup(id);
				group.mSystemId = g.getString(1);
				group.mAccountName = g.getString(2);
				group.mAccountType = g.getString(3);
				group.mTitle = g.getString(4);
				group.mVisible = (g.getInt(5) > 0);
				groupsMap.put(id, group);
				g.moveToNext();
			}
		} finally {
			g.close();
		}
	}

	private static <T> T[] expand(T[] src, T[] expand) {
		System.arraycopy(src, 0, expand, 0, src.length);
		return expand;
	}

	private void visitData() throws RemoteException {
		Cursor c = mCCClient.query(Data.CONTENT_URI, new String[] { Data._ID,
				Data.RAW_CONTACT_ID, Data.MIMETYPE, Data.IS_PRIMARY,
				Data.IS_SUPER_PRIMARY, Data.DATA1, Data.DATA2, Data.DATA3,
				Data.DATA15 }, null, null, null);
		if (c == null)
			return;
		if (!c.moveToFirst()) {
			c.close();
			return;
		}
		HashMap<Long, ConcretContact> rawMap = mRawMap;
		mPhonesMap = new HashMap<String, ConcretData.ConcretPhone>();
		mGroupListMap = new HashMap<Long, ArrayList<ConcretContact>>();
		HashMap<String, ConcretData.ConcretPhone> phonesMap = mPhonesMap;
		HashMap<Long, ArrayList<ConcretContact>> groupListMap = mGroupListMap;
		Set<Entry<Long, ConcretGroup>> groupset = mGroupsMap.entrySet();
		for (Entry<Long, ConcretGroup> e : groupset)
			groupListMap.put(e.getKey(), new ArrayList<ConcretContact>());
		//
		long dataId, rawId;
		ConcretContact contact = null;
		String mimetype = null;
		int index = 0;
		ConcretData data = null;
		IDataColumn[] datas = null;
		for (int i = 0; i < c.getCount(); ++i) {
			dataId = c.getLong(0);
			rawId = c.getLong(1);
			mimetype = c.getString(2);
			contact = rawMap.get(rawId);
			index = contact.mDataColumnLength;
			datas = contact.mDatas;
			//
			if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
				ConcretPhone phone = new ConcretPhone(dataId, contact);
				phone.mNumber = c.getString(5);
				phone.mType = c.getInt(6);
				phone.mLabel = c.getString(7);
				data = phone;
				phonesMap.put(phone.mNumber, phone);
			} else if (GroupMembership.CONTENT_ITEM_TYPE.equals(mimetype)) {
				ConcretGroupMembership group = new ConcretGroupMembership(
						dataId, contact);
				long groupId = c.getLong(5);
				group.mGroupId = groupId;
				data = group;
				//
				groupListMap.get(groupId).add(contact);
				//
			} else if (Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {
				contact.mPhoto = c.getBlob(8);
			}
			if (data != null) {
				if (index == datas.length) {
					datas = contact.mDatas = expand(datas,
							new IDataColumn[datas.length * 2]);
				}
				datas[index] = data;
				++contact.mDataColumnLength;
				data = null;
			}
			c.moveToNext();
		}
	}

	private void visitCallLogs() throws RemoteException {
		Cursor c = this.mCLClient.query(Calls.CONTENT_URI,
				new String[] { Calls._ID, Calls.NUMBER, Calls.TYPE, Calls.DATE,
						Calls.DURATION }, null, null, Calls.DEFAULT_SORT_ORDER);
		if (c == null)
			return;
		if (!c.moveToFirst()) {
			c.close();
			return;
		}
		final int count = c.getCount();
		ConcretCallLog[][] calls = new ConcretCallLog[5][];
		ConcretCallLog[] allcalls = new ConcretCallLog[count];
		calls[3] = allcalls;
		this.mCalls = calls;
		//
		List<ConcretCallLog> in = new ArrayList<ConcretCallLog>(), //
		out = new ArrayList<ConcretCallLog>(), // 
		missed = new ArrayList<ConcretCallLog>(), //
		unknown = new ArrayList<ConcretCallLog>();
		//
		ConcretCallLog call = null;
		IPhone phone = null;
		long id, date, dur;
		String number = null;
		int type;
		for (int i = 0; i < count; ++i) {
			id = c.getLong(0);
			number = c.getString(1);
			type = c.getInt(2);
			phone = this.lookup(number);
			call = new ConcretCallLog(id);
			call.setupNumber(number, type, phone);
			date = c.getLong(3);
			dur = c.getLong(4);
			call.mTime = date;
			call.mDuration = dur;
			allcalls[i] = call;
			switch (call.getType()) {
			case INCOMING:
				in.add(call);
				break;
			case OUTGOING:
				out.add(call);
				break;
			case MISSED:
				missed.add(call);
				break;
			}
			if (call.getParent() == null)
				unknown.add(call);
			c.moveToNext();
		}
		calls[0] = in.toArray(new ConcretCallLog[in.size()]);
		calls[1] = out.toArray(new ConcretCallLog[out.size()]);
		calls[2] = missed.toArray(new ConcretCallLog[missed.size()]);
		calls[4] = unknown.toArray(new ConcretCallLog[unknown.size()]);
		c.close();
	}

	private IPhone lookup(String number) {
		IPhone phone = this.mPhonesMap.get(number);
		if (phone != null)
			return phone;
		else {
			IPhone[] results = this.phoneLookup(number);
			if (results != null && results.length != 0)
				return results[0];
			else
				return null;
		}
	}
}
