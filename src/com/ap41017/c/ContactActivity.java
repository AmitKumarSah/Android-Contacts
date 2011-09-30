package com.ap41017.c;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.ap41017.c.impl.BasicContactManager;
import com.ap41017.c.interfaces.IContactsManager;

public class ContactActivity extends FragmentActivity implements
		LoaderCallbacks<IContactsManager> {

	private static final class BasicLoader extends
			AsyncTaskLoader<IContactsManager> {

		public BasicLoader(Context context) {
			super(context);
		}

		@Override
		protected void onStartLoading() {
			this.forceLoad();
		}

		@Override
		public IContactsManager loadInBackground() {
			return new BasicContactManager().loadDatas(this.getContext()
					.getContentResolver());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.getSupportLoaderManager().initLoader(0, null, this);
	}

	private IContactsManager mMgr;

	@Override
	public Loader<IContactsManager> onCreateLoader(int arg0, Bundle arg1) {
		return new BasicLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<IContactsManager> arg0,
			IContactsManager arg1) {
		this.mMgr = arg1;
		this.mMgr.getGroups();
	}

	@Override
	public void onLoaderReset(Loader<IContactsManager> arg0) {
		this.mMgr = null;
	}
}