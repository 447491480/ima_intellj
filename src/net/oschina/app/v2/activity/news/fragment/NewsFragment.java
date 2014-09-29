package net.oschina.app.v2.activity.news.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.news.adapter.NewsAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.News;
import net.oschina.app.v2.model.NewsList;
import net.oschina.app.v2.utils.UIHelper;
import android.view.View;
import android.widget.AdapterView;

/**
 * 新闻资讯
 * 
 * @author william_sim
 */
public class NewsFragment extends BaseListFragment {

	protected static final String TAG = NewsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "newslist_";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new NewsAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		NewsList list = NewsList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((NewsList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getNewsList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		News news = (News) mAdapter.getItem(position - 1);
		if (news != null)
			UIHelper.showNewsRedirect(view.getContext(), news);
	}
}
