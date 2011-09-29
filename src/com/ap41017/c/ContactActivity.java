package com.ap41017.c;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ap41017.c.impl.ContactsManagerLoader;
import com.ap41017.c.impl.ContactsManagerLoader.ContactsManagerCallbacks;
import com.ap41017.c.interfaces.IContactsManager;

public class ContactActivity extends FragmentActivity implements
		ContactsManagerCallbacks {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ContactsManagerLoader.initLoader(this, this.getSupportLoaderManager(),
				0, null);
		ContactsManagerLoader.registerCallback(this);
	}

	private IContactsManager mMgr;

	@Override
	public void onLoadFinish(IContactsManager manager) {
		this.mMgr = manager;
		this.mMgr.hashCode();
	}

	@Override
	public void onLoaderReset() {
		if (this.mMgr != null) {
			this.mMgr.releaseClients();
			this.mMgr = null;
		}
	}
}