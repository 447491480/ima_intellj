package net.oschina.app.v2.activity;

import net.oschina.app.v2.activity.active.fragment.ActiveViewPagerFragment;
import net.oschina.app.v2.activity.news.fragment.NewsViewPagerFragment;
import net.oschina.app.v2.activity.question.fragment.QuestionViewPagerFragment;
import net.oschina.app.v2.activity.tweet.fragment.TweetViewPagerFragment;

import com.tonlin.osc.happy.R;

public enum MainTab {

	NEWS(0, R.string.tab_name_article, R.drawable.tab_icon_new, NewsViewPagerFragment.class),

	QUESTION(1, R.string.tab_name_question, R.drawable.tab_icon_question,
			QuestionViewPagerFragment.class),

	TWEET(2, R.string.tab_name_tweet, R.drawable.tab_icon_tweet, TweetViewPagerFragment.class),

	ME(3, R.string.tab_name_me, R.drawable.tab_icon_me, ActiveViewPagerFragment.class);
	
	private int idx;
	private int resName;
	private int resIcon;
	private Class<?> clz;

	private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
		this.idx = idx;
		this.resName = resName;
		this.resIcon = resIcon;
		this.clz = clz;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getResName() {
		return resName;
	}

	public void setResName(int resName) {
		this.resName = resName;
	}

	public int getResIcon() {
		return resIcon;
	}

	public void setResIcon(int resIcon) {
		this.resIcon = resIcon;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}
}
