
package net.oschina.app.v2.activity.active;

import net.oschina.app.v2.activity.active.fragment.ActiveFragment;
import net.oschina.app.v2.activity.message.fragment.MessageFragment;
import net.oschina.app.v2.model.ActiveList;

import com.tonlin.osc.happy.R;


public enum ActiveTab {
    LASTEST(0,ActiveList.CATALOG_LASTEST, R.string.frame_title_active_lastest, ActiveFragment.class),
    ATME(1, ActiveList.CATALOG_ATME,R.string.frame_title_active_atme, ActiveFragment.class),
    COMMENT(2,ActiveList.CATALOG_COMMENT, R.string.frame_title_active_comment, ActiveFragment.class),
    MYSELF(3, ActiveList.CATALOG_MYSELF,R.string.frame_title_active_myself, ActiveFragment.class),
    MESSAGE(4, -1,R.string.frame_title_active_message, MessageFragment.class);
    
    private Class<?> clz;
    private int idx;
    private int title;
    private int catalog;

    private ActiveTab(int idx,int catalog, int title, Class<?> clz) {
        this.idx = idx;
        this.clz = clz;
        this.setCatalog(catalog);
        this.setTitle(title);
    }

    public static ActiveTab getTabByIdx(int idx) {
        for (ActiveTab t : values()) {
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
