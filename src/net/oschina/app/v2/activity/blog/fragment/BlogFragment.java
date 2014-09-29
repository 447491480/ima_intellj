package net.oschina.app.v2.activity.blog.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.blog.adapter.BlogAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Blog;
import net.oschina.app.v2.model.BlogList;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.utils.UIHelper;
import android.view.View;
import android.widget.AdapterView;

/**
 * 博客列表
 * 
 * @author william_sim
 */
public class BlogFragment extends BaseListFragment {

	protected static final String TAG = BlogFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "blog_list";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new BlogAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		BlogList list = BlogList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((BlogList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getBlogList(
				mCatalog == BlogList.CATALOG_RECOMMEND ? "recommend" : "latest",
				mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Blog blog = (Blog) mAdapter.getItem(position - 1);
		if (blog != null)
			UIHelper.showUrlRedirect(view.getContext(), blog.getUrl());
	}
}
