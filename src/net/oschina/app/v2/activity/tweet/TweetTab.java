
package net.oschina.app.v2.activity.tweet;

import net.oschina.app.v2.activity.tweet.fragment.TweetFragment;
import net.oschina.app.v2.model.TweetList;

import com.tonlin.osc.happy.R;


public enum TweetTab {
    LASTEST(0,TweetList.CATALOG_LASTEST, R.string.frame_title_tweet_lastest, TweetFragment.class),
    HOT(1, TweetList.CATALOG_HOT,R.string.frame_title_tweet_hot, TweetFragment.class),
    MY(2, 1 ,R.string.frame_title_tweet_my, TweetFragment.class);
    
    private Class<?> clz;
    private int idx;
    private int title;
    private int catalog;

    private TweetTab(int idx,int catalog, int title, Class<?> clz) {
        this.idx = idx;
        this.clz = clz;
        this.setCatalog(catalog);
        this.setTitle(title);
    }

    public static TweetTab getTabByIdx(int idx) {
        for (TweetTab t : values()) {
            if (t.getIdx() == idx)
                return t;
        }
        return LASTEST;
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
