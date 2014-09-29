package net.oschina.app.v2.activity.active.fragment;

import net.oschina.app.v2.activity.active.adapter.ActiveTabPagerAdapter;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.ui.BadgeView;
import net.oschina.app.v2.ui.pagertab.PagerSlidingTabStrip;
import net.oschina.app.v2.utils.TLog;
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

public class ActiveViewPagerFragment extends Fragment implements
		OnPageChangeListener {

	private PagerSlidingTabStrip mTabStrip;
	private ViewPager mViewPager;
	private ActiveTabPagerAdapter mTabAdapter;
	private BadgeView mBvAtMe,mBvComment,mBvMsg;

	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int atmeCount = intent.getIntExtra("atmeCount", 0);// @我
			int msgCount = intent.getIntExtra("msgCount", 0);// 留言
			int reviewCount = intent.getIntExtra("reviewCount", 0);// 评论
			int newFansCount = intent.getIntExtra("newFansCount", 0);// 新粉丝
			int activeCount = atmeCount + reviewCount + msgCount + newFansCount;// 信息总数

			TLog.log("@me:" + atmeCount + " msg:" + msgCount + " review:"
					+ reviewCount + " newFans:" + newFansCount + " active:"
					+ activeCount);

			if (atmeCount > 0) {
				mBvAtMe.setText(atmeCount + "");
				mBvAtMe.show();
			} else {
				mBvAtMe.hide();
			}
			
			if (reviewCount > 0) {
				mBvComment.setText(reviewCount + "");
				mBvComment.show();
			} else {
				mBvComment.hide();
			}
			
			if (msgCount > 0) {
				mBvMsg.setText(msgCount + "");
				mBvMsg.show();
			} else {
				mBvMsg.hide();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			mTabAdapter = new ActiveTabPagerAdapter(getChildFragmentManager(),
					getActivity(), mViewPager);
		}
		mViewPager.setOffscreenPageLimit(mTabAdapter.getCacheCount());
		mViewPager.setAdapter(mTabAdapter);
		mViewPager.setOnPageChangeListener(this);
		mTabStrip.setViewPager(mViewPager);

		mBvAtMe = new BadgeView(getActivity(), mTabStrip.getBadgeView(1));
		mBvAtMe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvAtMe.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mBvAtMe.setBackgroundResource(R.drawable.tab_notification_bg);

		mBvComment = new BadgeView(getActivity(), mTabStrip.getBadgeView(2));
		mBvComment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvComment.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mBvComment.setBackgroundResource(R.drawable.tab_notification_bg);
		
		mBvMsg = new BadgeView(getActivity(), mTabStrip.getBadgeView(4));
		mBvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvMsg.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mBvMsg.setBackgroundResource(R.drawable.tab_notification_bg);
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