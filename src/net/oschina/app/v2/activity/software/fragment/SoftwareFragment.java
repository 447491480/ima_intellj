package net.oschina.app.v2.activity.software.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.software.adapter.SoftwareAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.SoftwareList;
import net.oschina.app.v2.model.SoftwareList.Software;
import net.oschina.app.v2.utils.UIHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.umeng.analytics.MobclickAgent;

/**
 * 软件列表
 * 
 * @author william_sim
 */
public class SoftwareFragment extends BaseListFragment {

	protected static final String TAG = SoftwareFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "software_list";
	private static final String SOFTWARE_SCREEN = "software_screen";
	private String mCatalog;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getString(BUNDLE_KEY_CATALOG);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SOFTWARE_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SOFTWARE_SCREEN);
		MobclickAgent.onPause(getActivity());
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new SoftwareAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		SoftwareList list = SoftwareList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((SoftwareList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getSoftwareList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Software software = (Software) mAdapter.getItem(position - 1);
		if (software != null)
			UIHelper.showUrlRedirect(view.getContext(), software.url);
	}
}
