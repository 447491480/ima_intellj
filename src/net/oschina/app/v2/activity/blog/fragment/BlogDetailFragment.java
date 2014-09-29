package net.oschina.app.v2.activity.blog.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.blog.view.ReportDialog;
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
import net.oschina.app.v2.model.Blog;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.Entity;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.Report;
import net.oschina.app.v2.service.PublicCommentTask;
import net.oschina.app.v2.service.ServerTaskUtils;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class BlogDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl, ToolbarFragmentControl {

	protected static final String TAG = BlogDetailFragment.class
			.getSimpleName();
	private static final String BLOG_CACHE_KEY = "blog_";
	private static final String BLOG_DETAIL_SCREEN = "blog_detail_screen";
	private TextView mTvTitle, mTvSource, mTvTime;
	private TextView mTvCommentCount;
	private WebView mWebView;
	private int mBlogId;
	private Blog mBlog;
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
				if (mBlog != null)
					UIHelper.showBlogComment(getActivity(), mBlogId,
							mBlog.getAuthorId());
				break;
			case ACTION_FAVORITE:
				handleFavoriteOrNot();
				break;
			case ACTION_SHARE:
				handleShare();
				break;
			case ACTION_REPORT:
				onReportMenuClick();
				break;
			default:
				break;
			}
		}
	};

	private AsyncHttpResponseHandler mReportHandler = new TextHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, String arg2) {
			if (TextUtils.isEmpty(arg2)) {
				AppContext.showToastShort(R.string.tip_report_success);
			} else {
				AppContext.showToastShort(R.string.tip_report_faile);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, String arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.tip_report_faile);
		}

		@Override
		public void onFinish() {
			hideWaitDialog();
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
				UIHelper.showBlogComment(getActivity(), mBlog.getId(),
						mBlog.getAuthorId());
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_news_detail,
				container, false);

		mBlogId = getActivity().getIntent().getIntExtra("blog_id", 0);

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
	protected boolean hasReportMenu() {
		return true;
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(BLOG_CACHE_KEY).append(mBlogId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getBlogDetail(mBlogId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return Blog.parse(is);
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Blog) seri;
	}

	@Override
	protected void onCommentChanged(int opt, int id, int catalog,
			boolean isBlog, Comment comment) {
		if (id == mBlogId && isBlog) {
			if (Comment.OPT_ADD == opt && mBlog != null) {
				mBlog.setCommentCount(mBlog.getCommentCount() + 1);
				// if (mTvCommentCount != null) {
				// mTvCommentCount.setVisibility(View.VISIBLE);
				// mTvCommentCount.setText(getString(R.string.comment_count,
				// mBlog.getCommentCount()));
				// }
				if (mToolBarFragment != null) {
					mToolBarFragment.setCommentCount(mBlog.getCommentCount());
				}
			}
		}
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mBlog = (Blog) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mBlog.getTitle());
		mTvSource.setText(mBlog.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mBlog.getPubDate()));
		// if (mTvCommentCount != null) {
		// mTvCommentCount.setVisibility(View.VISIBLE);
		// mTvCommentCount.setText(getString(R.string.comment_count,
		// mBlog.getCommentCount()));
		// }
		if (mToolBarFragment != null) {
			mToolBarFragment.setCommentCount(mBlog.getCommentCount());
		}
		notifyFavorite(mBlog.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		String body = UIHelper.WEB_STYLE + mBlog.getBody();
		body = UIHelper.setHtmlCotentSupportImagePreview(body);
		body += UIHelper.WEB_LOAD_IMAGES;
		mWebView.setWebViewClient(mWebClient);
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
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
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_REPORT, true);
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
		task.setId(mBlogId);
		task.setContent(text);
		task.setUid(AppContext.instance().getLoginUid());
		ServerTaskUtils.publicBlogComment(getActivity(), task);
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
		return mBlog != null ? mBlog.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mBlog != null ? FavoriteList.TYPE_BLOG : -1;
	}

	@Override
	protected String getShareContent() {
		return mBlog != null ? mBlog.getTitle() : null;
	}

	@Override
	protected String getShareUrl() {
		return mBlog != null ? mBlog.getUrl() : null;
	}

	@Override
	protected void onReportMenuClick() {
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		if (mBlog == null)
			return;
		int reportId = AppContext.instance().getLoginUid();
		final ReportDialog dialog = new ReportDialog(getActivity(),
				mBlog.getUrl(), reportId);
		dialog.setCancelable(true);
		dialog.setTitle(R.string.report);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface d, int which) {
						Report report = null;
						if ((report = dialog.getReport()) != null) {
							showWaitDialog(R.string.progress_submit);
							NewsApi.report(report, mReportHandler);
						}
						d.dismiss();
					}
				});
		dialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(BLOG_DETAIL_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(BLOG_DETAIL_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
