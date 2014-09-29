package net.oschina.app.v2.activity.question.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.question.adapter.QuestionAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.model.PostList;
import net.oschina.app.v2.utils.UIHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 相关问题
 * 
 * @author william_sim
 */
public class QuestionTagFragment extends BaseListFragment {

	public static final String BUNDLE_KEY_TAG = "BUNDLE_KEY_TAG";
	protected static final String TAG = QuestionTagFragment.class
			.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "post_tag_";
	private static final String QUESTION_TAG_SCREEN = "question_tag_screen";
	private String mTag;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mTag = args.getString(BUNDLE_KEY_TAG);
			((BaseActivity) getActivity()).setActionBarTitle(getString(
					R.string.actionbar_title_question_tag, mTag));
		}
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new QuestionAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return new StringBuffer(CACHE_KEY_PREFIX).append(mTag).toString();
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
		NewsApi.getPostListByTag(mTag, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Post post = (Post) mAdapter.getItem(position - 1);
		if (post != null)
			UIHelper.showQuestionDetail(view.getContext(), post.getId());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(QUESTION_TAG_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(QUESTION_TAG_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
