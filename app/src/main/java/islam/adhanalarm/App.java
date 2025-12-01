package islam.adhanalarm;

import android.app.Application;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
// import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class App extends Application {

    private static App sInstance;

    private MediaPlayer mPlayer;

    // public static void broadcastPrayerTimeUpdate() {
    //     LocalBroadcastManager.getInstance(sInstance).sendBroadcast(new Intent(CONSTANT.ACTION_UPDATE_PRAYER_TIME));
    // }

    public static void startMedia(int resid) {
        sInstance.mPlayer.stop();
        sInstance.mPlayer = MediaPlayer.create(sInstance, resid);
        sInstance.mPlayer.setScreenOnWhilePlaying(true);
        sInstance.mPlayer.start();
    }

    public static void stopMedia() {
        sInstance.mPlayer.stop();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mPlayer = MediaPlayer.create(this, R.raw.bismillah);
    }

    @Override
    public void onTerminate() {
        sInstance.mPlayer.stop();
        super.onTerminate();
    }
}
