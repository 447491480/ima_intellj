
package net.oschina.app.v2.activity.question;

import net.oschina.app.v2.activity.question.fragment.QuestionFragment;
import net.oschina.app.v2.model.PostList;

import com.tonlin.osc.happy.R;


public enum QuestionTab {
    ASK(0,PostList.CATALOG_ASK, R.string.frame_title_question_ask, QuestionFragment.class),
    SHARE(1, PostList.CATALOG_SHARE,R.string.frame_title_question_share, QuestionFragment.class),
    OTHER(2,PostList.CATALOG_OTHER, R.string.frame_title_question_other, QuestionFragment.class),
    JOB(3, PostList.CATALOG_JOB,R.string.frame_title_question_job, QuestionFragment.class),
    SITE(4, PostList.CATALOG_SITE,R.string.frame_title_question_site, QuestionFragment.class);
    
    private Class<?> clz;
    private int idx;
    private int title;
    private int catalog;

    private QuestionTab(int idx,int catalog, int title, Class<?> clz) {
        this.idx = idx;
        this.clz = clz;
        this.setCatalog(catalog);
        this.setTitle(title);
    }

    public static QuestionTab getTabByIdx(int idx) {
        for (QuestionTab t : values()) {
            if (t.getIdx() == idx)
                return t;
        }
        return ASK;
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
