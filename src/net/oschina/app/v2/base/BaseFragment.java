package net.oschina.app.v2.base;

import net.oschina.app.v2.ui.dialog.DialogControl;
import net.oschina.app.v2.ui.dialog.WaitDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.tonlin.osc.happy.R;

public class BaseFragment extends Fragment implements View.OnClickListener {
	protected static final int STATE_NONE = 0;
	protected static final int STATE_REFRESH = 1;
	protected static final int STATE_LOADMORE = 2;
	protected int mState = STATE_NONE;
	
	protected void hideWaitDialog() {
		FragmentActivity activity = getActivity();
		if (activity instanceof DialogControl) {
			((DialogControl) activity).hideWaitDialog();
		}
	}

	protected WaitDialog showWaitDialog() {
		return showWaitDialog(R.string.loading);
	}

	protected WaitDialog showWaitDialog(int resid) {
		FragmentActivity activity = getActivity();
		if (activity instanceof DialogControl) {
			return ((DialogControl) activity).showWaitDialog(resid);
		}
		return null;
	}

	@Override
	public void onClick(View v) {
	}

	public boolean onBackPressed() {
		return false;
	}
}
