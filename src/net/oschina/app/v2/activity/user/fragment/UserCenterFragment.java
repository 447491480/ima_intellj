package net.oschina.app.v2.activity.user.fragment;

import java.io.ByteArrayInputStream;
import java.util.List;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.user.adapter.UserCenterAdapter;
import net.oschina.app.v2.activity.user.adapter.UserCenterAdapter.DislayModeChangeListener;
import net.oschina.app.v2.activity.user.adapter.UserCenterAdapter.DisplayMode;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.base.ListBaseAdapter;
import net.oschina.app.v2.model.Active;
import net.oschina.app.v2.model.Blog;
import net.oschina.app.v2.model.BlogList;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.User;
import net.oschina.app.v2.model.UserInformation;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.AvatarUtils;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class UserCenterFragment extends BaseFragment implements
		DislayModeChangeListener, OnItemClickListener {

	private static final Object FEMALE = "女";
	private static final String USER_CENTER_SCREEN = "user_center_screen";
	private StickyListHeadersListView mListView;
	private EmptyLayout mEmptyView;
	private ImageView mIvAvatar;
	private TextView mTvName, mTvFollowing, mTvFollower, mBtnPrivateMsg,
			mBtnFollowUser, mTvLastestLoginTime;
	private UserCenterAdapter mAdapter;
	private int mHisUid;
	private String mHisName;
	private int mUid;
	private int mActivePage = 0, mBlogPage = 0;
	private User mUser;
	private boolean hasLoadActive, hasLoadBlog;
	private int mBlogState = STATE_NONE;

	private AsyncHttpResponseHandler mActiveHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				UserInformation information = UserInformation
						.parse(new ByteArrayInputStream(arg2));
				mUser = information.getUser();
				fillUI();
				List<Active> data = information.getActivelist();
				if (mState == STATE_REFRESH)
					mAdapter.clear(DisplayMode.ACTIVE);
				mAdapter.addData(DisplayMode.ACTIVE, data);
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
				if (data.size() == 0 && mState == STATE_REFRESH) {
					// mEmptyView.setErrorType(EmptyLayout.NODATA);
					mAdapter.setState(DisplayMode.ACTIVE,
							ListBaseAdapter.STATE_NO_MORE);
				} else if (data.size() < TDevice.getPageSize()) {
					if (mState == STATE_REFRESH)
						mAdapter.setState(DisplayMode.ACTIVE,
								ListBaseAdapter.STATE_NO_MORE);
					else
						mAdapter.setState(DisplayMode.ACTIVE,
								ListBaseAdapter.STATE_NO_MORE);
				} else {
					mAdapter.setState(DisplayMode.ACTIVE,
							ListBaseAdapter.STATE_LOAD_MORE);
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

		@Override
		public void onFinish() {
			mState = STATE_NONE;
		}
	};

	private AsyncHttpResponseHandler mBlogHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				BlogList list = BlogList.parse(new ByteArrayInputStream(arg2));
				List<Blog> data = list.getBloglist();
				if (mBlogState == STATE_REFRESH)
					mAdapter.clear(DisplayMode.BLOG);
				mAdapter.addData(DisplayMode.BLOG, data);
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
				if (data.size() == 0 && mBlogState == STATE_REFRESH) {
					// mEmptyView.setErrorType(EmptyLayout.NODATA);
					mAdapter.setState(DisplayMode.BLOG,
							ListBaseAdapter.STATE_NO_MORE);
				} else if (data.size() < TDevice.getPageSize()) {
					if (mBlogState == STATE_REFRESH)
						mAdapter.setState(DisplayMode.BLOG,
								ListBaseAdapter.STATE_NO_MORE);
					else
						mAdapter.setState(DisplayMode.BLOG,
								ListBaseAdapter.STATE_NO_MORE);
				} else {
					mAdapter.setState(DisplayMode.BLOG,
							ListBaseAdapter.STATE_LOAD_MORE);
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

		@Override
		public void onFinish() {
			mBlogState = STATE_NONE;
		}
	};

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
				switch (mAdapter.getDisplayMode()) {
				case ACTIVE:
					if (mState == STATE_NONE
							&& mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
						mState = STATE_LOADMORE;
						mActivePage++;
						sendGetUserInfomation();
					}
					break;
				case BLOG:
					if (mBlogState == STATE_NONE
							&& mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
						mBlogState = STATE_LOADMORE;
						mBlogPage++;
						sendGetUserBlog();
					}
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_user_center,
				container, false);

		Bundle args = getArguments();

		mHisUid = args.getInt("his_id", 0);
		mHisName = args.getString("his_name");
		mUid = AppContext.instance().getLoginUid();

		initViews(view);

		return view;
	}

	@SuppressLint("InflateParams")
	private void initViews(View view) {
		mEmptyView = (EmptyLayout) view.findViewById(R.id.error_layout);
		mListView = (StickyListHeadersListView) view
				.findViewById(R.id.listview);
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		View header = LayoutInflater.from(getActivity()).inflate(
				R.layout.v2_list_header_user_center, null);
		mIvAvatar = (ImageView) header.findViewById(R.id.iv_avatar);
		mTvName = (TextView) header.findViewById(R.id.tv_name);
		mTvFollowing = (TextView) header.findViewById(R.id.tv_following_count);
		mTvFollower = (TextView) header.findViewById(R.id.tv_follower_count);
		mTvLastestLoginTime = (TextView) header
				.findViewById(R.id.tv_latest_login_time);

		mBtnPrivateMsg = (TextView) header
				.findViewById(R.id.tv_private_message);
		mBtnPrivateMsg.setOnClickListener(this);
		mBtnFollowUser = (TextView) header.findViewById(R.id.tv_follow_user);
		mBtnFollowUser.setOnClickListener(this);

		mListView.addHeaderView(header);

		if (mAdapter == null) {
			mAdapter = new UserCenterAdapter(this);

			mState = STATE_REFRESH;
			mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
			sendGetUserInfomation();
		}
		mListView.setAdapter(mAdapter);
	}

	private void fillUI() {
		ImageLoader.getInstance().displayImage(
				AvatarUtils.getLargeAvatar(mUser.getFace()), mIvAvatar);
		mTvName.setText(mUser.getName());

		int genderIcon = R.drawable.userinfo_icon_male;
		if (FEMALE.equals(mUser.getGender())) {
			genderIcon = R.drawable.userinfo_icon_female;
		}
		Drawable drawable = AppContext.resources().getDrawable(genderIcon);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		mTvName.setCompoundDrawables(null, null, drawable, null);

		mTvFollowing.setText(getString(R.string.following_count,
				mUser.getFollowers()));
		mTvFollower
				.setText(getString(R.string.follower_count, mUser.getFans()));
		mTvLastestLoginTime.setText(getString(R.string.latest_login_time,
				StringUtils.friendly_time(mUser.getLatestonline())));
		updateUserRelation();

		mAdapter.setUserInformation(mUser);
	}

	private void updateUserRelation() {
		switch (mUser.getRelation()) {
		case User.RELATION_TYPE_BOTH:
			mBtnFollowUser.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_follow_each_other, 0, 0, 0);
			mBtnFollowUser.setText(R.string.follow_each_other);
			mBtnFollowUser.setTextColor(getResources().getColor(R.color.black));
			mBtnFollowUser
					.setBackgroundResource(R.drawable.btn_small_white_selector);
			break;
		case User.RELATION_TYPE_FANS_HIM:
			mBtnFollowUser.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_followed, 0, 0, 0);
			mBtnFollowUser.setText(R.string.unfollow_user);
			mBtnFollowUser.setTextColor(getResources().getColor(R.color.black));
			mBtnFollowUser
					.setBackgroundResource(R.drawable.btn_small_white_selector);
			break;
		case User.RELATION_TYPE_FANS_ME:
			mBtnFollowUser.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_add_follow, 0, 0, 0);
			mBtnFollowUser.setText(R.string.follow_user);
			mBtnFollowUser.setTextColor(getResources().getColor(R.color.white));
			mBtnFollowUser
					.setBackgroundResource(R.drawable.btn_small_green_selector);
			break;
		case User.RELATION_TYPE_NULL:
			mBtnFollowUser.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_add_follow, 0, 0, 0);
			mBtnFollowUser.setText(R.string.follow_user);
			mBtnFollowUser.setTextColor(getResources().getColor(R.color.white));
			mBtnFollowUser
					.setBackgroundResource(R.drawable.btn_small_green_selector);
			break;
		}
		int padding = (int) TDevice.dpToPixel(20);
		mBtnFollowUser.setPadding(padding, 0, padding, 0);
	}

	@Override
	public void onDisplayModeChanged(DisplayMode mode) {
		switch (mode) {
		case ACTIVE:
			if (!hasLoadActive) {
				mState = STATE_REFRESH;
				mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
				sendGetUserInfomation();
			}
			break;
		case BLOG:
			if (!hasLoadBlog) {
				mBlogState = STATE_REFRESH;
				mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
				sendGetUserBlog();
			}
			break;
		case INFOMATION:
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.tv_follow_user) {
			handleUserRelation();
		} else if (id == R.id.tv_private_message) {
			UIHelper.showMessagePub(getActivity(), mHisUid, mHisName);
		}
	}

	private void handleUserRelation() {
		if (mUser == null)
			return;
		// 判断登录
		final AppContext ac = AppContext.instance();
		if (!ac.isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		String dialogTitle = "";
		int relationAction = 0;
		switch (mUser.getRelation()) {
		case User.RELATION_TYPE_BOTH:
			dialogTitle = "确定取消互粉吗？";
			relationAction = User.RELATION_ACTION_DELETE;
			break;
		case User.RELATION_TYPE_FANS_HIM:
			dialogTitle = "确定取消关注吗？";
			relationAction = User.RELATION_ACTION_DELETE;
			break;
		case User.RELATION_TYPE_FANS_ME:
			dialogTitle = "确定关注他吗？";
			relationAction = User.RELATION_ACTION_ADD;
			break;
		case User.RELATION_TYPE_NULL:
			dialogTitle = "确定关注他吗？";
			relationAction = User.RELATION_ACTION_ADD;
			break;
		}
		final int ra = relationAction;
		CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		// dialog.setTitle(R.string.app_name);
		dialog.setMessage(dialogTitle);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						sendUpdateRelcationRequest(ra);
						dialog.dismiss();
					}
				});
		dialog.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	private void sendUpdateRelcationRequest(int ra) {
		NewsApi.updateRelation(mUid, mHisUid, ra,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							Result result = Result
									.parse(new ByteArrayInputStream(arg2));
							if (result.OK()) {
								switch (mUser.getRelation()) {
								case User.RELATION_TYPE_BOTH:
									mBtnFollowUser
											.setCompoundDrawablesWithIntrinsicBounds(
													R.drawable.ic_add_follow,
													0, 0, 0);
									mBtnFollowUser
											.setText(R.string.follow_user);
									mBtnFollowUser.setTextColor(getResources()
											.getColor(R.color.white));
									mBtnFollowUser
											.setBackgroundResource(R.drawable.btn_small_green_selector);
									mUser.setRelation(User.RELATION_TYPE_FANS_ME);
									break;
								case User.RELATION_TYPE_FANS_HIM:
									mBtnFollowUser
											.setCompoundDrawablesWithIntrinsicBounds(
													R.drawable.ic_add_follow,
													0, 0, 0);
									mBtnFollowUser
											.setText(R.string.follow_user);
									mBtnFollowUser.setTextColor(getResources()
											.getColor(R.color.white));
									mBtnFollowUser
											.setBackgroundResource(R.drawable.btn_small_green_selector);
									mUser.setRelation(User.RELATION_TYPE_NULL);
									break;
								case User.RELATION_TYPE_FANS_ME:
									mBtnFollowUser
											.setCompoundDrawablesWithIntrinsicBounds(
													R.drawable.ic_followed, 0,
													0, 0);
									mBtnFollowUser
											.setText(R.string.follow_each_other);
									mBtnFollowUser.setTextColor(getResources()
											.getColor(R.color.black));
									mBtnFollowUser
											.setBackgroundResource(R.drawable.btn_small_white_selector);
									mUser.setRelation(User.RELATION_TYPE_BOTH);
									break;
								case User.RELATION_TYPE_NULL:
									mBtnFollowUser
											.setCompoundDrawablesWithIntrinsicBounds(
													R.drawable.ic_followed, 0,
													0, 0);
									mBtnFollowUser
											.setText(R.string.unfollow_user);
									mBtnFollowUser.setTextColor(getResources()
											.getColor(R.color.black));
									mBtnFollowUser
											.setBackgroundResource(R.drawable.btn_small_white_selector);
									mUser.setRelation(User.RELATION_TYPE_FANS_HIM);
									break;
								}
								int padding = (int) TDevice.dpToPixel(20);
								mBtnFollowUser.setPadding(padding, 0, padding,
										0);
							}
							AppContext.showToastShort(result.getErrorMessage());
						} catch (Exception e) {
							e.printStackTrace();
							onFailure(arg0, arg1, arg2, e);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
					}
				});
	}

	private void sendGetUserInfomation() {
		hasLoadActive = true;
		NewsApi.getUserInformation(mUid, mHisUid, mHisName, mActivePage,
				mActiveHandler);
	}

	private void sendGetUserBlog() {
		hasLoadBlog = true;
		NewsApi.getUserBlogList(mHisUid, mHisName, mUid, mBlogPage,
				mBlogHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DisplayMode mode = mAdapter.getDisplayMode();
		switch (mode) {
		case ACTIVE:
			Active active = (Active) mAdapter.getItem(position - 1);
			if (active != null) {
				UIHelper.showActiveRedirect(view.getContext(), active);
			}
			break;
		case BLOG:
			Blog blog = (Blog) mAdapter.getItem(position - 1);
			if (blog != null) {
				UIHelper.showUrlRedirect(view.getContext(), blog.getUrl());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(USER_CENTER_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(USER_CENTER_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
