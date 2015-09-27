package com.docflowdroid;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.widget.Toast;

import com.docflow.shared.SystemNames;
import com.docflow.shared.docflow.NewDocuments;

public class TimeService extends Service {
	// constant
	public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
	private static final int notifier_id = 9999;

	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	// timer handling
	private Timer mTimer = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// cancel if already existed
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// recreate new
			mTimer = new Timer();
		}
		// schedule task
		startTimer();
	}

	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0,
				DocFlow.user_obj.getAndroid_check_status_interval());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DocFlow.session_id = null;
		mTimer.cancel();
	}

	class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// display toast

					try {

						NewDocuments newDocuments = null;
						try {
							newDocuments = DocFlow.docFlowService
									.checkForNewDocumentsAndroid(
											DocFlow.session_id,
											DocFlow.user_obj.getUser()
													.getUser_id(),
											DocFlow.subregion_id,
											DocFlow.android_check_system_ids,
											DocFlow.device_id);
							DocFlow.session_id = newDocuments.getSession_id();
							if (newDocuments.getSystems() == null
									|| newDocuments.getSystems().isEmpty())
								return;
						} catch (Exception e) {
							Toast.makeText(
									getApplicationContext(),
									"Unable to connect server" + e.getMessage(),
									Toast.LENGTH_SHORT).show();
							return;
						}
						// Toast.makeText(getApplicationContext(),
						// "New documents", Toast.LENGTH_SHORT).show();
						String newMessage = "New messages count=<b>"
								+ newDocuments.getFull_count() + "<b>";

						Set<Integer> _system_ids = newDocuments.getSystems()
								.keySet();
						for (Integer _system : _system_ids) {
							newMessage += "<br>"
									+ ((SystemNames.getSystemName(_system)
											+ " count=<b>"
											+ newDocuments.getFull_count() + "<b>"));

						}
						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								TimeService.this)
								.setSmallIcon(R.drawable.mail_message_new)
								.setDefaults(Notification.DEFAULT_ALL)
								.setContentText(Html.fromHtml(newMessage))
								.setContentTitle("New documents")
								.setNumber(newDocuments.getSystems().size());

						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						notificationManager.cancel(notifier_id);

						Intent notificationIntent = new Intent(
								TimeService.this, MainActivity.class);
						notificationIntent.putExtra(MainActivity.NM_SESSION_ID,
								DocFlow.session_id);

						notificationIntent.setAction(Intent.ACTION_MAIN);
						notificationIntent
								.addCategory(Intent.CATEGORY_LAUNCHER);
						notificationIntent
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_SINGLE_TOP);

						PendingIntent pendingIntent = PendingIntent
								.getActivity(TimeService.this, 0,
										notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						mBuilder.setContentIntent(pendingIntent);
						Notification notification = mBuilder.build();

						notificationManager.notify(notifier_id, notification);
					} finally {
						try {
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}

				}

			});
		}
	}
}