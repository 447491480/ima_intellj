package net.oschina.app.v2.activity.search.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.search.adapter.SearchAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.SearchList;
import net.oschina.app.v2.model.SearchList.Result;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.UIHelper;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.umeng.analytics.MobclickAgent;

public class SearchFragment extends BaseListFragment {
	protected static final String TAG = SearchFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "search_list_";
	private static final String SEARCH_SCREEN = "search_screen";
	private String mCatalog;
	private String mSearch;
	private boolean mRquestDataIfCreated = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getString(BUNDLE_KEY_CATALOG);
		}

		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
		getActivity().getWindow().setSoftInputMode(mode);
	}

	public void search(String search) {
		mSearch = search;
		if (mErrorLayout != null) {
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
			mState = STATE_REFRESH;
			requestData(true);
		} else {
			mRquestDataIfCreated = true;
		}
	}

	@Override
	protected boolean requestDataIfViewCreated() {
		return mRquestDataIfCreated;
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new SearchAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + mCatalog + mSearch;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		SearchList list = SearchList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((SearchList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getSearchList(mCatalog, mSearch, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SearchList.Result res = (Result) mAdapter.getItem(position - 1);
		if (res != null)
			UIHelper.showUrlRedirect(view.getContext(), res.getUrl());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SEARCH_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SEARCH_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
