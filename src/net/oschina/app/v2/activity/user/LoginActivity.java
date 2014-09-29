package net.oschina.app.v2.activity.user;

import java.io.ByteArrayInputStream;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.api.ApiHttpClient;
import net.oschina.app.v2.api.remote.UserApi;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.User;
import net.oschina.app.v2.utils.SimpleTextWatcher;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.TLog;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

	public static final int REQUEST_CODE_INIT = 0;
	private static final String BUNDLE_KEY_REQUEST_CODE = "BUNDLE_KEY_REQUEST_CODE";
	protected static final String TAG = LoginActivity.class.getSimpleName();
	private static final String LOGIN_SCREEN = "LoginScreen";
	private EditText mEtUserName, mEtPassword;
	private View mIvClearUserName, mIvClearPassword;
	private Button mBtnLogin;
	private int requestCode = REQUEST_CODE_INIT;
	private String mUserName, mPassword;

	private TextWatcher mUserNameWatcher = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mIvClearUserName.setVisibility(TextUtils.isEmpty(s) ? View.GONE
					: View.VISIBLE);
		}
	};
	private TextWatcher mPassswordWatcher = new SimpleTextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mIvClearPassword.setVisibility(TextUtils.isEmpty(s) ? View.GONE
					: View.VISIBLE);
		}
	};

	private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				AsyncHttpClient client = ApiHttpClient.getHttpClient();
				HttpContext httpContext = client.getHttpContext();
				CookieStore cookies = (CookieStore) httpContext
						.getAttribute(ClientContext.COOKIE_STORE);
				if (cookies != null) {
					String tmpcookies = "";
					for (Cookie c : cookies.getCookies()) {
						TLog.log(TAG,
								"cookie:" + c.getName() + " " + c.getValue());
						tmpcookies += (c.getName() + "=" + c.getValue()) + ";";
					}
					TLog.log(TAG, "cookies:" + tmpcookies);
					AppContext.instance().setProperty("cookie", tmpcookies);
					ApiHttpClient.setCookie(ApiHttpClient.getCookie(AppContext
							.instance()));
				}
				User user = User.parse(new ByteArrayInputStream(arg2));
				user.setAccount(mUserName);
				user.setPwd(mPassword);
				user.setRememberMe(false);
				Result res = user.getValidate();
				if (res.OK()) {
					// 保存登录信息
					AppContext.instance().saveLoginInfo(user);
					hideWaitDialog();
					handleLoginSuccess();
				} else {
					AppContext.instance().cleanLoginInfo();
					hideWaitDialog();
					AppContext.showToast(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			hideWaitDialog();
			AppContext.showToast(R.string.tip_login_error_for_network);
		}
	};

	protected int getLayoutId() {
		return R.layout.v2_activity_login;
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getActionBarTitle() {
		return R.string.login;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		Intent data = getIntent();
		if (data != null) {
			requestCode = data.getIntExtra(BUNDLE_KEY_REQUEST_CODE,
					REQUEST_CODE_INIT);
		}
		initViews();
	}

	private void initViews() {
		mEtUserName = (EditText) findViewById(R.id.et_username);
		mEtUserName.addTextChangedListener(mUserNameWatcher);
		mEtPassword = (EditText) findViewById(R.id.et_password);
		mEtPassword.addTextChangedListener(mPassswordWatcher);
		mIvClearUserName = findViewById(R.id.iv_clear_username);
		mIvClearUserName.setOnClickListener(this);
		mIvClearPassword = findViewById(R.id.iv_clear_password);
		mIvClearPassword.setOnClickListener(this);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.iv_clear_username) {
			mEtUserName.getText().clear();
			mEtUserName.requestFocus();
		} else if (id == R.id.iv_clear_password) {
			mEtPassword.getText().clear();
			mEtPassword.requestFocus();
		} else if (id == R.id.btn_login) {
			handleLogin();
		}
	}

	private void handleLogin() {
		if (!prepareForLogin()) {
			return;
		}

		// if the data has ready
		mUserName = mEtUserName.getText().toString();
		mPassword = mEtPassword.getText().toString();

		showWaitDialog(R.string.progress_login);
		UserApi.login(mUserName, mPassword, mHandler);
	}

	private boolean prepareForLogin() {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_no_internet);
			return false;
		}
		String uName = mEtUserName.getText().toString();
		if (TextUtils.isEmpty(uName)) {
			AppContext.showToastShort(R.string.tip_please_input_username);
			mEtUserName.requestFocus();
			return false;
		}
		String pwd = mEtPassword.getText().toString();
		if (TextUtils.isEmpty(pwd)) {
			AppContext.showToastShort(R.string.tip_please_input_password);
			mEtPassword.requestFocus();
			return false;
		}
		return true;
	}

	private void handleLoginSuccess() {
		Intent data = new Intent();
		data.putExtra(BUNDLE_KEY_REQUEST_CODE, requestCode);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LOGIN_SCREEN);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LOGIN_SCREEN);
		MobclickAgent.onPause(this);
	}
}
