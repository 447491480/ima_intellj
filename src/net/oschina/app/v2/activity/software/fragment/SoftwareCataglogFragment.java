package net.oschina.app.v2.activity.software.fragment;

import java.io.ByteArrayInputStream;
import java.util.List;

import net.oschina.app.v2.activity.software.adapter.SoftwareAdapter;
import net.oschina.app.v2.activity.software.adapter.SoftwareCatalogAdapter;
import net.oschina.app.v2.activity.software.view.ScrollLayout;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseTabFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.SoftwareCatalogList;
import net.oschina.app.v2.model.SoftwareCatalogList.SoftwareType;
import net.oschina.app.v2.model.SoftwareList;
import net.oschina.app.v2.model.SoftwareList.Software;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonlin.osc.happy.R;

public class SoftwareCataglogFragment extends BaseTabFragment implements
		OnItemClickListener, OnRefreshListener<ListView>,
		OnLastItemVisibleListener {
	protected static final int STATE_NONE = 0;
	protected static final int STATE_REFRESH = 1;
	protected static final int STATE_LOADMORE = 2;

	private final static int SCREEN_CATALOG = 0;
	private final static int SCREEN_TAG = 1;
	private final static int SCREEN_SOFTWARE = 2;

	private ScrollLayout mScrollLayout;
	private ListView mLvCatalog, mLvTag;
	private PullToRefreshListView mLvSoftware;
	private EmptyLayout mEmptyView;
	private SoftwareCatalogAdapter mCatalogAdapter, mTagAdapter;
	private SoftwareAdapter mSoftwareAdapter;
	private int mState = STATE_NONE;
	private int curScreen = SCREEN_CATALOG;// 默认当前屏幕
	private int mCurrentTag;
	private int mCurrentPage;

	private AsyncHttpResponseHandler mCatalogHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				SoftwareCatalogList list = SoftwareCatalogList
						.parse(new ByteArrayInputStream(arg2));
				if (mState == STATE_REFRESH)
					mCatalogAdapter.clear();
				List<SoftwareType> data = list.getSoftwareTypelist();
				mCatalogAdapter.addData(data);
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
				if (data.size() == 0 && mState == STATE_REFRESH) {
					mEmptyView.setErrorType(EmptyLayout.NODATA);
				} else {
					mCatalogAdapter
							.setState(ListBaseAdapter.STATE_LESS_ONE_PAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
		}

		public void onFinish() {
			mState = STATE_NONE;
		}
	};

	private AsyncHttpResponseHandler mTagHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				SoftwareCatalogList list = SoftwareCatalogList
						.parse(new ByteArrayInputStream(arg2));
				if (mState == STATE_REFRESH)
					mTagAdapter.clear();
				List<SoftwareType> data = list.getSoftwareTypelist();
				mTagAdapter.addData(data);
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
				if (data.size() == 0 && mState == STATE_REFRESH) {
					mEmptyView.setErrorType(EmptyLayout.NODATA);
				} else {
					mTagAdapter.setState(ListBaseAdapter.STATE_LESS_ONE_PAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
		}

		public void onFinish() {
			mState = STATE_NONE;
		}
	};

	private AsyncHttpResponseHandler mSoftwareHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBytes) {
			try {
				SoftwareList list = SoftwareList
						.parse(new ByteArrayInputStream(responseBytes));
				if (mState == STATE_REFRESH)
					mSoftwareAdapter.clear();
				List<Software> data = list.getSoftwarelist();
				mSoftwareAdapter.addData(data);
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
				if (data.size() == 0 && mState == STATE_REFRESH) {
					mEmptyView.setErrorType(EmptyLayout.NODATA);
				} else if (data.size() < TDevice.getPageSize()) {
					if (mState == STATE_REFRESH)
						mSoftwareAdapter
								.setState(ListBaseAdapter.STATE_NO_MORE);
					else
						mSoftwareAdapter
								.setState(ListBaseAdapter.STATE_NO_MORE);
				} else {
					mSoftwareAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
				}
				// else {
				// mAdapter.setState(ListBaseAdapter.STATE_LESS_ONE_PAGE);
				// }
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(statusCode, headers, responseBytes, null);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
		}

		public void onFinish() {
			mState = STATE_NONE;
			mLvSoftware.onRefreshComplete();
		}
	};

	private OnItemClickListener mCatalogOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SoftwareType type = (SoftwareType) mCatalogAdapter
					.getItem(position);
			if (type != null && type.tag > 0) {
				// 加载二级分类
				curScreen = SCREEN_TAG;
				mScrollLayout.scrollToScreen(curScreen);
				mCurrentTag = type.tag;
				sendRequestCatalogData(mTagHandler);
			}
		}
	};

	private OnItemClickListener mTagOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SoftwareType type = (SoftwareType) mCatalogAdapter
					.getItem(position);
			if (type != null &&type.tag > 0) {
				// 加载二级分类
				curScreen = SCREEN_SOFTWARE;
				mScrollLayout.scrollToScreen(curScreen);
				mCurrentTag = type.tag;

				mState = STATE_REFRESH;
				mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
				sendRequestTagData();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_software, container,
				false);
		initViews(view);
		return view;
	}

	private void initViews(View view) {
		mScrollLayout = (ScrollLayout) view.findViewById(R.id.scrolllayout);
		mScrollLayout.setIsScroll(false);

		mEmptyView = (EmptyLayout) view.findViewById(R.id.error_layout);
		mLvCatalog = (ListView) view.findViewById(R.id.lv_catalog);
		mLvCatalog.setOnItemClickListener(mCatalogOnItemClick);
		mLvTag = (ListView) view.findViewById(R.id.lv_tag);
		mLvTag.setOnItemClickListener(mTagOnItemClick);
		mLvSoftware = (PullToRefreshListView) view
				.findViewById(R.id.lv_software);
		mLvSoftware.setOnRefreshListener(this);
		mLvSoftware.setOnLastItemVisibleListener(this);
		mLvSoftware.setOnItemClickListener(this);
		if (mCatalogAdapter == null) {
			mCatalogAdapter = new SoftwareCatalogAdapter();
			sendRequestCatalogData(mCatalogHandler);
		}
		mLvCatalog.setAdapter(mCatalogAdapter);

		if (mTagAdapter == null) {
			mTagAdapter = new SoftwareCatalogAdapter();
		}
		mLvTag.setAdapter(mTagAdapter);

		if (mSoftwareAdapter == null) {
			mSoftwareAdapter = new SoftwareAdapter();
		}
		mLvSoftware.setAdapter(mSoftwareAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Software software = (Software) mSoftwareAdapter.getItem(position - 1);
		if (software != null)
			UIHelper.showUrlRedirect(view.getContext(), software.url);
	}

	@Override
	public boolean onBackPressed() {
		switch (curScreen) {
		case SCREEN_SOFTWARE:
			// mTitle.setText(curTitleLV1);
			curScreen = SCREEN_TAG;
			mScrollLayout.scrollToScreen(SCREEN_TAG);
			return true;
		case SCREEN_TAG:
			// mTitle.setText("开源软件库");
			curScreen = SCREEN_CATALOG;
			mScrollLayout.scrollToScreen(SCREEN_CATALOG);
			return true;
		case SCREEN_CATALOG:
			return false;
		}
		return super.onBackPressed();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		mCurrentPage = 0;
		mState = STATE_REFRESH;
		sendRequestTagData();
	}

	@Override
	public void onLastItemVisible() {
		if (mState == STATE_NONE) {
			if (mSoftwareAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
				mCurrentPage++;
				mState = STATE_LOADMORE;
				sendRequestTagData();
			}
		}
	}

	private void sendRequestCatalogData(AsyncHttpResponseHandler handler) {
		mState = STATE_REFRESH;
		mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getSoftwareCatalogList(mCurrentTag, handler);
	}

	private void sendRequestTagData() {
		NewsApi.getSoftwareTagList(mCurrentTag, mCurrentPage, mSoftwareHandler);
	}
}
