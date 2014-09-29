package net.oschina.app.v2.activity.settings.fragment;

import java.io.File;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.ui.dialog.CommonDialog;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.tooglebutton.ToggleButton;
import net.oschina.app.v2.ui.tooglebutton.ToggleButton.OnToggleChanged;
import net.oschina.app.v2.utils.FileUtils;
import net.oschina.app.v2.utils.MethodsCompat;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SettingsFragment extends BaseFragment {

	private static final String SETTINGS_SCREEN = "settings_screen";
	private TextView mTvPicPath;
	private String mCachePicPath;
	private ToggleButton mTbLoadImage;
	private TextView mTvCacheSize;
	private TextView mTvVersionName;
	private UpdateResponse mUpdateInfo;
	private boolean mIsCheckingUpdate;
	private View mBtnLogout;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_settings, container,
				false);
		initViews(view);
		initData();
		return view;
	}

	@Override
	public void onDestroyView() {
		UmengUpdateAgent.setUpdateListener(null);
		super.onDestroyView();
	}

	private void initViews(View view) {
		view.findViewById(R.id.ly_about).setOnClickListener(this);
		view.findViewById(R.id.ly_pic_path).setOnClickListener(this);
		view.findViewById(R.id.ly_notification_settings).setOnClickListener(this);
		mTvPicPath = (TextView) view
				.findViewById(R.id.tv_current_picture_save_path);
		mTbLoadImage = (ToggleButton) view.findViewById(R.id.tb_load_picture);
		mTbLoadImage.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.setLoadImage(on);
			}
		});


		view.findViewById(R.id.ly_cache_size).setOnClickListener(this);
		mTvCacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
		mTvVersionName = (TextView) view.findViewById(R.id.tv_version_name);

		view.findViewById(R.id.ly_version_name).setOnClickListener(this);
		view.findViewById(R.id.ly_open_market).setOnClickListener(this);
		view.findViewById(R.id.ly_feedback).setOnClickListener(this);
		mBtnLogout = view.findViewById(R.id.btn_logout);
		mBtnLogout.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void initData() {
		if (AppContext.shouldLoadImage())
			mTbLoadImage.setToggleOn();
		else
			mTbLoadImage.setToggleOff();

		mCachePicPath = ImageLoader.getInstance().getDiskCache().getDirectory()
				.getAbsolutePath();

		mTvPicPath.setText(getString(R.string.current_picture_save_path,
				mCachePicPath));
		caculateCacheSize();

		mTvVersionName.setText(TDevice.getVersionName());

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				mIsCheckingUpdate = false;
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					mUpdateInfo = updateInfo;
					if (isAdded()) {
						mTvVersionName
								.setText(getString(R.string.found_new_version,
										updateInfo.version));
					}
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
		mIsCheckingUpdate = true;
		UmengUpdateAgent.update(getActivity().getApplicationContext());

		if (AppContext.instance().isLogin()) {
			mBtnLogout.setVisibility(View.VISIBLE);
		} else {
			mBtnLogout.setVisibility(View.GONE);
		}
	}

	private void caculateCacheSize() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getActivity().getFilesDir();
		File cacheDir = getActivity().getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat
					.getExternalCacheDir(getActivity());
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		mTvCacheSize.setText(cacheSize);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.ly_pic_path) {
			AppContext.showToastShort(mCachePicPath);
		} else if (id == R.id.ly_about) {
			UIHelper.showAbout(getActivity());
		} else if (id == R.id.ly_cache_size) {
			UIHelper.clearAppCache(getActivity());
			mTvCacheSize.setText("0KB");
		} else if (id == R.id.ly_version_name) {
			if (mIsCheckingUpdate) {
				AppContext.showToastShort(R.string.tip_checking_update);
			} else {
				if (mUpdateInfo != null) {
					UmengUpdateAgent.showUpdateDialog(getActivity()
							.getApplicationContext(), mUpdateInfo);
				} else {
					AppContext.showToastShort(R.string.tip_has_not_update);
				}
			}
		} else if (id == R.id.ly_open_market) {
			TDevice.openAppInMarket(getActivity());
		} else if (id == R.id.ly_feedback) {
			FeedbackAgent agent = new FeedbackAgent(getActivity());
			agent.startFeedbackActivity();
		} else if (id == R.id.btn_logout) {
			handleLogout();
		} else if(id == R.id.ly_notification_settings){
			UIHelper.showNotificaitonSettings(getActivity());
		}
	}

	private void handleLogout() {
		CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setMessage(R.string.message_logout);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppContext.instance().Logout();
						AppContext.showToastShort(R.string.tip_logout_success);
						getActivity().finish();
					}
				});
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SETTINGS_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SETTINGS_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
