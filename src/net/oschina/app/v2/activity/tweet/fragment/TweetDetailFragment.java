package net.oschina.app.v2.activity.tweet.fragment;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.comment.adapter.CommentAdapter;
import net.oschina.app.v2.activity.comment.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.v2.activity.news.fragment.EmojiFragmentControl;
import net.oschina.app.v2.api.OperationResponseHandler;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.cache.CacheManager;
import net.oschina.app.v2.emoji.EmojiFragment;
import net.oschina.app.v2.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.CommentList;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.Tweet;
import net.oschina.app.v2.service.PublicCommentTask;
import net.oschina.app.v2.service.ServerTaskUtils;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.HTMLSpirit;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class TweetDetailFragment extends BaseFragment implements
		EmojiTextListener, EmojiFragmentControl, OnOperationListener,
		OnItemClickListener, OnItemLongClickListener {
	protected static final String TAG = TweetDetailFragment.class
			.getSimpleName();
	private static final int REQUEST_CODE = 0x1;
	private static final String CACHE_KEY_PREFIX = "tweet_";
	private static final String CACHE_KEY_TWEET_COMMENT = "tweet_comment_";
	private static final String TWEET_DETAIL_SCREEN = "tweet_detail_screen";
	private ListView mListView;
	private EmptyLayout mEmptyView;
	private ImageView mIvAvatar, mIvPic;
	private TextView mTvName, mTvFrom, mTvTime, mTvCommentCount;
	private WebView mContent;
	private int mTweetId;
	private Tweet mTweet;
	private int mCurrentPage = 0;
	private CommentAdapter mAdapter;
	private EmojiFragment mEmojiFragment;
	private BroadcastReceiver mCommentReceiver;

	class CommentChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int opt = intent.getIntExtra(Comment.BUNDLE_KEY_OPERATION, 0);
			int id = intent.getIntExtra(Comment.BUNDLE_KEY_ID, 0);
			int catalog = intent.getIntExtra(Comment.BUNDLE_KEY_CATALOG, 0);
			boolean isBlog = intent.getBooleanExtra(Comment.BUNDLE_KEY_BLOG,
					false);
			Comment comment = intent
					.getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
			onCommentChanged(opt, id, catalog, isBlog, comment);
		}
	}

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mAdapter != null
					&& mAdapter.getDataSize() > 0
					&& mListView.getLastVisiblePosition() == (mListView
							.getCount() - 1)) {
				if (mState == STATE_NONE
						&& mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
					mState = STATE_LOADMORE;
					mCurrentPage++;
					requestTweetCommentData(true);
					// sendRequestCommentData();
				}
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Comment comment = data
					.getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
			if (comment != null && mTweet != null) {
				// mAdapter.addItem(0, comment);
				// mTweet.setCommentCount(mTweet.getCommentCount() + 1);
				// mTvCommentCount.setText(getString(R.string.comment_count,
				// mTweet.getCommentCount()));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void onCommentChanged(int opt, int id, int catalog, boolean isBlog,
			Comment comment) {
		if (Comment.OPT_ADD == opt && catalog == CommentList.CATALOG_TWEET
				&& id == mTweetId) {
			if (mTweet != null && mTvCommentCount != null) {
				mTweet.setCommentCount(mTweet.getCommentCount() + 1);

				mAdapter.addItem(0, comment);
				mTvCommentCount.setText(getString(R.string.comment_count,
						mTweet.getCommentCount()));
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		IntentFilter filter = new IntentFilter(
				Constants.INTENT_ACTION_COMMENT_CHANGED);
		mCommentReceiver = new CommentChangeReceiver();
		getActivity().registerReceiver(mCommentReceiver, filter);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		if (mCommentReceiver != null) {
			getActivity().unregisterReceiver(mCommentReceiver);
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_tweet_detail,
				container, false);
		mTweetId = getActivity().getIntent().getIntExtra("tweet_id", 0);

		initViews(view);

		// sendRequestData();
		requestTweetData(true);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initViews(View view) {
		mEmptyView = (EmptyLayout) view.findViewById(R.id.error_layout);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		View header = LayoutInflater.from(getActivity()).inflate(
				R.layout.v2_list_header_tweet_detail, null);
		mIvAvatar = (ImageView) header.findViewById(R.id.iv_avatar);
		mIvAvatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showUserCenter(getActivity(), mTweet.getAuthorId(),
						mTweet.getAuthor());
			}
		});

		mTvName = (TextView) header.findViewById(R.id.tv_name);
		mTvFrom = (TextView) header.findViewById(R.id.tv_from);
		mTvTime = (TextView) header.findViewById(R.id.tv_time);
		mTvCommentCount = (TextView) header.findViewById(R.id.tv_comment_count);
		mContent = (WebView) header.findViewById(R.id.webview);
		mIvPic = (ImageView) header.findViewById(R.id.iv_pic);
		mIvPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTweet != null && !TextUtils.isEmpty(mTweet.getImgSmall())) {
					UIHelper.showImagePreview(getActivity(),
							new String[] { mTweet.getImgBig() });
				}
			}
		});
		initWebView(mContent);

		mListView.addHeaderView(header);
		mAdapter = new CommentAdapter(this, true);
		mListView.setAdapter(mAdapter);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initWebView(WebView webView) {
		WebSettings settings = webView.getSettings();
		settings.setDefaultFontSize(20);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		int sysVersion = Build.VERSION.SDK_INT;
		if (sysVersion >= 11) {
			settings.setDisplayZoomControls(false);
		} else {
			ZoomButtonsController zbc = new ZoomButtonsController(webView);
			zbc.getZoomControls().setVisibility(View.GONE);
		}
		UIHelper.addWebImageShow(getActivity(), webView);
	}

	private void fillUI() {
		ImageLoader.getInstance().displayImage(mTweet.getFace(), mIvAvatar);
		mTvName.setText(mTweet.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mTweet.getPubDate()));
		switch (mTweet.getAppClient()) {
		default:
			mTvFrom.setText("");
			break;
		case Tweet.CLIENT_MOBILE:
			mTvFrom.setText(R.string.from_mobile);
			break;
		case Tweet.CLIENT_ANDROID:
			mTvFrom.setText(R.string.from_android);
			break;
		case Tweet.CLIENT_IPHONE:
			mTvFrom.setText(R.string.from_iphone);
			break;
		case Tweet.CLIENT_WINDOWS_PHONE:
			mTvFrom.setText(R.string.from_windows_phone);
			break;
		case Tweet.CLIENT_WECHAT:
			mTvFrom.setText(R.string.from_wechat);
			break;
		}

		mTvCommentCount.setText(getString(R.string.comment_count,
				mTweet.getCommentCount()));

		// mTvCommentCount.setText(mTweet.getBody());

		// set content
		String body = UIHelper.WEB_STYLE + mTweet.getBody();
		body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
		body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

		mContent.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
		mContent.setWebViewClient(UIHelper.getWebViewClient());

		if (TextUtils.isEmpty(mTweet.getImgSmall())) {
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
		mIvPic.setVisibility(View.VISIBLE);
		ImageLoader.getInstance().displayImage(mTweet.getImgSmall(), mIvPic,
				options);
	}

	private void sendRequestData() {
		mState = STATE_REFRESH;
		mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getTweetDetail(mTweetId, mDetailHandler);
	}

	private void sendRequestCommentData() {
		NewsApi.getCommentList(mTweetId, CommentList.CATALOG_TWEET,
				mCurrentPage, mCommentHandler);
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			mEmojiFragment.hideKeyboard();
			return;
		}
		if (TextUtils.isEmpty(text)) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			mEmojiFragment.requestFocusInput();
			return;
		}
		PublicCommentTask task = new PublicCommentTask();
		task.setId(mTweetId);
		task.setCatalog(CommentList.CATALOG_TWEET);
		task.setIsPostToMyZone(0);
		task.setContent(text);
		task.setUid(AppContext.instance().getLoginUid());
		ServerTaskUtils.publicComment(getActivity(), task);
		mEmojiFragment.reset();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Comment comment = (Comment) mAdapter.getItem(position - 1);
		if (comment == null)
			return;
		if (AppContext.instance().isLogin()
				&& AppContext.instance().getLoginUid() == comment.getAuthorId()) {
			final CommonDialog dialog = DialogHelper
					.getPinterestDialogCancelable(getActivity());
			String[] items = new String[] { getString(R.string.reply),
					getString(R.string.delete) };
			dialog.setTitle(R.string.operation);
			dialog.setItemsWithoutChk(items, new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dialog.dismiss();
					if (position == 0) {
						handleReplyComment(comment);
					} else if (position == 1) {
						handleDeleteComment(comment);
					}
				}

			});
			dialog.setNegativeButton(R.string.cancle, null);
			dialog.show();
		} else {
			handleReplyComment(comment);
		}
	}

	@Override
	public void onMoreClick(final Comment comment) {
	}

	private void handleReplyComment(Comment comment) {
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		UIHelper.showReplyCommentForResult(this, REQUEST_CODE, false, mTweetId,
				CommentList.CATALOG_TWEET, comment);
	}

	private void handleDeleteComment(Comment comment) {
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		AppContext.showToastShort(R.string.deleting);
		NewsApi.deleteComment(mTweetId, CommentList.CATALOG_TWEET,
				comment.getId(), comment.getAuthorId(),
				new DeleteOperationResponseHandler(comment));
	}

	class DeleteOperationResponseHandler extends OperationResponseHandler {

		DeleteOperationResponseHandler(Object... args) {
			super(args);
		}

		@Override
		public void onSuccess(int code, ByteArrayInputStream is, Object[] args) {
			try {
				Result res = Result.parse(is);
				if (res.OK()) {
					AppContext.showToastShort(R.string.delete_success);

					mAdapter.removeItem(args[0]);

					mTweet.setCommentCount(mTweet.getCommentCount() - 1);
					mTvCommentCount.setText(getString(R.string.comment_count,
							mTweet.getCommentCount()));
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(code, e.getMessage(), args);
			}
		}

		@Override
		public void onFailure(int code, String errorMessage, Object[] args) {
			AppContext.showToastShort(R.string.delete_faile);
		}
	}

	protected void requestTweetData(boolean refresh) {
		String key = getCacheKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestData();
		} else {
			readCacheData(key);
		}
	}

	private String getCacheKey() {
		return CACHE_KEY_PREFIX + mTweetId;
	}

	private void readCacheData(String cacheKey) {
		new CacheTask(getActivity()).execute(cacheKey);
	}

	private class CacheTask extends AsyncTask<String, Void, Tweet> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected Tweet doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return (Tweet) seri;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Tweet tweet) {
			super.onPostExecute(tweet);
			if (tweet != null) {
				executeOnLoadDataSuccess(tweet);
			} else {
				executeOnLoadDataError(null);
			}
			executeOnLoadFinish();
		}
	}

	private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
		private WeakReference<Context> mContext;
		private Serializable seri;
		private String key;

		private SaveCacheTask(Context context, Serializable seri, String key) {
			mContext = new WeakReference<Context>(context);
			this.seri = seri;
			this.key = key;
		}

		@Override
		protected Void doInBackground(Void... params) {
			CacheManager.saveObject(mContext.get(), seri, key);
			return null;
		}
	}

	private AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				mTweet = Tweet.parse(new ByteArrayInputStream(arg2));
				if (mTweet != null && mTweet.getId() > 0) {
					executeOnLoadDataSuccess(mTweet);
					new SaveCacheTask(getActivity(), mTweet, getCacheKey())
							.execute();
				} else {
					throw new RuntimeException("load detail error");
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			readCacheData(getCacheKey());
		}
	};

	private void executeOnLoadDataSuccess(Tweet tweet) {
		mTweet = tweet;
		if (mTweet != null && mTweet.getId() > 0) {
			fillUI();
			mCurrentPage = 0;

			mState = STATE_REFRESH;
			mCurrentPage = 0;
			mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
			requestTweetCommentData(true);
		} else {
			throw new RuntimeException("load detail error");
		}
	}

	private void executeOnLoadFinish() {
		mState = STATE_NONE;
	}

	private void executeOnLoadDataError(Object object) {
		mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
	}

	protected void requestTweetCommentData(boolean refresh) {
		String key = getCacheCommentKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestCommentData();
		} else {
			readCacheCommentData(key);
		}
	}

	private String getCacheCommentKey() {
		return CACHE_KEY_TWEET_COMMENT + mTweetId + "_" + mCurrentPage;
	}

	private void readCacheCommentData(String cacheKey) {
		new CacheCommentTask(getActivity()).execute(cacheKey);
	}

	private class CacheCommentTask extends AsyncTask<String, Void, CommentList> {
		private WeakReference<Context> mContext;

		private CacheCommentTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected CommentList doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return (CommentList) seri;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(CommentList list) {
			super.onPostExecute(list);
			if (list != null) {
				executeOnLoadCommentDataSuccess(list);
			} else {
				executeOnLoadCommentDataError(null);
			}
			executeOnLoadCommentFinish();
		}
	}

	private AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				CommentList list = CommentList.parse(new ByteArrayInputStream(
						arg2));
				executeOnLoadCommentDataSuccess(list);
				new SaveCacheTask(getActivity(), list, getCacheCommentKey())
						.execute();
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
		}

		public void onFinish() {
			mState = STATE_NONE;
		}
	};

	private void executeOnLoadCommentDataSuccess(CommentList list) {
		if (mState == STATE_REFRESH)
			mAdapter.clear();
		List<Comment> data = list.getCommentlist();
		mAdapter.addData(data);
		mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
		if (data.size() == 0 && mState == STATE_REFRESH) {
			mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else if (data.size() < TDevice.getPageSize()) {
			if (mState == STATE_REFRESH)
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
			else
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else {
			mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
		}
	}

	private void executeOnLoadCommentFinish() {
		mState = STATE_NONE;
	}

	private void executeOnLoadCommentDataError(Object object) {
		mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TWEET_DETAIL_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TWEET_DETAIL_SCREEN);
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final Comment item = (Comment) mAdapter.getItem(position - 1);
		if (item == null)
			return false;
		String[] items = new String[] { getResources().getString(R.string.copy) };
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setItemsWithoutChk(items, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialog.dismiss();
				TDevice.copyTextToBoard(HTMLSpirit.delHTMLTag(item
						.getContent()));
			}
		});
		dialog.show();
		return true;
	}
}
