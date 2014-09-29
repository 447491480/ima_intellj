package net.oschina.app.v2.broadcast;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.service.NoticeUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!AppContext.isNotificationDisableWhenExit()) {
			NoticeUtils.startNotifyService(context);
		}
	}
}
