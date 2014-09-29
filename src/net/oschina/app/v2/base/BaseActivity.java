package net.oschina.app.v2.base;

import net.oschina.app.v2.ui.dialog.CommonToast;
import net.oschina.app.v2.ui.dialog.DialogControl;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.dialog.WaitDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.tonlin.osc.happy.R;

public abstract class BaseActivity extends ActionBarActivity implements
		DialogControl, VisibilityControl, View.OnClickListener {
	public static final String INTENT_ACTION_EXIT_APP = "INTENT_ACTION_EXIT_APP";
	private boolean _isVisible;
	private WaitDialog _waitDialog;

	protected LayoutInflater mInflater;
	private ActionBar mActionBar;
	private TextView mTvActionTitle;

	private BroadcastReceiver mExistReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!hasActionBar()) {
			supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		onBeforeSetContentLayout();
		if (getLayoutId() != 0) {
			setContentView(getLayoutId());
		}
		mActionBar = getSupportActionBar();
		mInflater = getLayoutInflater();
		if (hasActionBar()) {
			initActionBar(mActionBar);
		}
		init(savedInstanceState);

		IntentFilter filter = new IntentFilter(INTENT_ACTION_EXIT_APP);
		registerReceiver(mExistReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mExistReceiver);
		mExistReceiver = null;
		super.onDestroy();
	}

	protected void onBeforeSetContentLayout() {
	}

	protected boolean hasActionBar() {
		return true;
	}

	protected int getLayoutId() {
		return 0;
	}

	protected View inflateView(int resId) {
		return mInflater.inflate(resId, null);
	}

	protected int getActionBarTitle() {
		return R.string.app_name;
	}

	protected boolean hasBackButton() {
		return false;
	}

	protected int getActionBarCustomView() {
		return 0;
	}

	protected void init(Bundle savedInstanceState) {
	}

	protected void initActionBar(ActionBar actionBar) {
		if (actionBar == null)
			return;
		if (hasBackButton()) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			int layoutRes = getActionBarCustomView();
			View view = inflateView(layoutRes == 0 ? R.layout.v2_actionbar_custom_backtitle
					: layoutRes);
			View back = view.findViewById(R.id.btn_back);
			if (back == null) {
				throw new IllegalArgumentException(
						"can not find R.id.btn_back in customView");
			}
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			mTvActionTitle = (TextView) view
					.findViewById(R.id.tv_actionbar_title);
			if (mTvActionTitle == null) {
				throw new IllegalArgumentException(
						"can not find R.id.tv_actionbar_title in customView");
			}
			int titleRes = getActionBarTitle();
			if (titleRes != 0) {
				mTvActionTitle.setText(titleRes);
			}
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(view, params);
		} else {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setDisplayUseLogoEnabled(false);
			int titleRes = getActionBarTitle();
			if (titleRes != 0) {
				actionBar.setTitle(titleRes);
			}
		}
	}

	public void setActionBarTitle(int resId) {
		if (resId != 0) {
			setActionBarTitle(getString(resId));
		}
	}

	public void setActionBarTitle(String title) {
		if (hasActionBar()) {
			if (mTvActionTitle != null) {
				mTvActionTitle.setText(title);
			}
			mActionBar.setTitle(title);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		_isVisible = false;
		hideWaitDialog();
		super.onPause();
	}

	@Override
	protected void onResume() {
		_isVisible = true;
		super.onResume();
	}

	public void showToast(int msgResid, int icon, int gravity) {
		showToast(getString(msgResid), icon, gravity);
	}

	public void showToast(String message, int icon, int gravity) {
		CommonToast toast = new CommonToast(this);
		toast.setMessage(message);
		toast.setMessageIc(icon);
		toast.setLayoutGravity(gravity);
		toast.show();
	}

	@Override
	public WaitDialog showWaitDialog() {
		return showWaitDialog(R.string.loading);
	}

	@Override
	public WaitDialog showWaitDialog(int resid) {
		return showWaitDialog(getString(resid));
	}

	@Override
	public WaitDialog showWaitDialog(String message) {
		if (_isVisible) {
			if (_waitDialog == null) {
				_waitDialog = DialogHelper.getWaitDialog(this, message);
			}
			if (_waitDialog != null) {
				_waitDialog.setMessage(message);
				_waitDialog.show();
			}
			return _waitDialog;
		}
		return null;
	}

	@Override
	public void hideWaitDialog() {
		if (_isVisible && _waitDialog != null) {
			try {
				_waitDialog.dismiss();
				_waitDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean isVisible() {
		return _isVisible;
	}
}
