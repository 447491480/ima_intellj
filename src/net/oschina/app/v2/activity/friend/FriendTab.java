package net.oschina.app.v2.activity.friend;

import net.oschina.app.v2.activity.friend.fragment.FriendFragment;
import net.oschina.app.v2.model.FriendList;

import com.tonlin.osc.happy.R;

public enum FriendTab {
	FOLLOWING(0, FriendList.TYPE_FOLLOWER,
			R.string.frame_title_friend_following, FriendFragment.class), FOLLOWER(
			1, FriendList.TYPE_FANS, R.string.frame_title_friend_follower,
			FriendFragment.class);

	private Class<?> clz;
	private int idx;
	private int title;
	private int catalog;

	private FriendTab(int idx, int catalog, int title, Class<?> clz) {
		this.idx = idx;
		this.clz = clz;
		this.setCatalog(catalog);
		this.setTitle(title);
	}

	public static FriendTab getTabByIdx(int idx) {
		for (FriendTab t : values()) {
			if (t.getIdx() == idx)
				return t;
		}
		return FOLLOWING;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
}
