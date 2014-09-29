package net.oschina.app.v2.activity.news.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.news.view.ShareDialog;
import net.oschina.app.v2.activity.news.view.ShareDialog.OnSharePlatformClick;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.cache.CacheManager;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.Entity;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.internal.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonlin.osc.happy.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class BaseDetailFragment extends BaseFragment implements
		OnItemClickListener {
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private ListPopupWindow mMenuWindow;
	private MenuAdapter mMenuAdapter;
	protected EmptyLayout mEmptyLayout;
	protected WebView mWebView;

	protected WebViewClient mWebClient = new WebViewClient() {

		private boolean receivedError = false;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			receivedError = false;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			UIHelper.showUrlRedirect(view.getContext(), url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (mEmptyLayout != null) {
				if (receivedError) {
					mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				} else {
					mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			receivedError = true;
		}
	};

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void initWebView(WebView webView) {
		WebSettings settings = webView.getSettings();
		settings.setDefaultFontSize(15);
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

	public class JavaScriptInterface {

		private Context ctx;

		public JavaScriptInterface(Context ctx) {
			this.ctx = ctx;
		}

		public void onImageClick(String bigImageUrl) {
			if (bigImageUrl != null) {
				// UIHelper.showImageZoomDialog(cxt, bigImageUrl);
				UIHelper.showImagePreview(ctx, new String[] { bigImageUrl });
			}
		}
	}

	protected void recycleWebView() {
		if (mWebView != null) {
			// webView.loadUrl("about:blank");
			// webView.destroy();
			// webView = null;
			mWebView.setVisibility(View.GONE);
			mWebView.removeAllViews();
			mWebView.destroy();
			mWebView = null;
		}
	}

	protected boolean shouldRegisterCommentChangedReceiver() {
		return true;
	}

	protected void onCommentChanged(int opt, int id, int catalog,
			boolean isBlog, Comment comment) {
	}

	private CommentChangeReceiver mReceiver;
	private AsyncTask<String, Void, Entity> mCacheTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuAdapter = new MenuAdapter(hasReportMenu());
		setHasOptionsMenu(true);

		if (shouldRegisterCommentChangedReceiver()) {
			mReceiver = new CommentChangeReceiver();
			IntentFilter filter = new IntentFilter(
					Constants.INTENT_ACTION_COMMENT_CHANGED);
			getActivity().registerReceiver(mReceiver, filter);
		}

		mController.getConfig().closeToast();
	}

	protected boolean hasReportMenu() {
		return false;
	}

	@Override
	public void onDestroyView() {
		recycleWebView();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		cancelReadCache();
		recycleWebView();
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(false);
	}

	protected String getCacheKey() {
		return null;
	}

	protected Entity parseData(InputStream is) throws Exception {
		return null;
	}

	protected Entity readData(Serializable seri) {
		return null;
	}

	protected void sendRequestData() {
	}

	protected void requestData(boolean refresh) {
		String key = getCacheKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestData();
		} else {
			readCacheData(key);
		}
	}

	private void readCacheData(String cacheKey) {
		cancelReadCache();
		mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
	}

	private void cancelReadCache() {
		if (mCacheTask != null) {
			mCacheTask.cancel(true);
			mCacheTask = null;
		}
	}

	private class CacheTask extends AsyncTask<String, Void, Entity> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected Entity doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return readData(seri);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Entity entity) {
			super.onPostExecute(entity);
			if (entity != null) {
				executeOnLoadDataSuccess(entity);
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

	protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Entity entity = parseData(new ByteArrayInputStream(arg2));
				if (entity != null && entity.getId() > 0) {
					executeOnLoadDataSuccess(entity);
					saveCache(entity);
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
			// executeOnLoadDataError(arg3.getMessage());
			readCacheData(getCacheKey());
		}
	};

	private boolean mIsFavorited;

	protected void saveCache(Entity entity) {
		new SaveCacheTask(getActivity(), entity, getCacheKey()).execute();
	}

	protected void executeOnLoadDataSuccess(Entity entity) {

	}

	protected void executeOnLoadDataError(String object) {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
		mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = STATE_REFRESH;
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				requestData(true);
			}
		});
	}

	protected void executeOnLoadFinish() {
	}

	protected void onFavoriteChanged(boolean flag) {
	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.detail_menu, menu);
//		MenuItem item = menu.findItem(R.id.detail_menu_favorite);
//		if (AppContext.instance().isLogin()
//				&& mIsFavorited) {
//			item.setIcon(R.drawable.actionbar_unfavorite_icon);
//			item.setTitle(R.string.detail_menu_unfavorite);
//		} else {
//			item.setIcon(R.drawable.actionbar_favorite_icon);
//			item.setTitle(R.string.detail_menu_favorite);
//		}
//		
//		MenuItem share = menu.findItem(R.id.detail_menu_share);
//		MenuItem more = menu.findItem(R.id.detail_menu_more);
//		if(hasReportMenu()) {
//			more.setVisible(true);
//			share.setVisible(false);
//		} else {
//			more.setVisible(false);
//			share.setVisible(true);
//		}
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.detail_menu_favorite:
			handleFavoriteOrNot();
			break;
		case R.id.detail_menu_share:
			handleShare();
			break;
		case R.id.detail_menu_more:
			showMoreOptionMenu(getActivity()
					.findViewById(R.id.detail_menu_more));
			break;
		}
		return true;
	}

	protected int getFavoriteTargetId() {
		return -1;
	}

	protected int getFavoriteTargetType() {
		return -1;
	}

	protected String getShareUrl() {
		return "";
	}

	protected String getShareTitle() {
		return getString(R.string.share_title);
	}

	protected String getShareContent() {
		return "";
	}

	private void showMoreOptionMenu(View view) {
		mMenuWindow = new ListPopupWindow(getActivity());
		mMenuWindow.setModal(true);
		mMenuWindow.setContentWidth(getResources().getDimensionPixelSize(
				R.dimen.popo_menu_dialog_width));
		mMenuWindow.setAdapter(mMenuAdapter);
		mMenuWindow.setOnItemClickListener(this);
		mMenuWindow.setAnchorView(view);
		mMenuWindow.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			//handleFavoriteOrNot();
			handleShare();
		} else if (position == 1) {
			onReportMenuClick();
		} else if (position == 2) {

		}
		if (mMenuWindow != null) {
			mMenuWindow.dismiss();
			mMenuWindow = null;
		}
	}

	protected void onReportMenuClick() {
	}

	protected void handleFavoriteOrNot() {
		if(!TDevice.hasInternet()){
			AppContext.showToastShort(R.string.tip_no_internet);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		if (getFavoriteTargetId() == -1 || getFavoriteTargetType() == -1) {
			return;
		}
		int uid = AppContext.instance().getLoginUid();
		if (mIsFavorited) {//mMenuAdapter.isFavorite()
			NewsApi.delFavorite(uid, getFavoriteTargetId(),
					getFavoriteTargetType(), mDelFavoriteHandler);
		} else {
			NewsApi.addFavorite(uid, getFavoriteTargetId(),
					getFavoriteTargetType(), mAddFavoriteHandler);
		}
	}

	protected void handleShare() {
		if (TextUtils.isEmpty(getShareContent())
				|| TextUtils.isEmpty(getShareUrl())) {
			return;
		}
		final ShareDialog dialog = new ShareDialog(getActivity());
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle(R.string.share_to);
		dialog.setOnPlatformClickListener(new OnSharePlatformClick() {

			@Override
			public void onPlatformClick(SHARE_MEDIA media) {
				switch (media) {
				case QQ:
					shareToQQ(media);
					break;
				case QZONE:
					shareToQZone();
					break;
				case TENCENT:
					shareToTencentWeibo();
					break;
				case SINA:
					shareToSinaWeibo();
					break;
				case WEIXIN:
					shareToWeiChat();
					break;
				case WEIXIN_CIRCLE:
					shareToWeiChatCircle();
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.show();
	}

	private void shareToWeiChatCircle() {
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),
				Constants.WEICHAT_APPID, Constants.WEICHAT_SECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		// 设置微信朋友圈分享内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(getShareContent());
		// 设置朋友圈title
		circleMedia.setTitle(getShareTitle());
		// circleMedia.setShareImage(localImage);
		circleMedia.setTargetUrl(getShareUrl());
		mController.setShareMedia(circleMedia);
		mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE,
				new SnsPostListener() {

					@Override
					public void onStart() {
						AppContext.showToastShort(R.string.tip_start_share);
					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,
							SocializeEntity arg2) {
						AppContext.showToastShort(R.string.tip_share_done);
					}
				});
	}

	private void shareToWeiChat() {
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(),
				Constants.WEICHAT_APPID, Constants.WEICHAT_SECRET);
		wxHandler.addToSocialSDK();
		// 设置微信好友分享内容
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		// 设置分享文字
		weixinContent.setShareContent(getShareContent());
		// 设置title
		weixinContent.setTitle(getShareTitle());
		// 设置分享内容跳转URL
		weixinContent.setTargetUrl(getShareUrl());
		// 设置分享图片
		// weixinContent.setShareImage(localImage);
		mController.setShareMedia(weixinContent);
		mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN,
				new SnsPostListener() {

					@Override
					public void onStart() {
						AppContext.showToastShort(R.string.tip_start_share);
					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,
							SocializeEntity arg2) {
						AppContext.showToastShort(R.string.tip_share_done);
					}
				});
	}

	private void shareToSinaWeibo() {
		// 设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
			shareContent(SHARE_MEDIA.SINA);
		} else {
			mController.doOauthVerify(getActivity(), SHARE_MEDIA.SINA,
					new UMAuthListener() {

						@Override
						public void onStart(SHARE_MEDIA arg0) {
						}

						@Override
						public void onError(SocializeException arg0,
								SHARE_MEDIA arg1) {
						}

						@Override
						public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
							shareContent(SHARE_MEDIA.SINA);
						}

						@Override
						public void onCancel(SHARE_MEDIA arg0) {
						}
					});
		}
	}

	private void shareToTencentWeibo() {
		// 设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.TENCENT)) {
			shareContent(SHARE_MEDIA.TENCENT);
		} else {
			mController.doOauthVerify(getActivity(), SHARE_MEDIA.TENCENT,
					new UMAuthListener() {

						@Override
						public void onStart(SHARE_MEDIA arg0) {
						}

						@Override
						public void onError(SocializeException arg0,
								SHARE_MEDIA arg1) {
						}

						@Override
						public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
							shareContent(SHARE_MEDIA.TENCENT);
						}

						@Override
						public void onCancel(SHARE_MEDIA arg0) {
						}
					});
		}
	}

	private void shareContent(SHARE_MEDIA media) {
		mController.setShareContent(getShareContent() + getShareUrl());
		mController.directShare(getActivity(), media, new SnsPostListener() {

			@Override
			public void onStart() {
				AppContext.showToastShort(R.string.tip_start_share);
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1,
					SocializeEntity arg2) {
				AppContext.showToastShort(R.string.tip_share_done);
			}
		});
	}

	private void shareToQZone() {
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(),
				Constants.QQ_APPID, Constants.QQ_APPKEY);
		qZoneSsoHandler.addToSocialSDK();
		QZoneShareContent qzone = new QZoneShareContent();
		// 设置分享文字
		qzone.setShareContent(getShareContent());
		// 设置点击消息的跳转URL
		qzone.setTargetUrl(getShareUrl());
		// 设置分享内容的标题
		qzone.setTitle(getShareTitle());
		// 设置分享图片
		// qzone.setShareImage(urlImage);
		mController.setShareMedia(qzone);
		mController.postShare(getActivity(), SHARE_MEDIA.QZONE,
				new SnsPostListener() {

					@Override
					public void onStart() {
						AppContext.showToastShort(R.string.tip_start_share);
					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,
							SocializeEntity arg2) {
						AppContext.showToastShort(R.string.tip_share_done);
					}
				});
	}

	protected void shareToQQ(SHARE_MEDIA media) {
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
				Constants.QQ_APPID, Constants.QQ_APPKEY);
		qqSsoHandler.setTargetUrl(getShareUrl());
		qqSsoHandler.setTitle(getShareContent());
		qqSsoHandler.addToSocialSDK();
		mController.setShareContent(getShareContent());
		mController.postShare(getActivity(), media, new SnsPostListener() {

			@Override
			public void onStart() {
				AppContext.showToastShort(R.string.tip_start_share);
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1,
					SocializeEntity arg2) {
				AppContext.showToastShort(R.string.tip_share_done);
			}
		});
	}

	protected void notifyFavorite(boolean favorite) {
		mIsFavorited = favorite;
		getActivity().supportInvalidateOptionsMenu();
		if (mMenuAdapter != null) {
			mMenuAdapter.setFavorite(favorite);
		}
		onFavoriteChanged(favorite);
	}

	@SuppressLint("ViewHolder")
	private static class MenuAdapter extends BaseAdapter {

		private boolean isFavorite;
		private boolean hasReport;

		public MenuAdapter(boolean hasReport) {
			this.hasReport = hasReport;
		}

		public boolean isFavorite() {
			return isFavorite;
		}

		public void setFavorite(boolean favorite) {
			isFavorite = favorite;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return 2;//hasReport ? 3 : 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.v2_list_cell_popup_menu, null);
			TextView name = (TextView) convertView.findViewById(R.id.tv_name);

			int iconResId = 0;
			//if (position == 0) {
			//	name.setText(isFavorite ? R.string.detail_menu_unfavorite
			//			: R.string.detail_menu_favorite);
			//	iconResId = isFavorite ? R.drawable.actionbar_menu_icn_unfavoirite
			//			: R.drawable.actionbar_menu_icn_favoirite;
			//} else 
			if (position == 0) {
				name.setText(parent.getResources().getString(
						R.string.detail_menu_for_share));
				iconResId = R.drawable.actionbar_menu_icn_share;
			} else if (position == 1) {
				name.setText(parent.getResources().getString(
						R.string.detail_menu_for_report));
				iconResId = R.drawable.actionbar_menu_icn_report;
			}
			Drawable drawable = AppContext.resources().getDrawable(iconResId);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			name.setCompoundDrawables(drawable, null, null, null);
			return convertView;
		}
	}

	private AsyncHttpResponseHandler mAddFavoriteHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(arg2));
				if (res.OK()) {
					AppContext.showToastShort(R.string.add_favorite_success);
					mMenuAdapter.setFavorite(true);
					mMenuAdapter.notifyDataSetChanged();
					mIsFavorited = true;
					getActivity().supportInvalidateOptionsMenu();
					onFavoriteChanged(true);
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}

			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.add_favorite_faile);
		}
	};

	private AsyncHttpResponseHandler mDelFavoriteHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(arg2));
				if (res.OK()) {
					AppContext.showToastShort(R.string.del_favorite_success);
					mMenuAdapter.setFavorite(false);
					mMenuAdapter.notifyDataSetChanged();
					mIsFavorited = false;
					getActivity().supportInvalidateOptionsMenu();
					onFavoriteChanged(false);
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.del_favorite_faile);
		}
	};

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
}
