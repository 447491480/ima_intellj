package net.oschina.app.v2.activity.favorite.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.favorite.adapter.FavoriteAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.FavoriteList.Favorite;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.utils.UIHelper;
import android.view.View;
import android.widget.AdapterView;

import com.umeng.analytics.MobclickAgent;

/**
 * 我的收藏
 * 
 * @author william_sim
 */
public class FavoriteFragment extends BaseListFragment {

	protected static final String TAG = FavoriteFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "favorite_list";
	private static final String FAVORITE_SCREEN = "favorite_screen";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new FavoriteAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		FavoriteList list = FavoriteList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((FavoriteList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getFavoriteList(AppContext.instance().getLoginUid(), mCatalog,
				mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Favorite item = (Favorite) mAdapter.getItem(position - 1);
		if (item != null)
			UIHelper.showUrlRedirect(view.getContext(), item.url);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(FAVORITE_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(FAVORITE_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
