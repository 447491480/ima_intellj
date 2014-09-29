package net.oschina.app.v2.activity.settings.fragment;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.base.BaseFragment;
import net.oschina.app.v2.ui.tooglebutton.ToggleButton;
import net.oschina.app.v2.ui.tooglebutton.ToggleButton.OnToggleChanged;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonlin.osc.happy.R;

public class NotifIcationSettingsFragment extends BaseFragment {
	private ToggleButton mTbNotificationSound, mTbDisableWhenExit;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.v2_fragment_notification_settings, container, false);
		initViews(view);
		initData();
		return view;
	}

	private void initViews(View view) {
		mTbNotificationSound = (ToggleButton) view
				.findViewById(R.id.tb_notification_sound);
		mTbNotificationSound.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.setNotificationSoundEnable(on);
			}
		});

		mTbDisableWhenExit = (ToggleButton) view
				.findViewById(R.id.tb_notification_disable_when_exit);
		mTbDisableWhenExit.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.setNotificationDisableWhenExit(on);
			}
		});
	}

	private void initData() {
		if (AppContext.isNotificationDisableWhenExit()) {
			mTbDisableWhenExit.setToggleOn();
		} else {
			mTbDisableWhenExit.setToggleOff();
		}
	}
}
