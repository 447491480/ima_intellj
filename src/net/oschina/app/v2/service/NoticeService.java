package net.oschina.app.v2.service;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.MainActivity;
import net.oschina.app.v2.activity.common.SimpleBackActivity;
import net.oschina.app.v2.activity.friend.fragment.FriendViewPagerFragment;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.broadcast.AlarmReceiver;
import net.oschina.app.v2.model.Notice;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.SimpleBackPage;
import net.oschina.app.v2.utils.TLog;
import net.oschina.app.v2.utils.UIHelper;

import org.apache.http.Header;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonlin.osc.happy.R;

public class NoticeService extends Service {
	public static final String INTENT_ACTION_GET = "net.oschina.app.v2.service.GET_NOTICE";
	public static final String INTENT_ACTION_CLEAR = "net.oschina.app.v2.service.CLEAR_NOTICE";
	public static final String INTENT_ACTION_BROADCAST = "net.oschina.app.v2.service.BROADCAST";
	public static final String INTENT_ACTION_SHUTDOWN = "net.oschina.app.v2.service.SHUTDOWN";
	public static final String INTENT_ACTION_REQUEST = "net.oschina.app.v2.service.REQUEST";
	public static final String BUNDLE_KEY_TPYE = "bundle_key_type";

	private static final long INTERVAL = 1000 * 120;
	private AlarmManager mAlarmMgr;

	private Notice mNotice;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.INTENT_ACTION_NOTICE.equals(action)) {
				int atmeCount = intent.getIntExtra("atmeCount", 0);// @我
				int msgCount = intent.getIntExtra("msgCount", 0);// 留言
				int reviewCount = intent.getIntExtra("reviewCount", 0);// 评论
				int newFansCount = intent.getIntExtra("newFansCount", 0);// 新粉丝
				int activeCount = atmeCount + reviewCount + msgCount
						+ newFansCount;
				if (activeCount == 0) {
					NotificationManagerCompat.from(NoticeService.this).cancel(
							R.string.you_have_news_messages);
				}
			} else if (INTENT_ACTION_BROADCAST.equals(action)) {
				if (mNotice != null) {
					UIHelper.sendBroadCast(NoticeService.this, mNotice);
				}
			} else if (INTENT_ACTION_SHUTDOWN.equals(action)) {
				stopSelf();
			} else if (INTENT_ACTION_REQUEST.equals(action)) {
				requestNotice();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		startRequestAlarm();
		requestNotice();

		IntentFilter filter = new IntentFilter(INTENT_ACTION_BROADCAST);
		filter.addAction(Constants.INTENT_ACTION_NOTICE);
		filter.addAction(INTENT_ACTION_SHUTDOWN);
		filter.addAction(INTENT_ACTION_REQUEST);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		cancelRequestAlarm();
		unregisterReceiver(mReceiver);
		TLog.log("消息通知服务关闭了");
		super.onDestroy();
	}

	private void startRequestAlarm() {
		cancelRequestAlarm();
		mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, INTERVAL,
				getOperationIntent());
	}

	private void cancelRequestAlarm() {
		mAlarmMgr.cancel(getOperationIntent());
	}

	private PendingIntent getOperationIntent() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return operation;
	}

	private void clearNotice(int uid, int type) {
		NewsApi.clearNotice(uid, type, mClearNoticeHandler);
	}

	private int lastNotifiyCount;

	private void notification(Notice notice) {
		int atmeCount = notice.getAtmeCount();
		int msgCount = notice.getMsgCount();
		int reviewCount = notice.getReviewCount();
		int newFansCount = notice.getNewFansCount();

		int count = atmeCount + msgCount + reviewCount + newFansCount;

		if (count == 0) {
			lastNotifiyCount = 0;
			NotificationManagerCompat.from(this).cancel(
					R.string.you_have_news_messages);
			return;
		}
		if (count == lastNotifiyCount)
			return;

		lastNotifiyCount = count;

		Resources res = getResources();
		String contentTitle = res.getString(R.string.you_have_news_messages,
				count);
		String contentText;
		StringBuffer sb = new StringBuffer();
		if (atmeCount > 0) {
			sb.append(getString(R.string.atme_count, atmeCount)).append(" ");
		}
		if (msgCount > 0) {
			sb.append(getString(R.string.msg_count, msgCount)).append(" ");
		}
		if (reviewCount > 0) {
			sb.append(getString(R.string.review_count, reviewCount))
					.append(" ");
		}
		if (newFansCount > 0) {
			sb.append(getString(R.string.fans_count, newFansCount));
		}
		contentText = sb.toString();

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("NOTICE", true);

		if (atmeCount == 0 && msgCount == 0 && reviewCount == 0
				&& newFansCount > 0) {
			// only fans
			Bundle args = new Bundle();
			args.putInt(FriendViewPagerFragment.BUNDLE_KEY_TABIDX, 1);
			intent = new Intent(this, SimpleBackActivity.class);
			intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
			intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,
					SimpleBackPage.FRIENDS.getValue());
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(this, 1000, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setTicker(contentTitle).setContentTitle(contentTitle)
				.setContentText(contentText).setAutoCancel(true)
				.setContentIntent(pi).setSmallIcon(R.drawable.ic_notification);

		if (AppContext.isNotificationSoundEnable()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		}

		Notification notification = builder.build();

		NotificationManagerCompat.from(this).notify(
				R.string.you_have_news_messages, notification);
	}

	private AsyncHttpResponseHandler mGetNoticeHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Notice notice = Notice.parse(new ByteArrayInputStream(arg2));
				if (notice != null) {
					UIHelper.sendBroadCast(NoticeService.this, notice);
					notification(notice);
					mNotice = notice;
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		};

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			arg3.printStackTrace();
		}
	};

	private AsyncHttpResponseHandler mClearNoticeHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(arg2));
				if (res.OK() && res.getNotice() != null) {
					mNotice = res.getNotice();
					UIHelper.sendBroadCast(NoticeService.this, res.getNotice());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
		}
	};

	private void requestNotice() {
		TLog.log("请求是否有新通知");
		NewsApi.getNotices(AppContext.instance().getLoginUid(),
				mGetNoticeHandler);
	}

	private static class ServiceStub extends INoticeService.Stub {
		WeakReference<NoticeService> mService;

		ServiceStub(NoticeService service) {
			mService = new WeakReference<NoticeService>(service);
		}

		@Override
		public void clearNotice(int uid, int type) throws RemoteException {
			mService.get().clearNotice(uid, type);
		}

		@Override
		public void scheduleNotice() throws RemoteException {
			mService.get().startRequestAlarm();
		}

		@Override
		public void requestNotice() throws RemoteException {
			mService.get().requestNotice();
		}
	}

	private final IBinder mBinder = new ServiceStub(this);
}
