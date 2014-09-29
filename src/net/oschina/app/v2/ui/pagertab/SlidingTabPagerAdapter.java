package net.oschina.app.v2.ui.pagertab;

import net.oschina.app.v2.base.BaseTabFragment;
import net.oschina.app.v2.base.BaseTabFragment.TabChangedListener;
import net.oschina.app.v2.ui.pagertab.PagerSlidingTabStrip.OnTabClickListener;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public abstract class SlidingTabPagerAdapter extends FragmentPagerAdapter
		implements TabChangedListener, OnTabClickListener {

	protected final Context context;
	protected final BaseTabFragment[] fragments;
	private final ViewPager pager;
	private int lastPostion;

	public SlidingTabPagerAdapter(FragmentManager mgr, int i, Context context,
			ViewPager vp) {
		super(mgr);
		lastPostion = 0;
		fragments = new BaseTabFragment[i];
		this.context = context;
		pager = vp;
		lastPostion = 0;
	}

	private BaseTabFragment getFragmentByPosition(int i) {
		BaseTabFragment fragment = null;
		if (i >= 0 && i < fragments.length)
			fragment = fragments[i];
		return fragment;
	}

	private void onLeave(int i) {
		BaseTabFragment fragment = getFragmentByPosition(lastPostion);
		lastPostion = i;
		if (fragment != null)
			fragment.g();
	}

	public abstract int getCacheCount();

	@Override
	public abstract int getCount();

	@Override
	public BaseTabFragment getItem(int i) {
		return fragments[i];
	}

	@Override
	public abstract CharSequence getPageTitle(int i);

	public boolean isCurrent(BaseTabFragment fragment) {
		boolean flag = false;
		int i = pager.getCurrentItem();
		int j = 0;
		do {
			label0: {
				if (j < fragments.length) {
					if (fragment != fragments[j] || j != i)
						break label0;
					flag = true;
				}
				return flag;
			}
			j++;
		} while (true);
	}

	public void onPageScrolled(int i) {
		BaseTabFragment fragment = getFragmentByPosition(i);
		if (fragment != null) {
			fragment.h();
			onLeave(i);
		}
	}

	public void onPageSelected(int i) {
		BaseTabFragment fragment = getFragmentByPosition(i);
		if (fragment != null) {
			fragment.f();
			onLeave(i);
		}
	}

	@Override
	public void onTabClicked(int i) {
		BaseTabFragment fragment = getFragmentByPosition(i);
		if (fragment != null)
			fragment.i();
	}
}
