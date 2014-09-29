package net.oschina.app.v2.activity.tweet.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.tweet.adapter.TweetAdapter;
import net.oschina.app.v2.api.OperationResponseHandler;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseListFragment;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.Tweet;
import net.oschina.app.v2.model.TweetList;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.HTMLSpirit;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.tonlin.osc.happy.R;

/**
 * 动弹
 * 
 * @author william_sim
 */
public class TweetFragment extends BaseListFragment implements
		OnItemLongClickListener {
	protected static final String TAG = TweetFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "tweet_list";
	private boolean mIsWatingLogin;

	class DeleteTweetResponseHandler extends OperationResponseHandler {

		DeleteTweetResponseHandler(Object... args) {
			super(args);
		}

		@Override
		public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
				throws Exception {
			try {
				Result res = Result.parse(is);
				if (res != null && res.OK()) {
					AppContext.showToastShort(R.string.tip_del_tweet_success);
					Tweet tweet = (Tweet) args[0];
					mAdapter.removeItem(tweet);
				} else {
					onFailure(code, res.getErrorMessage(), args);
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(code, e.getMessage(), args);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.tip_del_tweet_faile);
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mErrorLayout != null) {
				mIsWatingLogin = true;
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		if (mIsWatingLogin) {
			mCurrentPage = 0;
			mState = STATE_REFRESH;
			requestData(false);
		}
		super.onResume();
	}

	@Override
	protected void initViews(View view) {
		super.initViews(view);
		mListView.getRefreshableView().setOnItemLongClickListener(this);
		mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppContext.instance().isLogin()) {
					requestData(false);
				} else {
					UIHelper.showLogin(getActivity());
				}
			}
		});
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new TweetAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		TweetList list = TweetList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((TweetList) seri);
	}

	@Override
	protected void requestData(boolean refresh) {
		mErrorLayout.setErrorMessage("");
		if (mCatalog > 0) {
			if (AppContext.instance().isLogin()) {
				mCatalog = AppContext.instance().getLoginUid();
				mIsWatingLogin = false;
				super.requestData(refresh);
			} else {
				mIsWatingLogin = true;
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
			}
		} else {
			mIsWatingLogin = false;
			super.requestData(refresh);
		}
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getTweetList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tweet tweet = (Tweet) mAdapter.getItem(position - 1);
		if (tweet != null)
			UIHelper.showTweetDetail(view.getContext(), tweet.getId());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Tweet tweet = (Tweet) mAdapter.getItem(position - 1);
		if (tweet != null) {
			handleLongClick(tweet);
			return true;
		}
		return false;
	}

	private void handleLongClick(final Tweet tweet) {
		String[] items = null;
		if (AppContext.instance().getLoginUid() == tweet.getAuthorId()) {
			items = new String[] { getResources().getString(R.string.copy),
					getResources().getString(R.string.delete) };
		} else {
			items = new String[] { getResources().getString(R.string.copy) };
		}
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setItemsWithoutChk(items, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialog.dismiss();
				if (position == 0) {
					TDevice.copyTextToBoard(HTMLSpirit.delHTMLTag(tweet
							.getBody()));
				} else if (position == 1) {
					handleDeleteTweet(tweet);
				}
			}
		});
		dialog.show();
	}

	private void handleDeleteTweet(final Tweet tweet) {
		CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setMessage(R.string.message_delete_tweet);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						NewsApi.deleteTweet(tweet.getAuthorId(), tweet.getId(),
								new DeleteTweetResponseHandler(tweet));
					}
				});
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.show();
	}
}
