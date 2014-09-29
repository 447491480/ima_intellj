package net.oschina.app.v2.activity;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.model.User;
import net.oschina.app.v2.service.NoticeUtils;
import net.oschina.app.v2.ui.BadgeView;
import net.oschina.app.v2.utils.TLog;
import net.oschina.app.v2.utils.UIHelper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.internal.widget.ListPopupWindow;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 应用主界面
 * 
 * @author tonlin
 * @since 2014/08
 */
public class MainActivity extends BaseActivity implements OnTabChangeListener,
		OnItemClickListener {

	private static final String MAIN_SCREEN = "MainScreen";
	private FragmentTabHost mTabHost;
	private MenuAdapter mMenuAdapter;
	private ListPopupWindow mMenuWindow;

	// private Version mVersion;
	private BadgeView mBvTweet;

	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int atmeCount = intent.getIntExtra("atmeCount", 0);// @我
			int msgCount = intent.getIntExtra("msgCount", 0);// 留言
			int reviewCount = intent.getIntExtra("reviewCount", 0);// 评论
			int newFansCount = intent.getIntExtra("newFansCount", 0);// 新粉丝
			int activeCount = atmeCount + reviewCount + msgCount;// +
																	// newFansCount;//
																	// 信息总数

			TLog.log("@me:" + atmeCount + " msg:" + msgCount + " review:"
					+ reviewCount + " newFans:" + newFansCount + " active:"
					+ activeCount);

			if (activeCount > 0) {
				mBvTweet.setText(activeCount + "");
				mBvTweet.show();
			} else {
				mBvTweet.hide();
			}
		}
	};

	@Override
	protected int getLayoutId() {
		return R.layout.v2_activity_main;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (mVersion != null) {
		// UmengUpdateAgent.showUpdateDialog(getApplicationContext(),
		// mVersion.toVersion());
		// mVersion = null;
		// }

		if (mMenuAdapter != null) {
			mMenuAdapter.notifyDataSetChanged();
		}
		MobclickAgent.onPageStart(MAIN_SCREEN);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(MAIN_SCREEN);
		MobclickAgent.onPause(this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);

		// Intent intent = getIntent();
		// if (intent != null) {
		// mVersion = intent.getParcelableExtra(Version.BUNDLE_KEY_VERSION);
		// }

		AppContext.instance().initLoginInfo();

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			mTabHost.getTabWidget().setShowDividers(0);
		}

		initTabs();

		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(this);
		
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
		registerReceiver(mNoticeReceiver, filter);

		NoticeUtils.bindToService(this);
		UIHelper.sendBroadcastForNotice(this);
	}

	@Override
	protected void onDestroy() {
		NoticeUtils.unbindFromService(this);
		unregisterReceiver(mNoticeReceiver);
		NoticeUtils.tryToShutDown(this);
		super.onDestroy();
	}

	private void initTabs() {
		MainTab[] tabs = MainTab.values();
		final int size = tabs.length;
		for (int i = 0; i < size; i++) {
			MainTab mainTab = tabs[i];
			TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));

			View indicator = inflateView(R.layout.v2_tab_indicator);
			ImageView icon = (ImageView) indicator.findViewById(R.id.tab_icon);
			icon.setImageResource(mainTab.getResIcon());
			TextView title = (TextView) indicator.findViewById(R.id.tab_titile);
			title.setText(getString(mainTab.getResName()));
			tab.setIndicator(indicator);
			tab.setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					return new View(MainActivity.this);
				}
			});

			mTabHost.addTab(tab, mainTab.getClz(), null);
			if (mainTab.equals(MainTab.ME)) {
				View con = indicator.findViewById(R.id.container);
				// con.setBackgroundColor(Color.parseColor("#00ff00"));
				mBvTweet = new BadgeView(this, con);
				mBvTweet.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				mBvTweet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				mBvTweet.setBackgroundResource(R.drawable.tab_notification_bg);
			}
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		final int size = mTabHost.getTabWidget().getTabCount();
		for (int i = 0; i < size; i++) {
			View v = mTabHost.getTabWidget().getChildAt(i);
			if (i == mTabHost.getCurrentTab()) {
				v.findViewById(R.id.tab_icon).setSelected(true);
				v.findViewById(R.id.tab_titile).setSelected(true);
			} else {
				v.findViewById(R.id.tab_icon).setSelected(false);
				v.findViewById(R.id.tab_titile).setSelected(false);
			}
		}
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		boolean visible = false;
		int tab = mTabHost.getCurrentTab();
		if (tab == 1 || tab == 2) {
			visible = true;
		}
		menu.findItem(R.id.main_menu_post).setVisible(visible);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_post:
			if (mTabHost.getCurrentTab() == 1) {
				UIHelper.showQuestionPub(this);
			} else if (mTabHost.getCurrentTab() == 2) {
				UIHelper.showTweetPub(this);
			}
			break;
		case R.id.main_menu_search:
			UIHelper.showSearch(this);
			break;
		case R.id.main_menu_today:
			UIHelper.showDailyEnglish(this);
			break;
		case R.id.main_menu_more:
			showMoreOptionMenu(findViewById(R.id.main_menu_more));
			break;
		}
		return true;
	}

	private long mLastExitTime;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mLastExitTime < 2000) {
			super.onBackPressed();
		} else {
			mLastExitTime = System.currentTimeMillis();
			AppContext.showToastShort(R.string.tip_click_back_again_to_exist);
		}
	}

	private void showMoreOptionMenu(View view) {
		if (mMenuWindow != null) {
			mMenuWindow.dismiss();
			mMenuWindow = null;
		}
		mMenuWindow = new ListPopupWindow(this);
		if (mMenuAdapter == null) {
			mMenuAdapter = new MenuAdapter();
		}
		mMenuWindow.setModal(true);
		mMenuWindow.setContentWidth(getResources().getDimensionPixelSize(
				R.dimen.popo_menu_dialog_width));
		mMenuWindow.setAdapter(mMenuAdapter);
		mMenuWindow.setOnItemClickListener(this);
		mMenuWindow.setAnchorView(view);
		mMenuWindow.show();
		mMenuWindow.getListView().setDividerHeight(1);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			if (AppContext.instance().isLogin()) {
				UIHelper.showUserInfo(this);
			} else {
				UIHelper.showLogin(this);
			}
			break;
		case 1:
			UIHelper.showSoftware(this);
			break;
		case 2:
			UIHelper.showSetting(this);
			break;
		case 3:
			UIHelper.exitApp(this);
			break;
		default:
			break;
		}
		if (mMenuWindow != null) {
			mMenuWindow.dismiss();
			mMenuWindow = null;
		}
	}

	private static class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
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
			if (position == 0) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.v2_list_cell_popup_menu_userinfo, null);
				TextView name = (TextView) convertView
						.findViewById(R.id.tv_name);
				ImageView avatar = (ImageView) convertView
						.findViewById(R.id.iv_avatar);
				AppContext.instance().initLoginInfo();
				if (AppContext.instance().isLogin()) {
					User user = AppContext.instance().getLoginInfo();
					name.setText(user.getName());
					ImageLoader.getInstance().displayImage(user.getFace(),
							avatar);
				} else {
					name.setText(R.string.unlogin);
					avatar.setImageBitmap(null);
				}
			} else {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.v2_list_cell_popup_menu, null);
				TextView name = (TextView) convertView
						.findViewById(R.id.tv_name);
				int iconResId = 0;

				if (position == 1) {
					name.setText(R.string.main_menu_software);
					iconResId = R.drawable.actionbar_menu_icn_software;
				} else if (position == 2) {
					name.setText(R.string.main_menu_setting);
					iconResId = R.drawable.actionbar_menu_icn_set;
				} else if (position == 3) {
					name.setText(R.string.main_menu_exit);
					iconResId = R.drawable.actionbar_menu_icn_exit;
				}
				Drawable drawable = AppContext.resources().getDrawable(
						iconResId);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				name.setCompoundDrawables(drawable, null, null, null);
			}
			return convertView;
		}
	}
}
