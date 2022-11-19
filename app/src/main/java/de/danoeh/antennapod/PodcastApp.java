package de.danoeh.antennapod;

import android.content.ComponentName;
import android.content.Intent;
import androidx.multidex.MultiDexApplication;
import de.danoeh.antennapod.activity.SplashActivity;

/** Main application class. */
public class PodcastApp extends MultiDexApplication {

    private static PodcastApp singleton;

    public static PodcastApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static void forceRestart() {
        Intent intent = new Intent(getInstance(), SplashActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        getInstance().startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}
