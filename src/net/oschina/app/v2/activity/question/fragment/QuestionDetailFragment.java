package net.oschina.app.v2.activity.question.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.List;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.news.ToolbarEmojiVisiableControl;
import net.oschina.app.v2.activity.news.fragment.BaseDetailFragment;
import net.oschina.app.v2.activity.news.fragment.EmojiFragmentControl;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragmentControl;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.emoji.EmojiFragment;
import net.oschina.app.v2.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.CommentList;
import net.oschina.app.v2.model.Entity;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.service.PublicCommentTask;
import net.oschina.app.v2.service.ServerTaskUtils;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class QuestionDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl, ToolbarFragmentControl {

	protected static final String TAG = QuestionDetailFragment.class
			.getSimpleName();
	private static final String POST_CACHE_KEY = "post_";
	private static final String QUESTION_DETAIL_SCREEN = "question_detail_screen";
	private TextView mTvTitle, mTvSource, mTvTime;
	private WebView mWebView;
	private TextView mTvCommentCount;
	private int mPostId;
	private Post mPost;
	private EmojiFragment mEmojiFragment;
	private ToolbarFragment mToolBarFragment;

	private OnClickListener mMoreListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Activity act = getActivity();
			if (act != null && act instanceof ToolbarEmojiVisiableControl) {
				((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
			}
		}
	};

	private OnActionClickListener mActionListener = new OnActionClickListener() {

		@Override
		public void onActionClick(ToolAction action) {
			switch (action) {
			case ACTION_CHANGE:
				Activity act = getActivity();
				if (act != null && act instanceof ToolbarEmojiVisiableControl) {
					((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
				}
				break;
			case ACTION_WRITE_COMMENT:
				act = getActivity();
				if (act != null && act instanceof ToolbarEmojiVisiableControl) {
					((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
				}
				mEmojiFragment.showKeyboardIfNoEmojiGrid();
				break;
			case ACTION_VIEW_COMMENT:
				UIHelper.showComment(getActivity(), mPostId,
						CommentList.CATALOG_POST);
				break;
			case ACTION_FAVORITE:
				handleFavoriteOrNot();
				break;
			case ACTION_SHARE:
				handleShare();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ActionBarActivity act = (ActionBarActivity) activity;
		mTvCommentCount = (TextView) act.getSupportActionBar().getCustomView()
				.findViewById(R.id.tv_comment_count);
		mTvCommentCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showComment(getActivity(), mPost.getId(),
						CommentList.CATALOG_POST);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_news_detail,
				container, false);

		mPostId = getActivity().getIntent().getIntExtra("post_id", 0);

		initViews(view);

		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mTvTitle = (TextView) view.findViewById(R.id.tv_title);
		mTvSource = (TextView) view.findViewById(R.id.tv_source);
		mTvTime = (TextView) view.findViewById(R.id.tv_time);

		mWebView = (WebView) view.findViewById(R.id.webview);
		initWebView(mWebView);
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(POST_CACHE_KEY).append(mPostId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getPostDetail(mPostId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return Post.parse(is);
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Post) seri;
	}

	@Override
	protected void onCommentChanged(int opt, int id, int catalog,
			boolean isBlog, Comment comment) {
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mPost = (Post) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mPost.getTitle());
		mTvSource.setText(mPost.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mPost.getPubDate()));
		//if (mTvCommentCount != null) {
		//	mTvCommentCount.setVisibility(View.VISIBLE);
		//	mTvCommentCount.setText(getString(R.string.answer_count,
		//			mPost.getAnswerCount() + "/" + mPost.getViewCount()));
		//}
		if (mToolBarFragment != null) {
			mToolBarFragment.setCommentCount(mPost.getAnswerCount() + "/"
					+ mPost.getViewCount());
		}
		notifyFavorite(mPost.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		// 显示标签
		String tags = getPostTags(mPost.getTags());
		String body = UIHelper.WEB_STYLE + mPost.getBody() + tags
				+ "<div style=\"margin-bottom: 80px\" />";
		body = UIHelper.setHtmlCotentSupportImagePreview(body);
		body += UIHelper.WEB_LOAD_IMAGES;
		mWebView.setWebViewClient(mWebClient);
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}

	@SuppressWarnings("deprecation")
	private String getPostTags(List<String> taglist) {
		if (taglist == null)
			return "";
		String tags = "";
		for (String tag : taglist) {
			tags += String
					.format("<a class='tag' href='http://www.oschina.net/question/tag/%s' >&nbsp;%s&nbsp;</a>&nbsp;&nbsp;",
							URLEncoder.encode(tag), tag);
		}
		return String.format("<div style='margin-top:10px;'>%s</div>", tags);
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
		mEmojiFragment.setButtonMoreVisibility(View.VISIBLE);
		mEmojiFragment.setButtonMoreClickListener(mMoreListener);
	}

	@Override
	public void setToolBarFragment(ToolbarFragment fragment) {
		mToolBarFragment = fragment;
		mToolBarFragment.setOnActionClickListener(mActionListener);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_CHANGE, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_FAVORITE, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_WRITE_COMMENT,
				true);
		mToolBarFragment
				.setActionVisiable(ToolAction.ACTION_VIEW_COMMENT, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_SHARE, true);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		if (TextUtils.isEmpty(text)) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			mEmojiFragment.requestFocusInput();
			return;
		}
		PublicCommentTask task = new PublicCommentTask();
		task.setId(mPostId);
		task.setCatalog(CommentList.CATALOG_POST);
		task.setIsPostToMyZone(0);
		task.setContent(text);
		task.setUid(AppContext.instance().getLoginUid());
		ServerTaskUtils.publicComment(getActivity(), task);
		mEmojiFragment.reset();
	}
	
	@Override
	protected void onFavoriteChanged(boolean flag) {
		super.onFavoriteChanged(flag);
		if (mToolBarFragment != null) {
			mToolBarFragment.setFavorite(flag);
		}
	}
	
	@Override
	protected int getFavoriteTargetId() {
		return mPost != null ? mPost.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mPost != null ? FavoriteList.TYPE_POST : -1;
	}

	@Override
	protected String getShareContent() {
		return mPost != null ? mPost.getTitle() : null;
	}

	@Override
	protected String getShareUrl() {
		return mPost != null ? mPost.getUrl() : null;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(QUESTION_DETAIL_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(QUESTION_DETAIL_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
