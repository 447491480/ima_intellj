
package net.oschina.app.v2.activity.news;

import net.oschina.app.v2.activity.blog.fragment.BlogFragment;
import net.oschina.app.v2.activity.news.fragment.NewsFragment;
import net.oschina.app.v2.model.BlogList;
import net.oschina.app.v2.model.NewsList;

import com.tonlin.osc.happy.R;


public enum NewsTab {
    LASTEST(0,NewsList.CATALOG_ALL, R.string.frame_title_news_lastest, NewsFragment.class),
    BLOG(1, BlogList.CATALOG_LATEST,R.string.frame_title_news_blog, BlogFragment.class),
    RECOMMEND(2,BlogList.CATALOG_RECOMMEND, R.string.frame_title_news_recommend, BlogFragment.class);
    
    private Class<?> clz;
    private int idx;
    private int title;
    private int catalog;

    private NewsTab(int idx,int catalog, int title, Class<?> clz) {
        this.idx = idx;
        this.clz = clz;
        this.setCatalog(catalog);
        this.setTitle(title);
    }

    public static NewsTab getTabByIdx(int idx) {
        for (NewsTab t : values()) {
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
