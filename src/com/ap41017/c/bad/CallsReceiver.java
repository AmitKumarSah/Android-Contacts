package com.ap41017.c.bad;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;

public class CallsReceiver extends BroadcastReceiver {

	public static final boolean isAutoLoggedByPhone(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(TIMES_CON_AUTO_LOGGED_BY_PHONE_KEY, false);
	}

	private static final String TIMES_CON_AUTO_LOGGED_BY_PHONE_KEY = "_time_contacted_is_ok_key_";
	private static final String TEMP_TIMES_CONTACTED_KEY = "temp_times";

	private String mPhone;

	private boolean giveMePhone(Intent intent) {
		if (intent == null)
			return false;
		String action = intent.getAction();
		if (action == null)
			return false;
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
			mPhone = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		} else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
				mPhone = intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			}
		}
		return mPhone != null;
	}

	private Uri checkIfAutoLog(Context context) {
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(this.mPhone));
		ContentResolver resolver = context.getContentResolver();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!sp.contains(TIMES_CON_AUTO_LOGGED_BY_PHONE_KEY)) {
			Cursor c = resolver.query(lookupUri,
					new String[] { PhoneLookup.TIMES_CONTACTED }, null, null,
					null);
			if (c == null)
				return null;
			if (!c.moveToFirst()) {
				c.close();
				return null;
			}
			int times = c.getInt(0);
			c.close();
			int oldTimes = sp.getInt(TEMP_TIMES_CONTACTED_KEY, -1);
			boolean isAutoLog = times - oldTimes > 0;
			if (oldTimes != -1) {
				sp.edit()
						.putBoolean(TIMES_CON_AUTO_LOGGED_BY_PHONE_KEY,
								isAutoLog).remove(TEMP_TIMES_CONTACTED_KEY)
						.commit();
				if (isAutoLog)
					return null;
			} else {
				sp.edit().putInt(TEMP_TIMES_CONTACTED_KEY, times).commit();
				return null;
			}
		}
		return Contacts.lookupContact(resolver, lookupUri);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (isAutoLoggedByPhone(context) || !this.giveMePhone(intent))
			return;
		Uri uri = this.checkIfAutoLog(context);
		if (uri == null)
			return;
		long conid = ContentUris.parseId(uri);
		if (conid == -1)
			return;
		ContentResolver resolver = context.getContentResolver();
		Cursor c = resolver.query(uri,
				new String[] { Contacts.TIMES_CONTACTED }, null, null, null);
		c.moveToFirst();
		int times = c.getInt(0);
		c.close();
		++times;
		ContentValues values = new ContentValues();
		values.put(Contacts.TIMES_CONTACTED, times);
		resolver.update(uri, values, null, null);
	}
}
