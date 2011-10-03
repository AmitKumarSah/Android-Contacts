package com.ap41017.c.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import com.ap41017.c.impl.ConcretContactData.ConcretGroupMembership;
import com.ap41017.c.impl.ConcretContactData.ConcretPhone;
import com.ap41017.c.interfaces.ICallLog;
import com.ap41017.c.interfaces.ICallLog.CallType;
import com.ap41017.c.interfaces.IContact;
import com.ap41017.c.interfaces.IContactsManager;
import com.ap41017.c.interfaces.IGroup;

public class BasicContactManager implements IContactsManager {

	private static final int sCallLogAllIdx = 3;
	private static final int sCallLogUnknownIdx = 4;

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
	public ConcretPhone[] filterLookup(String textToFilter) {
		do {
			if (TextUtils.isEmpty(textToFilter))
				break;
			Cursor p = null;
			try {
				p = this.mCCClient.query(
						Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
								Uri.encode(textToFilter)),
						new String[] { Phone.NUMBER }, null, null, null);
			} catch (RemoteException e) {
				break;
			}
			if (p == null)
				break;
			//
			HashMap<String, ConcretPhone> phonesMap = this.mPhonesMap;
			ConcretPhone[] phones = null;
			final int count = p.getCount();
			if (p.moveToFirst() && count > 0) {
				phones = new ConcretPhone[count];
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
	public IGroup[] getAllGroups() {
		Collection<ConcretGroup> values = this.mGroupsMap.values();
		return values.toArray(new IGroup[values.size()]);
	}

	@Override
	public IGroup[] getVisibleGroups() {
		Collection<ConcretGroup> values = this.mGroupsMap.values();
		List<ConcretGroup> list = new ArrayList<ConcretGroup>(values.size());
		for (ConcretGroup group : values)
			if (group.getVisible())
				list.add(group);
		return list.toArray(new IGroup[list.size()]);
	}

	@Override
	public IContact[] getAllContacts() {
		return this.mAllContacts;
	}

	@Override
	public IContact[] getContactsIn(IGroup group) {
		if (group == null)
			throw new IllegalArgumentException("Group Cannot be null!!");
		ArrayList<ConcretContact> list = this.mGroupListMap.get(group.getId());
		return list.toArray(new IContact[list.size()]);
	}

	@Override
	public ICallLog[] getCallLogs(CallType callType, boolean getUnknown) {
		if (callType != null)
			return this.mCalls[callType.ordinal()];
		if (getUnknown)
			return this.mCalls[sCallLogUnknownIdx];
		else
			return this.mCalls[sCallLogAllIdx];
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
			HashMap<Long, ConcretContact> conMap = new HashMap<Long, ConcretContact>(
					c.getCount());
			HashMap<Long, ConcretContact> rawMap = new HashMap<Long, ConcretContact>(
					c.getCount());
			ConcretContact contact;
			long rawId, contactId;
			for (int i = 0; i < c.getCount(); ++i) {
				rawId = c.getLong(0);
				contactId = c.getLong(1);
				contact = conMap.get(contactId);
				if (contact == null)
					conMap.put(contactId, contact = new ConcretContact(
							contactId));
				contact.addRawId(rawId);
				rawMap.put(rawId, contact);
				c.moveToNext();
			}
			mConMap = conMap;
			mRawMap = rawMap;
		} finally {
			c.close();
		}
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
			long id;
			for (int i = 0, count = c.getCount(); i < count; ++i) {
				id = c.getLong(0);
				contact = conMap.get(id);
				if (contact == null) {
					contact = new ConcretContact(id);
					conMap.put(id, contact);
				}
				contact.setDisplayName(c.getString(1))
						.setLookupKey(c.getString(2))
						.setTimesContacted(c.getInt(3))
						.setLastTimeContact(c.getLong(4));
				c.moveToNext();
			}
		} finally {
			c.close();
		}
		mAllContacts = new ConcretContact[conMap.size()];
		mAllContacts = conMap.values().toArray(mAllContacts);
	}

	private void visitGroups() throws RemoteException {
		Cursor g = mCCClient.query(Groups.CONTENT_URI,
				new String[] { Groups._ID, Groups.SYSTEM_ID, Groups.TITLE,
						Groups.GROUP_VISIBLE, Groups.ACCOUNT_NAME,
						Groups.ACCOUNT_TYPE, }, null, null, null);
		if (g == null)
			return;
		if (!g.moveToFirst()) {
			g.close();
			return;
		}
		try {
			HashMap<Long, ConcretGroup> groupsMap = new HashMap<Long, ConcretGroup>(
					g.getCount());
			long id;
			ConcretGroup group;
			for (int i = 0, count = g.getCount(); i < count; ++i) {
				id = g.getLong(0);
				group = new ConcretGroup(id, g.getString(1), g.getString(2),
						(g.getInt(3) > 0));
				group.setAccount(g.getString(4), g.getString(5));
				groupsMap.put(id, group);
				g.moveToNext();
			}
			mGroupsMap = groupsMap;
		} finally {
			g.close();
		}
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
		HashMap<String, ConcretPhone> phonesMap = new HashMap<String, ConcretPhone>();
		HashMap<Long, ArrayList<ConcretContact>> groupListMap = new HashMap<Long, ArrayList<ConcretContact>>();
		HashMap<Long, ConcretGroup> groupMap = mGroupsMap;
		for (Entry<Long, ConcretGroup> e : groupMap.entrySet())
			groupListMap.put(e.getKey(), new ArrayList<ConcretContact>());
		//
		long dataId, rawId;
		ConcretContact contact = null;
		String mimetype = null;
		for (int i = 0, count = c.getCount(); i < count; ++i) {
			dataId = c.getLong(0);
			rawId = c.getLong(1);
			mimetype = c.getString(2);
			contact = rawMap.get(rawId);
			//
			if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
				ConcretPhone phone = new ConcretPhone(dataId, contact);
				String number = c.getString(5);
				phone.setPhone(number, c.getInt(6), c.getString(7));
				//
				phonesMap.put(number, phone);
				contact.addPhone(phone);
			} else if (GroupMembership.CONTENT_ITEM_TYPE.equals(mimetype)) {
				long groupId = c.getLong(5);
				ConcretGroupMembership group = new ConcretGroupMembership(
						dataId, contact, groupMap.get(groupId));
				//
				groupListMap.get(groupId).add(contact);
				contact.addGroupMembership(group);
			} else if (Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {
				contact.setPhoto(c.getBlob(8));
			}
			c.moveToNext();
		}
		mPhonesMap = phonesMap;
		mGroupListMap = groupListMap;
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
		this.mCalls = calls;
		//
		List<ConcretCallLog> in = new ArrayList<ConcretCallLog>(), //
		out = new ArrayList<ConcretCallLog>(), // 
		missed = new ArrayList<ConcretCallLog>(), //
		unknown = new ArrayList<ConcretCallLog>();
		//
		ConcretCallLog call = null;
		String number = null;
		for (int i = 0; i < count; ++i) {
			number = c.getString(1);
			call = ConcretCallLog.newInstance(c.getLong(0), number,
					this.lookup(number), unknown);
			call.setType(c.getInt(2), in, out, missed);
			call.setDate(c.getLong(3)).setDuration(c.getLong(4));
			allcalls[i] = call;
			c.moveToNext();
		}
		calls[0] = in.toArray(new ConcretCallLog[in.size()]);
		calls[1] = out.toArray(new ConcretCallLog[out.size()]);
		calls[2] = missed.toArray(new ConcretCallLog[missed.size()]);
		calls[sCallLogAllIdx] = allcalls;
		calls[sCallLogUnknownIdx] = unknown.toArray(new ConcretCallLog[unknown
				.size()]);
		c.close();
	}

	private ConcretPhone lookup(String number) {
		ConcretPhone phone = this.mPhonesMap.get(number);
		if (phone == null) {
			try {
				Cursor c = this.mCCClient.query(Uri.withAppendedPath(
						PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
						new String[] { PhoneLookup.NUMBER }, null, null, null);
				if (c == null)
					return null;
				if (c.moveToFirst()) {
					String realNum = c.getString(0);
					phone = this.mPhonesMap.get(realNum);
					this.mPhonesMap.put(number, phone);
				}
				c.close();
			} catch (RemoteException e) {
			}
		}
		return phone;
	}
}
