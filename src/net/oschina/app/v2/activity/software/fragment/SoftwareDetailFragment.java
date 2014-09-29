package net.oschina.app.v2.activity.software.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.activity.news.fragment.BaseDetailFragment;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.v2.activity.news.fragment.ToolbarFragmentControl;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.model.Entity;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.Software;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.UIHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 软件详情
 * 
 * @author william_sim
 * @since 2014/09/02
 */
public class SoftwareDetailFragment extends BaseDetailFragment implements
		ToolbarFragmentControl {

	protected static final String TAG = SoftwareDetailFragment.class
			.getSimpleName();
	private static final String SOFTWARE_CACHE_KEY = "software_";
	private static final String SOFTWARE_DETAIL_SCREEN = "software_detail_screen";
	private TextView mTvLicense, mTvLanguage, mTvOs, mTvRecordTime;
	private TextView mTvTitle;
	private WebView mWebView;
	private ImageView mIvLogo;
	private String mIdent;
	private Software mSoftware;
	private ToolbarFragment mToolBarFragment;

	private OnActionClickListener mActionListener = new OnActionClickListener() {

		@Override
		public void onActionClick(ToolAction action) {
			switch (action) {
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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_software_detail,
				container, false);

		mIdent = getActivity().getIntent().getStringExtra("ident");

		initViews(view);

		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		// mNewsContainer = (ScrollView)
		// view.findViewById(R.id.sv_news_container);
		mTvTitle = (TextView) view.findViewById(R.id.tv_title);

		mWebView = (WebView) view.findViewById(R.id.webview);
		initWebView(mWebView);

		mTvLicense = (TextView) view.findViewById(R.id.tv_software_license);
		mTvLanguage = (TextView) view.findViewById(R.id.tv_software_language);
		mTvOs = (TextView) view.findViewById(R.id.tv_software_os);
		mTvRecordTime = (TextView) view
				.findViewById(R.id.tv_software_recordtime);
		mIvLogo = (ImageView) view.findViewById(R.id.iv_logo);

		view.findViewById(R.id.btn_software_index).setOnClickListener(this);
		view.findViewById(R.id.btn_software_download).setOnClickListener(this);
		view.findViewById(R.id.btn_software_document).setOnClickListener(this);
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(SOFTWARE_CACHE_KEY).append(mIdent).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getSoftwareDetail(mIdent, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return Software.parse(is);
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Software) seri;
	}

	@Override
	protected boolean shouldRegisterCommentChangedReceiver() {
		// software has no comment so we do not need it
		return false;
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mSoftware = (Software) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mSoftware.getTitle());
		mTvLicense.setText(mSoftware.getLicense());
		mTvLanguage.setText(mSoftware.getLanguage());
		mTvOs.setText(mSoftware.getOs());
		mTvRecordTime.setText(mSoftware.getRecordtime());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
		ImageLoader.getInstance().displayImage(mSoftware.getLogo(), mIvLogo,
				options);

		notifyFavorite(mSoftware.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		String body = UIHelper.WEB_STYLE + mSoftware.getBody();
		body = UIHelper.setHtmlCotentSupportImagePreview(body);
		body += UIHelper.WEB_LOAD_IMAGES;
		mWebView.setWebViewClient(mWebClient);// UIHelper.getWebViewClient()
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_software_index) {
			UIHelper.openBrowser(v.getContext(), mSoftware.getHomepage());
		} else if (id == R.id.btn_software_download) {
			UIHelper.openBrowser(v.getContext(), mSoftware.getDownload());
		} else if (id == R.id.btn_software_document) {
			UIHelper.openBrowser(v.getContext(), mSoftware.getDocument());
		}
	}

	@Override
	public void setToolBarFragment(ToolbarFragment fragment) {
		mToolBarFragment = fragment;
		mToolBarFragment.setOnActionClickListener(mActionListener);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_CHANGE, false);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_FAVORITE, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_SHARE, true);
	}

	@Override
	protected void onFavoriteChanged(boolean flag) {
		mSoftware.setFavorite(flag ? 1 : 0);
		if (mToolBarFragment != null) {
			mToolBarFragment.setFavorite(flag);
		}
	}

	@Override
	protected int getFavoriteTargetId() {
		return mSoftware != null ? mSoftware.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mSoftware != null ? FavoriteList.TYPE_SOFTWARE : -1;
	}

	@Override
	protected String getShareContent() {
		return mSoftware != null ? mSoftware.getTitle() : "";
	}

	@Override
	protected String getShareUrl() {
		return mSoftware != null ? mSoftware.getUrl() : "";
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SOFTWARE_DETAIL_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SOFTWARE_DETAIL_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
