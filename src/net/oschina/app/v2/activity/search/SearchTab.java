
package net.oschina.app.v2.activity.search;

import net.oschina.app.v2.activity.search.fragment.SearchFragment;
import net.oschina.app.v2.model.SearchList;

import com.tonlin.osc.happy.R;


public enum SearchTab {
    SOFTWARE(0,SearchList.CATALOG_SOFTWARE, R.string.tab_software, SearchFragment.class),
    QA(1, SearchList.CATALOG_POST,R.string.tab_qa, SearchFragment.class),
    BLOG(2,SearchList.CATALOG_BLOG, R.string.tab_blog, SearchFragment.class),
    NEWS(3,SearchList.CATALOG_NEWS, R.string.tab_news, SearchFragment.class),
    CODE(4,SearchList.CATALOG_CODE, R.string.tab_code, SearchFragment.class);
    
    private Class<?> clz;
    private int idx;
    private int title;
    private String catalog;

    private SearchTab(int idx,String catalog, int title, Class<?> clz) {
        this.idx = idx;
        this.clz = clz;
        this.setCatalog(catalog);
        this.setTitle(title);
    }

    public static SearchTab getTabByIdx(int idx) {
        for (SearchTab t : values()) {
            if (t.getIdx() == idx)
                return t;
        }
        return SOFTWARE;
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

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
}
