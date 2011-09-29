package com.ap41017.c.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.ap41017.c.interfaces.IContactsManager;

public class ContactsManagerLoader extends AsyncTaskLoader<IContactsManager>
		implements LoaderCallbacks<IContactsManager> {

	public static interface ContactsManagerCallbacks {

		public void onLoadFinish(IContactsManager manager);

		public void onLoaderReset();
	}

	public static final boolean initLoader(Context context,
			LoaderManager mgr, int arg0, Bundle arg1) {
		if (sLoader == null) {
			mgr.initLoader(arg0, arg1, sLoader = new ContactsManagerLoader(
					context));
			return true;
		} else {
			//mgr.restartLoader(arg0, arg1, sLoader);
			return false;
		}
	}

	public static final boolean registerCallback(ContactsManagerCallbacks c) {
		if (sLoader != null) {
			synchronized (sLoader) {
				sLoader.mCallbacks.add(c);
			}
			return true;
		} else
			return false;
	}

	private static ContactsManagerLoader sLoader;

	private List<ContactsManagerCallbacks> mCallbacks = new ArrayList<ContactsManagerLoader.ContactsManagerCallbacks>();

	@Override
	public Loader<IContactsManager> onCreateLoader(int arg0, Bundle arg1) {
		return this;
	}

	private ContactsManagerLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		this.forceLoad();
	}

	@Override
	public IContactsManager loadInBackground() {
		return new ConcretManager().loadInBackground(this.getContext()
				.getContentResolver());
	}

	@Override
	public synchronized void onLoadFinished(Loader<IContactsManager> arg0,
			IContactsManager arg1) {
		for (ContactsManagerCallbacks c : this.mCallbacks)
			c.onLoadFinish(arg1);
	}

	@Override
	public synchronized void onLoaderReset(Loader<IContactsManager> arg0) {
		for (ContactsManagerCallbacks c : this.mCallbacks)
			c.onLoaderReset();
		this.mCallbacks.clear();
		sLoader = null;
	}
}