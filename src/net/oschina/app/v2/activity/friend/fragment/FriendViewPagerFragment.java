package net.oschina.app.v2.activity.friend.fragment;

import net.oschina.app.v2.activity.friend.adapter.FriendTabPagerAdapter;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.ui.BadgeView;
import net.oschina.app.v2.ui.pagertab.PagerSlidingTabStrip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonlin.osc.happy.R;

public class FriendViewPagerFragment extends Fragment implements
		OnPageChangeListener {

	public static final String BUNDLE_KEY_TABIDX = "BUNDLE_KEY_TABIDX";

	private PagerSlidingTabStrip mTabStrip;
	private ViewPager mViewPager;
	private FriendTabPagerAdapter mTabAdapter;

	private int mInitTabIdx;
	private BadgeView mBvNewFans;

	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int newFansCount = intent.getIntExtra("newFansCount", 0);// 新粉丝
			if (newFansCount > 0) {
				mBvNewFans.setText(newFansCount + "");
				mBvNewFans.show();
			} else {
				mBvNewFans.hide();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mInitTabIdx = args.getInt(BUNDLE_KEY_TABIDX, 0);

		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
		getActivity().registerReceiver(mNoticeReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mNoticeReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_viewpager, container,
				false);
		mTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		mViewPager = (ViewPager) view.findViewById(R.id.main_tab_pager);

		if (mTabAdapter == null) {
			mTabAdapter = new FriendTabPagerAdapter(getChildFragmentManager(),
					getActivity(), mViewPager);
		}
		mViewPager.setOffscreenPageLimit(mTabAdapter.getCacheCount());
		mViewPager.setAdapter(mTabAdapter);
		mViewPager.setOnPageChangeListener(this);
		mTabStrip.setViewPager(mViewPager);

		mViewPager.setCurrentItem(mInitTabIdx);

		mBvNewFans = new BadgeView(getActivity(), mTabStrip.getBadgeView(1));
		mBvNewFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvNewFans.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mBvNewFans.setBackgroundResource(R.drawable.tab_notification_bg);
		return view;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		mTabStrip.onPageScrollStateChanged(arg0);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		mTabStrip.onPageScrolled(arg0, arg1, arg2);
		mTabAdapter.onPageScrolled(arg0);
	}

	@Override
	public void onPageSelected(int arg0) {
		mTabStrip.onPageSelected(arg0);
		mTabAdapter.onPageSelected(arg0);
	}
}