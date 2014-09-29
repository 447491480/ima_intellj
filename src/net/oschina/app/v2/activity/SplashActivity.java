package net.oschina.app.v2.activity;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.model.DailyEnglish;
import net.oschina.app.v2.model.Version;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class SplashActivity extends Activity {

	private static final String SPLASH_SCREEN = "SplashScreen";
	public static final int MAX_WATTING_TIME = 3000;// 停留时间3秒
	protected Version mVersion;
	protected boolean mShouldGoTo = true;

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SPLASH_SCREEN); // 统计页面
		MobclickAgent.onResume(this); // 统计时长

		if (!mShouldGoTo) {
			redirectTo();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SPLASH_SCREEN); // 保证 onPageEnd 在onPause
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(TDevice.isServiceRunning(this, "net.oschina.app.v2.service.NoticeService")){
			redirectTo();
			return;
		}
		AppContext.requestDailyEnglish();
		checkUpdate();
		final View view = View.inflate(this, R.layout.v2_activity_splash, null);
		setContentView(view);

		DailyEnglish de = AppContext.getDailyEnglish();
		if (de != null) {
			TextView tvContent = (TextView) findViewById(R.id.tv_eng);
			tvContent.setText(de.getContent());
			TextView tvNote = (TextView) findViewById(R.id.tv_note);
			tvNote.setText(de.getNote());
		}

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2500);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (mShouldGoTo) {
					redirectTo();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		// 兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name)
					&& !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name",
						"cookie_value", "cookie_version", "cookie_path");
			}
		}
	}

	private void checkUpdate() {
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					// mVersion = new Version(updateInfo);
					mShouldGoTo = false;
					UmengUpdateAgent.showUpdateDialog(getApplicationContext(),
							updateInfo);
					break;
				case UpdateStatus.No: // has no update
					break;
				case UpdateStatus.NoneWifi: // none wifi
					break;
				case UpdateStatus.Timeout: // time out
					break;
				}
			}
		});
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(getApplicationContext());
	}

	private void redirectTo() {
		Intent intent = new Intent(this, MainActivity.class);
		// if (mVersion != null) {
		// intent.putExtra(Version.BUNDLE_KEY_VERSION, mVersion);
		// }
		startActivity(intent);
		finish();
	}
}