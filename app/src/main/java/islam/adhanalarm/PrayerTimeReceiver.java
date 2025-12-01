package islam.adhanalarm;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import islam.adhanalarm.widget.AllDayPrayersWidgetProvider;
import islam.adhanalarm.widget.NextPrayerWidgetProvider;

public class PrayerTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case CONSTANT.ACTION_UPDATE_PRAYER_TIMES:
                    PrayerTimeScheduler.scheduleAlarms(context);
                    break;
                case CONSTANT.ACTION_UPDATE_WIDGET:
                    updateWidgets(context);
                    break;
                default:
                    showPrayerTimeNotification(context, intent);
                    break;
            }
        } else {
            showPrayerTimeNotification(context, intent);
        }
    }

    private void showPrayerTimeNotification(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        int notificationId = intent.getIntExtra("notification_id", 1);
        String notificationTitle = "Prayer Time";
        String notificationMessage = "It's time for " + prayerName;
        NotificationHelper.showNotification(context, notificationTitle, notificationMessage, notificationId);
    }

    private void updateWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AllDayPrayersWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new AllDayPrayersWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds);
        }
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NextPrayerWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new NextPrayerWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}
