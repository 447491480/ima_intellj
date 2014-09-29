package net.oschina.app.v2.base;

public class BaseTabFragment extends BaseFragment {

	public static interface TabChangedListener {

		public abstract boolean isCurrent(BaseTabFragment fragment);
	}

	private TabChangedListener mListener;

	public BaseTabFragment() {
	}

	public final void a(TabChangedListener listener) {
		mListener = listener;
	}

	protected final boolean e() {
		return mListener.isCurrent(this);
	}

	public void f() {
	}

	public void g() {
	}

	public void h() {
	}

	public void i() {
	}
}
