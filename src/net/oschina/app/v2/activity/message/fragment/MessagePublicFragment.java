package net.oschina.app.v2.activity.message.fragment;

import java.io.ByteArrayInputStream;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.utils.SimpleTextWatcher;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class MessagePublicFragment extends BaseFragment {

	public static final String BUNDLE_KEY_UID = "bundle_key_uid";
	public static final String BUNDLE_KEY_FID = "bundle_key_fid";
	public static final String BUNDLE_KEY_FNAME = "bundle_key_fname";
	protected static final int MAX_TEXT_LENGTH = 250;
	private static final String MESSAGE_PUBLIC_SCREEN = "message_public_screen";

	private TextView mTvName;
	private EditText mEtContent;
	private int mUid, mFid;
	private String mFName;

	private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(arg2));
				if (res.OK()) {
					AppContext
							.showToastShort(R.string.tip_message_public_success);
					getActivity().finish();
				} else {
					AppContext.showToastShort(res.getErrorMessage());
					hideWaitDialog();
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToastShort(R.string.tip_message_public_faile);
			hideWaitDialog();
		}
	};
	private TextView mTvClear;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		getActivity().getWindow().setSoftInputMode(mode);

		Bundle args = getArguments();
		mUid = args.getInt(BUNDLE_KEY_UID);
		mFid = args.getInt(BUNDLE_KEY_FID);
		mFName = args.getString(BUNDLE_KEY_FNAME);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_message_public,
				container, false);
		initViews(view);
		initData();
		return view;
	}

	private void initViews(View view) {
		mTvName = (TextView) view.findViewById(R.id.tv_name);
		mTvClear = (TextView) view.findViewById(R.id.tv_clear);
		mTvClear.setOnClickListener(this);
		mTvClear.setText(String.valueOf(MAX_TEXT_LENGTH));
		mEtContent = (EditText) view.findViewById(R.id.et_content);
		mEtContent.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mTvClear.setText((MAX_TEXT_LENGTH - s.length()) + "");
			}
		});
	}

	private void initData() {
		mTvName.setText(mFName);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.tv_clear) {
			if (TextUtils.isEmpty(mEtContent.getText().toString()))
				return;
			final CommonDialog dialog = DialogHelper
					.getPinterestDialogCancelable(getActivity());
			dialog.setMessage(R.string.clearwords);
			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mEtContent.getText().clear();
							TDevice.showSoftKeyboard(mEtContent);
						}
					});
			dialog.setNegativeButton(R.string.cancle, null);
			dialog.show();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.public_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.public_menu_send:
			handleSubmit();
			break;
		default:
			break;
		}
		return true;
	}

	private void handleSubmit() {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		String content = mEtContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			mEtContent.requestFocus();
			AppContext.showToastShort(R.string.tip_content_empty);
			return;
		}
		if (content.length() > MAX_TEXT_LENGTH) {
			AppContext.showToastShort(R.string.tip_content_too_long);
			return;
		}
		showWaitDialog(R.string.progress_submit);
		NewsApi.publicMessage(mUid, mFid, content, mHandler);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(MESSAGE_PUBLIC_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(MESSAGE_PUBLIC_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
