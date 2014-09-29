package net.oschina.app.v2.activity.question.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.question.adapter.QuestionAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.model.PostList;
import net.oschina.app.v2.utils.UIHelper;
import android.view.View;
import android.widget.AdapterView;

/**
 * 问答
 * 
 * @author william_sim
 */
public class QuestionFragment extends BaseListFragment {

	protected static final String TAG = QuestionFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "post_list";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new QuestionAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		PostList list = PostList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((PostList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getPostList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Post post = (Post) mAdapter.getItem(position - 1);
		if (post != null)
			UIHelper.showQuestionDetail(view.getContext(), post.getId());
	}
}
