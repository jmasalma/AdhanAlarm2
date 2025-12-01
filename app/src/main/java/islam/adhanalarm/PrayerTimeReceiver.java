package islam.adhanalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PrayerTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        int notificationId = intent.getIntExtra("notification_id", 1);
        String notificationTitle = "Prayer Time";
        String notificationMessage = "It's time for " + prayerName;
        NotificationHelper.showNotification(context, notificationTitle, notificationMessage, notificationId);
    }
}
